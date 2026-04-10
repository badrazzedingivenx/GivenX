package com.example.client_mobile.screens.lawyer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.network.dto.LawyerProfileDto
import com.example.client_mobile.network.dto.LawyerStatsDto
import com.google.gson.Gson
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

    /** true after a network failure with no cached data — drives NoConnectionScreen. */
    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun clearError() { _errorMessage.value = null; _isError.value = false }

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
        // ── Step 1: seed from login cache so the screen renders instantly ────
        if (_profile.value == null) {
            val cached = TokenManager.getLawyerJson()
            if (cached != null) {
                try {
                    _profile.value = Gson().fromJson(cached, LawyerProfileDto::class.java)
                } catch (_: Exception) { /* malformed cache — ignore */ }
            }
        }

        // ── Step 2: refresh from network in the background ────────────────────
        try {
            // Priority: Real API (wrapped ApiResponse)
            val response = RetrofitClient.haqApi.getLawyerProfile()
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.data
                if (dto != null) {
                    _profile.value = dto
                    TokenManager.saveLawyerJson(Gson().toJson(dto))
                }
                return
            }
            // Fallback: Mock API (flat JSON)
            val mockResponse = RetrofitClient.mockApi.getLawyerProfile()
            if (mockResponse.isSuccessful) {
                val dto = mockResponse.body()
                if (dto != null) {
                    _profile.value = dto
                    TokenManager.saveLawyerJson(Gson().toJson(dto))
                }
            }
        } catch (_: Exception) {
            try {
                val mockResponse = RetrofitClient.mockApi.getLawyerProfile()
                if (mockResponse.isSuccessful) {
                    val dto = mockResponse.body()
                    if (dto != null) {
                        _profile.value = dto
                        TokenManager.saveLawyerJson(Gson().toJson(dto))
                    }
                } else {
                    if (_profile.value == null) { _isError.value = true; _errorMessage.value = "Erreur r\u00e9seau. V\u00e9rifiez votre connexion." }
                }
            } catch (_: Exception) {
                if (_profile.value == null) { _isError.value = true; _errorMessage.value = "Erreur r\u00e9seau. V\u00e9rifiez votre connexion." }
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
