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
 * Standardized for the production-like Node.js/Express backend.
 */
class UserDashboardViewModel : ViewModel() {

    private val _firstName = MutableStateFlow<String?>(null)
    val firstName: StateFlow<String?> = _firstName

    private val _dossiers = MutableStateFlow<List<DossierData>?>(null)
    val dossiers: StateFlow<List<DossierData>?> = _dossiers

    private val _appointments = MutableStateFlow<List<AppointmentDto>?>(null)
    val appointments: StateFlow<List<AppointmentDto>?> = _appointments

    private val _billing = MutableStateFlow<BillingSummaryDto?>(null)
    val billing: StateFlow<BillingSummaryDto?> = _billing

    private val _stories = MutableStateFlow<List<StoryDto>?>(null)
    val stories: StateFlow<List<StoryDto>?> = _stories

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun clearError() { _errorMessage.value = null }

    init {
        fetch()
    }

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

    private suspend fun fetchUser() {
        if (_firstName.value == null) {
            val cachedName = TokenManager.getFullName()
            if (cachedName.isNotBlank()) {
                _firstName.value = cachedName.split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: cachedName
            }
        }
        try {
            val response = RetrofitClient.haqApi.getUserProfile()
            if (response.isSuccessful) {
                val dto = response.body()?.data
                val fullName = dto?.effectiveFullName() ?: ""
                _firstName.value = fullName.split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: fullName
                if (dto != null) {
                    UserSession.name = fullName
                    TokenManager.saveFullName(fullName)
                }
            }
        } catch (_: Exception) {}
    }

    private suspend fun fetchDossiers() {
        try {
            val response = RetrofitClient.haqApi.getMyDossiers()
            _dossiers.value = if (response.isSuccessful) {
                response.body()?.data?.map { it.toDomain() } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            _dossiers.value = emptyList()
        }
    }

    private suspend fun fetchAppointments() {
        try {
            val response = RetrofitClient.haqApi.getMyAppointments()
            _appointments.value = if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            _appointments.value = emptyList()
        }
    }

    private suspend fun fetchBilling() {
        try {
            val response = RetrofitClient.haqApi.getMyBilling()
            _billing.value = if (response.isSuccessful) {
                response.body()?.data ?: BillingSummaryDto()
            } else {
                BillingSummaryDto()
            }
        } catch (_: Exception) {
            _billing.value = BillingSummaryDto()
        }
    }

    private suspend fun fetchStories() {
        try {
            val response = RetrofitClient.haqApi.getStories()
            _stories.value = if (response.isSuccessful) {
                response.body()?.data ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            _stories.value = emptyList()
        }
    }

    private suspend fun fetchNotifications() {
        try {
            val response = RetrofitClient.haqApi.getNotifications()
            if (response.isSuccessful) {
                val items = response.body()?.data?.map { dto ->
                    val type = try {
                        com.example.client_mobile.screens.shared.NotificationType.valueOf(dto.type.uppercase())
                    } catch (_: Exception) {
                        com.example.client_mobile.screens.shared.NotificationType.CASE_UPDATE
                    }
                    com.example.client_mobile.screens.shared.AppNotification(
                        id      = dto.id,
                        title   = dto.title,
                        message = dto.description,
                        type    = type,
                        isRead  = dto.isRead,
                        time    = dto.time
                    )
                } ?: return
                NotificationRepository.userNotifications.clear()
                NotificationRepository.userNotifications.addAll(items)
            }
        } catch (_: Exception) {}
    }

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
