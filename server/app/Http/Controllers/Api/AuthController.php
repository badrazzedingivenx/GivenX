<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class AuthController extends Controller
{
    public function me(): JsonResponse
    {
        $user = auth('api')->user();

        if ($user === null) {
            return response()->json(['user' => null]);
        }

        return response()->json(['user' => $this->formatUser($user)]);
    }

    public function login(Request $request): JsonResponse
    {
        $request->validate([
            'email'    => 'required|email|string',
            'password' => 'required|string',
        ]);

        try {
            $token = auth('api')->attempt($request->only('email', 'password'));
        } catch (\Throwable $e) {
            return response()->json(['error' => 'JWT auth error'], 500);
        }

        if (!$token) {
            return response()->json(['error' => 'Invalid credentials'], 401);
        }

        return response()->json([
            'token' => $token,
            'user'  => $this->formatUser(auth('api')->user()),
        ]);
    }

    private function formatUser($user): array
    {
        $payload = [
            'id'        => (int) $user->id,
            'username'  => $user->username ?? null,
            'full_name' => $user->full_name ?? null,
            'email'     => $user->email ?? null,
            'phone'     => $user->phone ?? null,
            'image'     => $user->image ?? null,
            'role'      => $user->role ?? null,
            'city'      => $user->city ?? null,
            'is_active' => (bool) ($user->is_active ?? true),
        ];

        if (($user->role ?? null) === 'lawyer') {
            $payload = array_merge($payload, [
                'experience'     => $user->experience ?? null,
                'office_address' => $user->office_address ?? null,
                'syndicate'      => $user->syndicate ?? null,
                'specialisation' => $user->specialisation ?? null,
            ]);
        }

        return $payload;
    }
}