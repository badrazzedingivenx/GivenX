package com.example.client_mobile.presentation.common.screens

import com.example.client_mobile.data.repository.ChatMessage
import com.example.client_mobile.data.repository.ConversationRepository
import com.example.client_mobile.presentation.common.viewmodel.*
import com.example.client_mobile.presentation.common.components.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage

// --- Chat Screen (bidirectional) ----------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    isLawyer: Boolean = false,
    currentUserName: String = if (isLawyer) "Avocat" else UserSession.name.ifBlank { "" },
    onBack: () -> Unit = {}
) {
    // Fetch messages from API on first composition
    val chatViewModel: ChatViewModel = viewModel(
        key = conversationId,
        factory = ChatViewModel.Factory(conversationId)
    )
    val conversation = ConversationRepository.conversations.find { it.id == conversationId }
    val messages = ConversationRepository.getMessages(conversationId)

    val otherName = conversation?.otherPartyName ?: ""
    val otherSubtitle = ""

    val initials = otherName
        .removePrefix("Maître ")
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")

    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage by chatViewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(errorMessage!!)
            chatViewModel.clearError()
        }
    }

    LaunchedEffect(conversationId) {
        if (isLawyer) ConversationRepository.markRead(conversationId)
        else ConversationRepository.markRead(conversationId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    AppScaffold(
        topBar = {
            StandardTopBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Avatar with gold border
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                                .border(1.5.dp, AppGoldColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (conversation?.avatarUrl?.isNotBlank() == true) {
                                AsyncImage(
                                    model = conversation.avatarUrl,
                                    contentDescription = otherName,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else {
                                Text(
                                    initials,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = AppGoldColor
                                )
                            }
                        }
                        Column {
                            Text(
                                otherName,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White, // White for visibility on dark green
                                maxLines = 1
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50)) // Modern green
                                )
                                Text(
                                    otherSubtitle.ifEmpty { "En ligne" },
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 11.sp,
                                    color = AppGoldColor.copy(alpha = 0.9f),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                },
                onBack = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                if (messages.isEmpty()) {
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
                                    "Écrivez à $otherName pour commencer.",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 12.sp,
                                    color = AppDarkGreen.copy(alpha = 0.35f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(messages, key = { it.id }) { msg ->
                        val fromMe = if (isLawyer) !msg.isFromUser else msg.isFromUser
                        ChatMessageBubble(message = msg, fromMe = fromMe)
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            // --- Input Area (Modern & Clean) ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                color = Color.White,
                shape = RoundedCornerShape(28.dp),
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, AppDarkGreen.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { /* attach */ },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Ajouter",
                            tint = AppDarkGreen.copy(alpha = 0.6f),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Écrivez votre message…",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = AppDarkGreen
                        ),
                        maxLines = 4
                    )

                    val isNotEmpty = messageText.trim().isNotEmpty()
                    
                    IconButton(
                        onClick = {
                            val trimmed = messageText.trim()
                            if (trimmed.isNotEmpty()) {
                                chatViewModel.send(
                                    text       = trimmed,
                                    senderName = currentUserName,
                                    isFromUser = !isLawyer
                                )
                                messageText = ""
                            }
                        },
                        enabled = isNotEmpty,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isNotEmpty) AppDarkGreen else Color.Transparent)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Envoyer",
                            tint = if (isNotEmpty) AppGoldColor else Color.Gray.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// --- Chat Message Bubble ------------------------------------------------------
@Composable
private fun ChatMessageBubble(message: ChatMessage, fromMe: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (fromMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (fromMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Surface(
                shape = if (fromMe)
                    RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
                else
                    RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                color = if (fromMe) AppDarkGreen else Color.White,
                border = if (fromMe)
                    BorderStroke(1.dp, AppGoldColor.copy(alpha = 0.2f))
                else
                    BorderStroke(1.dp, AppDarkGreen.copy(alpha = 0.08f)),
                shadowElevation = 2.dp
            ) {
                Text(
                    message.content,
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    color = if (fromMe) Color.White else AppDarkGreen.copy(alpha = 0.9f),
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
            
            Row(
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    message.timestamp,
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp,
                    color = AppDarkGreen.copy(alpha = 0.5f)
                )
                if (fromMe) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Default.DoneAll,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = AppGoldColor
                    )
                }
            }
        }
    }
}
