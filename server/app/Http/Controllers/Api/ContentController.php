<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class ContentController extends Controller
{
    /**
     * GET /api/content/trending
     */
    public function trending(Request $request)
    {
        $limit = (int) $request->query('limit', 10);
        if ($limit < 1) {
            $limit = 10;
        }

        // Trending topics come from the "interests" linked to the most engaged published videos.
        $topics = DB::table('video_interests as vi')
            ->join('interests as i', 'i.id', '=', 'vi.interest_id')
            ->join('videos as v', 'v.id', '=', 'vi.video_id')
            ->where('v.status', '=', 'published')
            ->whereNotNull('v.published_at')
            ->select(
                'i.id',
                'i.name',
                'i.icon',
                DB::raw('COUNT(DISTINCT v.id) as videos_count'),
                DB::raw('SUM(v.views_count + v.likes_count + v.comments_count) as score')
            )
            ->groupBy('i.id', 'i.name', 'i.icon')
            ->orderByDesc('score')
            ->limit($limit)
            ->get();

        // Trending hashtags come from the "tags" JSON array stored in videos.tags.
        $hashtagVideosLimit = max(50, $limit * 5);
        $videos = DB::table('videos')
            ->where('status', '=', 'published')
            ->whereNotNull('published_at')
            ->orderByDesc(DB::raw('(views_count + likes_count + comments_count)'))
            ->limit($hashtagVideosLimit)
            ->get(['tags']);

        $tagCounts = [];
        foreach ($videos as $video) {
            $tags = $video->tags;

            if ($tags === null) {
                continue;
            }

            $decoded = is_string($tags) ? json_decode($tags, true) : $tags;
            if (!is_array($decoded)) {
                continue;
            }

            foreach ($decoded as $tag) {
                if (!is_string($tag)) {
                    continue;
                }

                $tag = trim($tag);
                if ($tag === '') {
                    continue;
                }

                $tagCounts[$tag] = ($tagCounts[$tag] ?? 0) + 1;
            }
        }

        arsort($tagCounts);
        $hashtags = [];
        $i = 0;
        foreach ($tagCounts as $tag => $count) {
            $hashtags[] = [
                'tag' => $tag,
                'count' => (int) $count,
            ];
            $i++;
            if ($i >= $limit) {
                break;
            }
        }

        return response()->json([
            'trendingTopics' => $topics->map(function ($t) {
                return [
                    'id' => (int) $t->id,
                    'name' => $t->name,
                    'icon' => $t->icon,
                    'videos_count' => (int) $t->videos_count,
                    'score' => (int) $t->score,
                ];
            })->values(),
            'trendingHashtags' => $hashtags,
        ]);
    }

    /**
     * GET /api/content/culture/feed
     */
    public function cultureFeed(Request $request)
    {
        $page = (int) $request->query('page', 1);
        if ($page < 1) {
            $page = 1;
        }

        $limit = (int) $request->query('limit', 10);
        if ($limit < 1) {
            $limit = 10;
        }
        if ($limit > 50) {
            $limit = 50;
        }

        $offset = ($page - 1) * $limit;

        $total = DB::table('videos')
            ->where('status', '=', 'published')
            ->whereNotNull('published_at')
            ->count();

        $totalPages = $limit > 0 ? (int) ceil($total / $limit) : 1;

        $posts = DB::table('videos as v')
            ->leftJoin('users as u', 'u.id', '=', 'v.lawyer_id')
            ->where('v.status', '=', 'published')
            ->whereNotNull('v.published_at')
            ->orderByDesc('v.published_at')
            ->offset($offset)
            ->limit($limit)
            ->select(
                'v.id',
                'v.title',
                'v.description',
                'v.tags',
                'v.video_path',
                'v.thumbnail_path',
                'v.duration_seconds',
                'v.published_at',
                'v.views_count',
                'v.likes_count',
                'v.comments_count',
                'v.shares_count',
                'v.saves_count',
                'u.id as lawyer_id',
                'u.username',
                'u.full_name',
                'u.city',
                'u.specialisation',
                'u.office_address'
            )
            ->get();

        return response()->json([
            'posts' => $posts->map(function ($p) {
                $decodedTags = $p->tags;
                if (is_string($decodedTags)) {
                    $decodedTags = json_decode($decodedTags, true);
                }

                return [
                    'id' => (int) $p->id,
                    'title' => $p->title,
                    'description' => $p->description,
                    'tags' => is_array($decodedTags) ? $decodedTags : [],
                    'video_path' => $p->video_path,
                    'thumbnail_path' => $p->thumbnail_path,
                    'duration_seconds' => (int) $p->duration_seconds,
                    'published_at' => $p->published_at,
                    'views_count' => (int) $p->views_count,
                    'likes_count' => (int) $p->likes_count,
                    'comments_count' => (int) $p->comments_count,
                    'shares_count' => (int) $p->shares_count,
                    'saves_count' => (int) $p->saves_count,
                    'lawyer' => $p->lawyer_id ? [
                        'id' => (int) $p->lawyer_id,
                        'username' => $p->username,
                        'full_name' => $p->full_name,
                        'city' => $p->city,
                        'specialisation' => $p->specialisation,
                        'office_address' => $p->office_address,
                    ] : null,
                ];
            })->values(),
            'pagination' => [
                'page' => $page,
                'limit' => $limit,
                'total' => (int) $total,
                'total_pages' => (int) $totalPages,
            ],
        ]);
    }
}

