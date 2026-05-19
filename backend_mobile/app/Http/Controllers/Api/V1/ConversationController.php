<?php

namespace App\Http\Controllers\Api\V1;

use App\Models\Conversation;
use App\Models\Message;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Str;
use Tymon\JWTAuth\Facades\JWTAuth;

class ConversationController extends ApiController
{
    /**
     * List conversations for current user
     * GET /api/v1/conversations
     */
    public function index(Request $request): JsonResponse
    {
        $user = JWTAuth::user();

        $page = $request->input('page', 1);
        $limit = $request->input('limit', 20);

        $conversations = Conversation::forUser($user->id)
            ->with(['lawyer.profile', 'client.profile'])
            ->orderBy('last_message_sent_at', 'desc')
            ->paginate($limit, ['*'], 'page', $page);

        $data = $conversations->map(function ($conv) use ($user) {
            $isClient = $conv->client_user_id === $user->id;

            return [
                'id' => $conv->id,
                'lawyer' => [
                    'full_name' => $conv->lawyer->profile?->full_name ?? $conv->lawyer->full_name,
                ],
                'client' => [
                    'full_name' => $conv->client->profile?->full_name ?? $conv->client->full_name,
                ],
                'last_message' => $conv->last_message_content ? [
                    'content' => $conv->last_message_content,
                    'sent_at' => $conv->last_message_sent_at?->toIso8601String(),
                ] : null,
                'unread_count_user' => $isClient ? $conv->unread_count_user : $conv->unread_count_lawyer,
            ];
        });

        return $this->success([
            'conversations' => $data,
            'pagination' => [
                'current_page' => $conversations->currentPage(),
                'last_page' => $conversations->lastPage(),
                'per_page' => $conversations->perPage(),
                'total' => $conversations->total(),
            ],
        ]);
    }

    /**
     * Create new conversation
     */
    public function store(Request $request): JsonResponse
    {
        $request->validate([
            'lawyer_id' => 'required_without:client_id|integer|exists:users,id',
            'client_id' => 'required_without:lawyer_id|integer|exists:users,id',
            'initial_message' => 'required|string|min:1|max:2000',
        ]);

        $user = JWTAuth::user();

        if ($user->isClient()) {
            $lawyerId = $request->input('lawyer_id');
            $clientId = $user->id;
        } else {
            $lawyerId = $user->id;
            $clientId = $request->input('client_id');
        }

        // Check if conversation already exists
        $existing = Conversation::where('lawyer_user_id', $lawyerId)
            ->where('client_user_id', $clientId)
            ->first();

        if ($existing) {
            return $this->error('CONVERSATION_EXISTS', 'Conversation already exists.', 409);
        }

        // Create conversation
        $conversationId = 'conv_' . Str::random(12);
        $conversation = Conversation::create([
            'id' => $conversationId,
            'lawyer_user_id' => $lawyerId,
            'client_user_id' => $clientId,
        ]);

        // Create initial message
        $this->createMessage($conversation, $user, $request->input('initial_message'), 'text');

        return $this->success(
            $this->formatConversation($conversation->load(['lawyer.profile', 'client.profile']), $user),
            'Conversation created successfully.',
            201
        );
    }

    /**
     * Show conversation details
     */
    public function show(string $id): JsonResponse
    {
        $user = JWTAuth::user();

        $conversation = Conversation::with(['lawyer.profile', 'client.profile'])
            ->where('id', $id)
            ->first();

        if (!$conversation) {
            return $this->error('CONVERSATION_NOT_FOUND', 'Conversation not found.', 404);
        }

        // Check authorization
        if ($conversation->lawyer_user_id !== $user->id && $conversation->client_user_id !== $user->id) {
            return $this->error('FORBIDDEN', 'You are not authorized to view this conversation.', 403);
        }

        return $this->success($this->formatConversation($conversation, $user));
    }

    /**
     * Get messages for conversation
     */
    public function messages(string $id, Request $request): JsonResponse
    {
        $user = JWTAuth::user();

        $conversation = Conversation::where('id', $id)->first();

        if (!$conversation) {
            return $this->error('CONVERSATION_NOT_FOUND', 'Conversation not found.', 404);
        }

        if ($conversation->lawyer_user_id !== $user->id && $conversation->client_user_id !== $user->id) {
            return $this->error('FORBIDDEN', 'You are not authorized to view this conversation.', 403);
        }

        $messages = Message::where('conversation_id', $id)
            ->with('sender')
            ->orderBy('sent_at', 'desc')
            ->paginate($request->input('per_page', 50));

        $data = $messages->map(fn($msg) => [
            'id' => $msg->id,
            'content' => $msg->content,
            'type' => $msg->type,
            'file_url' => $msg->file_url,
            'file_name' => $msg->file_name,
            'is_from_me' => $msg->sender_id === $user->id,
            'sender_name' => $msg->sender_name,
            'sent_at' => $msg->sent_at->toIso8601String(),
        ]);

        return $this->success([
            'data' => $data,
            'pagination' => [
                'current_page' => $messages->currentPage(),
                'last_page' => $messages->lastPage(),
                'per_page' => $messages->perPage(),
                'total' => $messages->total(),
            ],
        ]);
    }

    /**
     * Send message
     * POST /api/v1/conversations/{id}/messages
     */
    public function sendMessage(string $id, Request $request): JsonResponse
    {
        $request->validate([
            'content' => 'required|string|min:1|max:2000',
        ]);

        $user = JWTAuth::user();

        $conversation = Conversation::where('id', $id)->first();

        if (!$conversation) {
            return $this->error('CONVERSATION_NOT_FOUND', 'Conversation not found.', 404);
        }

        if ($conversation->lawyer_user_id !== $user->id && $conversation->client_user_id !== $user->id) {
            return $this->error('FORBIDDEN', 'You are not authorized to send messages in this conversation.', 403);
        }

        $message = $this->createMessage($conversation, $user, $request->input('content'), 'text');

        return $this->success([
            'id' => $message->id,
            'content' => $message->content,
            'sender_id' => $message->sender_id,
            'type' => $message->type,
            'sent_at' => $message->sent_at->toIso8601String(),
        ], 'Message sent successfully.');
    }

    /**
     * Send file in conversation
     * POST /api/v1/conversations/{id}/messages/file
     */
    public function sendFile(string $id, Request $request): JsonResponse
    {
        $request->validate([
            'file' => 'required|file|mimes:jpg,jpeg,png,pdf,docx|max:20480', // 20MB max
            'caption' => 'nullable|string|max:1000',
        ]);

        $user = JWTAuth::user();

        $conversation = Conversation::where('id', $id)->first();

        if (!$conversation) {
            return $this->error('CONVERSATION_NOT_FOUND', 'Conversation not found.', 404);
        }

        if ($conversation->lawyer_user_id !== $user->id && $conversation->client_user_id !== $user->id) {
            return $this->error('FORBIDDEN', 'You are not authorized to send files in this conversation.', 403);
        }

        try {
            $file = $request->file('file');
            $filename = 'msg_' . $id . '_' . time() . '_' . Str::random(8) . '.' . $file->getClientOriginalExtension();
            $path = $file->storeAs('messages', $filename, 'public');
            $fileUrl = Storage::url($path);

            $caption = $request->input('caption');
            $message = $this->createMessage(
                $conversation,
                $user,
                $caption,
                'file',
                $fileUrl,
                $file->getClientOriginalName()
            );

            return $this->success([
                'id' => $message->id,
                'type' => $message->type,
                'file_url' => $message->file_url,
                'file_name' => $message->file_name,
                'caption' => $message->content,
                'sent_at' => $message->sent_at->toIso8601String(),
            ], 'File sent successfully.');
        } catch (\Exception $e) {
            return $this->error('UPLOAD_FAILED', 'Failed to upload file: ' . $e->getMessage(), 500);
        }
    }

    /**
     * Mark conversation as read
     */
    public function markRead(string $id): JsonResponse
    {
        $user = JWTAuth::user();

        $conversation = Conversation::where('id', $id)->first();

        if (!$conversation) {
            return $this->error('CONVERSATION_NOT_FOUND', 'Conversation not found.', 404);
        }

        if ($conversation->lawyer_user_id !== $user->id && $conversation->client_user_id !== $user->id) {
            return $this->error('FORBIDDEN', 'You are not authorized to access this conversation.', 403);
        }

        // Reset unread count for current user
        if ($conversation->client_user_id === $user->id) {
            $conversation->update(['unread_count_user' => 0]);
        } else {
            $conversation->update(['unread_count_lawyer' => 0]);
        }

        return $this->success(null, 'Conversation marked as read.');
    }

    /**
     * Create message
     */
    private function createMessage(
        Conversation $conversation,
        $sender,
        ?string $content,
        string $type,
        ?string $fileUrl = null,
        ?string $fileName = null
    ): Message {
        $messageId = 'msg_' . Str::random(16);
        $isFromUser = $conversation->client_user_id === $sender->id;

        $message = Message::create([
            'id' => $messageId,
            'conversation_id' => $conversation->id,
            'sender_id' => $sender->id,
            'content' => $content,
            'type' => $type,
            'file_url' => $fileUrl,
            'file_name' => $fileName,
            'is_from_user' => $isFromUser,
            'sent_at' => now(),
            'sender_name' => $sender->full_name,
        ]);

        // Update conversation
        $conversation->update([
            'last_message_content' => $content ?? ($type === 'file' ? 'Sent a file' : null),
            'last_message_sent_at' => now(),
            'last_message_sender_id' => $sender->id,
        ]);

        // Increment unread count for other party
        if ($isFromUser) {
            $conversation->increment('unread_count_lawyer');
        } else {
            $conversation->increment('unread_count_user');
        }

        return $message;
    }

    /**
     * Format conversation for response
     */
    private function formatConversation(Conversation $conversation, $currentUser): array
    {
        $isClient = $conversation->client_user_id === $currentUser->id;
        $otherParty = $isClient ? $conversation->lawyer : $conversation->client;

        return [
            'id' => $conversation->id,
            'other_party' => [
                'id' => $otherParty->id,
                'full_name' => $otherParty->profile?->full_name ?? $otherParty->full_name,
                'avatar_url' => $otherParty->profile?->avatar_url ?? $otherParty->avatar_url,
                'role' => $isClient ? 'lawyer' : 'user',
            ],
            'unread_count' => $isClient ? $conversation->unread_count_user : $conversation->unread_count_lawyer,
            'created_at' => $conversation->created_at->toIso8601String(),
        ];
    }
}

