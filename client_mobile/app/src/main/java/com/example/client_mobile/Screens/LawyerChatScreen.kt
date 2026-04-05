package com.example.client_mobile.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Lawyer Chat Screen ───────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerChatScreen(
    lawyerId: String = "1",
    currentUserName: String = "Karim Bennani",
    onBack: () -> Unit = {}
) {
    val lawyer = sampleLawyers.find { it.id == lawyerId } ?: sampleLawyers.first()
    val sentMessages = MessageRepository.messages.filter { it.lawyerId == lawyerId }

    var messageText by remember { mutableStateOf("") }
    var showSentSnackbar by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(sentMessages.size) {
        if (sentMessages.isNotEmpty()) {
            listState.animateScrollToItem(sentMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val initials = lawyer.name
                            .removePrefix("Maître ")
                            .split(" ")
                            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                            .take(2)
                            .joinToString("")
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AppDarkGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initials, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AppGoldColor)
                        }
                        Column {
                            Text(
                                lawyer.name,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = AppDarkGreen,
                                maxLines = 1
                            )
                            Text(
                                lawyer.specialty,
                                fontFamily = FontFamily.Serif,
                                fontSize = 11.sp,
                                color = AppGoldColor,
                                maxLines = 1
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = AppDarkGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        DashBoardBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // ── Messages area ─────────────────────────────────────────────
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    if (sentMessages.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.padding(vertical = 60.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ChatBubbleOutline,
                                        contentDescription = null,
                                        tint = AppDarkGreen.copy(alpha = 0.28f),
                                        modifier = Modifier.size(52.dp)
                                    )
                                    Text(
                                        "Aucun message pour le moment",
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 14.sp,
                                        color = AppDarkGreen.copy(alpha = 0.45f),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        "Écrivez à ${lawyer.name} pour commencer.",
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 12.sp,
                                        color = AppDarkGreen.copy(alpha = 0.35f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(sentMessages) { msg ->
                            ChatBubble(message = msg)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // ── Snackbar ──────────────────────────────────────────────────
                if (showSentSnackbar) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF34A853)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text("Message envoyé à ${lawyer.name}", fontFamily = FontFamily.Serif, fontSize = 13.sp, color = Color.White)
                        }
                    }
                }

                // ── Input bar ─────────────────────────────────────────────────
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White.copy(alpha = 0.97f),
                    border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.12f)),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text(
                                    "Écrivez votre message…",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 13.sp,
                                    color = AppDarkGreen.copy(alpha = 0.40f)
                                )
                            },
                            shape = RoundedCornerShape(14.dp),
                            minLines = 1,
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppDarkGreen,
                                unfocusedBorderColor = AppDarkGreen.copy(alpha = 0.22f),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White.copy(alpha = 0.88f),
                                cursorColor = AppDarkGreen
                            )
                        )
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = if (messageText.isNotBlank()) AppDarkGreen else AppDarkGreen.copy(alpha = 0.25f),
                            border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = if (messageText.isNotBlank()) 0.55f else 0.20f))
                        ) {
                            IconButton(
                                onClick = {
                                    val trimmed = messageText.trim()
                                    if (trimmed.isNotEmpty()) {
                                        MessageRepository.sendMessage(
                                            fromName = currentUserName,
                                            content = trimmed,
                                            lawyerId = lawyerId
                                        )
                                        messageText = ""
                                        showSentSnackbar = true
                                    }
                                },
                                enabled = messageText.isNotBlank()
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Envoyer",
                                    tint = if (messageText.isNotBlank()) AppGoldColor else Color.White.copy(alpha = 0.40f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Chat Bubble ─────────────────────────────────────────────────────────────
@Composable
private fun ChatBubble(message: InboxMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Surface(
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
                color = AppDarkGreen,
                border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.30f))
            ) {
                Text(
                    message.content,
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                message.timestamp,
                fontFamily = FontFamily.Serif,
                fontSize = 10.sp,
                color = AppDarkGreen.copy(alpha = 0.40f)
            )
        }
    }
}
