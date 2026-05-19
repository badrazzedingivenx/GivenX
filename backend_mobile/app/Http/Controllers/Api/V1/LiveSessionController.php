<?php

namespace App\Http\Controllers\Api\V1;

use App\Models\LiveComment;
use App\Models\LiveSession;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Str;
use Tymon\JWTAuth\Facades\JWTAuth;

class LiveSessionController extends ApiController
{
    /**
     * List live sessions
     */
    public function index(Request $request): JsonResponse
    {
        $status = $request->input('status', 'LIVE');
        $domain = $request->input('domain');

        $query = LiveSession::with('lawyer.profile')
            ->orderBy('started_at', 'desc');

        if ($status) {
            $query->where('status', $status);
        }

        if ($domain) {
            $query->where('domain', $domain);
        }

        $sessions = $query->paginate($request->input('per_page', 20));

        $data = $sessions->map(fn($session) => [
            'id' => $session->id,
            'topic' => $session->topic,
            'description' => $session->description,
            'domain' => $session->domain,
            'status' => $session->status,
            'viewer_count' => $session->viewer_count,
            'thumbnail_url' => $session->thumbnail_url,
            'stream_url' => $session->stream_url,
            'started_at' => $session->started_at?->toIso8601String(),
            'lawyer' => [
                'id' => $session->lawyer->id,
                'full_name' => $session->lawyer_name ?? $session->lawyer->profile?->full_name,
                'avatar_url' => $session->lawyer->profile?->avatar_url,
            ],
        ]);

        return $this->success([
            'data' => $data,
            'pagination' => [
                'current_page' => $sessions->currentPage(),
                'last_page' => $sessions->lastPage(),
                'per_page' => $sessions->perPage(),
                'total' => $sessions->total(),
            ],
        ]);
    }

    /**
     * Create new live session (lawyer only)
     * POST /api/v1/live-sessions
     */
    public function store(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'topic' => 'required|string|max:255',
            'description' => 'nullable|string|max:1000',
            'domain' => 'nullable|string|max:255',
            'scheduled_at' => 'nullable|date|after:now',
        ]);

        $user = JWTAuth::user();
        $lawyer = $user->lawyer;

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer profile not found.', 404);
        }

        // Check if lawyer already has an active session
        $activeSession = LiveSession::where('lawyer_user_id', $user->id)
            ->where('status', 'LIVE')
            ->first();

        if ($activeSession) {
            return $this->error('ACTIVE_SESSION_EXISTS', 'You already have an active live session.', 400);
        }

        $sessionId = 'live_' . Str::random(12);
        $streamKey = Str::random(32);

        // Determine if starting immediately or scheduled
        $isScheduled = isset($validated['scheduled_at']);
        $status = $isScheduled ? 'SCHEDULED' : 'LIVE';
        $startedAt = $isScheduled ? null : now();

        // Build streaming URLs from environment config
        $streamingHost = config('services.streaming.host', '127.0.0.1');
        $hlsPort = config('services.streaming.hls_port', '8888');

        $session = LiveSession::create([
            'id' => $sessionId,
            'lawyer_user_id' => $user->id,
            'lawyer_name' => $lawyer->name ?? $user->full_name,
            'topic' => $validated['topic'],
            'description' => $validated['description'] ?? null,
            'domain' => $validated['domain'] ?? null,
            'status' => $status,
            'stream_key' => $streamKey,
            'rtmp_url' => "rtmp://{$streamingHost}/live",
            'playback_url' => "http://{$streamingHost}:{$hlsPort}/live/{$streamKey}/index.m3u8",
            'thumbnail_url' => $request->input('thumbnail_url'),
            'scheduled_at' => $validated['scheduled_at'] ?? null,
            'started_at' => $startedAt,
        ]);

        return $this->success([
            'session_id' => $session->id,
            'topic' => $session->topic,
            'status' => $session->status,
            'stream_key' => $session->stream_key,
            'rtmp_url' => $session->rtmp_url,
            'playback_url' => $session->playback_url,
            'scheduled_at' => $session->scheduled_at?->toIso8601String(),
            'started_at' => $session->started_at?->toIso8601String(),
        ], 'Live session started successfully.', 201);
    }

    /**
     * Show live session details
     */
    public function show(string $id): JsonResponse
    {
        $session = LiveSession::with(['lawyer.profile', 'comments'])
            ->where('id', $id)
            ->first();

        if (!$session) {
            return $this->error('SESSION_NOT_FOUND', 'Live session not found.', 404);
        }

        // Increment viewer count
        if ($session->isLive()) {
            $session->increment('viewer_count');
        }

        return $this->success([
            'id' => $session->id,
            'topic' => $session->topic,
            'description' => $session->description,
            'domain' => $session->domain,
            'status' => $session->status,
            'viewer_count' => $session->viewer_count,
            'participants' => $session->participants,
            'thumbnail_url' => $session->thumbnail_url,
            'stream_url' => $session->stream_url,
            'started_at' => $session->started_at?->toIso8601String(),
            'duration_sec' => $session->duration_sec,
            'lawyer' => [
                'id' => $session->lawyer->id,
                'full_name' => $session->lawyer_name ?? $session->lawyer->profile?->full_name,
                'avatar_url' => $session->lawyer->profile?->avatar_url,
            ],
        ]);
    }

    /**
     * End live session (lawyer only)
     * PATCH /api/v1/live-sessions/{id}/end
     */
    public function end(string $id): JsonResponse
    {
        $user = JWTAuth::user();

        $session = LiveSession::where('id', $id)
            ->where('lawyer_user_id', $user->id)
            ->first();

        if (!$session) {
            return $this->error('SESSION_NOT_FOUND', 'Live session not found.', 404);
        }

        // Check if user is the session owner
        if ($session->lawyer_user_id !== $user->id) {
            return $this->error('NOT_SESSION_OWNER', 'The authenticated lawyer is not the owner of this session.', 403);
        }

        if (!$session->isLive()) {
            return $this->error('SESSION_NOT_LIVE', 'This session is not currently live.', 400);
        }

        $session->end();

        // TODO: Publish live.ended event to all viewers via WebSocket

        return $this->success([
            'session_id' => $session->id,
            'status' => $session->status,
            'duration_sec' => $session->duration_sec,
            'total_viewers' => $session->viewer_count,
        ], 'Live session ended successfully.');
    }

    /**
     * Get live session comments
     * GET /api/v1/live-sessions/{id}/comments
     */
    public function comments(string $id, Request $request): JsonResponse
    {
        $session = LiveSession::where('id', $id)->first();

        if (!$session) {
            return $this->error('SESSION_NOT_FOUND', 'Live session not found.', 404);
        }

        $comments = LiveComment::where('live_session_id', $id)
            ->orderBy('sent_at', 'desc')
            ->paginate($request->input('per_page', 50));

        $data = $comments->map(fn($comment) => [
            'id' => $comment->id,
            'author_name' => $comment->author_name,
            'content' => $comment->content,
            'sent_at' => $comment->sent_at->toIso8601String(),
        ]);

        return $this->success([
            'data' => $data,
            'pagination' => [
                'current_page' => $comments->currentPage(),
                'last_page' => $comments->lastPage(),
                'per_page' => $comments->perPage(),
                'total' => $comments->total(),
            ],
        ]);
    }

    /**
     * Add comment to live session
     * POST /api/v1/live-sessions/{id}/comments
     */
    public function addComment(string $id, Request $request): JsonResponse
    {
        $validated = $request->validate([
            'content' => 'required|string|max:500',
        ]);

        $session = LiveSession::where('id', $id)->first();

        if (!$session) {
            return $this->error('SESSION_NOT_FOUND', 'Live session not found.', 404);
        }

        $user = JWTAuth::user();

        $comment = LiveComment::create([
            'live_session_id' => $id,
            'user_id' => $user->id,
            'author_name' => $user->full_name,
            'content' => $validated['content'],
            'sent_at' => now(),
        ]);

        // TODO: Broadcast comment via WebSocket for real-time updates

        return $this->success([
            'id' => $comment->id,
            'author_name' => $comment->author_name,
            'content' => $comment->content,
            'sent_at' => $comment->sent_at->toIso8601String(),
        ], 'Comment added successfully.', 201);
    }
}

