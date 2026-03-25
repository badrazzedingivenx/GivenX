<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class AuthController extends Controller
{
    /**
     * GET /api/auth/me
     * Return current user when logged in, otherwise user=null.
     */
    public function me(Request $request): JsonResponse
    {
        // 1) Session-based auth
        $user = auth()->user();

        if ($user === null) {
            // 2) Sanctum bearer tokens
            $user = auth('sanctum')->user();
        }

        if ($user === null) {
            // 3) Fallback (in case the app auth driver maps differently)
            $user = Auth::user();
        }

        if ($user === null) {
            return response()->json(['user' => null]);
        }

        $baseUserPayload = [
            'id' => (int) $user->id,
            'username' => $user->username ?? null,
            'full_name' => $user->full_name ?? null,
            'email' => $user->email ?? null,
            'phone' => $user->phone ?? null,
            'image' => $user->image ?? null,
            'role' => $user->role ?? null,
            'city' => $user->city ?? null,
            'is_active' => (bool) ($user->is_active ?? true),
        ];

        if (($user->role ?? null) === 'lawyer') {
            $baseUserPayload = array_merge($baseUserPayload, [
                'experience' => $user->experience ?? null,
                'office_address' => $user->office_address ?? null,
                'syndicate' => $user->syndicate ?? null,
                'specialisation' => $user->specialisation ?? null,
            ]);
        }

        return response()->json([
            'user' => $baseUserPayload,
        ]);
    }
}
