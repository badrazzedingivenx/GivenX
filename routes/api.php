<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AiController;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\ContentController;
use App\Http\Controllers\Api\LawyerController;

// ── Public ─────────────────────────────────────────────────
Route::post('/auth/login', [AuthController::class, 'login']);
Route::get('/content/trending', [ContentController::class, 'trending']);
Route::get('/content/culture/feed', [ContentController::class, 'cultureFeed']);
Route::get('/lawyers', [LawyerController::class, 'index']);
Route::get('/test', fn () => 'API OK');

// ── Protected (JWT) ────────────────────────────────────────
Route::middleware('auth:api')->group(function () {
    Route::get('/auth/me', [AuthController::class, 'me']);

    Route::post('/ai/ask', [AiController::class, 'ask']);
    Route::post('/ai/chat', [AiController::class, 'chat']);
    Route::get('/ai/history', [AiController::class, 'history']);
    Route::delete('/ai/history', [AiController::class, 'clearHistory']);
});