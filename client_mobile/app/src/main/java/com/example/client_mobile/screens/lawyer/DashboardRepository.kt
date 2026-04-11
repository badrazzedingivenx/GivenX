package com.example.client_mobile.screens.lawyer

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
     * GET /api/lawyers/me/consultations/recent
     *
     * Returns the latest consultations list for the "Consultations récentes" card.
     * Each item includes [RecentConsultationDto.status], which drives the
     * green / amber / red status pill in the UI.
     *
     * Returns an empty list on network failure (non-critical — empty state shown).
     */
    suspend fun fetchRecentConsultations(): List<RecentConsultationDto> {
        return try {
            val response = RetrofitClient.haqApi.getRecentConsultations()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data ?: emptyList()
            } else emptyList()
        } catch (_: Exception) { emptyList() }
    }
}
