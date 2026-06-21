package com.example.client_mobile.screens.lawyer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.network.dto.ReservationDto
import com.example.client_mobile.network.dto.ReservationMessageDto
import com.example.client_mobile.network.dto.SendReservationMessageRequest
import com.example.client_mobile.screens.shared.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationChatScreen(
    reservationId: String,
    onBack: () -> Unit = {}
) {
    val messages = remember { mutableStateListOf<ReservationMessageDto>() }
    var messageText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var reservation by remember { mutableStateOf<ReservationDto?>(null) }
    var paymentError by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val isLawyer = TokenManager.getUserType() == "lawyer"
    val myId = if (isLawyer) TokenManager.getLawyerId().toString() else TokenManager.getClientId().toString()

    fun computeReceiverId(): String {
        val res = reservation ?: return ""
        return if (isLawyer) res.clientId else res.lawyerId
    }

    fun loadMessages() {
        scope.launch {
            isLoading = true
            error = null
            try {
                val msgResponse = RetrofitClient.messageApi.getReservationMessages(reservationId)
                if (msgResponse.isSuccessful && msgResponse.body()?.success == true) {
                    messages.clear()
                    messages.addAll(msgResponse.body()?.data ?: emptyList())
                    Log.d("ReservationChat", "Loaded ${messages.size} messages for reservation $reservationId")
                } else {
                    error = msgResponse.body()?.errorMessage() ?: "Erreur de chargement"
                }
                val resResponse = RetrofitClient.reservationApi.getReservations()
                val allRes = resResponse.body()?.data ?: emptyList()
                reservation = allRes.find { it.id == reservationId }
                val res = reservation
                if (res != null) {
                    Log.d("ReservationChat", "Loaded reservation: status=${res.status}, paymentStatus=${res.paymentStatus}, clientId=${res.clientId}, lawyerId=${res.lawyerId}")
                    if (res.status != "accepted") {
                        paymentError = "Reservation non acceptee"
                    } else if (res.paymentStatus != "paid") {
                        paymentError = "Paiement requis pour envoyer des messages"
                    } else {
                        paymentError = null
                    }
                }
            } catch (e: Exception) {
                error = e.message ?: "Erreur reseau"
            }
            isLoading = false
        }
    }

    LaunchedEffect(reservationId) {
        loadMessages()
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    AppScaffold(
        topBar = {
            StandardTopBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                "Reservation",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(
                                "#$reservationId",
                                fontFamily = FontFamily.Serif,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.55f)
                            )
                        }
                    }
                },
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AppCreamBg)
        ) {
            if (isLoading) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppDarkGreen, strokeWidth = 2.5.dp)
                }
            } else if (error != null) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = AppDarkGreen.copy(alpha = 0.40f), modifier = Modifier.size(48.dp))
                        Text(error!!, fontFamily = FontFamily.Serif, fontSize = 14.sp, color = AppDarkGreen.copy(alpha = 0.60f))
                        OutlinedButton(onClick = { loadMessages() }) {
                            Text("Reessayer", fontFamily = FontFamily.Serif)
                        }
                    }
                }
            } else if (messages.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = AppDarkGreen.copy(alpha = 0.25f), modifier = Modifier.size(48.dp))
                        Text("Aucun message", fontFamily = FontFamily.Serif, fontSize = 14.sp, color = AppDarkGreen.copy(alpha = 0.50f))
                        Text("Envoyez votre premier message", fontFamily = FontFamily.Serif, fontSize = 12.sp, color = AppDarkGreen.copy(alpha = 0.35f))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        val isMine = msg.senderId == myId
                        val bgColor = if (isMine) AppDarkGreen else Color.White
                        val textColor = if (isMine) Color.White else AppDarkGreen
                        val bubbleShape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isMine) 18.dp else 4.dp,
                            bottomEnd = if (isMine) 4.dp else 18.dp
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
                        ) {
                            Surface(
                                shape = bubbleShape,
                                color = bgColor,
                                border = BorderStroke(0.5.dp, if (isMine) AppGoldColor.copy(alpha = 0.30f) else AppDarkGreen.copy(alpha = 0.08f)),
                                shadowElevation = 1.dp
                            ) {
                                Text(
                                    msg.content,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 14.sp,
                                    color = textColor,
                                    lineHeight = 20.sp
                                )
                            }
                            if (msg.timestamp.isNotBlank()) {
                                Text(
                                    msg.timestamp.takeLast(8).take(5),
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 9.sp,
                                    color = AppDarkGreen.copy(alpha = 0.30f),
                                    modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (paymentError != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF3E0)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(18.dp))
                        Text(
                            paymentError!!,
                            fontFamily = FontFamily.Serif,
                            fontSize = 13.sp,
                            color = Color(0xFFE65100),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.08f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = {
                        Text("Votre message...", fontFamily = FontFamily.Serif, fontSize = 14.sp, color = AppDarkGreen.copy(alpha = 0.35f))
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppDarkGreen.copy(alpha = 0.30f),
                        unfocusedBorderColor = AppDarkGreen.copy(alpha = 0.12f),
                        cursorColor = AppDarkGreen
                    ),
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Serif, fontSize = 14.sp, color = AppDarkGreen),
                    maxLines = 3,
                    enabled = paymentError == null
                )
                FilledIconButton(
                    onClick = {
                        val text = messageText.trim()
                        if (text.isBlank()) return@FilledIconButton
                        messageText = ""
                        scope.launch {
                            try {
                                val receiverId = computeReceiverId()
                                Log.d("ReservationChat", "Sending message: senderId=$myId, receiverId=$receiverId, reservationId=$reservationId, content=$text")
                                RetrofitClient.messageApi.sendReservationMessage(
                                    SendReservationMessageRequest(
                                        senderId      = myId,
                                        receiverId    = receiverId,
                                        reservationId = reservationId,
                                        content       = text,
                                        timestamp     = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
                                    )
                                )
                                loadMessages()
                            } catch (_: Exception) {}
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = if (paymentError == null) AppDarkGreen else Color.Gray),
                    enabled = paymentError == null
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Envoyer", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
