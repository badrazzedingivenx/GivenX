package com.example.client_mobile.screens.shared

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

// --- Chat Screen (bidirectional) ----------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    isLawyer: Boolean = false,
    currentUserName: String = if (isLawyer) "Avocat" else UserSession.name.ifBlank { "" },
    onBack: () -> Unit = {}
) {
    val conversation = ConversationRepository.conversations.find { it.id == conversationId }
    val messages = ConversationRepository.getMessages(conversationId)

    val otherName = if (isLawyer) conversation?.clientName ?: "" else conversation?.lawyerName ?: ""
    val otherSubtitle = if (isLawyer) "Client" else conversation?.lawyerSpecialty ?: ""

    val initials = otherName
        .removePrefix("Ma�tre ")
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")

    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(conversationId) {
        if (isLawyer) ConversationRepository.markReadByLawyer(conversationId)
        else ConversationRepository.markReadByUser(conversationId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(AppDarkGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                initials,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = AppGoldColor
                            )
                        }
                        Column {
                            Text(
                                otherName,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = AppDarkGreen,
                                maxLines = 1
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF34A853))
                                )
                                Text(
                                    otherSubtitle.ifEmpty { "En ligne" },
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 11.sp,
                                    color = AppGoldColor,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = AppDarkGreen
                        )
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
                                        "�crivez � $otherName pour commencer.",
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

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White.copy(alpha = 0.97f),
                    border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.12f)),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { /* attach document � future feature */ },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.AttachFile,
                                contentDescription = "Joindre un fichier",
                                tint = AppDarkGreen.copy(alpha = 0.55f),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text(
                                    "�crivez votre message�",
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
                            modifier = Modifier.size(46.dp),
                            shape = CircleShape,
                            color = if (messageText.isNotBlank()) AppDarkGreen else AppDarkGreen.copy(alpha = 0.25f),
                            border = BorderStroke(
                                0.5.dp,
                                AppGoldColor.copy(alpha = if (messageText.isNotBlank()) 0.55f else 0.20f)
                            )
                        ) {
                            IconButton(
                                onClick = {
                                    val trimmed = messageText.trim()
                                    if (trimmed.isNotEmpty()) {
                                        if (isLawyer) {
                                            ConversationRepository.sendLawyerMessage(
                                                conversationId = conversationId,
                                                content = trimmed,
                                                senderName = currentUserName
                                            )
                                        } else {
                                            ConversationRepository.sendUserMessage(
                                                conversationId = conversationId,
                                                content = trimmed,
                                                senderName = currentUserName
                                            )
                                        }
                                        messageText = ""
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

// --- Chat Message Bubble ------------------------------------------------------
@Composable
private fun ChatMessageBubble(message: ChatMessage, fromMe: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (fromMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (fromMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Surface(
                shape = if (fromMe)
                    RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
                else
                    RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
                color = if (fromMe) AppDarkGreen else Color.White,
                border = if (fromMe)
                    BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.30f))
                else
                    BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.12f)),
                shadowElevation = 1.dp
            ) {
                Text(
                    message.content,
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    color = if (fromMe) Color.White else AppDarkGreen,
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


