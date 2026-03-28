<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Services\AIService;
use App\Models\AiConversation;
use App\Models\AiMessage;

class AiController extends Controller
{
    // ── Helpers ────────────────────────────────────────────

    private function currentUser()
    {
        return auth('api')->user();
    }

    private function getOrCreateConversation(int $userId): AiConversation
    {
        return AiConversation::where('user_id', $userId)
            ->where('is_archived', false)
            ->latest()
            ->first()
            ?? AiConversation::create([
                'user_id'         => $userId,
                'content'         => null,
                'category'        => 'أخرى',
                'questions_count' => 0,
                'is_archived'     => false,
            ]);
    }

    private function getContextMessages(int $conversationId): array
    {
        return AiMessage::where('conversation_id', $conversationId)
            ->orderBy('created_at')
            ->get(['role', 'content'])
            ->map(fn ($m) => ['role' => $m->role, 'content' => $m->content])
            ->values()
            ->all();
    }

    private function saveTurn(int $conversationId, string $userMessage, string $aiReply): void
    {
        AiMessage::create([
            'conversation_id' => $conversationId,
            'role'            => 'user',
            'content'         => $userMessage,
            'tokens_used'     => 0,
        ]);
        AiMessage::create([
            'conversation_id' => $conversationId,
            'role'            => 'assistant',
            'content'         => $aiReply,
            'tokens_used'     => 0,
        ]);
    }

    private function updateConversationMeta(AiConversation $conversation, string $lastReply): void
    {
        $conversation->content = $lastReply;
        $conversation->questions_count = AiMessage::where('conversation_id', $conversation->id)
            ->where('role', 'user')
            ->count();
        $conversation->save();
    }

    // ── Endpoints ──────────────────────────────────────────

    /**
     * POST /api/ai/ask
     * Body: { message: string, langue?: fr|ar|en }
     */
    public function ask(Request $request, AIService $ai)
    {
        $request->validate([
            'message' => 'required|string|max:2000',
            'langue'  => 'sometimes|string|in:fr,ar,en',
        ]);

        try {
            $user         = $this->currentUser();
            $langue       = $request->input('langue', 'fr');
            $conversation = $this->getOrCreateConversation($user->id);
            $context      = $this->getContextMessages($conversation->id);

            $reply = $ai->chat(
                array_merge($context, [['role' => 'user', 'content' => $request->message]]),
                $langue
            );

            $this->saveTurn($conversation->id, $request->message, $reply);
            $this->updateConversationMeta($conversation, $reply);

            return response()->json(['success' => true, 'answer' => $reply]);

        } catch (\Exception $e) {
            return response()->json(['success' => false, 'error' => $e->getMessage()], 500);
        }
    }

    /**
     * POST /api/ai/chat
     * Body: { prompt: string, history?: [{role, content}], langue?: fr|ar|en }
     */
    public function chat(Request $request, AIService $ai)
    {
        $request->validate([
            'prompt'           => 'required|string|max:2000',
            'history'          => 'sometimes|array',
            'history.*.role'   => 'required_with:history|string|in:user,assistant',
            'history.*.content'=> 'required_with:history|string|max:4000',
            'langue'           => 'sometimes|string|in:fr,ar,en',
        ]);

        try {
            $user         = $this->currentUser();
            $langue       = $request->input('langue', 'fr');
            $prompt       = $request->input('prompt');
            $historyParam = $request->input('history');
            $conversation = $this->getOrCreateConversation($user->id);

            // Si le client envoie un historique complet → resynchroniser
            if (is_array($historyParam)) {
                AiMessage::where('conversation_id', $conversation->id)->delete();
                foreach ($historyParam as $m) {
                    AiMessage::create([
                        'conversation_id' => $conversation->id,
                        'role'            => $m['role'],
                        'content'         => $m['content'],
                        'tokens_used'     => 0,
                    ]);
                }
            }

            $context = $this->getContextMessages($conversation->id);
            $reply   = $ai->chat(
                array_merge($context, [['role' => 'user', 'content' => $prompt]]),
                $langue
            );

            $this->saveTurn($conversation->id, $prompt, $reply);
            $this->updateConversationMeta($conversation, $reply);

            return response()->json(['reply' => $reply]);

        } catch (\Exception $e) {
            return response()->json(['error' => $e->getMessage()], 500);
        }
    }

    /**
     * GET /api/ai/history
     */
    public function history()
    {
        $user         = $this->currentUser();
        $conversation = AiConversation::where('user_id', $user->id)
            ->where('is_archived', false)
            ->latest()
            ->first();

        if (!$conversation) {
            return response()->json(['messages' => []]);
        }

        return response()->json(['messages' => $this->getContextMessages($conversation->id)]);
    }

    /**
     * DELETE /api/ai/history
     */
    public function clearHistory()
    {
        $user         = $this->currentUser();
        $conversation = AiConversation::where('user_id', $user->id)
            ->where('is_archived', false)
            ->latest()
            ->first();

        if ($conversation) {
            AiMessage::where('conversation_id', $conversation->id)->delete();
            $conversation->update(['content' => null, 'questions_count' => 0]);
        }

        return response()->json(['success' => true]);
    }
}

## Tester dans Postman

### 1. Login → récupérer le token

#POST http://127.0.0.1:8000/api/auth/login
#Body (JSON):
#{
 # "email": "test@test.com",
  #"password": "123456"
#}