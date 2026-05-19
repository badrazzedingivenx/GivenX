package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.dto.BillingSummaryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BillingViewModel : ViewModel() {

    /** null = still loading */
    private val _summary = MutableStateFlow<BillingSummaryDto?>(null)
    val summary: StateFlow<BillingSummaryDto?> = _summary

    init {
        fetch()
    }

    fun fetch() {
        viewModelScope.launch {
            _summary.value = null  // trigger loading state
            try {
                val response = RetrofitClient.haqApi.getMyBilling()
                _summary.value = if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data ?: BillingSummaryDto()
                } else {
                    BillingSummaryDto()
                }
            } catch (e: Exception) {
                _summary.value = BillingSummaryDto()
            }
        }
    }
}
