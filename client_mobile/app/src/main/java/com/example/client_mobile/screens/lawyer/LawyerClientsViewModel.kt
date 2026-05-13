package com.example.client_mobile.screens.lawyer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.DossierApiRepository
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.services.DossierData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Fetches the lawyer's client dossiers from GET /api/dossiers/me.
 * Only lawyers have this page, so the RBAC guard checks the stored role.
 */
class LawyerClientsViewModel : ViewModel() {

    private val _dossiers = MutableStateFlow<List<DossierData>>(emptyList())
    val dossiers: StateFlow<List<DossierData>> = _dossiers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    init { fetch() }

    fun refresh() {
        _isRefreshing.value = true
        fetch(isRefresh = true)
    }

    fun fetch(isRefresh: Boolean = false) {
        if (TokenManager.getUserType() != "lawyer") return
        if (!isRefresh) _isLoading.value = true
        _isError.value = false
        viewModelScope.launch {
            val result = DossierApiRepository.getDossiersForCurrentUser()
            if (result == null) {
                // Null = network/API failure
                if (_dossiers.value.isEmpty()) _isError.value = true
            } else {
                // Non-null (even empty list) = successful response
                _dossiers.value = result
            }
            _isLoading.value = false
            _isRefreshing.value = false
        }
    }
}
