package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.network.dto.AppointmentDto
import com.example.client_mobile.network.dto.BillingSummaryDto
import com.example.client_mobile.network.dto.DossierDto
import com.example.client_mobile.network.dto.StoryDto
import com.example.client_mobile.screens.shared.NotificationRepository
import com.example.client_mobile.screens.shared.UserSession
import com.example.client_mobile.services.DossierData
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Consolidates all data needed by the UserDashboard into a single ViewModel.
 * A single [fetch] call fires all 5 API requests in parallel.
 */
class UserDashboardViewModel : ViewModel() {

    /** null = loading, blank = authenticated but no name */
    private val _firstName = MutableStateFlow<String?>(null)
    val firstName: StateFlow<String?> = _firstName

    /** null = loading, empty list = none found */
    private val _dossiers = MutableStateFlow<List<DossierData>?>(null)
    val dossiers: StateFlow<List<DossierData>?> = _dossiers

    /** null = loading, empty list = none found */
    private val _appointments = MutableStateFlow<List<AppointmentDto>?>(null)
    val appointments: StateFlow<List<AppointmentDto>?> = _appointments

    /** null = loading */
    private val _billing = MutableStateFlow<BillingSummaryDto?>(null)
    val billing: StateFlow<BillingSummaryDto?> = _billing

    /** null = loading, empty list = none available */
    private val _stories = MutableStateFlow<List<StoryDto>?>(null)
    val stories: StateFlow<List<StoryDto>?> = _stories

    /** true while any network request is in flight */
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun clearError() { _errorMessage.value = null }

    init {
        fetch()
    }

    /** Refresh all dashboard data in parallel. */
    fun fetch() {
        viewModelScope.launch {
            if (!TokenManager.isLoggedIn()) {
                _isRefreshing.value = false
                return@launch
            }
            _isRefreshing.value = true
            val userJob    = async { fetchUser() }
            val dossierJob = async { fetchDossiers() }
            val appointJob = async { fetchAppointments() }
            val billingJob = async { fetchBilling() }
            val storiesJob = async { fetchStories() }
            val notifJob   = async { fetchNotifications() }
            userJob.await()
            dossierJob.await()
            appointJob.await()
            billingJob.await()
            storiesJob.await()
            notifJob.await()
            _isRefreshing.value = false
        }
    }

    // ── Private fetchers ──────────────────────────────────────────────────────

    private suspend fun fetchUser() {
        // Seed immediately from login cache so the greeting renders before network
        if (_firstName.value == null) {
            val cachedName = TokenManager.getFullName()
            if (cachedName.isNotBlank()) {
                _firstName.value = cachedName.split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: cachedName
            }
        }
        try {
            val response = RetrofitClient.mockApi.getMe()
            if (response.isSuccessful) {
                val dto = response.body()
                val fullName = dto?.effectiveFullName() ?: ""
                _firstName.value = fullName.split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: fullName
                // Sync into UserSession for backward-compatible screens (e.g. LawyerChatScreen)
                if (dto != null) {
                    if (fullName.isNotBlank())    UserSession.name    = fullName
                    if (dto.email.isNotBlank())   UserSession.email   = dto.email
                    if (dto.phone.isNotBlank())   UserSession.phone   = dto.phone
                    if (dto.address.isNotBlank()) UserSession.address = dto.address
                    val avatar = dto.effectiveAvatarUrl()
                    if (avatar.isNotBlank()) UserSession.avatarUrl = avatar
                    // Refresh TokenManager with latest values
                    if (fullName.isNotBlank())    TokenManager.saveFullName(fullName)
                    if (avatar.isNotBlank())      TokenManager.saveAvatarUrl(avatar)
                    if (dto.city.isNotBlank())    TokenManager.saveCity(dto.city)
                }
            } else {
                if (_firstName.value == null) _firstName.value = ""
            }
        } catch (_: Exception) {
            if (_firstName.value == null) _firstName.value = ""
            _errorMessage.value = "Erreur de chargement. Tirez vers le bas pour réessayer."
        }
    }

    private suspend fun fetchDossiers() {
        _dossiers.value = null
        try {
            val response = RetrofitClient.mockApi.getDossiers()
            _dossiers.value = if (response.isSuccessful) {
                response.body()?.map { it.toDomain() } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            _dossiers.value = emptyList()
            _errorMessage.value = "Erreur de chargement. Tirez vers le bas pour réessayer."
        }
    }

    private suspend fun fetchAppointments() {
        _appointments.value = null
        try {
            val response = RetrofitClient.mockApi.getAppointments()
            _appointments.value = if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            _appointments.value = emptyList()
            _errorMessage.value = "Erreur de chargement. Tirez vers le bas pour réessayer."
        }
    }

    private suspend fun fetchBilling() {
        _billing.value = null
        try {
            val response = RetrofitClient.mockApi.getBilling()
            _billing.value = if (response.isSuccessful) {
                response.body() ?: BillingSummaryDto()
            } else {
                BillingSummaryDto()
            }
        } catch (_: Exception) {
            _billing.value = BillingSummaryDto()
            _errorMessage.value = "Erreur de chargement. Tirez vers le bas pour réessayer."
        }
    }

    private suspend fun fetchStories() {
        try {
            val response = RetrofitClient.mockApi.getStories()
            _stories.value = if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            _stories.value = emptyList()
            _errorMessage.value = "Erreur de chargement. Tirez vers le bas pour réessayer."
        }
    }

    private suspend fun fetchNotifications() {
        try {
            val response = RetrofitClient.mockApi.getNotifications()
            if (response.isSuccessful) {
                val items = response.body()?.map { dto ->
                    val type = when {
                        dto.title.contains("rendez-vous", ignoreCase = true) ->
                            com.example.client_mobile.screens.shared.NotificationType.APPOINTMENT
                        dto.title.contains("message", ignoreCase = true) ->
                            com.example.client_mobile.screens.shared.NotificationType.MESSAGE
                        else ->
                            com.example.client_mobile.screens.shared.NotificationType.CASE_UPDATE
                    }
                    com.example.client_mobile.screens.shared.AppNotification(
                        id      = dto.id,
                        title   = dto.title,
                        message = dto.description,
                        type    = type,
                        isRead  = dto.isRead,
                        time    = if (dto.time.length >= 10) dto.time.substring(0, 10) else dto.time
                    )
                } ?: return
                NotificationRepository.userNotifications.clear()
                NotificationRepository.userNotifications.addAll(items)
            }
        } catch (_: Exception) {
            _errorMessage.value = "Erreur de chargement. Tirez vers le bas pour réessayer."
        }
    }

    // ── Mapping helpers ───────────────────────────────────────────────────────

    private fun DossierDto.toDomain() = DossierData(
        id              = id,
        caseNumber      = caseNumber.ifBlank { id },
        category        = category,
        status          = status,
        openingDate     = openingDate,
        lawyerId        = lawyerId,
        lawyerName      = lawyerName,
        lawyerSpecialty = lawyerSpecialty,
        progress        = progress
    )
}
