package com.example.client_mobile.screens.lawyer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.network.dto.LawyerProfileDto
import com.example.client_mobile.network.dto.LawyerStatsDto
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the authenticated lawyer's dashboard and profile screens.
 *
 * Fetches:
 *  - GET /api/lawyers/me       → [profile] (full name, speciality, bar info, etc.)
 *  - GET /api/lawyers/me/stats → [stats]   (Mes Clients, Audiences du jour, Honoraires)
 *
 * Both calls fire in parallel inside [fetch].
 * RBAC guard: returns immediately if the stored role is not "lawyer".
 */
class LawyerDashboardViewModel : ViewModel() {

    /** null = loading, non-null = fetched (may be empty on error) */
    private val _profile = MutableStateFlow<LawyerProfileDto?>(null)
    val profile: StateFlow<LawyerProfileDto?> = _profile

    /** null = loading, non-null = fetched */
    private val _stats = MutableStateFlow<LawyerStatsDto?>(null)
    val stats: StateFlow<LawyerStatsDto?> = _stats

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init { fetch() }

    fun fetch() {
        viewModelScope.launch {
            // RBAC guard: only lawyers may call these endpoints
            if (!TokenManager.isLoggedIn() || TokenManager.getUserType() != "lawyer") {
                _isRefreshing.value = false
                return@launch
            }
            _isRefreshing.value = true
            val profileJob = async { fetchProfile() }
            val statsJob   = async { fetchStats() }
            profileJob.await()
            statsJob.await()
            _isRefreshing.value = false
        }
    }

    // ── Private fetchers ──────────────────────────────────────────────────────

    private suspend fun fetchProfile() {
        try {
            val response = RetrofitClient.mockApi.getLawyerProfile()
            if (response.isSuccessful) {
                _profile.value = response.body()
            }
        } catch (_: Exception) { /* keep existing state */ }
    }

    private suspend fun fetchStats() {
        try {
            val response = RetrofitClient.mockApi.getLawyerStats()
            if (response.isSuccessful) {
                _stats.value = response.body()
            }
        } catch (_: Exception) { /* keep existing state */ }
    }
}
