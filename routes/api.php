<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AiController;

Route::post('/ai/ask', [AiController::class, 'ask']);
Route::get('/test', function () {
    return "API OK";
});