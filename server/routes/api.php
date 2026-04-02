<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AiController;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\ContentController;
use App\Http\Controllers\Api\LawyerController;
use App\Http\Controllers\Api\VideoController;

// ── Public ─────────────────────────────────────────────────
Route::post('/auth/login', [AuthController::class, 'login']);
Route::get('/content/trending', [ContentController::class, 'trending']);
Route::get('/content/culture/feed', [ContentController::class, 'cultureFeed']);
Route::prefix('lawyers')->group(function () {
    Route::get('/', [LawyerController::class, 'index']);
    Route::get('/{id}', [LawyerController::class, 'show'])->where('id', '[0-9]+');
    Route::get('/{id}/reviews', [LawyerController::class, 'reviews'])->where('id', '[0-9]+');
});
Route::get('/test', fn () => 'API OK');

Route::prefix('videos')->group(function () {
    Route::get('/feed', [VideoController::class, 'feed']);
    Route::get('/{id}/comments', [VideoController::class, 'comments']);
    Route::post('/{id}/share', [VideoController::class, 'share']);
});
// ── Protected (JWT) ────────────────────────────────────────
Route::middleware('auth:api')->group(function () {
    Route::get('/auth/me', [AuthController::class, 'me']);

    Route::post('/ai/ask', [AiController::class, 'ask']);
    Route::post('/ai/chat', [AiController::class, 'chat']);
    Route::get('/ai/history', [AiController::class, 'history']);
    Route::delete('/ai/history', [AiController::class, 'clearHistory']);

    Route::prefix('videos')->group(function () {
        Route::post('/{id}/like', [VideoController::class, 'like']);
        Route::post('/{id}/save', [VideoController::class, 'save']);
        Route::post('/{id}/comments', [VideoController::class, 'addComment']);
        Route::delete('/comments/{commentId}', [VideoController::class, 'deleteComment']);
    });

    Route::prefix('lawyers')->group(function () {
        Route::get('/favorites', [LawyerController::class, 'favorites']);
        Route::post('/{id}/reviews', [LawyerController::class, 'addReview'])->where('id', '[0-9]+');
        Route::post('/{id}/favorite', [LawyerController::class, 'favorite'])->where('id', '[0-9]+');
    });
});