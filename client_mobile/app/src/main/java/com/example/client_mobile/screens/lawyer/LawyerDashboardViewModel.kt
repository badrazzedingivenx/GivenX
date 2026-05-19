package com.example.client_mobile.screens.lawyer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.network.dto.LawyerProfileDto
import com.example.client_mobile.network.dto.LawyerStatsDto
import com.example.client_mobile.network.dto.RecentConsultationDto
import com.example.client_mobile.network.dto.RevenueMonthDto
import com.example.client_mobile.screens.shared.LawyerSession
import com.example.client_mobile.screens.shared.NotificationViewModel
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

    /** Service layer — all API calls go through the repository. */
    private val repository = DashboardRepository()

    /** null = loading, non-null = fetched (may be empty on error) */
    private val _profile = MutableStateFlow<LawyerProfileDto?>(null)
    val profile: StateFlow<LawyerProfileDto?> = _profile

    /** null = loading, non-null = fetched */
    private val _stats = MutableStateFlow<LawyerStatsDto?>(null)
    val stats: StateFlow<LawyerStatsDto?> = _stats

    private val _revenueMonthly = MutableStateFlow<List<RevenueMonthDto>>(emptyList())
    val revenueMonthly: StateFlow<List<RevenueMonthDto>> = _revenueMonthly

    private val _recentConsultations = MutableStateFlow<List<RecentConsultationDto>>(emptyList())
    val recentConsultations: StateFlow<List<RecentConsultationDto>> = _recentConsultations

    /** true only when the network call failed AND there is no cached data to show. */
    private val _consultationsError = MutableStateFlow(false)
    val consultationsError: StateFlow<Boolean> = _consultationsError

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    /** true after a network failure with no cached data — drives NoConnectionScreen. */
    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** Retry fetching consultations after a network failure. Called from the 'Réessayer' button. */
    fun retryConsultations() {
        viewModelScope.launch { fetchRecentConsultations() }
    }

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
            // fetchStats() also populates _revenueMonthly from the embedded
            // monthly_revenue array, so no separate revenue job is needed.
            val profileJob       = async { fetchProfile() }
            val statsJob         = async { fetchStats() }
            val consultationsJob = async { fetchRecentConsultations() }
            profileJob.await()
            statsJob.await()
            consultationsJob.await()

            // Trigger notification sync
            NotificationViewModel().fetch()

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
        // Step 1: seed from login cache so the screen renders instantly
        if (_profile.value == null) {
            val cached = TokenManager.getLawyerJson()
            if (cached != null) {
                try {
                    val dto = Gson().fromJson(cached, LawyerProfileDto::class.java)
                    _profile.value = dto
                    syncLawyerSession(dto)
                } catch (_: Exception) { /* malformed cache — ignore */ }
            }
        }

        // Step 2: refresh from network via repository
        val dto = repository.fetchProfile()
        if (dto != null) {
            _profile.value = dto
            syncLawyerSession(dto)
            TokenManager.saveLawyerJson(Gson().toJson(dto))
        } else if (_profile.value == null) {
            _isError.value = true
            _errorMessage.value = "Erreur réseau. Vérifiez votre connexion."
        }
    }

    /** Pushes lawyer profile data into [LawyerSession] so all screens see it immediately. */
    private fun syncLawyerSession(dto: LawyerProfileDto) {
        if (dto.fullName.isNotBlank())    LawyerSession.fullName = dto.fullName
        if (dto.speciality.isNotBlank())  LawyerSession.title    = dto.speciality
        if (dto.email.isNotBlank())       LawyerSession.email    = dto.email
        if (dto.phone.isNotBlank())       LawyerSession.phone    = dto.phone
        if (dto.address.isNotBlank())     LawyerSession.address  = dto.address
        if (dto.bio.isNotBlank())         LawyerSession.bio      = dto.bio
        if (dto.avatarUrl.isNotBlank())   LawyerSession.avatarUrl = dto.avatarUrl
        // Save key fields back to TokenManager for cross-session persistence
        if (dto.fullName.isNotBlank())    TokenManager.saveFullName(dto.fullName)
        if (dto.avatarUrl.isNotBlank())   TokenManager.saveAvatarUrl(dto.avatarUrl)
    }

    private suspend fun fetchStats() {
        val stats = repository.fetchStats()
        _stats.value = stats
        if (stats != null) {
            // The stats response embeds the monthly_revenue array — use it as
            // the primary source so we save a separate network round-trip.
            _revenueMonthly.value = repository.fetchRevenueMonthly(
                statsEmbedded = stats.monthlyRevenue
            )
        }
    }

    private suspend fun fetchRecentConsultations() {
        // GET /api/avocat/consultations/recent (primary) / GET /api/lawyers/me/consultations/recent (fallback)
        // Status values drive the pill color in ConsultationRow:
        //   "terminé" / "completed" → Green pill
        //   "en attente" / "pending" → Gold pill
        //   anything else           → Rose pill
        _consultationsError.value = false
        try {
            _recentConsultations.value = repository.fetchRecentConsultations()
        } catch (e: Exception) {
            Log.e("GivenX-API", "[Consultations] fetch failed: ${e.message}", e)
            // Only surface the error banner if we have no cached data to display
            if (_recentConsultations.value.isEmpty()) {
                _consultationsError.value = true
            }
        }
    }
}
