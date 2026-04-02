<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Video;
use App\Models\VideoComment;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class VideoController extends Controller
{
    /**
     * Get Videos Feed
     * GET /api/videos/feed
     */
    public function feed(Request $request)
    {
        $limit = $request->input('limit', 15);
        
        $query = Video::with(['lawyer:id,username,full_name,image'])
                      ->where('status', 'published')
                      ->latest('published_at');

        $videos = $query->paginate($limit);

        // Map liked and saved status if user is logged in
        // In Laravel, Auth::id() works using the guard defined in the route.
        // If the 'auth:api' is not required but optional, Auth::id() will return the correct id if a token is present and valid.
        $userId = Auth::id();
        
        $videos->getCollection()->transform(function ($video) use ($userId) {
            $video->is_liked = $userId ? $video->likes()->where('user_id', $userId)->exists() : false;
            $video->is_saved = $userId ? $video->saves()->where('user_id', $userId)->exists() : false;
            return $video;
        });

        return response()->json([
            'videos' => $videos->items(),
            'pagination' => [
                'current_page' => $videos->currentPage(),
                'last_page' => $videos->lastPage(),
                'per_page' => $videos->perPage(),
                'total' => $videos->total(),
            ]
        ]);
    }

    /**
     * Like / Unlike Video
     * POST /api/videos/{id}/like
     */
    public function like(Request $request, $id)
    {
        $video = Video::findOrFail($id);
        $user = Auth::user();

        $like = $video->likes()->where('user_id', $user->id)->first();

        if ($like) {
            $like->delete();
            $video->decrement('likes_count');
            $liked = false;
        } else {
            $video->likes()->create(['user_id' => $user->id]);
            $video->increment('likes_count');
            $liked = true;
        }

        return response()->json(['liked' => $liked]);
    }

    /**
     * Save Video
     * POST /api/videos/{id}/save
     */
    public function save(Request $request, $id)
    {
        $video = Video::findOrFail($id);
        $user = Auth::user();

        $save = $video->saves()->where('user_id', $user->id)->first();

        if ($save) {
            $save->delete();
            $video->decrement('saves_count');
            $saved = false;
        } else {
            $video->saves()->create(['user_id' => $user->id]);
            $video->increment('saves_count');
            $saved = true;
        }

        return response()->json(['saved' => $saved]);
    }

    /**
     * Share Video
     * POST /api/videos/{id}/share
     */
    public function share(Request $request, $id)
    {
        $video = Video::findOrFail($id);
        $video->increment('shares_count');

        return response()->json(['success' => true]);
    }

    /**
     * Get Comments
     * GET /api/videos/{id}/comments
     */
    public function comments(Request $request, $id)
    {
        $limit = $request->input('limit', 15);
        $video = Video::findOrFail($id);

        $comments = $video->comments()
            ->with(['user:id,username,full_name,image'])
            ->latest()
            ->paginate($limit);

        return response()->json([
            'comments' => $comments->items(),
            'pagination' => [
                'current_page' => $comments->currentPage(),
                'last_page' => $comments->lastPage(),
                'per_page' => $comments->perPage(),
                'total' => $comments->total(),
            ]
        ]);
    }

    /**
     * Add Comment
     * POST /api/videos/{id}/comments
     */
    public function addComment(Request $request, $id)
    {
        $request->validate([
            'content' => 'required|string|max:1000'
        ]);

        $video = Video::findOrFail($id);
        $user = Auth::user();

        $comment = $video->comments()->create([
            'user_id' => $user->id,
            'content' => $request->input('content')
        ]);

        $video->increment('comments_count');

        // Load user data for the response
        $comment->load('user:id,username,full_name,image');

        return response()->json(['comment' => $comment]);
    }

    /**
     * Delete Comment
     * DELETE /api/videos/comments/{commentId}
     */
    public function deleteComment(Request $request, $commentId)
    {
        $comment = VideoComment::findOrFail($commentId);
        $user = Auth::user();

        // Check if user owns the comment or owns the video
        if ($comment->user_id !== $user->id && $comment->video->lawyer_id !== $user->id) {
            return response()->json(['error' => 'Unauthorized'], 403);
        }

        $comment->delete();
        $comment->video->decrement('comments_count');

        return response()->json(['success' => true]);
    }
}
