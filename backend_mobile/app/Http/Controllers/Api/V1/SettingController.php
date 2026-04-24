<?php

namespace App\Http\Controllers\Api\V1;

use App\Models\DeviceToken;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;
use Tymon\JWTAuth\Facades\JWTAuth;

class SettingController extends ApiController
{
    /**
     * Get user settings
     * GET /api/v1/settings
     */
    public function show(): JsonResponse
    {
        $user = JWTAuth::user();

        // Get settings from cache or use defaults
        $settings = Cache::get('user_settings_' . $user->id, [
            'language' => 'fr',
            'theme' => 'light',
            'notifications_enabled' => true,
            'email_notifications' => true,
            'push_notifications' => true,
            'privacy_profile_visible' => true,
            'privacy_show_online_status' => false,
        ]);

        return $this->success([
            'notifications' => [
                'push_enabled' => $settings['push_notifications'] ?? true,
                'email_enabled' => $settings['email_notifications'] ?? true,
                'new_message' => true,
                'appointment_reminder' => true,
            ],
            'privacy' => [
                'profile_visible' => $settings['privacy_profile_visible'] ?? true,
                'show_online_status' => $settings['privacy_show_online_status'] ?? false,
            ],
            'language' => $settings['language'] ?? 'fr',
            'theme' => $settings['theme'] ?? 'light',
        ]);
    }

    /**
     * Update user settings
     */
    public function update(Request $request): JsonResponse
    {
        $request->validate([
            'language' => 'sometimes|in:fr,en,ar',
            'theme' => 'sometimes|in:light,dark,system',
            'notifications_enabled' => 'sometimes|boolean',
            'email_notifications' => 'sometimes|boolean',
            'push_notifications' => 'sometimes|boolean',
            'privacy_profile_visible' => 'sometimes|boolean',
            'privacy_show_online_status' => 'sometimes|boolean',
        ]);

        $user = JWTAuth::user();

        // Get existing settings
        $settings = Cache::get('user_settings_' . $user->id, [
            'language' => 'fr',
            'theme' => 'light',
            'notifications_enabled' => true,
            'email_notifications' => true,
            'push_notifications' => true,
            'privacy_profile_visible' => true,
            'privacy_show_online_status' => true,
            'two_factor_enabled' => false,
        ]);

        // Update with new values
        $settings = array_merge($settings, $request->only([
            'language',
            'theme',
            'notifications_enabled',
            'email_notifications',
            'push_notifications',
            'privacy_profile_visible',
            'privacy_show_online_status',
        ]));

        Cache::put('user_settings_' . $user->id, $settings, now()->addDays(365));

        return $this->success($settings, 'Settings updated successfully.');
    }

    /**
     * Revoke all sessions (logout all devices)
     */
    public function revokeSessions(): JsonResponse
    {
        $user = JWTAuth::user();

        // Delete all device tokens
        DeviceToken::where('user_id', $user->id)->delete();

        // Invalidate current token
        try {
            JWTAuth::invalidate(JWTAuth::getToken());
        } catch (\Exception $e) {
            // Token might already be invalid
        }

        return $this->success(null, 'All sessions revoked successfully.');
    }
}

