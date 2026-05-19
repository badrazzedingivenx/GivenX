<?php

namespace App\Http\Controllers\Api\V1;

use App\Models\Appointment;
use App\Models\Invoice;
use App\Models\Lawyer;
use App\Models\Payment;
use App\Models\RefundRequest;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Str;
use Tymon\JWTAuth\Facades\JWTAuth;

class PaymentController extends ApiController
{
    /**
     * List payments for current user
     */
    public function index(Request $request): JsonResponse
    {
        $user = JWTAuth::user();

        if ($user->isClient()) {
            $payments = Payment::where('client_id', $user->client?->id)
                ->with(['lawyer', 'appointment'])
                ->orderBy('created_at', 'desc')
                ->paginate($request->input('per_page', 20));
        } else {
            $payments = Payment::where('lawyer_id', $user->lawyer?->id)
                ->with(['client', 'appointment'])
                ->orderBy('created_at', 'desc')
                ->paginate($request->input('per_page', 20));
        }

        $data = $payments->map(fn($payment) => $this->formatPayment($payment, $user));

        return $this->success([
            'data' => $data,
            'pagination' => [
                'current_page' => $payments->currentPage(),
                'last_page' => $payments->lastPage(),
                'per_page' => $payments->perPage(),
                'total' => $payments->total(),
            ],
        ]);
    }

    /**
     * Initiate payment
     * POST /api/v1/payments/initiate
     */
    public function initiate(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'appointment_id' => 'required|string|exists:appointments,id',
            'method' => 'required|in:card,bank_transfer,payment_link',
            'return_url' => 'required_if:method,card|nullable|url|max:500',
        ]);

        $user = JWTAuth::user();
        $client = $user->client;

        if (!$client) {
            return $this->error('CLIENT_NOT_FOUND', 'Client profile not found.', 404);
        }

        $appointment = Appointment::where('id', $validated['appointment_id'])
            ->where('client_user_id', $user->id)
            ->first();

        if (!$appointment) {
            return $this->error('APPOINTMENT_NOT_FOUND', 'No appointment found with the provided ID.', 404);
        }

        // Check if appointment is confirmed
        if ($appointment->status !== 'confirmed') {
            return $this->error('APPOINTMENT_NOT_CONFIRMED', 'Appointment must be confirmed before payment.', 400);
        }

        // Check if payment already exists
        $existingPayment = Payment::where('appointment_id', $appointment->id)
            ->where('status', 'Completed')
            ->first();
        if ($existingPayment) {
            return $this->error('APPOINTMENT_ALREADY_PAID', 'This appointment has already been paid for.', 400);
        }

        $lawyer = Lawyer::whereHas('profile.user', fn($q) => $q->where('id', $appointment->lawyer_user_id))->first();

        // Generate payment ID
        $paymentId = 'pay_' . Str::random(12);

        // Create payment record
        $payment = Payment::create([
            'id' => $paymentId,
            'client_id' => $client->id,
            'lawyer_id' => $lawyer?->id,
            'appointment_id' => $appointment->id,
            'date' => now()->toDateString(),
            'status' => 'Pending',
            'subject' => 'Consultation - ' . substr($appointment->notes ?? 'Appointment', 0, 100),
            'method' => $validated['method'],
            'amount_text' => number_format($appointment->price, 2) . ' MAD',
            'amount' => $appointment->price,
            'currency' => 'MAD',
        ]);

        // Generate payment response based on method
        $responseData = [
            'payment_id' => $payment->id,
            'appointment_id' => $appointment->id,
            'amount' => (float) $appointment->price,
            'currency' => 'MAD',
            'status' => $payment->status,
        ];

        switch ($validated['method']) {
            case 'card':
                // Return checkout URL for card payment
                $responseData['checkout_url'] = 'https://payment.haq.ma/checkout/' . $paymentId;
                $responseData['return_url'] = $validated['return_url'];
                break;

            case 'bank_transfer':
                // Return bank details
                $responseData['bank_details'] = [
                    'rib' => '007090000100100102584530',
                    'bank_name' => 'Attijariwafa Bank',
                    'account_name' => 'HAQ Platform SARL',
                    'reference' => $paymentId,
                ];
                break;

            case 'payment_link':
                // Return shareable payment link
                $responseData['payment_link'] = 'https://pay.haq.ma/link/' . $paymentId;
                $responseData['expires_at'] = now()->addHours(24)->toIso8601String();
                break;
        }

        // Auto-complete in local environment for testing
        if (app()->environment('local')) {
            $payment->update([
                'status' => 'Completed',
                'paid_at' => now(),
            ]);
            $responseData['status'] = 'Completed';
            $responseData['paid_at'] = $payment->paid_at->toIso8601String();

            // Create invoice
            $this->createInvoice($payment);
        }

        return $this->success($responseData, 'Payment initiated successfully.', 201);
    }

    /**
     * Show payment details
     */
    public function show(string $id): JsonResponse
    {
        $user = JWTAuth::user();

        $payment = Payment::with(['lawyer', 'client', 'appointment'])
            ->where('id', $id)
            ->first();

        if (!$payment) {
            return $this->error('PAYMENT_NOT_FOUND', 'Payment not found.', 404);
        }

        // Check authorization
        $isAuthorized = ($user->isClient() && $payment->client_id === $user->client?->id) ||
            ($user->isLawyer() && $payment->lawyer_id === $user->lawyer?->id);

        if (!$isAuthorized) {
            return $this->error('FORBIDDEN', 'You are not authorized to view this payment.', 403);
        }

        return $this->success($this->formatPayment($payment, $user));
    }

    /**
     * Request refund
     * POST /api/v1/payments/{id}/refund
     */
    public function refund(string $id, Request $request): JsonResponse
    {
        $validated = $request->validate([
            'reason' => 'required|string|min:10|max:1000',
        ]);

        $user = JWTAuth::user();

        $payment = Payment::where('id', $id)->first();

        if (!$payment) {
            return $this->error('PAYMENT_NOT_FOUND', 'Payment not found.', 404);
        }

        if ($payment->client_id !== $user->client?->id) {
            return $this->error('FORBIDDEN', 'You are not authorized to request a refund for this payment.', 403);
        }

        if ($payment->status !== 'Completed') {
            return $this->error('NOT_ELIGIBLE_FOR_REFUND', 'The payment does not qualify for a refund based on platform policy.', 400);
        }

        // Check if refund already requested
        $existingRefund = RefundRequest::where('payment_id', $id)->first();
        if ($existingRefund) {
            return $this->error('REFUND_EXISTS', 'Refund already requested for this payment.', 400);
        }

        $refund = RefundRequest::create([
            'id' => 'refund_' . Str::random(12),
            'payment_id' => $id,
            'user_id' => $user->id,
            'reason' => $validated['reason'],
            'status' => 'pending_review',
        ]);

        return $this->success([
            'refund_request_id' => $refund->id,
            'payment_id' => $payment->id,
            'status' => 'pending_review',
            'amount' => (float) $payment->amount,
            'reason' => $refund->reason,
            'created_at' => $refund->created_at->toIso8601String(),
        ], 'Refund request submitted successfully.');
    }

    /**
     * Get invoice
     */
    public function invoice(string $id): JsonResponse
    {
        $user = JWTAuth::user();

        $payment = Payment::with(['lawyer', 'client', 'invoice'])
            ->where('id', $id)
            ->first();

        if (!$payment) {
            return $this->error('PAYMENT_NOT_FOUND', 'Payment not found.', 404);
        }

        // Check authorization
        $isAuthorized = ($user->isClient() && $payment->client_id === $user->client?->id) ||
            ($user->isLawyer() && $payment->lawyer_id === $user->lawyer?->id);

        if (!$isAuthorized) {
            return $this->error('FORBIDDEN', 'You are not authorized to view this invoice.', 403);
        }

        if (!$payment->invoice) {
            return $this->error('INVOICE_NOT_FOUND', 'Invoice not found.', 404);
        }

        return $this->success([
            'id' => $payment->invoice->id,
            'payment_id' => $payment->id,
            'date' => $payment->invoice->date?->toDateString(),
            'status' => $payment->invoice->status,
            'amount' => $payment->invoice->amount,
            'amount_text' => $payment->invoice->amount_text,
            'lawyer_name' => $payment->invoice->lawyer_name,
            'download_url' => $payment->invoice_url,
        ]);
    }

    /**
     * Handle payment gateway webhook
     * POST /api/v1/payments/webhook
     */
    public function webhook(Request $request): JsonResponse
    {
        // Verify webhook signature
        $signature = $request->header('X-Webhook-Signature');
        $payload = $request->getContent();

        // TODO: Implement actual signature verification
        // $expectedSignature = hash_hmac('sha256', $payload, config('services.payment.webhook_secret'));
        // if (!hash_equals($expectedSignature, $signature)) {
        //     return response()->json(['error' => 'Invalid signature'], 401);
        // }

        // Parse webhook payload
        $data = $request->json()->all();

        // Process payment status update
        if (isset($data['payment_id']) && isset($data['status'])) {
            $payment = Payment::where('id', $data['payment_id'])->first();

            if ($payment) {
                $newStatus = match ($data['status']) {
                    'success', 'completed' => 'Completed',
                    'failed', 'declined' => 'Failed',
                    'pending', 'processing' => 'Pending',
                    default => $payment->status,
                };

                $payment->update([
                    'status' => $newStatus,
                    'paid_at' => $newStatus === 'Completed' ? now() : $payment->paid_at,
                    'gateway_response' => json_encode($data),
                ]);

                // Create invoice if payment completed
                if ($newStatus === 'Completed' && !$payment->invoice) {
                    $this->createInvoice($payment);
                }

                // Update appointment status if needed
                if ($newStatus === 'Completed' && $payment->appointment) {
                    $payment->appointment->update(['payment_status' => 'paid']);
                }
            }
        }

        return response()->json(['received' => true], 200);
    }

    /**
     * Format payment for response
     */
    private function formatPayment(Payment $payment, $currentUser): array
    {
        $isClient = $currentUser->isClient();

        return [
            'id' => $payment->id,
            'date' => $payment->date?->toDateString(),
            'status' => $payment->status,
            'subject' => $payment->subject,
            'method' => $payment->method,
            'amount' => $payment->amount,
            'amount_text' => $payment->amount_text,
            'currency' => $payment->currency,
            'paid_at' => $payment->paid_at?->toIso8601String(),
            'other_party' => $isClient
                ? [
                    'name' => $payment->lawyer?->name ?? 'Lawyer',
                ]
                : [
                    'name' => $payment->client?->profile?->full_name ?? 'Client',
                ],
            'appointment_id' => $payment->appointment_id,
            'invoice_available' => $payment->invoice !== null,
        ];
    }

    /**
     * Create invoice for payment
     */
    private function createInvoice(Payment $payment): void
    {
        $invoiceId = 'inv_' . Str::random(12);

        Invoice::create([
            'id' => $invoiceId,
            'payment_id' => $payment->id,
            'date' => now()->toDateString(),
            'status' => 'issued',
            'amount_text' => $payment->amount_text,
            'amount' => $payment->amount,
            'lawyer_name' => $payment->lawyer?->name ?? 'Lawyer',
        ]);

        // Update payment with invoice URL
        $payment->update([
            'invoice_url' => url('/api/v1/payments/' . $payment->id . '/invoice/download'),
        ]);
    }
}

