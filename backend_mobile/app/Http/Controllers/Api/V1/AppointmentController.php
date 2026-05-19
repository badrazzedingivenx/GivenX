<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Requests\Api\V1\StoreAppointmentRequest;
use App\Http\Requests\Api\V1\UpdateAppointmentRequest;
use App\Models\Appointment;
use App\Models\Lawyer;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Str;
use Tymon\JWTAuth\Facades\JWTAuth;

class AppointmentController extends ApiController
{
    /**
     * List appointments for current user
     * GET /api/v1/appointments
     */
    public function index(Request $request): JsonResponse
    {
        $user = JWTAuth::user();

        $query = Appointment::forUser($user->id)
            ->with(['client.profile', 'lawyer.profile']);

        // Filter by status
        $status = $request->input('status', 'all');
        if ($status !== 'all') {
            $query->where('status', $status);
        }

        // Filter by date range
        if ($request->has('from_date')) {
            $query->where('date', '>=', $request->input('from_date'));
        }
        if ($request->has('to_date')) {
            $query->where('date', '<=', $request->input('to_date'));
        }

        // Sorting
        $sortBy = $request->input('sort_by', 'date');
        $sortOrder = $request->input('sort_order', 'asc');
        $query->orderBy($sortBy, $sortOrder);

        // Pagination
        $page = $request->input('page', 1);
        $limit = $request->input('limit', 20);
        $appointments = $query->paginate($limit, ['*'], 'page', $page);

        $data = $appointments->map(fn($apt) => $this->formatAppointment($apt, $user));

        return $this->success([
            'appointments' => $data,
            'pagination' => [
                'current_page' => $appointments->currentPage(),
                'last_page' => $appointments->lastPage(),
                'per_page' => $appointments->perPage(),
                'total' => $appointments->total(),
            ],
        ]);
    }

    /**
     * Create new appointment (Book Appointment)
     * POST /api/v1/appointments
     */
    public function store(StoreAppointmentRequest $request): JsonResponse
    {
        $user = JWTAuth::user();
        $data = $request->validated();

        // Check if lawyer exists and is available
        $lawyer = Lawyer::with('profile.user')
            ->whereHas('profile.user', fn($q) => $q->where('id', $data['lawyer_id']))
            ->first();

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer not found.', 404);
        }

        if (!$lawyer->is_available) {
            return $this->error('LAWYER_NOT_AVAILABLE', 'Lawyer is not accepting appointments.', 403);
        }

        // Check if the time slot is available
        $existingAppointment = Appointment::where('lawyer_user_id', $data['lawyer_id'])
            ->where('date', $data['date'])
            ->where('time', $data['time'])
            ->whereNotIn('status', ['cancelled', 'no_show'])
            ->exists();

        if ($existingAppointment) {
            return $this->error('SLOT_NOT_AVAILABLE', 'The requested time slot is already booked.', 409);
        }

        // Generate unique ID
        $appointmentId = 'apt_' . Str::random(12);

        $appointment = Appointment::create([
            'id' => $appointmentId,
            'client_user_id' => $user->id,
            'lawyer_user_id' => $data['lawyer_id'],
            'date' => $data['date'],
            'time' => $data['time'],
            'duration_min' => $data['duration_min'] ?? 60,
            'type' => $data['type'],
            'status' => 'pending',
            'notes' => $data['notes'] ?? null,
            'price' => $this->calculatePrice($lawyer, $data['duration_min'] ?? 60),
        ]);

        return $this->success(
            $this->formatAppointment($appointment->load(['client.profile', 'lawyer.profile']), $user),
            'Appointment booked successfully.',
            201
        );
    }

    /**
     * Show appointment details
     */
    public function show(string $id): JsonResponse
    {
        $user = JWTAuth::user();

        $appointment = Appointment::with(['client.profile', 'lawyer.profile'])
            ->where('id', $id)
            ->first();

        if (!$appointment) {
            return $this->error('APPOINTMENT_NOT_FOUND', 'Appointment not found.', 404);
        }

        // Check if user is authorized to view this appointment
        if ($appointment->client_user_id !== $user->id && $appointment->lawyer_user_id !== $user->id) {
            return $this->error('FORBIDDEN', 'You are not authorized to view this appointment.', 403);
        }

        return $this->success($this->formatAppointment($appointment, $user));
    }

    /**
     * Update appointment
     */
    public function update(string $id, UpdateAppointmentRequest $request): JsonResponse
    {
        $user = JWTAuth::user();
        $data = $request->validated();

        $appointment = Appointment::where('id', $id)->first();

        if (!$appointment) {
            return $this->error('APPOINTMENT_NOT_FOUND', 'Appointment not found.', 404);
        }

        // Check authorization
        if ($appointment->client_user_id !== $user->id && $appointment->lawyer_user_id !== $user->id) {
            return $this->error('FORBIDDEN', 'You are not authorized to update this appointment.', 403);
        }

        // Only pending appointments can be rescheduled
        if ($appointment->status !== 'pending' && ($data['date'] ?? $data['time'])) {
            return $this->error('CANNOT_RESCHEDULE', 'Only pending appointments can be rescheduled.', 400);
        }

        $appointment->update($data);

        return $this->success(
            $this->formatAppointment($appointment->load(['client.profile', 'lawyer.profile']), $user),
            'Appointment updated successfully.'
        );
    }

    /**
     * Update appointment status
     * PATCH /api/v1/appointments/{id}/status
     */
    public function updateStatus(string $id, Request $request): JsonResponse
    {
        $validated = $request->validate([
            'status' => 'required|in:confirmed,completed,cancelled',
            'cancellation_reason' => 'required_if:status,cancelled|nullable|string|max:500',
        ]);

        $user = JWTAuth::user();
        $appointment = Appointment::with(['client.profile', 'lawyer.profile'])
            ->where('id', $id)
            ->first();

        if (!$appointment) {
            return $this->error('APPOINTMENT_NOT_FOUND', 'Appointment not found.', 404);
        }

        // Check if user is a participant
        if ($appointment->client_user_id !== $user->id && $appointment->lawyer_user_id !== $user->id) {
            return $this->error('FORBIDDEN', 'You are not a participant of this appointment.', 403);
        }

        $newStatus = $validated['status'];

        // Validate status transitions
        if (!$this->isValidStatusTransition($appointment->status, $newStatus, $user, $appointment)) {
            return $this->error('INVALID_STATUS_TRANSITION', 'The requested status change is not permitted from the current state.', 400);
        }

        $updateData = ['status' => $newStatus];
        if ($newStatus === 'cancelled' && isset($validated['cancellation_reason'])) {
            $updateData['cancellation_reason'] = $validated['cancellation_reason'];
        }

        $appointment->update($updateData);

        return $this->success(
            $this->formatAppointment($appointment, $user),
            'Appointment status updated successfully.'
        );
    }

    /**
     * Validate status transition
     */
    private function isValidStatusTransition(string $currentStatus, string $newStatus, $user, $appointment): bool
    {
        // Can't transition to the same status
        if ($currentStatus === $newStatus) {
            return false;
        }

        // Can't modify completed or cancelled appointments
        if (in_array($currentStatus, ['completed', 'cancelled'])) {
            return false;
        }

        // Only lawyer can confirm
        if ($newStatus === 'confirmed' && $appointment->lawyer_user_id !== $user->id) {
            return false;
        }

        // Only lawyer can mark as completed
        if ($newStatus === 'completed' && $appointment->lawyer_user_id !== $user->id) {
            return false;
        }

        // Both parties can cancel
        if ($newStatus === 'cancelled') {
            return $appointment->client_user_id === $user->id || $appointment->lawyer_user_id === $user->id;
        }

        return true;
    }

    /**
     * Get lawyer availability slots
     * GET /api/v1/appointments/lawyer/{lawyer_id}/availability
     */
    public function availability(string $lawyerId, Request $request): JsonResponse
    {
        $validated = $request->validate([
            'from_date' => 'required|date',
            'to_date' => 'required|date|after_or_equal:from_date',
        ]);

        $fromDate = \Carbon\Carbon::parse($validated['from_date']);
        $toDate = \Carbon\Carbon::parse($validated['to_date']);

        // Max 30 days range
        if ($fromDate->diffInDays($toDate) > 30) {
            return $this->error('DATE_RANGE_TOO_LARGE', 'Date range cannot exceed 30 days.', 400);
        }

        // Check if lawyer exists
        $lawyer = Lawyer::whereHas('profile.user', fn($q) => $q->where('id', $lawyerId))->first();
        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer not found.', 404);
        }

        $slots = [];
        $currentDate = $fromDate->copy();

        while ($currentDate <= $toDate) {
            $dateStr = $currentDate->toDateString();

            // Get existing appointments for this date
            $bookedTimes = Appointment::where('lawyer_user_id', $lawyerId)
                ->where('date', $dateStr)
                ->whereNotIn('status', ['cancelled', 'no_show'])
                ->pluck('time')
                ->toArray();

            // Generate available time slots (9 AM to 6 PM, hourly)
            $availableTimes = [];
            for ($hour = 9; $hour < 18; $hour++) {
                $time = sprintf('%02d:00', $hour);
                if (!in_array($time, $bookedTimes)) {
                    $availableTimes[] = $time;
                }
            }

            if (!empty($availableTimes)) {
                $slots[] = [
                    'date' => $dateStr,
                    'times' => $availableTimes,
                ];
            }

            $currentDate->addDay();
        }

        return $this->success([
            'lawyer_id' => $lawyerId,
            'from_date' => $validated['from_date'],
            'to_date' => $validated['to_date'],
            'slots' => $slots,
        ]);
    }

    /**
     * Format appointment for response
     */
    private function formatAppointment(Appointment $appointment, $currentUser): array
    {
        return [
            'id' => $appointment->id,
            'lawyer' => [
                'full_name' => $appointment->lawyer->profile?->full_name ?? $appointment->lawyer->full_name,
                'speciality' => $appointment->lawyer->lawyer?->speciality ?? null,
            ],
            'client' => [
                'full_name' => $appointment->client->profile?->full_name ?? $appointment->client->full_name,
            ],
            'date' => $appointment->date?->toDateString(),
            'time' => $appointment->time,
            'duration_min' => $appointment->duration_min,
            'type' => $appointment->type,
            'status' => $appointment->status,
            'price' => (float) $appointment->price,
            'notes' => $appointment->notes,
            'cancellation_reason' => $appointment->cancellation_reason ?? null,
            'created_at' => $appointment->created_at->toIso8601String(),
        ];
    }

    /**
     * Calculate appointment price
     */
    private function calculatePrice(Lawyer $lawyer, int $durationMin): float
    {
        // Base rate: 500 MAD per hour
        $hourlyRate = 500;
        return round(($durationMin / 60) * $hourlyRate, 2);
    }
}

