<?php

namespace App\Http\Controllers\Api\V1;

use App\Models\Consultation;
use App\Models\Lawyer;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;
use Tymon\JWTAuth\Facades\JWTAuth;

class MatchingController extends ApiController
{
    /**
     * AI-assisted lawyer matching
     * POST /api/v1/matching/request
     */
    public function requestMatch(Request $request): JsonResponse
    {
        $validated = $request->validate([
            'legal_domain' => 'required|string|max:255',
            'description' => 'required|string|min:50|max:5000',
            'urgency' => 'required|in:low,medium,high',
            'budget_range' => 'nullable|string|max:100',
            'preferred_city' => 'nullable|string|max:255',
        ]);

        $user = JWTAuth::user();
        $client = $user->client;

        if (!$client) {
            return $this->error('CLIENT_NOT_FOUND', 'Client profile not found.', 404);
        }

        $legalDomain = $validated['legal_domain'];
        $description = $validated['description'];
        $urgency = $validated['urgency'];
        $budgetRange = $validated['budget_range'] ?? null;
        $preferredCity = $validated['preferred_city'] ?? null;

        // Simple keyword-based matching algorithm
        $keywords = $this->extractKeywords($description . ' ' . $legalDomain);

        $query = Lawyer::verified()
            ->where('is_available', true);

        if ($preferredCity) {
            $query->where('city', 'like', '%' . $preferredCity . '%');
        }

        // Get all verified lawyers
        $lawyers = $query->get();

        // Score lawyers based on keyword matching
        $scoredLawyers = $lawyers->map(function ($lawyer) use ($keywords, $legalDomain) {
            $score = 0;
            $matchReasons = [];
            $lawyerText = strtolower($lawyer->speciality . ' ' . $lawyer->bio . ' ' . $lawyer->domaine);

            foreach ($keywords as $keyword) {
                if (str_contains($lawyerText, $keyword)) {
                    $score += 10;
                    $matchReasons[] = 'Matches keyword: ' . $keyword;
                }
            }

            // Bonus for matching legal domain
            if (str_contains(strtolower($lawyer->speciality), strtolower($legalDomain))) {
                $score += 30;
                $matchReasons[] = 'Specializes in ' . $legalDomain;
            }

            // Bonus for higher rating
            $score += ($lawyer->rating ?? 0) * 5;

            // Bonus for more experience
            $score += min($lawyer->years_experience, 20);

            // Availability reason
            if ($lawyer->is_available) {
                $matchReasons[] = 'Available for new cases';
            }

            // Remove duplicate reasons
            $matchReasons = array_unique($matchReasons);

            return [
                'lawyer' => $lawyer,
                'score' => $score,
                'match_reasons' => $matchReasons,
            ];
        })->sortByDesc('score')->take(5);

        // Create a consultation request
        $topLawyer = $scoredLawyers->first();
        if (!$topLawyer) {
            return $this->error('NO_MATCHES', 'No matching lawyers found for your request.', 404);
        }

        $consultation = Consultation::create([
            'client_id' => $client->id,
            'lawyer_id' => $topLawyer['lawyer']->id,
            'status' => 'pending',
            'subject' => substr($description, 0, 255),
            'date' => now(),
        ]);

        // Generate estimated cost based on urgency
        $estimatedCost = $this->estimateCost($urgency, $budgetRange);

        // Generate availability slots
        $availabilitySlots = $this->generateAvailabilitySlots($topLawyer['lawyer']);

        // Format response
        $matches = $scoredLawyers->map(function($item) use ($estimatedCost, $availabilitySlots) {
            $lawyer = $item['lawyer'];
            return [
                'lawyer' => [
                    'id' => (string) $lawyer->profile->user_id,
                    'full_name' => $lawyer->name,
                    'speciality' => $lawyer->speciality,
                    'bio' => $lawyer->bio,
                    'city' => $lawyer->city,
                    'avatar_url' => $lawyer->avatar_url,
                    'rating' => (float) $lawyer->rating,
                    'review_count' => $lawyer->review_count,
                    'years_experience' => $lawyer->years_experience,
                    'bar_number' => $lawyer->bar_number,
                ],
                'match_score' => min(round($item['score'], 2), 100.0),
                'match_reasons' => $item['match_reasons'],
                'estimated_cost' => $estimatedCost,
                'availability_slots' => $availabilitySlots,
            ];
        })->values();

        // Store in cache for history
        $matchId = 'match_' . $user->id . '_' . time();
        Cache::put($matchId, [
            'match_id' => $matchId,
            'user_id' => $user->id,
            'legal_domain' => $legalDomain,
            'description' => $description,
            'urgency' => $urgency,
            'matches_count' => $matches->count(),
            'created_at' => now()->toIso8601String(),
        ], now()->addDays(30));

        // Add match key to user's history list
        $matchKeys = Cache::get('match_keys_' . $user->id, []);
        $matchKeys[] = $matchId;
        Cache::put('match_keys_' . $user->id, $matchKeys, now()->addDays(30));

        return $this->success([
            'match_id' => $matchId,
            'consultation_id' => $consultation->id,
            'matches' => $matches,
        ], 'Matching completed successfully.');
    }

    /**
     * Estimate consultation cost based on urgency
     */
    private function estimateCost(string $urgency, ?string $budgetRange): string
    {
        if ($budgetRange) {
            return $budgetRange;
        }

        $ranges = [
            'low' => '300-500 MAD',
            'medium' => '500-800 MAD',
            'high' => '800-1200 MAD',
        ];

        return $ranges[$urgency] ?? '500-800 MAD';
    }

    /**
     * Generate availability time slots for next 7 days
     */
    private function generateAvailabilitySlots($lawyer): array
    {
        $slots = [];
        $today = now();

        for ($i = 0; $i < 7; $i++) {
            $date = $today->copy()->addDays($i);
            $dayName = strtolower($date->dayName);

            // Generate sample time slots
            $slots[] = [
                'date' => $date->toDateString(),
                'times' => ['09:00', '10:00', '14:00', '15:00', '16:00'],
            ];
        }

        return $slots;
    }

    /**
     * Get matching history
     * GET /api/v1/matching/history
     */
    public function history(): JsonResponse
    {
        $user = JWTAuth::user();

        // Get all cache keys for this user's matches
        $history = [];
        $pattern = 'match_' . $user->id . '_*';

        // Since Laravel cache doesn't support pattern matching out of the box,
        // we'll store match IDs in a user-specific list
        $matchKeys = Cache::get('match_keys_' . $user->id, []);

        foreach ($matchKeys as $key) {
            $match = Cache::get($key);
            if ($match) {
                $history[] = $match;
            }
        }

        // Sort by created_at desc
        usort($history, fn($a, $b) => strtotime($b['created_at']) - strtotime($a['created_at']));

        return $this->success($history);
    }

    /**
     * Extract keywords from case description
     */
    private function extractKeywords(string $text): array
    {
        $legalKeywords = [
            'divorce', 'custody', 'family', 'inheritance', 'property', 'real estate',
            'criminal', 'defense', 'accident', 'injury', 'personal injury',
            'contract', 'business', 'corporate', 'commercial', 'employment',
            'immigration', 'visa', 'citizenship', 'asylum',
            'intellectual property', 'patent', 'trademark', 'copyright',
            'tax', 'fiscal', 'banking', 'finance',
            'administrative', 'constitutional', 'human rights',
            'maritime', 'aviation', 'transport',
            'environmental', 'energy', 'mining',
            'medical', 'malpractice', 'health',
            'consumer', 'protection', 'competition',
        ];

        $text = strtolower($text);
        $found = [];

        foreach ($legalKeywords as $keyword) {
            if (str_contains($text, $keyword)) {
                $found[] = $keyword;
            }
        }

        return $found;
    }
}

