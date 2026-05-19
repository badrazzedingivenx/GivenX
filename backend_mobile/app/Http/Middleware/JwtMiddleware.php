<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;
use Tymon\JWTAuth\Facades\JWTAuth;
use Tymon\JWTAuth\Exceptions\TokenExpiredException;
use Tymon\JWTAuth\Exceptions\TokenInvalidException;
use Tymon\JWTAuth\Exceptions\JWTException;

class JwtMiddleware
{
    /**
     * Handle an incoming request.
     */
    public function handle(Request $request, Closure $next): Response
    {
        try {
            $user = JWTAuth::parseToken()->authenticate();

            if (!$user) {
                return response()->json([
                    'success' => false,
                    'error' => 'USER_NOT_FOUND',
                    'message' => 'User not found.',
                ], 404);
            }

            // Check account status
            if ($user->isSuspended()) {
                return response()->json([
                    'success' => false,
                    'error' => 'ACCOUNT_SUSPENDED',
                    'message' => 'The account has been suspended by an administrator.',
                ], 403);
            }

            if ($user->isLawyer() && $user->isPendingVerification()) {
                return response()->json([
                    'success' => false,
                    'error' => 'ACCOUNT_PENDING_VERIFICATION',
                    'message' => 'The lawyer account is awaiting admin approval.',
                ], 403);
            }

        } catch (TokenExpiredException $e) {
            return response()->json([
                'success' => false,
                'error' => 'TOKEN_EXPIRED',
                'message' => 'Token has expired.',
            ], 401);
        } catch (TokenInvalidException $e) {
            return response()->json([
                'success' => false,
                'error' => 'TOKEN_INVALID',
                'message' => 'Token is invalid.',
            ], 401);
        } catch (JWTException $e) {
            return response()->json([
                'success' => false,
                'error' => 'TOKEN_NOT_PROVIDED',
                'message' => 'Authorization token not provided.',
            ], 401);
        }

        return $next($request);
    }
}
