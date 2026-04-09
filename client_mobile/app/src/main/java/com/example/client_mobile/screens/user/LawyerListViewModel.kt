package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.LawyerApiRepository
import com.example.client_mobile.screens.shared.LawyerItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Real-time ViewModel for the lawyer list and detail screens.
 * Data source: REST API via [LawyerApiRepository] (MySQL backend).
 * To revert to Firestore replace LawyerApiRepository with LawyerService.
 */
class LawyerListViewModel : ViewModel() {

    /** null = loading, emptyList = no data / error */
    private val _lawyers = MutableStateFlow<List<LawyerItem>?>(null)
    val lawyers: StateFlow<List<LawyerItem>?> = _lawyers

    init {
        startListening()
    }

    private fun startListening() {
        _lawyers.value = null
        viewModelScope.launch {
            LawyerApiRepository.getLawyersFlow()
                .catch { _lawyers.value = emptyList() }
                .collect { list -> _lawyers.value = list }
        }
    }

    /** Returns the [LawyerItem] with the given ID, or null. */
    fun findById(id: String): LawyerItem? = _lawyers.value?.firstOrNull { it.id == id }
}
