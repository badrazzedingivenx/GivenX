package com.example.client_mobile.screens.lawyer

import android.util.Log
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.dto.LawyerProfileDto
import com.example.client_mobile.network.dto.LawyerStatsDto
import com.example.client_mobile.network.dto.RecentConsultationDto
import com.example.client_mobile.network.dto.ReservationDto
import com.example.client_mobile.network.dto.RevenueMonthDto
import com.example.client_mobile.repository.ReservationRepository
import com.example.client_mobile.repository.Result

/**
 * Service layer for the Avocat Dashboard.
 *
 * All dashboard data flows through this class, making the ViewModel free of
 * direct Retrofit/network concerns and allowing straightforward unit testing.
 *
 * Priority strategy for every call:
 *   1. Real API  (RetrofitClient.haqApi)
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
                val dto = response.body()?.data
                Log.d("DashboardRepo", "Profile: name=${dto?.fullName}, spec=${dto?.speciality}")
                dto
            } else {
                Log.w("DashboardRepo", "Profile API failed: ${response.code()}")
                null
            }
        } catch (_: Exception) {
            null
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
                val dto = response.body()?.data
                Log.d("DashboardRepo", "Stats: clients=${dto?.activeClients}, revenue=${dto?.totalRevenueMonth}, pending=${dto?.newRequests}")
                dto
            } else {
                Log.w("DashboardRepo", "Stats API failed: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.w("DashboardRepo", "Stats API threw: ${e.message}")
            null
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
     *
     * In production mode a complete failure still throws
     * so the ViewModel surfaces the "Impossible de charger" error with Réessayer.
     */
    suspend fun fetchRecentConsultations(): List<RecentConsultationDto> {
        // ── 1. Primary real endpoint ──────────────────────────────────────────
        try {
            val primary = RetrofitClient.haqApi.getAvocatConsultationsRecent()
            if (primary.isSuccessful && primary.body()?.success == true) {
                val data = primary.body()?.data ?: emptyList()
                Log.d("DashboardRepo", "Consultations: ${data.size} items")
                return data
            }
        } catch (e: Exception) {
            Log.w("GivenX-API", "[Consultations] primary threw: ${e.message}")
        }

        // ── 2. Legacy fallback real endpoint ──────────────────────────────────
        try {
            val fallback = RetrofitClient.haqApi.getRecentConsultations()
            if (fallback.isSuccessful && fallback.body()?.success == true) {
                return fallback.body()?.data ?: emptyList()
            }
        } catch (e: Exception) {
            Log.w("GivenX-API", "[Consultations] fallback threw: ${e.message}")
        }

        return emptyList()
    }

    // ── Lawyer reservations (raw ReservationDto) ──────────────────────────────

    /**
     * Fetches all reservations for the current lawyer.
     * Returns the raw [ReservationDto] objects (not mapped to [RecentConsultationDto]).
     */
    suspend fun fetchLawyerReservations(lawyerId: String): List<ReservationDto> {
        return try {
            val repo = ReservationRepository()
            when (val result = repo.getLawyerReservations(lawyerId)) {
                is Result.Success -> result.data
                is Result.Error -> {
                    Log.w("DashboardRepo", "Lawyer reservations fetch failed: ${result.message}")
                    emptyList()
                }
            }
        } catch (e: Exception) {
            Log.w("DashboardRepo", "Lawyer reservations fetch threw: ${e.message}")
            emptyList()
        }
    }
}
