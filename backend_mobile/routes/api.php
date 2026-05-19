<?php

use App\Http\Controllers\Api\V1\AdminController;
use App\Http\Controllers\Api\V1\AppointmentController;
use App\Http\Controllers\Api\V1\AuthController;
use App\Http\Controllers\Api\V1\ContentController;
use App\Http\Controllers\Api\V1\ConversationController;
use App\Http\Controllers\Api\V1\DashboardController;
use App\Http\Controllers\Api\V1\DocumentController;
use App\Http\Controllers\Api\V1\LawyerController;
use App\Http\Controllers\Api\V1\LiveSessionController;
use App\Http\Controllers\Api\V1\MatchingController;
use App\Http\Controllers\Api\V1\NotificationController;
use App\Http\Controllers\Api\V1\PaymentController;
use App\Http\Controllers\Api\V1\SettingController;
use App\Http\Controllers\Api\V1\UserProfileController;
use App\Http\Middleware\JwtMiddleware;
use App\Http\Middleware\RoleMiddleware;
use Illuminate\Support\Facades\Route;

Route::prefix('v1')->group(function (): void {
    // Public routes
    Route::prefix('auth')->group(function (): void {
        Route::post('register-user', [AuthController::class, 'registerUser']);
        Route::post('register-lawyer', [AuthController::class, 'registerLawyer']);
        Route::post('login', [AuthController::class, 'login']);
        Route::post('refresh-token', [AuthController::class, 'refreshToken']);
        Route::post('forgot-password', [AuthController::class, 'forgotPassword']);
        Route::post('reset-password', [AuthController::class, 'resetPassword']);
        Route::post('verify-email', [AuthController::class, 'verifyEmail']);
        Route::post('resend-verification', [AuthController::class, 'resendVerification']);
    });

    // Protected routes
    Route::middleware([JwtMiddleware::class])->group(function (): void {
        Route::prefix('auth')->group(function (): void {
            Route::post('logout', [AuthController::class, 'logout']);
            Route::post('change-password', [AuthController::class, 'changePassword']);
        });

        // User routes (CLIENT only)
        Route::prefix('users')->middleware([RoleMiddleware::class . ':CLIENT'])->group(function (): void {
            Route::get('me', [UserProfileController::class, 'me']);
            Route::put('me', [UserProfileController::class, 'update']);
            Route::post('me/avatar', [UserProfileController::class, 'uploadAvatar']);
            Route::delete('me/account', [UserProfileController::class, 'deleteAccount']);
        });

        // Lawyer routes (LAWYER only)
        Route::prefix('lawyers')->group(function (): void {
            // Protected lawyer routes (MUST be before {id} route)
            Route::middleware([RoleMiddleware::class . ':LAWYER'])->group(function (): void {
                Route::get('me', [LawyerController::class, 'me']);
                Route::put('me', [LawyerController::class, 'updateMe']);
                Route::post('me/avatar', [LawyerController::class, 'uploadAvatar']);
                Route::get('me/stats', [LawyerController::class, 'stats']);
                Route::put('me/availability', [LawyerController::class, 'updateAvailability']);
                Route::get('me/shared-documents', [LawyerController::class, 'sharedDocuments']);
                Route::get('me/clients', [LawyerController::class, 'clients']);
                Route::get('me/requests', [LawyerController::class, 'requests']);
                Route::patch('me/requests/{id}', [LawyerController::class, 'updateRequest']);
            });

            // Public lawyer discovery routes
            Route::get('domains', [LawyerController::class, 'domains']);
            Route::get('/', [LawyerController::class, 'index']);
            Route::get('{id}', [LawyerController::class, 'show']);

            // Reviews (CLIENT only)
            Route::post('{id}/reviews', [LawyerController::class, 'addReview'])->middleware([RoleMiddleware::class . ':CLIENT']);
        });

        // Matching (CLIENT only)
        Route::prefix('matching')->middleware([RoleMiddleware::class . ':CLIENT'])->group(function (): void {
            Route::post('request', [MatchingController::class, 'requestMatch']);
            Route::get('history', [MatchingController::class, 'history']);
        });

        // Appointments (CLIENT, LAWYER)
        Route::prefix('appointments')->group(function (): void {
            Route::get('/', [AppointmentController::class, 'index']);
            Route::post('/', [AppointmentController::class, 'store'])->middleware([RoleMiddleware::class . ':CLIENT']);
            Route::get('lawyer/{lawyerId}/availability', [AppointmentController::class, 'availability']);
            Route::get('{id}', [AppointmentController::class, 'show']);
            Route::put('{id}', [AppointmentController::class, 'update']);
            Route::patch('{id}/status', [AppointmentController::class, 'updateStatus']);
        });

        // Payments (CLIENT, LAWYER)
        Route::prefix('payments')->group(function (): void {
            Route::get('/', [PaymentController::class, 'index']);
            Route::post('initiate', [PaymentController::class, 'initiate'])->middleware([RoleMiddleware::class . ':CLIENT']);
            Route::get('{id}', [PaymentController::class, 'show']);
            Route::post('{id}/refund', [PaymentController::class, 'refund'])->middleware([RoleMiddleware::class . ':CLIENT']);
            Route::get('{id}/invoice', [PaymentController::class, 'invoice']);
        });

        // Webhook (public, no auth)
        Route::post('payments/webhook', [PaymentController::class, 'webhook'])->withoutMiddleware([JwtMiddleware::class]);

        // Conversations (CLIENT, LAWYER)
        Route::prefix('conversations')->group(function (): void {
            Route::get('/', [ConversationController::class, 'index']);
            Route::post('/', [ConversationController::class, 'store']);
            Route::get('{id}', [ConversationController::class, 'show']);
            Route::get('{id}/messages', [ConversationController::class, 'messages']);
            Route::post('{id}/messages', [ConversationController::class, 'sendMessage']);
            Route::post('{id}/messages/file', [ConversationController::class, 'sendFile']);
            Route::patch('{id}/read', [ConversationController::class, 'markRead']);
        });

        // Notifications (CLIENT, LAWYER)
        Route::prefix('notifications')->group(function (): void {
            Route::get('/', [NotificationController::class, 'index']);
            Route::patch('read-all', [NotificationController::class, 'markAllRead']);
            Route::delete('all', [NotificationController::class, 'destroyAll']);
            Route::post('device-token', [NotificationController::class, 'registerDeviceToken']);
            Route::delete('device-token', [NotificationController::class, 'deleteDeviceToken']);
            Route::patch('{id}/read', [NotificationController::class, 'markRead']);
            Route::delete('{id}', [NotificationController::class, 'destroy']);
        });

        // Live Sessions
        Route::prefix('live-sessions')->group(function (): void {
            Route::get('/', [LiveSessionController::class, 'index']);
            Route::get('{id}', [LiveSessionController::class, 'show']);
            Route::get('{id}/comments', [LiveSessionController::class, 'comments']);
            Route::post('{id}/comments', [LiveSessionController::class, 'addComment']); // User | Lawyer

            // Lawyer only
            Route::post('/', [LiveSessionController::class, 'store'])->middleware([RoleMiddleware::class . ':LAWYER']);
            Route::patch('{id}/end', [LiveSessionController::class, 'end'])->middleware([RoleMiddleware::class . ':LAWYER']);
        });

        // Stories
        Route::get('stories', [ContentController::class, 'stories']);
        Route::post('stories', [ContentController::class, 'storeStory'])->middleware([RoleMiddleware::class . ':LAWYER']);
        Route::delete('stories/{id}', [ContentController::class, 'deleteStory'])->middleware([RoleMiddleware::class . ':LAWYER']);
        Route::post('stories/{id}/view', [ContentController::class, 'viewStory'])->middleware([RoleMiddleware::class . ':CLIENT']);

        // Reels
        Route::get('reels', [ContentController::class, 'reels']);
        Route::post('reels', [ContentController::class, 'storeReel'])->middleware([RoleMiddleware::class . ':LAWYER']);
        Route::delete('reels/{id}', [ContentController::class, 'deleteReel'])->middleware([RoleMiddleware::class . ':LAWYER']);
        Route::post('reels/{id}/like', [ContentController::class, 'likeReel'])->middleware([RoleMiddleware::class . ':CLIENT']);
        Route::post('reels/{id}/view', [ContentController::class, 'viewReel']);

        // Documents (CLIENT, LAWYER)
        Route::prefix('documents')->group(function (): void {
            Route::get('/', [DocumentController::class, 'index']);
            Route::post('/', [DocumentController::class, 'store'])->middleware([RoleMiddleware::class . ':CLIENT']);
            Route::get('{id}', [DocumentController::class, 'show']);
            Route::patch('{id}', [DocumentController::class, 'update'])->middleware([RoleMiddleware::class . ':CLIENT']);
            Route::delete('{id}', [DocumentController::class, 'destroy'])->middleware([RoleMiddleware::class . ':CLIENT']);
            Route::post('{id}/share', [DocumentController::class, 'share'])->middleware([RoleMiddleware::class . ':CLIENT']);
        });

        // Dashboard
        Route::prefix('dashboard')->group(function (): void {
            Route::get('user', [DashboardController::class, 'user'])->middleware([RoleMiddleware::class . ':CLIENT']);
            Route::get('lawyer', [DashboardController::class, 'lawyer'])->middleware([RoleMiddleware::class . ':LAWYER']);
            Route::get('lawyer/schedule', [DashboardController::class, 'lawyerSchedule'])->middleware([RoleMiddleware::class . ':LAWYER']);
            Route::post('lawyer/tasks', [DashboardController::class, 'addTask'])->middleware([RoleMiddleware::class . ':LAWYER']);
            Route::patch('lawyer/tasks/{id}', [DashboardController::class, 'updateTask'])->middleware([RoleMiddleware::class . ':LAWYER']);
            Route::delete('lawyer/tasks/{id}', [DashboardController::class, 'deleteTask'])->middleware([RoleMiddleware::class . ':LAWYER']);
        });

        // Settings (CLIENT, LAWYER)
        Route::prefix('settings')->group(function (): void {
            Route::get('/', [SettingController::class, 'show']);
            Route::put('/', [SettingController::class, 'update']);
            Route::delete('sessions', [SettingController::class, 'revokeSessions']);
        });

        // Admin (ADMIN only)
        Route::prefix('admin')->middleware([RoleMiddleware::class . ':ADMIN'])->group(function (): void {
            Route::get('lawyers/pending', [AdminController::class, 'pendingLawyers']);
            Route::patch('lawyers/{id}/verify', [AdminController::class, 'verifyLawyer']);
            Route::patch('users/{id}/suspend', [AdminController::class, 'suspendUser']);
            Route::get('stats', [AdminController::class, 'stats']);
        });
    });
});

