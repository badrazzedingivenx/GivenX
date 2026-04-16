package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.LawyerApiRepository
import com.example.client_mobile.services.ConsultationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LawyerViewModel : ViewModel() {

    private val _lawyers = MutableStateFlow<List<MatchCard>?>(null)
    val lawyers: StateFlow<List<MatchCard>?> = _lawyers

    init {
        startListening()
    }

    /**
     * Polls the REST API every 30 s via [LawyerApiRepository.getLawyersFlow].
     * _lawyers == null means "loading skeleton"; emptyList means error / no data.
     */
    private fun startListening() {
        _lawyers.value = null
        viewModelScope.launch {
            LawyerApiRepository.getLawyersFlow()
                .catch { _lawyers.value = emptyList() }
                .collect { lawyerItems ->
                    _lawyers.value = lawyerItems.mapIndexed { index, item ->
                        MatchCard(
                            id           = index,
                            firestoreId  = item.id,
                            name         = item.name,
                            specialty    = item.specialty,
                            city         = item.city,
                            rating       = item.rating,
                            yearsExp     = item.yearsExp,
                            isVerified   = item.isVerified,
                            tagline      = "${item.specialty} · ${item.city}",
                            matchPercent = 0
                        )
                    }
                }
        }
    }

    /** Called from the empty-state Reload button. */
    fun fetchLawyers() { startListening() }

    /**
     * Persists a matched lawyer to the consultations endpoint.
     * Fire-and-forget; UI is not blocked.
     */
    fun saveConsultation(card: MatchCard) {
        viewModelScope.launch {
            try {
                ConsultationService.saveConsultation(
                    lawyerFirestoreId = card.firestoreId,
                    lawyerName        = card.name,
                    lawyerSpecialty   = card.specialty
                )
            } catch (_: Exception) { /* silent — non-critical */ }
        }
    }
}

