package com.example.client_mobile.screens.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Messages Inbox Screen ────────────────────────────────────────────────────
@Composable
fun MessagesInboxScreen(
    isLawyer: Boolean,
    paddingValues: PaddingValues = PaddingValues(),
    onNavigateToChat: (String) -> Unit = {}
) {
    val conversations = ConversationRepository.conversations

    if (conversations.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = null,
                    tint = AppDarkGreen.copy(alpha = 0.30f),
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Aucune conversation",
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkGreen
                )
                Text(
                    text = if (isLawyer)
                        "Les messages de vos clients apparaîtront ici."
                    else
                        "Consultez un avocat et commencez une discussion.",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.Gray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionHeader(title = "Conversations (${conversations.size})")
            }
            items(conversations, key = { it.id }) { conv ->
                ConversationCard(
                    conversation = conv,
                    isLawyer = isLawyer,
                    onClick = { onNavigateToChat(conv.id) }
                )
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

// ─── Conversation Card ────────────────────────────────────────────────────────
@Composable
private fun ConversationCard(
    conversation: Conversation,
    isLawyer: Boolean,
    onClick: () -> Unit
) {
    val otherName = if (isLawyer) conversation.clientName else conversation.lawyerName
    val unreadCount = if (isLawyer) conversation.unreadCountForLawyer else conversation.unreadCountForUser
    val initials = otherName
        .removePrefix("Maître ")
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (unreadCount > 0) Color.White else Color.White.copy(alpha = 0.78f),
        border = BorderStroke(
            width = if (unreadCount > 0) 1.dp else 0.5.dp,
            color = if (unreadCount > 0) AppGoldColor.copy(alpha = 0.55f) else AppDarkGreen.copy(alpha = 0.10f)
        ),
        shadowElevation = if (unreadCount > 0) 4.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AppDarkGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    initials,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = AppGoldColor
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        otherName,
                        fontFamily = FontFamily.Serif,
                        fontWeight = if (unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp,
                        color = AppDarkGreen,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (conversation.lastTimestamp.isNotEmpty()) {
                        Text(
                            conversation.lastTimestamp,
                            fontFamily = FontFamily.Serif,
                            fontSize = 10.sp,
                            color = AppDarkGreen.copy(alpha = 0.40f),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (conversation.lastMessage.isNotEmpty()) conversation.lastMessage
                        else "Nouvelle conversation",
                        fontFamily = FontFamily.Serif,
                        fontSize = 12.sp,
                        color = if (unreadCount > 0) AppDarkGreen.copy(alpha = 0.80f)
                                else AppDarkGreen.copy(alpha = 0.50f),
                        fontWeight = if (unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(AppDarkGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (unreadCount > 9) "9+" else "$unreadCount",
                                fontFamily = FontFamily.Serif,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppGoldColor
                            )
                        }
                    }
                }

                if (!isLawyer && conversation.lawyerSpecialty.isNotEmpty()) {
                    Text(
                        conversation.lawyerSpecialty,
                        fontFamily = FontFamily.Serif,
                        fontSize = 10.sp,
                        color = AppGoldColor.copy(alpha = 0.80f)
                    )
                }
            }
        }
    }
}
