package com.example.client_mobile.presentation.user.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.data.remote.RetrofitClient
import com.example.client_mobile.core.utils.TokenManager
import com.example.client_mobile.data.model.dto.PaymentDto
import com.example.client_mobile.data.model.dto.PaymentSummary
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PaymentState {
    object Loading : PaymentState()
    data class Success(
        val payments: List<PaymentDto>,
        val summary: PaymentSummary
    ) : PaymentState()
    data class Error(val message: String) : PaymentState()
}

class PaymentViewModel(
    private val lawyerId: Int? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentState>(PaymentState.Loading)
    val uiState: StateFlow<PaymentState> = _uiState

    init {
        fetchPayments()
    }

    fun fetchPayments() {
        _uiState.value = PaymentState.Loading
        viewModelScope.launch {
            try {
                val response = if (lawyerId != null) {
                    Log.d("PaymentDebug", "Requesting payments for lawyerId: $lawyerId")
                    RetrofitClient.haqApi.getPayments(lawyerId = lawyerId)
                } else {
                    val clientId = TokenManager.getClientId()
                    if (clientId == -1) {
                        _uiState.value = PaymentState.Error("Session client introuvable")
                        return@launch
                    }
                    Log.d("PaymentDebug", "Requesting payments for clientId: $clientId")
                    RetrofitClient.haqApi.getPayments(clientId = clientId)
                }
                Log.d("PaymentDebug", "Response URL: ${response.raw().request.url}")

                if (response.isSuccessful && response.body()?.success == true) {
                    val payments = response.body()?.data ?: emptyList()
                    Log.d("PaymentDebug", "Raw response size: ${payments.size}")
                    if (payments.isNotEmpty()) {
                        Log.e("PaymentDebug", "First item: ${payments.first()}")
                    } else {
                        Log.e("PaymentDebug", "Payments list is EMPTY")
                    }
                    val summary = calculateSummary(payments)
                    _uiState.value = PaymentState.Success(payments, summary)
                } else {
                    Log.e("PaymentDebug", "Error response: ${response.code()} - ${response.message()}")
                    _uiState.value = PaymentState.Error("Erreur lors de la récupération des paiements")
                }
            } catch (e: Exception) {
                _uiState.value = PaymentState.Error("Erreur réseau : ${e.localizedMessage}")
            }
        }
    }

    private fun calculateSummary(payments: List<PaymentDto>): PaymentSummary {
        // Simple logic for mock: sum Completed as Paid, Pending as Pending
        // In a real app, this might come from a dedicated summary endpoint
        var paid = 0
        var pending = 0
        
        payments.forEach {
            val amountValue = it.amount.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
            if (it.status.equals("Completed", ignoreCase = true)) {
                paid += amountValue
            } else if (it.status.equals("Pending", ignoreCase = true)) {
                pending += amountValue
            }
        }

        return PaymentSummary(
            totalPaid = "$paid DH",
            pendingAmount = "$pending DH"
        )
    }
}

class PaymentViewModelFactory(private val lawyerId: Int?) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaymentViewModel(lawyerId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
