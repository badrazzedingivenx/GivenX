package com.example.client_mobile.screens.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.network.dto.PaymentDto
import com.example.client_mobile.network.dto.PaymentSummary
import com.example.client_mobile.network.dto.ReservationDto
import com.example.client_mobile.screens.shared.ConsultationRepository
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

/**
 * ViewModel for payment screens.
 *
 * Two modes:
 *  - **Lawyer mode** (`lawyerId != null`): financial summary is computed reactively
 *    from [ConsultationRepository.reservationsFlow] — updates automatically when
 *    the lawyer accepts/rejects a reservation.
 *  - **Client mode** (`lawyerId == null`): fetches payment records from the API
 *    via [HaqApiService.getPayments].
 */
class PaymentViewModel(
    private val lawyerId: Int? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentState>(PaymentState.Loading)
    val uiState: StateFlow<PaymentState> = _uiState

    /** Reactive total paid amount for lawyers (e.g. "1 400 DH"). */
    private val _totalPaid = MutableStateFlow("0 DH")
    val totalPaid: StateFlow<String> = _totalPaid

    /** Reactive pending amount for lawyers (e.g. "150 DH"). */
    private val _pendingAmount = MutableStateFlow("0 DH")
    val pendingAmount: StateFlow<String> = _pendingAmount

    /** Transaction history for lawyers — only [ReservationDto]s with status == "accepted". */
    private val _transactions = MutableStateFlow<List<ReservationDto>>(emptyList())
    val transactions: StateFlow<List<ReservationDto>> = _transactions

    init {
        if (lawyerId != null) {
            observeReservations()
        } else {
            fetchPayments()
        }
    }

    /**
     * For clients — fetches payment records from the API.
     * No-op in lawyer mode (the reservation flow handles that).
     */
    fun fetchPayments() {
        if (lawyerId != null) return // Lawyer mode: data comes from reservation flow
        _uiState.value = PaymentState.Loading
        viewModelScope.launch {
            try {
                val clientId = TokenManager.getClientId()
                if (clientId == -1) {
                    _uiState.value = PaymentState.Error("Session client introuvable")
                    return@launch
                }
                Log.d("PaymentDebug", "Requesting payments for clientId: $clientId")
                val response = RetrofitClient.haqApi.getPayments(clientId = clientId)
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
                    _totalPaid.value = summary.totalPaid
                    _pendingAmount.value = summary.pendingAmount
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

    private fun observeReservations() {
        Log.d("PaymentVM", "Lawyer mode: observing reservation flow (id=$lawyerId)")
        ConsultationRepository.refresh()
        viewModelScope.launch {
            ConsultationRepository.reservationsFlow.collect { reservations ->
                val myReservations = reservations.filter { it.lawyerId == lawyerId.toString() }
                Log.d("PaymentVM", "Reservations updated: ${myReservations.size} for lawyer $lawyerId")
                _transactions.value = myReservations.filter { it.status == "accepted" }
                computeSummary(myReservations)
            }
        }
    }

    /**
     * Summarises reservation amounts per the payment workflow:
     *  - **totalPaid**  = sum of price where status == "accepted" AND paymentStatus == "paid"
     *  - **pendingAmount** = sum of price where status == "accepted" AND paymentStatus == "unpaid"
     */
    private fun computeSummary(reservations: List<ReservationDto>) {
        var paid = 0
        var pending = 0
        reservations.forEach {
            if (it.status == "accepted") {
                when (it.paymentStatus) {
                    "paid"   -> paid   += it.price
                    "unpaid" -> pending += it.price
                }
            }
        }
        _totalPaid.value   = "${formatPrice(paid)} DH"
        _pendingAmount.value = "${formatPrice(pending)} DH"
        Log.d("PaymentVM", "Summary: paid=$paid, pending=$pending")
    }

    /** Formats a price with thousand separators (e.g. 1400 → "1 400"). */
    private fun formatPrice(price: Int): String {
        if (price < 1000) return price.toString()
        val s = price.toString()
        val sb = StringBuilder()
        var count = 0
        for (i in s.lastIndex downTo 0) {
            if (count > 0 && count % 3 == 0) sb.insert(0, ' ')
            sb.insert(0, s[i])
            count++
        }
        return sb.toString()
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

        val fmtPaid    = "${formatPrice(paid)} DH"
        val fmtPending = "${formatPrice(pending)} DH"
        _totalPaid.value   = fmtPaid
        _pendingAmount.value = fmtPending
        return PaymentSummary(totalPaid = fmtPaid, pendingAmount = fmtPending)
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
