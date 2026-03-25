<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AiController;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\ContentController;
use App\Http\Controllers\Api\LawyerController;

Route::post('/ai/ask', [AiController::class, 'ask']);

Route::get('/content/trending', [ContentController::class, 'trending']);
Route::get('/content/culture/feed', [ContentController::class, 'cultureFeed']);
Route::get('/lawyers', [LawyerController::class, 'index']);

Route::get('/auth/me', [AuthController::class, 'me']);

Route::get('/test', function () {
    return "API OK";
});