package com.example.client_mobile.presentation.user.screens

import com.example.client_mobile.presentation.common.screens.*
import com.example.client_mobile.presentation.common.viewmodel.*
import com.example.client_mobile.presentation.common.components.*
import com.example.client_mobile.presentation.common.repositories.UserSession

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
import com.example.client_mobile.presentation.common.screens.*
import com.example.client_mobile.presentation.common.viewmodel.*
import com.example.client_mobile.presentation.auth.screens.*
import com.example.client_mobile.data.model.dto.PaymentDto
import com.example.client_mobile.data.model.dto.PaymentSummary

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

    BaseScreen(
        title = "PAIEMENTS",
        onBack = onBack
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
                    PaymentContent(state.payments, state.summary)
                }
            }
        }
    }
}

@Composable
fun PaymentContent(payments: List<PaymentDto>, summary: PaymentSummary) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(4.dp)) }
        item {
            PaymentSummarySection(summary)
        }

        item {
            SectionHeader(title = "Historique des transactions")
        }

        if (payments.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Aucun paiement trouvé", style = MaterialTheme.typography.bodyMedium, color = AppDarkGreen.copy(alpha = 0.5f))
                }
            }
        } else {
            items(payments) { payment ->
                PaymentItem(payment)
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
fun PaymentSummarySection(summary: PaymentSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            label = "Total Payé",
            amount = summary.totalPaid,
            containerColor = StatusGreenBg,
            contentColor = StatusGreen,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = "En attente",
            amount = summary.pendingAmount,
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
