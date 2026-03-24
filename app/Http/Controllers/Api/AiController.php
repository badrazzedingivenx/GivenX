<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Services\AIService;

class AiController extends Controller
{
    public function ask(Request $request, AIService $ai)
    {
        $request->validate([
            'message' => 'required|string|max:2000',
            'langue'  => 'sometimes|string|in:fr,ar,en'
        ]);

        try {
            $response = $ai->ask($request->message, $request->langue ?? 'fr');

            return response()->json([
                'success' => true,
                'answer'  => $response
            ]);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'error'   => $e->getMessage()
            ], 500);
        }
    }
}