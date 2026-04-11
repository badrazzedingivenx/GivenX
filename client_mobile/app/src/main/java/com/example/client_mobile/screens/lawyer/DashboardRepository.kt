package com.example.client_mobile.screens.lawyer

import android.util.Log
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.dto.LawyerProfileDto
import com.example.client_mobile.network.dto.LawyerStatsDto
import com.example.client_mobile.network.dto.RecentConsultationDto
import com.example.client_mobile.network.dto.RevenueMonthDto

/**
 * Service layer for the Avocat Dashboard.
 *
 * All dashboard data flows through this class, making the ViewModel free of
 * direct Retrofit/network concerns and allowing straightforward unit testing.
 *
 * Priority strategy for every call:
 *   1. Real API  (RetrofitClient.haqApi)
 *   2. Mock API  (RetrofitClient.mockApi) — offline / staging fallback
 */
class DashboardRepository {

    // ── Profile ───────────────────────────────────────────────────────────────

    /**
     * GET /api/lawyers/me
     * Returns the authenticated lawyer's full profile, or null on failure.
     */
    suspend fun fetchProfile(): LawyerProfileDto? {
        return try {
            val response = RetrofitClient.haqApi.getLawyerProfile()
            if (response.isSuccessful && response.body()?.success == true) {
                return response.body()?.data
            }
            // Fallback: mock API (returns flat JSON, not wrapped)
            RetrofitClient.mockApi.getLawyerProfile()
                .takeIf { it.isSuccessful }?.body()
        } catch (_: Exception) {
            try {
                RetrofitClient.mockApi.getLawyerProfile()
                    .takeIf { it.isSuccessful }?.body()
            } catch (_: Exception) { null }
        }
    }

    // ── Stats ─────────────────────────────────────────────────────────────────

    /**
     * GET /api/lawyers/me/stats
     *
     * Returns KPI stats including:
     *  - total_clients / active_clients
     *  - new_requests / closed_cases
     *  - total_revenue_month / total_revenue_year / average_rating
     *  - monthly_revenue  (List<RevenueMonthDto>) — 6-month chart data
     *  - revenue_change / clients_change / rating_change / requests_change — trend %
     */
    suspend fun fetchStats(): LawyerStatsDto? {
        return try {
            val response = RetrofitClient.haqApi.getLawyerStats()
            if (response.isSuccessful && response.body()?.success == true) {
                return response.body()?.data
            }
            RetrofitClient.mockApi.getLawyerStats()
                .takeIf { it.isSuccessful }?.body()
        } catch (_: Exception) {
            try {
                RetrofitClient.mockApi.getLawyerStats()
                    .takeIf { it.isSuccessful }?.body()
            } catch (_: Exception) { null }
        }
    }

    // ── Revenue chart ─────────────────────────────────────────────────────────

    /**
     * Provides 6-month revenue data.
     *
     * Priority:
     *  1. [statsEmbedded] — data already parsed from GET /api/lawyers/me/stats
     *     (preferred: single network round-trip)
     *  2. GET /api/lawyers/me/revenue/monthly — dedicated endpoint if stats
     *     didn't include the embedded array
     */
    suspend fun fetchRevenueMonthly(
        statsEmbedded: List<RevenueMonthDto> = emptyList()
    ): List<RevenueMonthDto> {
        // Use what's already embedded in the stats response if available
        if (statsEmbedded.isNotEmpty()) return statsEmbedded
        return try {
            val response = RetrofitClient.haqApi.getLawyerRevenueMonthly()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data ?: emptyList()
            } else emptyList()
        } catch (_: Exception) { emptyList() }
    }

    // ── Recent consultations ──────────────────────────────────────────────────

    /**
     * Fetches recent consultations.
     *
     * Priority chain:
     *  1. GET /api/avocat/consultations/recent      (real API, primary)
     *  2. GET /api/lawyers/me/consultations/recent  (real API, legacy fallback)
     *  3. GET /api/avocat/consultations/recent      (mockApi — for Mockable.io dev mode)
     *
     * In mock mode (USE_MOCK_SERVER = true) the primary/fallback real endpoints will
     * not be configured on Mockable.io, so the mock path is tried next; if that also
     * returns a non-2xx (endpoint not yet configured) we return an empty list rather
     * than throwing — the dashboard shows the "Aucune consultation" empty state
     * instead of the network-error banner.
     *
     * In production mode (USE_MOCK_SERVER = false) a complete failure still throws
     * so the ViewModel surfaces the "Impossible de charger" error with Réessayer.
     */
    suspend fun fetchRecentConsultations(): List<RecentConsultationDto> {
        // ── Localhost safety check ────────────────────────────────────────────
        if (RetrofitClient.BASE_URL.contains("localhost")) {
            Log.e("GivenX-API",
                "⚠ BASE_URL contains 'localhost' — Android Emulator cannot reach the host " +
                "via 'localhost'. Set LOCAL_BASE_URL = \"http://10.0.2.2:3000/\" in RetrofitClient.")
        }

        val primaryUrl  = "${RetrofitClient.BASE_URL}api/avocat/consultations/recent"
        val fallbackUrl = "${RetrofitClient.BASE_URL}api/lawyers/me/consultations/recent"

        Log.d("GivenX-API", "[Consultations] mode=${if (RetrofitClient.isMockMode) "MOCK" else "REAL"}")
        Log.d("GivenX-API", "[Consultations] → GET $primaryUrl")

        // ── 1. Primary real endpoint ──────────────────────────────────────────
        try {
            val primary = RetrofitClient.haqApi.getAvocatConsultationsRecent()
            if (primary.isSuccessful && primary.body()?.success == true) {
                Log.d("GivenX-API", "[Consultations] ✓ primary ${primary.code()} (${primary.body()?.data?.size ?: 0} items)")
                return primary.body()?.data ?: emptyList()
            }
            Log.w("GivenX-API", "[Consultations] primary failed (${primary.code()}) → trying legacy fallback")
        } catch (e: Exception) {
            Log.w("GivenX-API", "[Consultations] primary threw: ${e.message}")
        }

        // ── 2. Legacy fallback real endpoint ──────────────────────────────────
        Log.d("GivenX-API", "[Consultations] → GET $fallbackUrl")
        try {
            val fallback = RetrofitClient.haqApi.getRecentConsultations()
            if (fallback.isSuccessful && fallback.body()?.success == true) {
                Log.d("GivenX-API", "[Consultations] ✓ fallback ${fallback.code()} (${fallback.body()?.data?.size ?: 0} items)")
                return fallback.body()?.data ?: emptyList()
            }
            Log.w("GivenX-API", "[Consultations] fallback failed (${fallback.code()})")
        } catch (e: Exception) {
            Log.w("GivenX-API", "[Consultations] fallback threw: ${e.message}")
        }

        // ── 3. Mock API path (dev/staging mode only) ─────────────────────────
        if (RetrofitClient.isMockMode) {
            Log.d("GivenX-API", "[Consultations] mock mode → trying mockApi path")
            try {
                val mock = RetrofitClient.mockApi.getRecentConsultations()
                if (mock.isSuccessful) {
                    val items = mock.body() ?: emptyList()
                    Log.d("GivenX-API", "[Consultations] ✓ mock ${mock.code()} (${items.size} items)")
                    return items
                }
                Log.w("GivenX-API", "[Consultations] mock returned ${mock.code()} — endpoint not configured on Mockable.io")
            } catch (e: Exception) {
                Log.w("GivenX-API", "[Consultations] mock threw: ${e.message}")
            }
            // Mock mode — endpoint not configured yet → show empty state, not error
            Log.d("GivenX-API", "[Consultations] mock mode graceful fallback → returning emptyList()")
            return emptyList()
        }

        // ── All production paths failed → throw so ViewModel shows Réessayer ─
        val msg = "All consultation endpoints failed for BASE_URL=$primaryUrl"
        Log.e("GivenX-API", "[Consultations] ✗ $msg")
        throw Exception(msg)
    }
}
