<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;
use Tymon\JWTAuth\Facades\JWTAuth;

class RoleMiddleware
{
    /**
     * Handle an incoming request.
     */
    public function handle(Request $request, Closure $next, string ...$roles): Response
    {
        $user = JWTAuth::user();

        if (!$user) {
            return response()->json([
                'success' => false,
                'error' => 'UNAUTHORIZED',
                'message' => 'Unauthorized.',
            ], 401);
        }

        // Convert roles to uppercase for comparison
        $allowedRoles = array_map('strtoupper', $roles);
        $userRole = strtoupper($user->role);

        if (!in_array($userRole, $allowedRoles)) {
            return response()->json([
                'success' => false,
                'error' => 'FORBIDDEN',
                'message' => 'You do not have permission to access this resource.',
            ], 403);
        }

        return $next($request);
    }
}
