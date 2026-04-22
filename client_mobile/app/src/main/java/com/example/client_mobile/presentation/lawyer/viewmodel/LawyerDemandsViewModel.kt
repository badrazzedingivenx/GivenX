package com.example.client_mobile.presentation.lawyer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.data.repository.DossierApiRepository
import com.example.client_mobile.core.utils.TokenManager
import com.example.client_mobile.core.utils.DossierData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Fetches all dossiers and exposes only the ones with a "pending" status.
 * Accept/Reject actions call PATCH /api/dossiers/{id}/status with optimistic UI updates.
 */
class LawyerDemandsViewModel : ViewModel() {

    /** Statuses considered "awaiting lawyer action". */
    private val PENDING_STATUSES = setOf("Nouveau", "En attente", "Pending", "pending")

    private val _pending = MutableStateFlow<List<DossierData>>(emptyList())
    val pending: StateFlow<List<DossierData>> = _pending

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    /** Non-null when an accept/reject API call fails. Cleared after being read. */
    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError

    init { fetch() }

    fun refresh() {
        _isRefreshing.value = true
        fetch(isRefresh = true)
    }

    fun clearActionError() {
        _actionError.value = null
    }

    private fun fetch(isRefresh: Boolean = false) {
        if (TokenManager.getUserType() != "lawyer") return
        if (!isRefresh) _isLoading.value = true
        _isError.value = false
        viewModelScope.launch {
            val all = DossierApiRepository.getDossiersForCurrentUser()
            _pending.value = all.filter { it.status in PENDING_STATUSES }
            _isLoading.value = false
            _isRefreshing.value = false
        }
    }

    /** Optimistically removes the dossier from the pending list, then calls the API. */
    fun accept(id: String) = updateStatus(id, newStatus = "En cours")

    /** Optimistically removes the dossier from the pending list, then calls the API. */
    fun reject(id: String) = updateStatus(id, newStatus = "Refusé")

    private fun updateStatus(id: String, newStatus: String) {
        val item = _pending.value.find { it.id == id } ?: return
        // Optimistic remove
        _pending.value = _pending.value.filter { it.id != id }
        viewModelScope.launch {
            val ok = DossierApiRepository.updateDossierStatus(id, newStatus)
            if (!ok) {
                // Revert on failure
                _pending.value = (_pending.value + item).sortedBy { it.caseNumber }
                _actionError.value = "Échec de la mise à jour. Veuillez réessayer."
            }
        }
    }
}
