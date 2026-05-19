<?php

namespace App\Http\Controllers\Api\V1;

use App\Models\Appointment;
use App\Models\Lawyer;
use App\Models\Payment;
use App\Models\User;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Tymon\JWTAuth\Facades\JWTAuth;

class AdminController extends ApiController
{
    /**
     * List pending lawyer verifications
     * GET /api/v1/admin/lawyers/pending
     */
    public function pendingLawyers(Request $request): JsonResponse
    {
        $page = $request->input('page', 1);
        $limit = $request->input('limit', 20);

        $lawyers = Lawyer::with('profile.user')
            ->where('is_verified', false)
            ->whereHas('profile.user', fn($q) => $q->where('status', 'pending_verification'))
            ->orderBy('created_at', 'desc')
            ->paginate($limit, ['*'], 'page', $page);

        $data = $lawyers->map(fn($lawyer) => [
            'id' => $lawyer->profile->user_id,
            'full_name' => $lawyer->name,
            'email' => $lawyer->profile->user->email,
            'phone' => $lawyer->profile->phone,
            'speciality' => $lawyer->speciality,
            'bar_number' => $lawyer->bar_number,
            'years_experience' => $lawyer->years_experience,
            'bio' => $lawyer->bio,
            'created_at' => $lawyer->created_at->toIso8601String(),
        ]);

        return $this->success([
            'lawyers' => $data,
            'pagination' => [
                'current_page' => $lawyers->currentPage(),
                'last_page' => $lawyers->lastPage(),
                'per_page' => $lawyers->perPage(),
                'total' => $lawyers->total(),
            ],
        ]);
    }

    /**
     * Verify/reject lawyer
     * PATCH /api/v1/admin/lawyers/{id}/verify
     */
    public function verifyLawyer(string $id, Request $request): JsonResponse
    {
        $request->validate([
            'action' => 'required|in:approve,reject',
            'reject_reason' => 'required_if:action,reject|string|max:500',
        ]);

        $user = User::where('id', $id)->where('role', 'LAWYER')->first();

        if (!$user) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer not found.', 404);
        }

        $lawyer = $user->lawyer;

        if (!$lawyer) {
            return $this->error('LAWYER_PROFILE_NOT_FOUND', 'Lawyer profile not found.', 404);
        }

        $action = $request->input('action');

        if ($action === 'approve') {
            $lawyer->update(['is_verified' => true]);
            $user->update(['status' => 'active']);
            $message = 'Lawyer verified successfully.';
            $status = 'active';
        } else {
            $user->update(['status' => 'rejected']);
            $message = 'Lawyer rejected.';
            $status = 'rejected';
        }

        return $this->success([
            'lawyer_id' => $id,
            'status' => $status,
        ], $message);
    }

    /**
     * Suspend/unsuspend user
     */
    public function suspendUser(string $id, Request $request): JsonResponse
    {
        $request->validate([
            'action' => 'required|in:suspend,unsuspend',
            'reason' => 'required_if:action,suspend|string|max:500',
        ]);

        $targetUser = User::where('id', $id)->first();

        if (!$targetUser) {
            return $this->error('USER_NOT_FOUND', 'User not found.', 404);
        }

        $currentUser = JWTAuth::user();

        // Prevent self-suspension
        if ($targetUser->id === $currentUser->id) {
            return $this->error('CANNOT_SUSPEND_SELF', 'You cannot suspend yourself.', 400);
        }

        // Prevent suspending other admins
        if ($targetUser->isAdmin()) {
            return $this->error('CANNOT_SUSPEND_ADMIN', 'Cannot suspend another admin.', 403);
        }

        $action = $request->input('action');

        if ($action === 'suspend') {
            $targetUser->update(['status' => 'suspended']);
            $message = 'User suspended successfully.';
        } else {
            $targetUser->update(['status' => 'active']);
            $message = 'User unsuspended successfully.';
        }

        return $this->success([
            'id' => $id,
            'status' => $targetUser->status,
        ], $message);
    }

    /**
     * Platform statistics
     */
    public function stats(): JsonResponse
    {
        // User counts
        $totalUsers = User::where('role', 'CLIENT')->count();
        $totalLawyers = User::where('role', 'LAWYER')->count();
        $verifiedLawyers = Lawyer::where('is_verified', true)->count();
        $pendingLawyers = Lawyer::where('is_verified', false)->count();

        // Appointment stats
        $totalAppointments = Appointment::count();
        $completedAppointments = Appointment::where('status', 'completed')->count();
        $pendingAppointments = Appointment::where('status', 'pending')->count();

        // Payment stats
        $totalPayments = Payment::where('status', 'Completed')->count();
        $totalRevenue = Payment::where('status', 'Completed')->sum('amount');

        // Recent activity
        $recentUsers = User::orderBy('created_at', 'desc')
            ->take(5)
            ->get()
            ->map(fn($u) => [
                'id' => $u->id,
                'full_name' => $u->full_name,
                'role' => strtolower($u->role),
                'created_at' => $u->created_at->toIso8601String(),
            ]);

        return $this->success([
            'users' => [
                'total_clients' => $totalUsers,
                'total_lawyers' => $totalLawyers,
                'verified_lawyers' => $verifiedLawyers,
                'pending_verifications' => $pendingLawyers,
            ],
            'appointments' => [
                'total' => $totalAppointments,
                'completed' => $completedAppointments,
                'pending' => $pendingAppointments,
            ],
            'payments' => [
                'total_completed' => $totalPayments,
                'total_revenue' => $totalRevenue,
                'currency' => 'MAD',
            ],
            'recent_activity' => [
                'new_users' => $recentUsers,
            ],
        ]);
    }
}

