<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Requests\Api\V1\AddReviewRequest;
use App\Http\Requests\Api\V1\UpdateLawyerProfileRequest;
use App\Http\Requests\Api\V1\UploadAvatarRequest;
use App\Models\Appointment;
use App\Models\Consultation;
use App\Models\Document;
use App\Models\DocumentShare;
use App\Models\Lawyer;
use App\Models\Payment;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Storage;
use Tymon\JWTAuth\Facades\JWTAuth;

class LawyerController extends ApiController
{
    /**
     * Get current lawyer profile
     * GET /api/v1/lawyers/me
     */
    public function me(): JsonResponse
    {
        $user = JWTAuth::user();
        $lawyer = Lawyer::whereHas('profile', fn($q) => $q->where('user_id', $user->id))->first();

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer profile not found.', 404);
        }

        return $this->success([
            'id' => (string) $user->id,
            'full_name' => $user->full_name,
            'email' => $user->email,
            'speciality' => $lawyer->speciality,
            'bar_number' => $lawyer->bar_number,
            'is_verified' => $lawyer->is_verified,
            'is_available' => $lawyer->is_available,
            'rating' => (float) $lawyer->rating,
            'review_count' => $lawyer->review_count,
            'status' => $user->status,
            'updated_at' => $user->updated_at->toIso8601String(),
        ]);
    }

    /**
     * Update lawyer profile
     * PUT /api/v1/lawyers/me
     */
    public function updateMe(UpdateLawyerProfileRequest $request): JsonResponse
    {
        $user = JWTAuth::user();
        $data = $request->validated();
        $lawyer = $user->profile?->lawyer;

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer profile not found.', 404);
        }

        // Only update provided fields (partial update)
        $userUpdateData = array_filter([
            'full_name' => $data['full_name'] ?? null,
            'phone' => $data['phone'] ?? null,
            'address' => $data['address'] ?? null,
        ], fn($value) => $value !== null);
        $user->update($userUpdateData);

        // Update profile record
        if ($user->profile) {
            $user->profile->update($userUpdateData);
        }

        // Update lawyer record
        $lawyerUpdateData = array_filter([
            'name' => $data['full_name'] ?? null,
            'speciality' => $data['speciality'] ?? null,
            'bio' => $data['bio'] ?? null,
            'years_experience' => $data['years_experience'] ?? null,
            'specializations' => array_key_exists('specializations', $data) ? $data['specializations'] : null,
            'is_available' => array_key_exists('is_available', $data) ? $data['is_available'] : null,
        ], fn($value) => $value !== null);
        $lawyer->update($lawyerUpdateData);

        return $this->success([
            'id' => (string) $user->id,
            'full_name' => $user->full_name,
            'email' => $user->email,
            'speciality' => $lawyer->speciality,
            'bar_number' => $lawyer->bar_number,
            'bio' => $lawyer->bio,
            'years_experience' => $lawyer->years_experience,
            'specializations' => $lawyer->specializations,
            'is_available' => $lawyer->is_available,
            'is_verified' => $lawyer->is_verified,
            'rating' => (float) $lawyer->rating,
            'review_count' => $lawyer->review_count,
            'status' => $user->status,
            'updated_at' => $user->updated_at->toIso8601String(),
        ]);
    }

    /**
     * Upload lawyer avatar
     */
    public function uploadAvatar(UploadAvatarRequest $request): JsonResponse
    {
        $user = JWTAuth::user();
        $lawyer = $user->profile?->lawyer;

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer profile not found.', 404);
        }

        try {
            $file = $request->file('avatar');
            $filename = 'avatar_lawyer_' . $user->id . '_' . time() . '.' . $file->getClientOriginalExtension();
            $path = $file->storeAs('avatars', $filename, 'public');
            $avatarUrl = Storage::url($path);

            // Update lawyer, profile, and user
            $lawyer->update(['avatar_url' => $avatarUrl]);
            if ($user->profile) {
                $user->profile->update(['avatar_url' => $avatarUrl]);
            }
            $user->update(['avatar_url' => $avatarUrl]);

            return $this->success([
                'avatar_url' => $avatarUrl,
            ], 'Avatar uploaded successfully.');
        } catch (\Exception $e) {
            return $this->error('UPLOAD_FAILED', 'Failed to upload avatar: ' . $e->getMessage(), 500);
        }
    }

    /**
     * Get lawyer stats
     * GET /api/v1/lawyers/me/stats
     */
    public function stats(): JsonResponse
    {
        $user = JWTAuth::user();
        $lawyer = $user->profile?->lawyer;

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer profile not found.', 404);
        }

        $totalClients = Appointment::where('lawyer_user_id', $user->id)
            ->distinct('client_user_id')
            ->count('client_user_id');

        $activeClients = Appointment::where('lawyer_user_id', $user->id)
            ->whereIn('status', ['pending', 'confirmed'])
            ->distinct('client_user_id')
            ->count('client_user_id');

        $newRequests = Consultation::where('lawyer_id', $lawyer->id)
            ->where('status', 'pending')
            ->count();

        $closedCases = Appointment::where('lawyer_user_id', $user->id)
            ->where('status', 'completed')
            ->count();

        $totalRevenueMonth = Payment::where('lawyer_id', $lawyer->id)
            ->where('status', 'Completed')
            ->whereYear('created_at', now()->year)
            ->whereMonth('created_at', now()->month)
            ->sum('amount');

        $totalRevenueYear = Payment::where('lawyer_id', $lawyer->id)
            ->where('status', 'Completed')
            ->whereYear('created_at', now()->year)
            ->sum('amount');

        // Monthly revenue for the current year
        $monthlyRevenue = [];
        for ($i = 1; $i <= 12; $i++) {
            $monthName = \Carbon\Carbon::createFromDate(now()->year, $i, 1)->shortMonthName;
            $amount = Payment::where('lawyer_id', $lawyer->id)
                ->where('status', 'Completed')
                ->whereYear('created_at', now()->year)
                ->whereMonth('created_at', $i)
                ->sum('amount');
            $monthlyRevenue[] = [
                'month' => $monthName,
                'amount' => (float) $amount,
            ];
        }

        return $this->success([
            'total_clients' => $totalClients,
            'active_clients' => $activeClients,
            'new_requests' => $newRequests,
            'closed_cases' => $closedCases,
            'total_revenue_month' => (float) $totalRevenueMonth,
            'total_revenue_year' => (float) $totalRevenueYear,
            'average_rating' => (float) $lawyer->rating,
            'monthly_revenue' => $monthlyRevenue,
        ]);
    }

    /**
     * Set lawyer availability schedule
     * PUT /api/v1/lawyers/me/availability
     */
    public function updateAvailability(Request $request): JsonResponse
    {
        $user = JWTAuth::user();
        $lawyer = $user->profile?->lawyer;

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer profile not found.', 404);
        }

        $validated = $request->validate([
            'schedule' => 'required|array|min:1',
            'schedule.*.day' => 'required|string|in:monday,tuesday,wednesday,thursday,friday,saturday,sunday',
            'schedule.*.is_working' => 'required|boolean',
            'schedule.*.start_time' => 'nullable|string|date_format:H:i',
            'schedule.*.end_time' => 'nullable|string|date_format:H:i',
            'schedule.*.break_start' => 'nullable|string|date_format:H:i',
            'schedule.*.break_end' => 'nullable|string|date_format:H:i',
            'slot_duration_min' => 'sometimes|integer|min:15|max:120',
            'buffer_between_min' => 'sometimes|integer|min:0|max:60',
        ]);

        $lawyer->update([
            'schedule' => $validated['schedule'],
            'slot_duration_min' => $validated['slot_duration_min'] ?? $lawyer->slot_duration_min,
            'buffer_between_min' => $validated['buffer_between_min'] ?? $lawyer->buffer_between_min,
        ]);

        return $this->success([
            'schedule' => $lawyer->schedule,
            'slot_duration_min' => $lawyer->slot_duration_min,
            'buffer_between_min' => $lawyer->buffer_between_min,
        ]);
    }

    /**
     * List lawyers with filters
     */
    public function index(Request $request): JsonResponse
    {
        $query = Lawyer::with('profile.user')
            ->verified()
            ->where('is_available', true);

        // Apply filters
        if ($request->has('speciality')) {
            $query->where('speciality', 'like', '%' . $request->input('speciality') . '%');
        }

        if ($request->has('city')) {
            $query->where('city', 'like', '%' . $request->input('city') . '%');
        }

        if ($request->has('min_rating')) {
            $query->where('rating', '>=', $request->input('min_rating'));
        }

        $lawyers = $query->paginate($request->input('per_page', 20));

        $data = $lawyers->map(fn($lawyer) => [
            'id' => $lawyer->profile->user_id,
            'full_name' => $lawyer->name,
            'speciality' => $lawyer->speciality,
            'location' => $lawyer->location,
            'city' => $lawyer->city,
            'avatar_url' => $lawyer->avatar_url,
            'rating' => $lawyer->rating,
            'review_count' => $lawyer->review_count,
            'years_experience' => $lawyer->years_experience,
        ]);

        return $this->success([
            'data' => $data,
            'pagination' => [
                'current_page' => $lawyers->currentPage(),
                'last_page' => $lawyers->lastPage(),
                'per_page' => $lawyers->perPage(),
                'total' => $lawyers->total(),
            ],
        ]);
    }

    /**
     * Show public lawyer profile
     */
    public function show(string $id): JsonResponse
    {
        $lawyer = Lawyer::with('profile.user')
            ->whereHas('profile.user', fn($q) => $q->where('id', $id))
            ->first();

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer not found.', 404);
        }

        return $this->success([
            'id' => $id,
            'full_name' => $lawyer->name,
            'speciality' => $lawyer->speciality,
            'bio' => $lawyer->bio,
            'location' => $lawyer->location,
            'city' => $lawyer->city,
            'avatar_url' => $lawyer->avatar_url,
            'rating' => $lawyer->rating,
            'review_count' => $lawyer->review_count,
            'years_experience' => $lawyer->years_experience,
            'is_verified' => $lawyer->is_verified,
        ]);
    }

    /**
     * Add review to lawyer
     */
    public function addReview(string $id, AddReviewRequest $request): JsonResponse
    {
        $data = $request->validated();
        $user = JWTAuth::user();

        $lawyer = Lawyer::with('profile.user')
            ->whereHas('profile.user', fn($q) => $q->where('id', $id))
            ->first();

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer not found.', 404);
        }

        // Check if user has had an appointment with this lawyer
        $hasAppointment = Appointment::where('client_user_id', $user->id)
            ->where('lawyer_user_id', $id)
            ->where('status', 'completed')
            ->exists();

        if (!$hasAppointment) {
            return $this->error('NO_COMPLETED_APPOINTMENT', 'You must have a completed appointment to leave a review.', 403);
        }

        // Update lawyer rating
        $newReviewCount = $lawyer->review_count + 1;
        $newRating = (($lawyer->rating * $lawyer->review_count) + $data['rating']) / $newReviewCount;

        $lawyer->update([
            'rating' => round($newRating, 2),
            'review_count' => $newReviewCount,
        ]);

        // TODO: Store review comment in a separate reviews table if needed

        return $this->success([
            'rating' => $lawyer->rating,
            'review_count' => $lawyer->review_count,
        ], 'Review added successfully.');
    }

    /**
     * Get legal domains/specialities
     */
    public function domains(): JsonResponse
    {
        $domains = Lawyer::verified()
            ->whereNotNull('speciality')
            ->distinct()
            ->pluck('speciality');

        return $this->success($domains);
    }

    /**
     * Get documents shared with lawyer
     */
    public function sharedDocuments(): JsonResponse
    {
        $user = JWTAuth::user();
        $lawyer = $user->lawyer;

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer profile not found.', 404);
        }

        $shares = DocumentShare::with('document')
            ->where('lawyer_id', $lawyer->id)
            ->where(function ($q) {
                $q->whereNull('expires_at')
                    ->orWhere('expires_at', '>', now());
            })
            ->get();

        $data = $shares->map(fn($share) => [
            'id' => $share->document->id,
            'title' => $share->document->title,
            'file_url' => $share->document->file_url,
            'file_type' => $share->document->file_type,
            'shared_at' => $share->created_at->toIso8601String(),
            'expires_at' => $share->expires_at?->toIso8601String(),
        ]);

        return $this->success($data);
    }

    /**
     * Get lawyer's clients
     */
    public function clients(): JsonResponse
    {
        $user = JWTAuth::user();

        $clientIds = Appointment::where('lawyer_user_id', $user->id)
            ->distinct()
            ->pluck('client_user_id');

        $clients = \App\Models\User::with('profile', 'client')
            ->whereIn('id', $clientIds)
            ->get();

        $data = $clients->map(fn($client) => [
            'id' => $client->id,
            'full_name' => $client->profile?->full_name ?? $client->full_name,
            'email' => $client->email,
            'phone' => $client->profile?->phone ?? $client->phone,
            'company_name' => $client->client?->company_name,
        ]);

        return $this->success($data);
    }

    /**
     * Get consultation requests
     */
    public function requests(): JsonResponse
    {
        $user = JWTAuth::user();
        $lawyer = $user->lawyer;

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer profile not found.', 404);
        }

        $requests = Consultation::with('client.profile.user')
            ->where('lawyer_id', $lawyer->id)
            ->where('status', 'pending')
            ->orderBy('created_at', 'desc')
            ->get();

        $data = $requests->map(fn($req) => [
            'id' => $req->id,
            'subject' => $req->subject,
            'client_name' => $req->client?->profile?->full_name,
            'created_at' => $req->created_at->toIso8601String(),
        ]);

        return $this->success($data);
    }

    /**
     * Update request status
     */
    public function updateRequest(string $id, Request $request): JsonResponse
    {
        $request->validate(['status' => 'required|in:accepted,rejected']);
        $user = JWTAuth::user();
        $lawyer = $user->lawyer;

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer profile not found.', 404);
        }

        $consultation = Consultation::where('id', $id)
            ->where('lawyer_id', $lawyer->id)
            ->first();

        if (!$consultation) {
            return $this->error('REQUEST_NOT_FOUND', 'Request not found.', 404);
        }

        $consultation->update(['status' => $request->input('status')]);

        return $this->success([
            'id' => $consultation->id,
            'status' => $consultation->status,
        ], 'Request updated successfully.');
    }
}

