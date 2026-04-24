<?php

namespace App\Http\Controllers\Api\V1;

use App\Models\DeviceToken;
use App\Models\Notification;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Tymon\JWTAuth\Facades\JWTAuth;

class NotificationController extends ApiController
{
    /**
     * List notifications for current user
     * GET /api/v1/notifications
     */
    public function index(Request $request): JsonResponse
    {
        $user = JWTAuth::user();

        $page = $request->input('page', 1);
        $limit = $request->input('limit', 30);
        $isRead = $request->input('is_read');

        $query = Notification::forUser($user->id)
            ->orderBy('created_at', 'desc');

        // Filter by read status if provided
        if ($isRead !== null) {
            $query->where('is_read', filter_var($isRead, FILTER_VALIDATE_BOOLEAN));
        }

        $notifications = $query->paginate($limit, ['*'], 'page', $page);

        $data = $notifications->map(fn($notif) => [
            'id' => $notif->id,
            'type' => $notif->type,
            'title' => $notif->title,
            'body' => $notif->content,
            'is_read' => $notif->is_read,
            'created_at' => $notif->created_at->toIso8601String(),
        ]);

        return $this->success([
            'notifications' => $data,
            'unread_count' => Notification::forUser($user->id)->unread()->count(),
            'pagination' => [
                'current_page' => $notifications->currentPage(),
                'last_page' => $notifications->lastPage(),
                'per_page' => $notifications->perPage(),
                'total' => $notifications->total(),
            ],
        ]);
    }

    /**
     * Mark notification as read
     * PATCH /api/v1/notifications/{id}/read
     */
    public function markRead(string $id): JsonResponse
    {
        $user = JWTAuth::user();

        $notification = Notification::where('id', $id)
            ->where('user_id', $user->id)
            ->first();

        if (!$notification) {
            return $this->error('NOTIFICATION_NOT_FOUND', 'Notification not found.', 404);
        }

        $notification->markAsRead();

        return $this->success(null, 'Notification marked as read.');
    }

    /**
     * Mark all notifications as read
     * PATCH /api/v1/notifications/read-all
     */
    public function markAllRead(): JsonResponse
    {
        $user = JWTAuth::user();

        Notification::forUser($user->id)
            ->unread()
            ->update(['is_read' => true]);

        return $this->success(null, 'All notifications marked as read.');
    }

    /**
     * Delete notification
     * DELETE /api/v1/notifications/{id}
     */
    public function destroy(string $id): JsonResponse
    {
        $user = JWTAuth::user();

        $notification = Notification::where('id', $id)
            ->where('user_id', $user->id)
            ->first();

        if (!$notification) {
            return $this->error('NOTIFICATION_NOT_FOUND', 'Notification not found.', 404);
        }

        $notification->delete();

        return $this->success(null, 'Notification deleted.');
    }

    /**
     * Delete all notifications
     * DELETE /api/v1/notifications/all
     */
    public function destroyAll(): JsonResponse
    {
        $user = JWTAuth::user();

        Notification::forUser($user->id)->delete();

        return $this->success(null, 'All notifications deleted.');
    }

    /**
     * Register device token for push notifications
     * POST /api/v1/notifications/device-token
     */
    public function registerDeviceToken(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'token' => 'required|string',
            'platform' => 'required|in:android,ios',
        ]);

        $user = JWTAuth::user();

        // Delete existing token if exists (prevent duplicates)
        DeviceToken::where('token', $validated['token'])->delete();

        DeviceToken::create([
            'user_id' => $user->id,
            'token' => $validated['token'],
            'platform' => $validated['platform'],
        ]);

        return $this->success(null, 'Device token registered successfully.');
    }

    /**
     * Delete device token
     * DELETE /api/v1/notifications/device-token
     */
    public function deleteDeviceToken(Request $request): JsonResponse
    {
        $validated = $request->validate(['token' => 'required|string']);
        $user = JWTAuth::user();

        DeviceToken::where('user_id', $user->id)
            ->where('token', $validated['token'])
            ->delete();

        return $this->success(null, 'Device token removed successfully.');
    }
}

