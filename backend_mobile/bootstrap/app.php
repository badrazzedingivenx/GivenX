<?php

use Illuminate\Foundation\Application;
use Illuminate\Foundation\Configuration\Exceptions;
use Illuminate\Foundation\Configuration\Middleware;
use Illuminate\Validation\ValidationException;
use Symfony\Component\HttpKernel\Exception\HttpException;

return Application::configure(basePath: dirname(__DIR__))
    ->withRouting(
        web: __DIR__.'/../routes/web.php',
        api: __DIR__.'/../routes/api.php',
        commands: __DIR__.'/../routes/console.php',
        health: '/up',
    )
    ->withMiddleware(function (Middleware $middleware): void {
        //
    })
    ->withExceptions(function (Exceptions $exceptions): void {
        // Ensure all API exceptions return JSON
        $exceptions->render(function (Throwable $e, $request) {
            // If it's an API request, always return JSON
            if ($request->is('api/*') || $request->expectsJson()) {
                // Handle validation exceptions
                if ($e instanceof ValidationException) {
                    return response()->json([
                        'success' => false,
                        'error' => 'VALIDATION_ERROR',
                        'message' => 'The given data was invalid.',
                        'details' => $e->errors(),
                    ], 422);
                }
                
                // Handle HTTP exceptions
                if ($e instanceof HttpException) {
                    return response()->json([
                        'success' => false,
                        'error' => 'HTTP_ERROR',
                        'message' => $e->getMessage() ?: 'An error occurred',
                    ], $e->getStatusCode());
                }
            }
        });
    })->create();
