<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class LawyerController extends Controller
{
    /**
     * GET /api/lawyers
     */
    public function index(Request $request)
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

        $total = DB::table('users')->where('role', '=', 'lawyer')->count();
        $totalPages = $limit > 0 ? (int) ceil($total / $limit) : 1;

        $lawyers = DB::table('users as u')
            ->leftJoin('lawyer_followers as lf', 'lf.lawyer_id', '=', 'u.id')
            ->where('u.role', '=', 'lawyer')
            ->groupBy('u.id', 'u.username', 'u.full_name', 'u.city', 'u.specialisation', 'u.office_address')
            ->select(
                'u.id',
                'u.username',
                'u.full_name',
                'u.city',
                'u.specialisation',
                'u.office_address',
                DB::raw('COUNT(lf.id) as followers_count')
            )
            ->orderByDesc(DB::raw('COUNT(lf.id)'))
            ->offset($offset)
            ->limit($limit)
            ->get();

        return response()->json([
            'lawyers' => $lawyers->map(function ($l) {
                return [
                    'id' => (int) $l->id,
                    'username' => $l->username,
                    'full_name' => $l->full_name,
                    'city' => $l->city,
                    'specialisation' => $l->specialisation,
                    'office_address' => $l->office_address,
                    'followers_count' => (int) $l->followers_count,
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
