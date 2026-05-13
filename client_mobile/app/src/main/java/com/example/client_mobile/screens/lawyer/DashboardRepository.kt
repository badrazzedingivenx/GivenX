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
                response.body()?.data
            } else {
                Log.e("DashboardRepo", "fetchProfile HTTP ${response.code()} — ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("DashboardRepo", "fetchProfile threw: ${e.message}", e)
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
                response.body()?.data
            } else {
                Log.e("DashboardRepo", "fetchStats HTTP ${response.code()} — ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("DashboardRepo", "fetchStats threw: ${e.message}", e)
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
            } else {
                Log.e("DashboardRepo", "fetchRevenueMonthly HTTP ${response.code()} — ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("DashboardRepo", "fetchRevenueMonthly threw: ${e.message}", e)
            emptyList()
        }
    }

    // ── Recent consultations ──────────────────────────────────────────────────

    /**
     * GET /appointments
     * Returns the most recent appointments for the authenticated lawyer.
     * The server paginates via { "data": { "appointments": [...], "pagination": {...} } }.
     */
    suspend fun fetchRecentConsultations(): List<RecentConsultationDto> {
        return try {
            val response = RetrofitClient.haqApi.getAppointments(limit = 10, sortBy = "date")
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.appointments ?: emptyList()
            } else {
                Log.e("DashboardRepo", "fetchConsultations HTTP ${response.code()} — ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("DashboardRepo", "fetchConsultations threw: ${e.message}", e)
            emptyList()
        }
    }
}
