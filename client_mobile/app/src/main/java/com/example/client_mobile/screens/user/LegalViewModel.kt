package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.LegalRepository
import com.example.client_mobile.network.dto.Consultation
import com.example.client_mobile.network.dto.Profile
import com.example.client_mobile.network.dto.Specialty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State for the Legal screen.
 */
sealed class LegalUiState<out T> {
    object Idle : LegalUiState<Nothing>()
    object Loading : LegalUiState<Nothing>()
    data class Success<T>(val data: T) : LegalUiState<T>()
    data class Error(val message: String) : LegalUiState<Nothing>()
}

/**
 * ViewModel for managing the legal services UI state.
 */
class LegalViewModel(private val repository: LegalRepository) : ViewModel() {

    // ── StateFlows for UI observation ───────────────────────────────────────────

    private val _lawyersState = MutableStateFlow<LegalUiState<List<Profile>>>(LegalUiState.Idle)
    val lawyersState: StateFlow<LegalUiState<List<Profile>>> = _lawyersState.asStateFlow()

    private val _consultationsState = MutableStateFlow<LegalUiState<List<Consultation>>>(LegalUiState.Idle)
    val consultationsState: StateFlow<LegalUiState<List<Consultation>>> = _consultationsState.asStateFlow()

    private val _specialtiesState = MutableStateFlow<LegalUiState<List<Specialty>>>(LegalUiState.Idle)
    val specialtiesState: StateFlow<LegalUiState<List<Specialty>>> = _specialtiesState.asStateFlow()

    private val _createConsultationState = MutableStateFlow<LegalUiState<Consultation>>(LegalUiState.Idle)
    val createConsultationState: StateFlow<LegalUiState<Consultation>> = _createConsultationState.asStateFlow()

    // ── Actions ───────────────────────────────────────────────────────────────

    /**
     * Fetch all profiles with the 'LAWYER' role.
     */
    fun fetchLawyers() {
        viewModelScope.launch {
            _lawyersState.value = LegalUiState.Loading
            repository.getProfiles(role = "LAWYER")
                .onSuccess { lawyers ->
                    _lawyersState.value = LegalUiState.Success(lawyers)
                }
                .onFailure { exception ->
                    _lawyersState.value = LegalUiState.Error(exception.message ?: "Failed to fetch lawyers")
                }
        }
    }

    /**
     * Fetch consultations for a specific client.
     */
    fun fetchConsultationsForClient(clientId: Int) {
        viewModelScope.launch {
            _consultationsState.value = LegalUiState.Loading
            repository.getConsultations(clientId = clientId)
                .onSuccess { consultations ->
                    _consultationsState.value = LegalUiState.Success(consultations)
                }
                .onFailure { exception ->
                    _consultationsState.value = LegalUiState.Error(exception.message ?: "Failed to fetch consultations")
                }
        }
    }

    /**
     * Fetch all legal specialties.
     */
    fun fetchSpecialties() {
        viewModelScope.launch {
            _specialtiesState.value = LegalUiState.Loading
            repository.getSpecialties()
                .onSuccess { specialties ->
                    _specialtiesState.value = LegalUiState.Success(specialties)
                }
                .onFailure { exception ->
                    _specialtiesState.value = LegalUiState.Error(exception.message ?: "Failed to fetch specialties")
                }
        }
    }

    /**
     * Create a new consultation request.
     */
    fun createConsultation(consultation: Consultation) {
        viewModelScope.launch {
            _createConsultationState.value = LegalUiState.Loading
            repository.createConsultation(consultation)
                .onSuccess { created ->
                    _createConsultationState.value = LegalUiState.Success(created)
                }
                .onFailure { exception ->
                    _createConsultationState.value = LegalUiState.Error(exception.message ?: "Failed to create consultation")
                }
        }
    }
}
