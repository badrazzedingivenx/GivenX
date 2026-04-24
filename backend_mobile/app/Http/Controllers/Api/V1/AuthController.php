<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Requests\Api\V1\ChangePasswordRequest;
use App\Http\Requests\Api\V1\ForgotPasswordRequest;
use App\Http\Requests\Api\V1\LoginRequest;
use App\Http\Requests\Api\V1\RefreshTokenRequest;
use App\Http\Requests\Api\V1\RegisterLawyerRequest;
use App\Http\Requests\Api\V1\RegisterUserRequest;
use App\Http\Requests\Api\V1\ResendVerificationRequest;
use App\Http\Requests\Api\V1\ResetPasswordRequest;
use App\Http\Requests\Api\V1\VerifyEmailRequest;
use App\Mail\EmailVerificationCode;
use App\Mail\PasswordResetToken;
use App\Models\Client;
use App\Models\Lawyer;
use App\Models\Profile;
use App\Models\User;
use Carbon\Carbon;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Facades\Password;
use Illuminate\Support\Str;
use Tymon\JWTAuth\Facades\JWTAuth;

class AuthController extends ApiController
{
    /**
     * Register a new client user
     */
    public function registerUser(RegisterUserRequest $request): JsonResponse
    {
        try {
            $data = $request->validated();

            // Check if email already exists
            if (User::where('email', $data['email'])->exists()) {
                return $this->error('EMAIL_ALREADY_EXISTS', 'An account with this email address already exists.', 409);
            }

            $user = User::create([
                'name' => $data['full_name'],
                'full_name' => $data['full_name'],
                'email' => $data['email'],
                'password' => Hash::make($data['password']),
                'phone' => $data['phone'],
                'address' => $data['address'] ?? null,
                'role' => 'CLIENT',
                'status' => 'active',
            ]);

            // Create profile and client record
            $profile = Profile::create([
                'user_id' => $user->id,
                'full_name' => $data['full_name'],
                'phone' => $data['phone'],
                'role' => 'CLIENT',
                'address' => $data['address'] ?? null,
            ]);

            Client::create([
                'profile_id' => $profile->id,
            ]);

            // Generate OTP for email verification and send email
            $otp = $this->generateOtp($user->email);
            
            try {
                Mail::to($user->email)->send(new EmailVerificationCode($otp, $user->full_name));
                \Log::info('Verification email sent successfully', ['email' => $user->email]);
            } catch (\Exception $mailError) {
                \Log::error('Failed to send verification email', [
                    'email' => $user->email,
                    'error' => $mailError->getMessage()
                ]);
                // Continue registration even if email fails
            }

            // Generate tokens
            $token = JWTAuth::fromUser($user);
            $refreshToken = $this->generateRefreshToken($user);

            return $this->success([
                'user' => [
                    'id' => $user->id,
                    'full_name' => $user->full_name,
                    'email' => $user->email,
                    'role' => 'user',
                    'created_at' => $user->created_at->toIso8601String(),
                ],
                'token' => $token,
                'refresh_token' => $refreshToken,
                'verification_code' => app()->environment('local') ? $otp : null,
            ], 'User registered successfully. Please verify your email.', 201);
        } catch (\Exception $e) {
            \Log::error('User registration error: ' . $e->getMessage());
            return $this->error('INTERNAL_SERVER_ERROR', 'An unexpected error occurred. Please try again later.', 500);
        }
    }

    /**
     * Register a new lawyer
     */
    public function registerLawyer(RegisterLawyerRequest $request): JsonResponse
    {
        try {
            $data = $request->validated();

            // Check if email already exists
            if (User::where('email', $data['email'])->exists()) {
                return $this->error('EMAIL_ALREADY_EXISTS', 'An account with this email address already exists.', 409);
            }

            $user = User::create([
                'name' => $data['full_name'],
                'full_name' => $data['full_name'],
                'email' => $data['email'],
                'password' => Hash::make($data['password']),
                'phone' => $data['phone'],
                'address' => $data['address'],
                'role' => 'LAWYER',
                'status' => 'pending_verification',
            ]);

            // Create profile
            $profile = Profile::create([
                'user_id' => $user->id,
                'full_name' => $data['full_name'],
                'phone' => $data['phone'],
                'role' => 'LAWYER',
                'address' => $data['address'],
            ]);

            // Create lawyer record
            Lawyer::create([
                'profile_id' => $profile->id,
                'name' => $data['full_name'],
                'speciality' => $data['speciality'],
                'bar_number' => $data['bar_number'],
                'years_experience' => $data['years_experience'],
                'bio' => $data['bio'] ?? null,
                'is_verified' => false,
                'is_available' => true,
            ]);

            // Generate OTP for email verification and send email
            $otp = $this->generateOtp($user->email);
            
            try {
                Mail::to($user->email)->send(new EmailVerificationCode($otp, $user->full_name));
                \Log::info('Lawyer verification email sent successfully', ['email' => $user->email]);
            } catch (\Exception $mailError) {
                \Log::error('Failed to send lawyer verification email', [
                    'email' => $user->email,
                    'error' => $mailError->getMessage()
                ]);
                // Continue registration even if email fails
            }

            // Generate tokens
            $token = JWTAuth::fromUser($user);
            $refreshToken = $this->generateRefreshToken($user);

            return $this->success([
                'lawyer' => [
                    'id' => $user->id,
                    'full_name' => $user->full_name,
                    'role' => 'lawyer',
                    'status' => 'pending_verification',
                    'created_at' => $user->created_at->toIso8601String(),
                ],
                'token' => $token,
                'refresh_token' => $refreshToken,
                'verification_code' => app()->environment('local') ? $otp : null,
            ], 'Lawyer registered successfully. Account pending admin verification.', 201);
        } catch (\Exception $e) {
            \Log::error('Lawyer registration error: ' . $e->getMessage());
            return $this->error('INTERNAL_SERVER_ERROR', 'An unexpected error occurred. Please try again later.', 500);
        }
    }

    /**
     * Login user/lawyer
     */
    public function login(LoginRequest $request): JsonResponse
    {
        try {
            $credentials = $request->only('email', 'password');

            if (!$token = JWTAuth::attempt($credentials)) {
                return $this->error('INVALID_CREDENTIALS', 'Email or password is incorrect.', 401);
            }

            $user = JWTAuth::user();

            // Check account status
            if ($user->isSuspended()) {
                return $this->error('ACCOUNT_SUSPENDED', 'The account has been suspended by an administrator.', 403);
            }

            if ($user->isLawyer() && $user->isPendingVerification()) {
                return $this->error('ACCOUNT_PENDING_VERIFICATION', 'The lawyer account is awaiting admin approval.', 403);
            }

            $refreshToken = $this->generateRefreshToken($user);

            return $this->success([
                'profile' => [
                    'id' => $user->id,
                    'full_name' => $user->full_name,
                    'email' => $user->email,
                    'role' => strtolower($user->role),
                    'status' => $user->status,
                    'avatar_url' => $user->avatar_url,
                ],
                'token' => $token,
                'refresh_token' => $refreshToken,
            ]);
        } catch (\Exception $e) {
            \Log::error('Login error: ' . $e->getMessage());
            return $this->error('INTERNAL_SERVER_ERROR', 'An unexpected error occurred. Please try again later.', 500);
        }
    }

    /**
     * Logout user
     */
    public function logout(Request $request): JsonResponse
    {
        $refreshToken = $request->input('refresh_token');

        if ($refreshToken) {
            // Blacklist refresh token
            Cache::forget('refresh_token:' . $refreshToken);
        }

        try {
            JWTAuth::invalidate(JWTAuth::getToken());
        } catch (\Exception $e) {
            // Token might already be invalid
        }

        return $this->success(null, 'Logged out successfully');
    }

    /**
     * Refresh access token
     */
    public function refreshToken(RefreshTokenRequest $request): JsonResponse
    {
        $refreshToken = $request->input('refresh_token');

        // Validate refresh token
        $tokenData = Cache::get('refresh_token:' . $refreshToken);

        if (!$tokenData) {
            return $this->error('INVALID_REFRESH_TOKEN', 'The provided refresh token is not recognized.', 401);
        }

        // Check if token is expired (for more specific error)
        $createdAt = Carbon::parse($tokenData['created_at']);
        if ($createdAt->addDays(30)->isPast()) {
            Cache::forget('refresh_token:' . $refreshToken);
            return $this->error('REFRESH_TOKEN_EXPIRED', 'The refresh token has expired; user must log in again.', 401);
        }

        $user = User::find($tokenData['user_id']);

        if (!$user) {
            Cache::forget('refresh_token:' . $refreshToken);
            return $this->error('INVALID_REFRESH_TOKEN', 'The provided refresh token is not recognized.', 401);
        }

        // Invalidate old refresh token
        Cache::forget('refresh_token:' . $refreshToken);

        // Generate new tokens
        $newToken = JWTAuth::fromUser($user);
        $newRefreshToken = $this->generateRefreshToken($user);

        return $this->success([
            'token' => $newToken,
            'refresh_token' => $newRefreshToken,
        ]);
    }

    /**
     * Forgot password - send reset link
     */
    public function forgotPassword(ForgotPasswordRequest $request): JsonResponse
    {
        $email = $request->input('email');
        \Log::info('Forgot password requested for: ' . $email);

        // Check if user exists
        $user = User::where('email', $email)->first();

        if ($user) {
            \Log::info('User found: ' . $user->full_name);
            // Generate reset token and send email
            $token = Str::random(64);
            Cache::put('password_reset:' . $email, $token, now()->addHour());
            \Log::info('Token generated and cached');

            try {
                Mail::to($user->email)->send(new PasswordResetToken($token, $user->full_name));
                \Log::info('Password reset email sent successfully to: ' . $user->email);
            } catch (\Exception $e) {
                \Log::error('Failed to send password reset email: ' . $e->getMessage());
            }
        } else {
            \Log::info('User not found for email: ' . $email);
        }

        // Always return same response to prevent user enumeration
        return $this->success([
            'reset_token' => app()->environment('local') && $user ? $token : null,
        ], 'Reset link sent if account exists');
    }

    /**
     * Reset password with token
     */
    public function resetPassword(ResetPasswordRequest $request): JsonResponse
    {
        $data = $request->validated();

        // Find user by trying all emails with cached tokens
        $user = null;
        $cachedToken = null;
        
        // Search for the token in cache
        foreach (User::all() as $u) {
            $token = Cache::get('password_reset:' . $u->email);
            if ($token && $token === $data['token']) {
                $user = $u;
                $cachedToken = $token;
                break;
            }
        }

        if (!$user || !$cachedToken) {
            return $this->error('INVALID_OR_EXPIRED_TOKEN', 'The reset token is invalid or has expired.', 400);
        }

        $user->update([
            'password' => Hash::make($data['new_password']),
        ]);

        Cache::forget('password_reset:' . $user->email);

        return $this->success(null, 'Password updated successfully');
    }

    /**
     * Change password (authenticated)
     */
    public function changePassword(ChangePasswordRequest $request): JsonResponse
    {
        $data = $request->validated();
        $user = JWTAuth::user();

        if (!Hash::check($data['current_password'], $user->password)) {
            return $this->error('WRONG_CURRENT_PASSWORD', 'The provided current password is incorrect.', 400);
        }

        $user->update([
            'password' => Hash::make($data['new_password']),
        ]);

        return $this->success(null, 'Password changed successfully.');
    }

    /**
     * Verify email with code
     */
    public function verifyEmail(VerifyEmailRequest $request): JsonResponse
    {
        $data = $request->validated();

        $cachedCode = Cache::get('email_verification:' . $data['email']);

        if (!$cachedCode) {
            return $this->error('CODE_EXPIRED', 'The OTP has expired; request a new one.', 400);
        }

        if ($cachedCode !== $data['code']) {
            return $this->error('INVALID_CODE', 'The OTP does not match the expected value.', 400);
        }

        $user = User::where('email', $data['email'])->first();

        if ($user) {
            $user->update([
                'email_verified_at' => now(),
            ]);
        }

        Cache::forget('email_verification:' . $data['email']);

        return $this->success(null, 'Email verified successfully');
    }

    /**
     * Resend verification code
     */
    public function resendVerification(ResendVerificationRequest $request): JsonResponse
    {
        $email = $request->input('email');

        // Rate limiting: max 3 requests per 5 minutes
        $rateKey = 'verification_rate:' . $email;
        $attempts = Cache::get($rateKey, 0);
        
        if ($attempts >= 3) {
            $ttl = Cache::get($rateKey . ':ttl', 300);
            return response()->json([
                'success' => false,
                'error' => 'TOO_MANY_REQUESTS',
                'message' => 'Rate limit exceeded; retry after the indicated number of seconds.',
                'data' => [
                    'retry_after' => $ttl
                ]
            ], 429);
        }

        $user = User::where('email', $email)->first();

        if (!$user) {
            // Return same message to prevent user enumeration
            return $this->success(null, 'Verification code resent');
        }

        if ($user->email_verified_at) {
            return $this->error('ALREADY_VERIFIED', 'Email is already verified.', 400);
        }

        // Increment rate limit
        Cache::put($rateKey, $attempts + 1, now()->addMinutes(5));
        Cache::put($rateKey . ':ttl', 300, now()->addMinutes(5));

        $code = $this->generateOtp($email);

        // Send verification code email
        Mail::to($user->email)->send(new EmailVerificationCode($code, $user->full_name));

        return $this->success([
            'verification_code' => app()->environment('local') ? $code : null,
        ], 'Verification code resent');
    }

    /**
     * Generate and store OTP
     */
    private function generateOtp(string $email): string
    {
        $otp = str_pad((string) random_int(0, 999999), 6, '0', STR_PAD_LEFT);
        Cache::put('email_verification:' . $email, $otp, now()->addMinutes(30));
        return $otp;
    }

    /**
     * Generate refresh token
     */
    private function generateRefreshToken(User $user): string
    {
        $refreshToken = Str::random(128);
        Cache::put('refresh_token:' . $refreshToken, [
            'user_id' => $user->id,
            'created_at' => now()->toIso8601String(),
        ], now()->addDays(30));
        return $refreshToken;
    }
}

