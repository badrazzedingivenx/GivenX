package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.dto.AppointmentDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppointmentViewModel : ViewModel() {

    /** null = still loading; empty list = loaded but no appointments */
    private val _appointments = MutableStateFlow<List<AppointmentDto>?>(null)
    val appointments: StateFlow<List<AppointmentDto>?> = _appointments

    init {
        fetch()
    }

    fun fetch() {
        viewModelScope.launch {
            _appointments.value = null  // trigger loading state
            try {
                val response = RetrofitClient.mockApi.getAppointments()
                _appointments.value = if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                _appointments.value = emptyList()
            }
        }
    }
}
