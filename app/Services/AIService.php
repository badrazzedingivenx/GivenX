<?php

namespace App\Services;

use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Log;

class AIService
{
    private string $apiKey;
    private string $model;
    private string $apiUrl;

    private array $prompts = [
        'fr' => 'Tu es un assistant juridique spécialisé dans le droit marocain. '
              . 'Réponds toujours en français, de manière claire et professionnelle.',
        'ar' => 'أنت مساعد قانوني متخصص في القانون المغربي. '
              . 'أجب دائماً باللغة العربية بأسلوب واضح ومهني.',
        'en' => 'You are a legal assistant specialized in Moroccan law. '
              . 'Always respond in English, clearly and professionally.',
    ];

    public function __construct()
    {
        $this->apiKey = env('OPENROUTER_API_KEY');
        $this->model  = 'openai/gpt-4o-mini';
        $this->apiUrl = 'https://openrouter.ai/api/v1/chat/completions';
    }

    public function ask(string $message, string $langue = 'fr'): string
    {
        if (empty($this->apiKey)) {
            throw new \Exception('Clé API OpenRouter manquante dans le fichier .env');
        }

        $systemPrompt = $this->prompts[$langue] ?? $this->prompts['fr'];

        $response = Http::withoutVerifying()
            ->withHeaders([
                'Authorization' => 'Bearer ' . $this->apiKey,
                'HTTP-Referer'  => env('APP_URL', 'http://localhost'),
                'X-Title'       => env('APP_NAME', 'Haqqi AI'),
                'Content-Type'  => 'application/json',
            ])->post($this->apiUrl, [
                'model'    => $this->model,
                'messages' => [
                    [
                        'role'    => 'system',
                        'content' => $systemPrompt
                    ],
                    [
                        'role'    => 'user',
                        'content' => $message
                    ]
                ]
            ]);

        // Erreur HTTP (4xx, 5xx)
        if ($response->failed()) {
            Log::error('OpenRouter HTTP error', [
                'status' => $response->status(),
                'body'   => $response->body(),
            ]);
            throw new \Exception('Erreur de communication avec OpenRouter (HTTP ' . $response->status() . ')');
        }

        $data = $response->json();

        // Réponse inattendue
        if (!isset($data['choices'][0]['message']['content'])) {
            Log::error('OpenRouter invalid response', ['data' => $data]);
            throw new \Exception('Réponse invalide reçue d\'OpenRouter');
        }

        return $data['choices'][0]['message']['content'];
    }
}