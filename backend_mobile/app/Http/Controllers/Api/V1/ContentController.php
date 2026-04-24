<?php

namespace App\Http\Controllers\Api\V1;

use App\Models\Reel;
use App\Models\ReelLike;
use App\Models\Story;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Str;
use Tymon\JWTAuth\Facades\JWTAuth;

class ContentController extends ApiController
{
    // ==================== STORIES ====================

    /**
     * List active stories
     * GET /api/v1/stories
     */
    public function stories(): JsonResponse
    {
        $user = JWTAuth::user();
        
        $stories = Story::with('lawyer.profile')
            ->active()
            ->orderBy('created_at', 'desc')
            ->get();

        $data = $stories->map(function ($story) use ($user) {
            // TODO: Track is_seen status in database
            return [
                'id' => $story->id,
                'lawyer' => [
                    'full_name' => $story->lawyer_name ?? $story->lawyer->profile?->full_name,
                ],
                'media_url' => $story->media_url,
                'caption' => $story->caption,
                'is_seen' => false,
                'expires_at' => $story->expires_at->toIso8601String(),
            ];
        });

        return $this->success([
            'stories' => $data,
        ]);
    }

    /**
     * Create new story (lawyer only)
     * POST /api/v1/stories
     */
    public function storeStory(Request $request): JsonResponse
    {
        $request->validate([
            'media' => 'required|file|mimes:jpeg,png,jpg,webp,mp4,mov,avi|max:51200', // 50MB max (images + videos)
            'caption' => 'nullable|string|max:200',
        ]);

        $user = JWTAuth::user();
        $lawyer = $user->lawyer;

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer profile not found.', 404);
        }

        try {
            $file = $request->file('media');
            $isVideo = str_starts_with($file->getMimeType(), 'video/');
            $folder = $isVideo ? 'stories/videos' : 'stories/images';
            $filename = 'story_' . $user->id . '_' . time() . '.' . $file->getClientOriginalExtension();
            $path = $file->storeAs($folder, $filename, 'public');
            $mediaUrl = Storage::url($path);

            $storyId = 'story_' . Str::random(12);

            $story = Story::create([
                'id' => $storyId,
                'lawyer_user_id' => $user->id,
                'lawyer_name' => $lawyer->name ?? $user->full_name,
                'media_url' => $mediaUrl,
                'caption' => $request->input('caption'),
                'expires_at' => now()->addHours(24),
                'views' => 0,
            ]);

            return $this->success([
                'id' => $story->id,
                'media_url' => $story->media_url,
                'caption' => $story->caption,
                'expires_at' => $story->expires_at->toIso8601String(),
            ], 'Story created successfully.', 201);
        } catch (\Exception $e) {
            return $this->error('UPLOAD_FAILED', 'Failed to upload story: ' . $e->getMessage(), 500);
        }
    }

    /**
     * Delete story (lawyer only)
     */
    public function deleteStory(string $id): JsonResponse
    {
        $user = JWTAuth::user();

        $story = Story::where('id', $id)
            ->where('lawyer_user_id', $user->id)
            ->first();

        if (!$story) {
            return $this->error('STORY_NOT_FOUND', 'Story not found.', 404);
        }

        $story->delete();

        return $this->success(null, 'Story deleted successfully.');
    }

    /**
     * View story (client only)
     */
    public function viewStory(string $id): JsonResponse
    {
        $user = JWTAuth::user();

        $story = Story::where('id', $id)->first();

        if (!$story) {
            return $this->error('STORY_NOT_FOUND', 'Story not found.', 404);
        }

        if ($story->isExpired()) {
            return $this->error('STORY_EXPIRED', 'Story has expired.', 410);
        }

        // Record view
        $story->recordView($user->id);

        return $this->success([
            'id' => $story->id,
            'views' => $story->views,
        ]);
    }

    // ==================== REELS ====================

    /**
     * List reels feed
     * GET /api/v1/reels
     */
    public function reels(Request $request): JsonResponse
    {
        $domain = $request->input('domain');
        $page = $request->input('page', 1);
        $limit = $request->input('limit', 20);

        $query = Reel::with('lawyer.profile')
            ->orderBy('created_at', 'desc');

        if ($domain) {
            $query->where('domain', $domain);
        }

        $reels = $query->paginate($limit, ['*'], 'page', $page);
        $user = JWTAuth::user();

        $data = $reels->map(function ($reel) use ($user) {
            return [
                'id' => $reel->id,
                'lawyer' => [
                    'id' => $reel->lawyer->id,
                    'full_name' => $reel->lawyer_name ?? $reel->lawyer->profile?->full_name,
                    'avatar_url' => $reel->lawyer->profile?->avatar_url,
                ],
                'title' => $reel->title,
                'video_url' => $reel->video_url,
                'likes_count' => $reel->likes_count,
                'views_count' => $reel->views_count,
                'is_liked' => $user ? $reel->isLikedBy($user) : false,
                'duration_sec' => $reel->duration,
            ];
        });

        return $this->success([
            'reels' => $data,
            'pagination' => [
                'current_page' => $reels->currentPage(),
                'last_page' => $reels->lastPage(),
                'per_page' => $reels->perPage(),
                'total' => $reels->total(),
            ],
        ]);
    }

    /**
     * Create new reel (lawyer only)
     * POST /api/v1/reels
     */
    public function storeReel(Request $request): JsonResponse
    {
        $request->validate([
            'video' => 'required|file|mimes:mp4|max:102400', // 100MB max
            'thumbnail' => 'nullable|image|mimes:jpeg,png,jpg|max:5120',
            'title' => 'required|string|max:100',
            'domain' => 'nullable|string|max:255',
        ]);

        $user = JWTAuth::user();
        $lawyer = $user->lawyer;

        if (!$lawyer) {
            return $this->error('LAWYER_NOT_FOUND', 'Lawyer profile not found.', 404);
        }

        try {
            // Upload video
            $video = $request->file('video');
            $videoFilename = 'reel_' . $user->id . '_' . time() . '.' . $video->getClientOriginalExtension();
            $videoPath = $video->storeAs('reels/videos', $videoFilename, 'public');
            $videoUrl = Storage::url($videoPath);

            // Upload thumbnail if provided
            $thumbnailUrl = null;
            if ($request->hasFile('thumbnail')) {
                $thumbnail = $request->file('thumbnail');
                $thumbFilename = 'reel_thumb_' . $user->id . '_' . time() . '.' . $thumbnail->getClientOriginalExtension();
                $thumbPath = $thumbnail->storeAs('reels/thumbnails', $thumbFilename, 'public');
                $thumbnailUrl = Storage::url($thumbPath);
            }

            $reelId = 'reel_' . Str::random(12);

            $reel = Reel::create([
                'id' => $reelId,
                'lawyer_user_id' => $user->id,
                'lawyer_name' => $lawyer->name ?? $user->full_name,
                'title' => $request->input('title'),
                'caption' => $request->input('caption'),
                'video_url' => $videoUrl,
                'thumbnail_url' => $thumbnailUrl,
                'domain' => $request->input('domain'),
                'likes_count' => 0,
                'views_count' => 0,
            ]);

            return $this->success([
                'id' => $reel->id,
                'status' => 'processing',
                'video_url' => null,
                'thumbnail_url' => null,
            ], 'Reel uploaded successfully. Processing will begin shortly.', 201);
        } catch (\Exception $e) {
            return $this->error('UPLOAD_FAILED', 'Failed to upload reel: ' . $e->getMessage(), 500);
        }
    }

    /**
     * Delete reel (lawyer only)
     */
    public function deleteReel(string $id): JsonResponse
    {
        $user = JWTAuth::user();

        $reel = Reel::where('id', $id)
            ->where('lawyer_user_id', $user->id)
            ->first();

        if (!$reel) {
            return $this->error('REEL_NOT_FOUND', 'Reel not found.', 404);
        }

        $reel->delete();

        return $this->success(null, 'Reel deleted successfully.');
    }

    /**
     * Like/unlike reel (client only)
     */
    public function likeReel(string $id): JsonResponse
    {
        $user = JWTAuth::user();

        $reel = Reel::where('id', $id)->first();

        if (!$reel) {
            return $this->error('REEL_NOT_FOUND', 'Reel not found.', 404);
        }

        $existingLike = ReelLike::where('reel_id', $id)
            ->where('user_id', $user->id)
            ->first();

        if ($existingLike) {
            // Unlike
            $existingLike->delete();
            $reel->decrementLikes();
            $message = 'Reel unliked.';
        } else {
            // Like
            ReelLike::create([
                'reel_id' => $id,
                'user_id' => $user->id,
            ]);
            $reel->incrementLikes();
            $message = 'Reel liked.';
        }

        return $this->success([
            'likes_count' => $reel->likes_count,
            'is_liked' => !$existingLike,
        ], $message);
    }

    /**
     * View reel
     */
    public function viewReel(string $id): JsonResponse
    {
        $reel = Reel::where('id', $id)->first();

        if (!$reel) {
            return $this->error('REEL_NOT_FOUND', 'Reel not found.', 404);
        }

        $reel->incrementViews();

        return $this->success([
            'id' => $reel->id,
            'views_count' => $reel->views_count,
        ]);
    }
}

