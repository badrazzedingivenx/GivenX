package com.example.client_mobile.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.client_mobile.screens.shared.*
import com.example.client_mobile.network.dto.PaymentDto
import com.example.client_mobile.network.dto.PaymentSummary
import com.example.client_mobile.network.dto.ReservationDto
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PaymentScreen(
    onBack: () -> Unit,
    lawyerId: Int = -1,
    viewModel: PaymentViewModel = viewModel(
        key = if (lawyerId != -1) "lawyer_$lawyerId" else "client",
        factory = PaymentViewModelFactory(if (lawyerId != -1) lawyerId else null)
    )
) {
    LaunchedEffect(lawyerId) {
        viewModel.fetchPayments()
    }
    val uiState by viewModel.uiState.collectAsState()
    val totalPaid by viewModel.totalPaid.collectAsState()
    val pendingAmount by viewModel.pendingAmount.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    BaseScreen(
        title = "PAIEMENTS",
        onBack = onBack
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (lawyerId != -1) {
                // Lawyer mode — financial data is reactive from reservation flow
                PaymentContent(
                    reservationTransactions = transactions,
                    totalPaid = totalPaid,
                    pendingAmount = pendingAmount
                )
            } else {
                // Client mode — existing API-based flow
                when (val state = uiState) {
                    is PaymentState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    is PaymentState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(state.message, color = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.height(16.dp))
                                LegalButton(
                                    text = "Réessayer",
                                    onClick = { viewModel.fetchPayments() },
                                    modifier = Modifier.width(200.dp)
                                )
                            }
                        }
                    }
                    is PaymentState.Success -> {
                        PaymentContent(
                            payments = state.payments,
                            totalPaid = state.summary.totalPaid,
                            pendingAmount = state.summary.pendingAmount
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentContent(
    payments: List<PaymentDto> = emptyList(),
    reservationTransactions: List<ReservationDto> = emptyList(),
    totalPaid: String,
    pendingAmount: String
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(4.dp)) }
        item {
            PaymentSummarySection(totalPaid, pendingAmount)
        }

        item {
            SectionHeader(title = "Historique des transactions")
        }

        when {
            reservationTransactions.isNotEmpty() -> {
                items(reservationTransactions, key = { it.id }) { r ->
                    TransactionRow(r)
                }
            }
            payments.isNotEmpty() -> {
                items(payments) { payment ->
                    PaymentItem(payment)
                }
            }
            else -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Aucun paiement trouvé", style = MaterialTheme.typography.bodyMedium, color = AppDarkGreen.copy(alpha = 0.5f))
                    }
                }
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun TransactionRow(reservation: ReservationDto) {
    val paymentLabel = if (reservation.paymentStatus == "paid") "Payé" else "En attente"
    val (chipBg, chipColor) = if (reservation.paymentStatus == "paid")
        StatusGreenBg to StatusGreen
    else
        StatusOrangeBg to StatusOrange

    DashCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AppDarkGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = null,
                    tint = AppDarkGreen
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reservation.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkGreen
                )
                Text(
                    text = formatPaymentDate(reservation.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = AppDarkGreen.copy(alpha = 0.5f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${reservation.price} DH",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkGreen
                )
                com.example.client_mobile.screens.shared.StatusChip(
                    label = paymentLabel,
                    containerColor = chipBg,
                    textColor = chipColor
                )
            }
        }
    }
}

/** Parses ISO 8601 date and returns French short format e.g. "12 Juin". */
private fun formatPaymentDate(isoDate: String): String {
    if (isoDate.isBlank()) return ""
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.FRENCH)
        val date = parser.parse(isoDate)
        val formatter = SimpleDateFormat("d MMM", Locale.FRENCH)
        formatter.format(date!!)
            .replaceFirstChar { if (it.isLowerCase()) it.uppercase() else it.toString() }
    } catch (_: Exception) {
        try {
            val parts = isoDate.take(10).split("-")
            val months = listOf("Jan","Fév","Mar","Avr","Mai","Juin","Juil","Août","Sep","Oct","Nov","Déc")
            "${parts[2].trimStart('0')} ${months.getOrNull(parts[1].toInt() - 1) ?: parts[1]}"
        } catch (_: Exception) { isoDate }
    }
}

@Composable
fun PaymentSummarySection(totalPaid: String, pendingAmount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            label = "Total Payé",
            amount = totalPaid,
            containerColor = StatusGreenBg,
            contentColor = StatusGreen,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = "En attente",
            amount = pendingAmount,
            containerColor = StatusOrangeBg,
            contentColor = StatusOrange,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryCard(
    label: String,
    amount: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    DashCard(
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = 0.8f)
            )
            Text(
                text = amount,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
fun PaymentItem(payment: PaymentDto) {
    DashCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AppDarkGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = null,
                    tint = AppDarkGreen
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payment.subject,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkGreen
                )
                Text(
                    text = "${payment.date} • ${payment.method}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppDarkGreen.copy(alpha = 0.5f)
                )
            }

            // Amount and Status
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = payment.amount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkGreen
                )
                StatusChip(status = payment.status)
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "completed", "reçu" -> StatusGreenBg to StatusGreen
        "pending", "en attente" -> StatusOrangeBg to StatusOrange
        "failed", "échoué" -> StatusRedBg to StatusRed
        else -> StatusGrayBg to StatusGray
    }

    com.example.client_mobile.screens.shared.StatusChip(
        label = status,
        containerColor = backgroundColor,
        textColor = textColor
    )
}
