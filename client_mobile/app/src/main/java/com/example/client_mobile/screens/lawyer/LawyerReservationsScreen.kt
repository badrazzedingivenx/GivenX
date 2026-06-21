package com.example.client_mobile.screens.lawyer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.client_mobile.network.dto.ReservationDto
import com.example.client_mobile.screens.shared.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerReservationsScreen(
    onBack: () -> Unit,
    onChat: ((String) -> Unit)? = null,
    viewModel: ReservationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.startAutoRefresh()
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.stopAutoRefresh() }
    }

    BaseScreen(
        title = "Reservations",
        onBack = onBack
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppDarkGreen, strokeWidth = 2.5.dp)
                }
            }
            uiState.error != null -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = AppDarkGreen.copy(alpha = 0.40f), modifier = Modifier.size(48.dp))
                        Text(uiState.error!!, fontFamily = FontFamily.Serif, fontSize = 14.sp, color = AppDarkGreen.copy(alpha = 0.60f))
                        OutlinedButton(onClick = { viewModel.loadLawyerReservations() }) {
                            Text("Reessayer", fontFamily = FontFamily.Serif)
                        }
                    }
                }
            }
            uiState.reservations.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.EventBusy, contentDescription = null, tint = AppDarkGreen.copy(alpha = 0.30f), modifier = Modifier.size(64.dp))
                        Text("Aucune reservation", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppDarkGreen)
                        Text("Les reservations de vos clients apparaissent ici.", fontFamily = FontFamily.Serif, fontSize = 13.sp, color = AppDarkGreen.copy(alpha = 0.55f))
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(Modifier.height(4.dp)) }
                    items(uiState.reservations, key = { it.id }) { reservation ->
                        ReservationCard(
                            reservation = reservation,
                            onAccept = { viewModel.acceptReservation(reservation.id) },
                            onReject = { viewModel.rejectReservation(reservation.id) },
                            onPay = { viewModel.payReservation(reservation.id) },
                            onChat = onChat
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ReservationCard(
    reservation: ReservationDto,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onPay: () -> Unit,
    onChat: ((String) -> Unit)? = null
) {
    val statusColor = when (reservation.status) {
        "pending"  -> Color(0xFFF59E0B)
        "accepted" -> Color(0xFF10B981)
        "rejected" -> Color(0xFFEF4444)
        else       -> AppDarkGreen.copy(alpha = 0.45f)
    }
    val statusLabel = when (reservation.status) {
        "pending"  -> "En attente"
        "accepted" -> "Acceptee"
        "rejected" -> "Refusee"
        else       -> reservation.status
    }
    val paymentLabel = when (reservation.paymentStatus) {
        "paid"   -> "Payee"
        "unpaid" -> "Non payee"
        else     -> reservation.paymentStatus
    }
    val paymentColor = when (reservation.paymentStatus) {
        "paid"   -> Color(0xFF10B981)
        "unpaid" -> Color(0xFFF59E0B)
        else     -> AppDarkGreen.copy(alpha = 0.45f)
    }
    val modeLabel = when (reservation.mode) {
        "VIDEO"    -> "Consultation Video"
        "CABINET"  -> "Consultation en Cabinet"
        "MESSAGE"  -> "Message Prioritaire"
        else       -> reservation.mode
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.10f)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(reservation.fullName, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AppDarkGreen)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (reservation.status == "accepted") {
                        Surface(shape = RoundedCornerShape(8.dp), color = paymentColor.copy(alpha = 0.12f)) {
                            Text(
                                paymentLabel,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = paymentColor
                            )
                        }
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = statusColor.copy(alpha = 0.12f)) {
                        Text(
                            statusLabel,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = statusColor
                        )
                    }
                }
            }

            HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.06f))

            InfoRow(icon = Icons.Default.Phone, label = "Contact", value = reservation.contact)
            InfoRow(icon = Icons.Default.Gavel, label = "Domaine", value = reservation.domain)
            InfoRow(icon = Icons.Default.Description, label = "Besoin", value = reservation.description)
            InfoRow(icon = Icons.Default.Videocam, label = "Mode", value = modeLabel)
            InfoRow(icon = Icons.Default.AttachMoney, label = "Montant", value = "${reservation.price} DH")
            if (reservation.createdAt.isNotBlank()) {
                InfoRow(icon = Icons.Default.CalendarToday, label = "Date", value = reservation.createdAt.take(10))
            }

            if (reservation.status == "pending") {
                HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.06f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.40f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Refuser", fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppDarkGreen)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Accepter", fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.White)
                    }
                }
            }
            if (reservation.status == "accepted") {
                HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.06f))
                if (reservation.paymentStatus == "paid" && onChat != null) {
                    Button(
                        onClick = { onChat(reservation.id) },
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppDarkGreen)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Contacter le client", fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.White)
                    }
                } else {
                    Button(
                        onClick = onPay,
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppGoldColor)
                    ) {
                        Icon(Icons.Default.AttachMoney, contentDescription = null, tint = AppDarkGreen, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Marquer comme paye", fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = AppDarkGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(15.dp))
        Text(
            text = "$label : $value",
            fontFamily = FontFamily.Serif,
            fontSize = 13.sp,
            color = AppDarkGreen,
            fontWeight = FontWeight.Medium
        )
    }
}
