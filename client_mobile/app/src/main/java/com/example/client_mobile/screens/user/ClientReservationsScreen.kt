package com.example.client_mobile.screens.user

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
import android.util.Log
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.network.dto.ReservationDto
import com.example.client_mobile.screens.shared.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientReservationsScreen(
    onBack: () -> Unit = {}
) {
    var reservations by remember { mutableStateOf<List<ReservationDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val clientId = TokenManager.getClientId().toString()

    fun loadReservations() {
        scope.launch {
            isLoading = true
            error = null
            Log.d("ClientReservations", "Fetching reservations for clientId=$clientId")
            try {
                val response = RetrofitClient.reservationApi.getReservations(clientId = clientId)
                if (response.isSuccessful && response.body()?.success == true) {
                    reservations = response.body()?.data ?: emptyList()
                    Log.d("ClientReservations", "Found ${reservations.size} reservations")
                } else {
                    error = response.body()?.errorMessage() ?: "Erreur lors du chargement"
                    Log.e("ClientReservations", "Error: $error")
                }
            } catch (e: Exception) {
                error = e.message ?: "Erreur reseau"
                Log.e("ClientReservations", "Exception: $error")
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadReservations()
    }

    BaseScreen(
        title = "Mes Reservations",
        onBack = onBack
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppDarkGreen, strokeWidth = 2.5.dp)
                }
            }
            error != null -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = AppDarkGreen.copy(alpha = 0.40f), modifier = Modifier.size(48.dp))
                        Text(error!!, fontFamily = FontFamily.Serif, fontSize = 14.sp, color = AppDarkGreen.copy(alpha = 0.60f))
                        OutlinedButton(onClick = { loadReservations() }) {
                            Text("Reessayer", fontFamily = FontFamily.Serif)
                        }
                    }
                }
            }
            reservations.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.EventBusy, contentDescription = null, tint = AppDarkGreen.copy(alpha = 0.30f), modifier = Modifier.size(64.dp))
                        Text("Aucune reservation", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppDarkGreen)
                        Text("Vous n'avez pas encore reserve de consultation.", fontFamily = FontFamily.Serif, fontSize = 13.sp, color = AppDarkGreen.copy(alpha = 0.55f))
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(Modifier.height(4.dp)) }
                    items(reservations, key = { it.id }) { reservation ->
                        ClientReservationCard(reservation = reservation)
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ClientReservationCard(reservation: ReservationDto) {
    val statusColor = when (reservation.status) {
        "pending"  -> Color(0xFF9E9E9E)
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
    val statusIcon = when (reservation.status) {
        "pending"  -> Icons.Default.HourglassEmpty
        "accepted" -> Icons.Default.CheckCircle
        "rejected" -> Icons.Default.Cancel
        else       -> Icons.Default.Info
    }
    val modeLabel = when (reservation.mode) {
        "VIDEO"    -> "Consultation Video"
        "CABINET"  -> "Consultation en Cabinet"
        "MESSAGE"  -> "Message Prioritaire"
        else       -> reservation.mode
    }
    val payLabel = when (reservation.paymentStatus) {
        "paid"   -> "Payee"
        "unpaid" -> "Non payee"
        else     -> reservation.paymentStatus
    }
    val payColor = when (reservation.paymentStatus) {
        "paid"   -> Color(0xFF10B981)
        "unpaid" -> Color(0xFFF59E0B)
        else     -> AppDarkGreen.copy(alpha = 0.45f)
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.10f)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(reservation.lawyerName, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AppDarkGreen)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (reservation.status == "accepted") {
                        Surface(shape = RoundedCornerShape(8.dp), color = payColor.copy(alpha = 0.12f)) {
                            Text(
                                payLabel,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = payColor
                            )
                        }
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = statusColor.copy(alpha = 0.12f)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(14.dp))
                            Text(statusLabel, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = statusColor)
                        }
                    }
                }
            }

            HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.06f))

            InfoRow(icon = Icons.Default.Gavel, label = "Domaine", value = reservation.domain)
            InfoRow(icon = Icons.Default.Description, label = "Besoin", value = reservation.description)
            InfoRow(icon = Icons.Default.Videocam, label = "Mode", value = modeLabel)
            InfoRow(icon = Icons.Default.AttachMoney, label = "Montant", value = "${reservation.price} DH")
            if (reservation.createdAt.isNotBlank()) {
                InfoRow(icon = Icons.Default.CalendarToday, label = "Date", value = reservation.createdAt.take(10))
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
