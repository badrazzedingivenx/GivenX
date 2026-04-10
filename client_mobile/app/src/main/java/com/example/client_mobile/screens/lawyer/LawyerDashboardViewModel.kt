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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun clearError() { _errorMessage.value = null }

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

    /**
     * Updates the authenticated lawyer's profile on the server.
     * Fires [HaqApiService.updateLawyerProfile].
     */
    fun updateProfile(updatedProfile: LawyerProfileDto) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.haqApi.updateLawyerProfile(updatedProfile)
                if (response.isSuccessful && response.body()?.success == true) {
                    _profile.value = response.body()?.data
                } else {
                    _errorMessage.value = "Erreur lors de la mise à jour."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erreur réseau : ${e.localizedMessage}"
            }
        }
    }

    // ── Private fetchers ──────────────────────────────────────────────────────

    private suspend fun fetchProfile() {
        try {
            // Priority: Real API
            val response = RetrofitClient.haqApi.getLawyerProfile()
            if (response.isSuccessful && response.body()?.success == true) {
                _profile.value = response.body()?.data
                return
            }
            // Fallback: Mock API
            val mockResponse = RetrofitClient.mockApi.getLawyerProfile()
            if (mockResponse.isSuccessful) {
                _profile.value = mockResponse.body()
            }
        } catch (_: Exception) {
            // Final attempt: Mock API (in case HaqApi threw network error)
            try {
                val mockResponse = RetrofitClient.mockApi.getLawyerProfile()
                if (mockResponse.isSuccessful) {
                    _profile.value = mockResponse.body()
                } else {
                    _errorMessage.value = "Erreur réseau. Vérifiez votre connexion."
                }
            } catch (_: Exception) {
                _errorMessage.value = "Erreur réseau. Vérifiez votre connexion."
            }
        }
    }

    private suspend fun fetchStats() {
        try {
            // Priority: Real API
            val response = RetrofitClient.haqApi.getLawyerStats()
            if (response.isSuccessful && response.body()?.success == true) {
                _stats.value = response.body()?.data
                return
            }
            // Fallback: Mock API
            val mockResponse = RetrofitClient.mockApi.getLawyerStats()
            if (mockResponse.isSuccessful) {
                _stats.value = mockResponse.body()
            }
        } catch (_: Exception) {
            // Final attempt: Mock API
            try {
                val mockResponse = RetrofitClient.mockApi.getLawyerStats()
                if (mockResponse.isSuccessful) {
                    _stats.value = mockResponse.body()
                } else {
                    _errorMessage.value = "Erreur réseau. Vérifiez votre connexion."
                }
            } catch (_: Exception) {
                _errorMessage.value = "Erreur réseau. Vérifiez votre connexion."
            }
        }
    }
}
