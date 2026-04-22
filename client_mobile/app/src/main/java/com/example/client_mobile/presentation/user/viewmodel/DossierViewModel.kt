package com.example.client_mobile.presentation.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.data.repository.DossierApiRepository
import com.example.client_mobile.core.utils.TokenManager
import com.example.client_mobile.core.utils.DossierData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DossierViewModel : ViewModel() {

    /** null = loading, empty list = none found / unauthenticated */
    private val _dossiers = MutableStateFlow<List<DossierData>?>(null)
    val dossiers: StateFlow<List<DossierData>?> = _dossiers

    init {
        fetchDossiers()
    }

    fun fetchDossiers() {
        _dossiers.value = null   // triggers loading skeleton in the UI
        viewModelScope.launch {
            try {
                val userId = TokenManager.getUserId() ?: ""  // set after login
                _dossiers.value = DossierApiRepository.getDossiersForCurrentUser(userId)
            } catch (e: Exception) {
                _dossiers.value = emptyList()
            }
        }
    }
}
