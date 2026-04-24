<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Requests\Api\V1\DeleteAccountRequest;
use App\Http\Requests\Api\V1\UpdateProfileRequest;
use App\Http\Requests\Api\V1\UploadAvatarRequest;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Storage;
use Tymon\JWTAuth\Facades\JWTAuth;

class UserProfileController extends ApiController
{
    /**
     * Get current user profile
     * GET /api/v1/users/me
     */
    public function me(): JsonResponse
    {
        $user = JWTAuth::user();

        return $this->success([
            'id' => (string) $user->id,
            'full_name' => $user->full_name,
            'email' => $user->email,
            'phone' => $user->phone,
            'address' => $user->address,
            'avatar_url' => $user->avatar_url,
            'role' => 'user',
            'status' => $user->status,
            'created_at' => $user->created_at->toIso8601String(),
            'updated_at' => $user->updated_at->toIso8601String(),
        ]);
    }

    /**
     * Update user profile
     * PUT /api/v1/users/me
     */
    public function update(UpdateProfileRequest $request): JsonResponse
    {
        $user = JWTAuth::user();
        $data = $request->validated();

        // Only update provided fields (partial update)
        $updateData = array_filter([
            'full_name' => $data['full_name'] ?? null,
            'phone' => $data['phone'] ?? null,
            'address' => $data['address'] ?? null,
        ], fn($value) => $value !== null);

        $user->update($updateData);

        // Also update profile record if exists
        if ($user->profile) {
            $user->profile->update($updateData);
        }

        return $this->success([
            'id' => (string) $user->id,
            'full_name' => $user->full_name,
            'email' => $user->email,
            'phone' => $user->phone,
            'address' => $user->address,
            'avatar_url' => $user->avatar_url,
            'role' => 'user',
            'status' => $user->status,
            'created_at' => $user->created_at->toIso8601String(),
            'updated_at' => $user->updated_at->toIso8601String(),
        ]);
    }

    /**
     * Upload user avatar
     * POST /api/v1/users/me/avatar
     */
    public function uploadAvatar(UploadAvatarRequest $request): JsonResponse
    {
        $user = JWTAuth::user();
        $file = $request->file('avatar');

        // Validate file type manually for custom error codes
        $allowedMimes = ['image/jpeg', 'image/png', 'image/webp'];
        if (!in_array($file->getMimeType(), $allowedMimes)) {
            return $this->error('INVALID_FILE_TYPE', 'Unsupported file format. Accepted: JPG, PNG, WebP.', 400);
        }

        // Validate file size (5 MB)
        if ($file->getSize() > 5 * 1024 * 1024) {
            return $this->error('FILE_TOO_LARGE', 'File exceeds the 5 MB limit.', 400);
        }

        try {
            // Delete old avatar if exists
            if ($user->avatar_url) {
                $oldPath = str_replace('/storage/', '', $user->avatar_url);
                Storage::disk('public')->delete($oldPath);
            }

            $filename = 'avatars/' . $user->id . '_' . time() . '.' . $file->getClientOriginalExtension();
            $path = $file->storeAs('', $filename, 'public');
            $avatarUrl = Storage::url($filename);

            // Update user and profile
            $user->update(['avatar_url' => $avatarUrl]);
            if ($user->profile) {
                $user->profile->update(['avatar_url' => $avatarUrl]);
            }

            return $this->success([
                'avatar_url' => $avatarUrl,
            ]);
        } catch (\Exception $e) {
            return $this->error('UPLOAD_FAILED', 'Failed to upload avatar.', 500);
        }
    }

    /**
     * Delete user account
     * DELETE /api/v1/users/me/account
     */
    public function deleteAccount(DeleteAccountRequest $request): JsonResponse
    {
        $user = JWTAuth::user();

        // Verify password
        if (!Hash::check($request->input('password'), $user->password)) {
            return $this->error('WRONG_PASSWORD', 'The provided confirmation password is incorrect.', 400);
        }

        try {
            // Permanently delete account and associated data
            // Delete profile
            if ($user->profile) {
                $user->profile->delete();
            }

            // Delete client record
            if ($user->client) {
                $user->client->delete();
            }

            // Delete avatar file
            if ($user->avatar_url) {
                $oldPath = str_replace('/storage/', '', $user->avatar_url);
                Storage::disk('public')->delete($oldPath);
            }

            // Invalidate JWT token
            JWTAuth::invalidate(JWTAuth::getToken());

            // Delete user
            $user->delete();

            return $this->success(null, 'Account deleted successfully');
        } catch (\Exception $e) {
            return $this->error('DELETE_FAILED', 'Failed to delete account.', 500);
        }
    }
}

