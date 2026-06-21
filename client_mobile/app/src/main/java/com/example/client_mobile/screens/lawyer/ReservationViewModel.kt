package com.example.client_mobile.screens.lawyer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.repository.ReservationRepository
import com.example.client_mobile.repository.Result
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.network.dto.ReservationDto
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReservationUiState(
    val reservations: List<ReservationDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val actionSuccess: Boolean = false
)

class ReservationViewModel : ViewModel() {

    private val repository = ReservationRepository()
    private var autoRefreshJob: Job? = null

    private val _uiState = MutableStateFlow(ReservationUiState())
    val uiState: StateFlow<ReservationUiState> = _uiState.asStateFlow()

    fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (true) {
                loadLawyerReservations()
                delay(10_000L)
            }
        }
    }

    fun stopAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    fun loadLawyerReservations() {
        val rawLawyerId = TokenManager.getLawyerId()
        Log.d("ReservationVM", "loadLawyerReservations: rawLawyerId=$rawLawyerId")
        val lawyerId = rawLawyerId.toString()
        if (lawyerId.isBlank() || lawyerId == "0" || lawyerId == "-1") {
            Log.w("ReservationVM", "loadLawyerReservations: invalid lawyerId=$lawyerId, skipping")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            Log.d("ReservationVM", "Fetching reservations for lawyerId=$lawyerId")
            when (val result = repository.getLawyerReservations(lawyerId)) {
                is Result.Success -> {
                    Log.d("ReservationVM", "Loaded ${result.data.size} reservations")
                    _uiState.value = _uiState.value.copy(
                        reservations = result.data, isLoading = false
                    )
                }
                is Result.Error -> {
                    Log.w("ReservationVM", "Failed: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        error = result.message, isLoading = false
                    )
                }
            }
        }
    }

    fun loadClientReservations(clientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.getClientReservations(clientId)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    reservations = result.data, isLoading = false
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    error = result.message, isLoading = false
                )
            }
        }
    }

    fun acceptReservation(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(error = null, actionSuccess = false)
            when (repository.updateReservationStatus(id, "accepted")) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(actionSuccess = true)
                    loadLawyerReservations()
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    error = "Erreur lors de l'acceptation"
                )
            }
        }
    }

    fun rejectReservation(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(error = null, actionSuccess = false)
            when (repository.updateReservationStatus(id, "rejected")) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(actionSuccess = true)
                    loadLawyerReservations()
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    error = "Erreur lors du refus"
                )
            }
        }
    }

    fun payReservation(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(error = null, actionSuccess = false)
            when (repository.payReservation(id)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(actionSuccess = true)
                    loadLawyerReservations()
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    error = "Erreur lors du paiement"
                )
            }
        }
    }

    fun canSendMessage(reservation: ReservationDto): Boolean {
        return reservation.status == "accepted" && reservation.paymentStatus == "paid"
    }
}
