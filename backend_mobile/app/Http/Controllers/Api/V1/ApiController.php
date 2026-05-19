<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use Illuminate\Http\JsonResponse;

class ApiController extends Controller
{
    protected function success(mixed $data = null, string $message = '', int $status = 200): JsonResponse
    {
        return response()->json([
            'success' => true,
            'data' => $data,
            'message' => $message,
        ], $status);
    }

    protected function error(string $error, string $message = '', int $status = 400, array $details = []): JsonResponse
    {
        $payload = [
            'success' => false,
            'error' => $error,
            'message' => $message,
        ];

        if ($details !== []) {
            $payload['details'] = $details;
        }

        return response()->json($payload, $status);
    }

    protected function notImplemented(string $module, string $action): JsonResponse
    {
        return $this->error(
            'NOT_IMPLEMENTED',
            sprintf('%s: %s is scaffolded but not implemented yet.', $module, $action),
            501
        );
    }
}

