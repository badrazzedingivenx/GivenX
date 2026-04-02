<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Models\User;
use App\Models\Review;
use App\Models\LawyerFollower;

class LawyerController extends Controller
{
    /**
     * GET /api/lawyers
     */
    public function index(Request $request)
    {
        $page = max(1, (int) $request->query('page', 1));
        $limit = max(1, min(50, (int) $request->query('limit', 10)));

        $query = User::where('role', 'lawyer')
            ->withCount('followers as followers_count')
            ->withAvg('receivedReviews as rating', 'rating');

        if ($request->filled('search')) {
            $search = $request->query('search');
            $query->where(function($q) use ($search) {
                $q->where('full_name', 'like', "%{$search}%")
                  ->orWhere('username', 'like', "%{$search}%");
            });
        }

        if ($request->filled('specialty')) {
            $query->where('specialisation', $request->query('specialty'));
        }

        if ($request->filled('location')) {
            $query->where('city', $request->query('location'));
        }

        if ($request->filled('rating')) {
            $minRating = (float) $request->query('rating');
            $query->having('rating', '>=', $minRating);
        }

        $lawyers = $query->get();
        $total = $lawyers->count();
        $totalPages = $total > 0 ? (int) ceil($total / $limit) : 1;
        
        $paginatedLawyers = $lawyers->slice(($page - 1) * $limit, $limit)->values();

        return response()->json([
            'lawyers' => $paginatedLawyers->map(function ($l) {
                return [
                    'id' => (int) $l->id,
                    'username' => $l->username,
                    'name' => $l->full_name,
                    'bio' => $l->experience ? $l->experience . ' years of experience.' : 'Passionate lawyer.',
                    'specialty' => $l->specialisation,
                    'city' => $l->city,
                    'office_address' => $l->office_address,
                    'rating' => $l->rating ? round($l->rating, 1) : 0,
                    'followers_count' => (int) $l->followers_count,
                    'availability' => 'Available',
                ];
            }),
            'pagination' => [
                'page' => $page,
                'limit' => $limit,
                'total' => $total,
                'total_pages' => $totalPages,
            ],
        ]);
    }

    /**
     * GET /api/lawyers/:id
     */
    public function show($id)
    {
        $lawyer = User::where('role', 'lawyer')
            ->withCount('followers as followers_count')
            ->withAvg('receivedReviews as rating', 'rating')
            ->find($id);

        if (!$lawyer) {
            return response()->json(['message' => 'Lawyer not found'], 404);
        }

        return response()->json([
            'lawyer' => [
                'id' => (int) $lawyer->id,
                'name' => $lawyer->full_name,
                'bio' => $lawyer->experience ? $lawyer->experience . ' years of experience. ' . $lawyer->office_address : 'Passionate lawyer.',
                'specialty' => $lawyer->specialisation,
                'rating' => $lawyer->rating ? round($lawyer->rating, 1) : 0,
                'availability' => 'Available',
                'followers_count' => (int) $lawyer->followers_count,
            ]
        ]);
    }

    /**
     * GET /api/lawyers/:id/reviews
     */
    public function reviews(Request $request, $id)
    {
        $page = max(1, (int) $request->query('page', 1));
        $limit = max(1, min(50, (int) $request->query('limit', 10)));

        $lawyer = User::where('role', 'lawyer')->find($id);
        if (!$lawyer) {
            return response()->json(['message' => 'Lawyer not found'], 404);
        }

        $query = Review::where('lawyer_id', $id)->with('user:id,username,full_name,image');
        $total = $query->count();
        $totalPages = $total > 0 ? (int) ceil($total / $limit) : 1;

        $reviews = $query->orderByDesc('created_at')
                         ->offset(($page - 1) * $limit)
                         ->limit($limit)
                         ->get();

        return response()->json([
            'reviews' => $reviews->map(function ($r) {
                return [
                    'id' => (int) $r->id,
                    'rating' => (float) $r->rating,
                    'comment' => $r->comment,
                    'created_at' => $r->created_at,
                    'user' => [
                        'id' => (int) $r->user->id,
                        'name' => $r->user->full_name,
                        'image' => $r->user->image,
                    ]
                ];
            }),
            'pagination' => [
                'page' => $page,
                'limit' => $limit,
                'total' => $total,
                'total_pages' => $totalPages,
            ]
        ]);
    }

    /**
     * POST /api/lawyers/:id/reviews
     */
    public function addReview(Request $request, $id)
    {
        $request->validate([
            'rating' => 'required|numeric|min:1|max:5',
            'comment' => 'nullable|string'
        ]);

        $lawyer = User::where('role', 'lawyer')->find($id);
        if (!$lawyer) {
            return response()->json(['message' => 'Lawyer not found'], 404);
        }

        $userId = Auth::id();

        // Check if user already reviewed
        $existing = Review::where('user_id', $userId)->where('lawyer_id', $id)->first();
        if ($existing) {
            return response()->json(['message' => 'You have already reviewed this lawyer'], 400);
        }

        $review = Review::create([
            'user_id' => $userId,
            'lawyer_id' => $id,
            'rating' => $request->rating,
            'comment' => $request->comment,
        ]);

        $review->load('user:id,username,full_name,image');

        return response()->json([
            'review' => [
                'id' => (int) $review->id,
                'rating' => (float) $review->rating,
                'comment' => $review->comment,
                'created_at' => $review->created_at,
                'user' => [
                    'id' => (int) $review->user->id,
                    'name' => $review->user->full_name,
                    'image' => $review->user->image,
                ]
            ]
        ], 201);
    }

    /**
     * POST /api/lawyers/:id/favorite
     */
    public function favorite(Request $request, $id)
    {
        $lawyer = User::where('role', 'lawyer')->find($id);
        if (!$lawyer) {
            return response()->json(['message' => 'Lawyer not found'], 404);
        }

        $userId = Auth::id();
        $existing = LawyerFollower::where('user_id', $userId)->where('lawyer_id', $id)->first();

        if ($existing) {
            $existing->delete();
            return response()->json(['favorited' => false]);
        } else {
            LawyerFollower::create([
                'user_id' => $userId,
                'lawyer_id' => $id,
            ]);
            return response()->json(['favorited' => true]);
        }
    }

    /**
     * GET /api/lawyers/favorites
     */
    public function favorites(Request $request)
    {
        $userId = Auth::id();
        $user = User::with(['followingLawyers' => function ($query) {
            $query->withAvg('receivedReviews as rating', 'rating');
        }])->find($userId);

        if (!$user) {
            return response()->json(['message' => 'User not found'], 404);
        }

        $lawyers = $user->followingLawyers;

        return response()->json([
            'lawyers' => $lawyers->map(function ($l) {
                return [
                    'id' => (int) $l->id,
                    'name' => $l->full_name,
                    'specialty' => $l->specialisation,
                    'rating' => $l->rating ? round($l->rating, 1) : 0,
                    'followers_count' => $l->followers()->count(),
                ];
            })
        ]);
    }
}
