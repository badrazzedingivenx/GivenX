package com.example.client_mobile.network

import com.example.client_mobile.network.dto.LawyerDto
import com.example.client_mobile.screens.shared.LawyerItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository that fetches lawyers from the REST API and maps them to the
 * UI domain model [LawyerItem].
 *
 * Drop-in replacement for LawyerService.getLawyersFlow() — the ViewModel
 * only needs to switch which flow it collects.
 */
object LawyerApiRepository {

    // ── Mapping ───────────────────────────────────────────────────────────────

    private fun LawyerDto.toItem() = LawyerItem(
        id          = id,
        name        = name,
        specialty   = specialty,
        city        = location,
        rating      = rating,
        reviewCount = reviewCount,
        yearsExp    = experience,
        bio         = bio,
        isVerified  = isVerified,
        domaine     = domaine.ifBlank { specialty }
    )

    // ── Polling flow (replaces Firestore real-time listener) ─────────────────

    /**
     * Emits the full lawyer list immediately, then re-polls every [pollInterval]ms.
     * null = loading (first emission not yet received).
     *
     * Use this in the ViewModel exactly like LawyerService.getLawyersFlow():
     *   LawyerApiRepository.getLawyersFlow().collect { ... }
     */
    fun getLawyersFlow(pollIntervalMs: Long = 30_000L): Flow<List<LawyerItem>> = flow {
        while (true) {
            try {
                // Use haqApi as the primary source of truth
                val response = RetrofitClient.haqApi.getLawyers()
                if (response.isSuccessful) {
                    val list = response.body()?.data?.map { it.toItem() } ?: emptyList()
                    emit(list)
                }
            } catch (e: Exception) {
                emit(emptyList()) 
            }
            delay(pollIntervalMs)
        }
    }

    // ── Single-shot fetch ─────────────────────────────────────────────────────

    /** Fetches one page of lawyers. Returns an empty list on failure. */
    suspend fun getLawyers(): List<LawyerItem> = try {
        val response = RetrofitClient.haqApi.getLawyers()
        if (response.isSuccessful) {
            response.body()?.data?.map { it.toItem() } ?: emptyList()
        } else {
            emptyList()
        }
    } catch (_: Exception) {
        emptyList()
    }

    /** Returns a single lawyer by ID, or null on failure. */
    suspend fun getLawyerById(id: String): LawyerItem? = try {
        val response = RetrofitClient.haqApi.getLawyerById(id)
        if (response.isSuccessful) response.body()?.data?.toItem() else null
    } catch (_: Exception) {
        null
    }
}
