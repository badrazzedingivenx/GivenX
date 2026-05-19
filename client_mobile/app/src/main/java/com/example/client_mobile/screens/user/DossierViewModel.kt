package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.DossierApiRepository
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.services.DossierData
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
