package com.example.client_mobile.screens.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

// ─── Messages Inbox Screen ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesInboxScreen(
    isLawyer: Boolean,
    paddingValues: PaddingValues = PaddingValues(),
    onNavigateToChat: (String) -> Unit = {},
    conversationViewModel: ConversationViewModel = viewModel()
) {
    val conversations  = ConversationRepository.conversations
    val isLoading      by conversationViewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing   by conversationViewModel.isRefreshing.collectAsStateWithLifecycle()
    val isError        by conversationViewModel.isError.collectAsStateWithLifecycle()
    val errorMessage   by conversationViewModel.errorMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(errorMessage!!)
            conversationViewModel.clearError()
        }
    }

    // ── No-connection full-screen state ───────────────────────────────────
    if (isError && conversations.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            NoConnectionScreen(
                onRetry   = { conversationViewModel.refresh() },
                modifier  = Modifier.fillMaxSize()
            )
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        // ── Loading state (Skeleton) ──────────────────────────────────────────
        if (isLoading && conversations.isEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { SkeletonBox(modifier = Modifier.width(150.dp).height(24.dp)) }
                items(5) {
                    SkeletonBox(modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(18.dp))
                }
            }
        }

        // ── Pull-to-refresh + conversation list ───────────────────────────────
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh    = { conversationViewModel.refresh() },
            modifier     = Modifier.fillMaxSize()
        ) {
            MessagesInboxContent(
                conversations    = conversations,
                isLawyer         = isLawyer,
                paddingValues    = PaddingValues(0.dp),
                onNavigateToChat = onNavigateToChat
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ─── Inner content (list + empty state) ──────────────────────────────────
@Composable
private fun MessagesInboxContent(
    conversations: List<Conversation>,
    isLawyer: Boolean,
    paddingValues: PaddingValues,
    onNavigateToChat: (String) -> Unit
) {
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
    val otherName = conversation.otherPartyName
    val unreadCount = conversation.unreadCount
    val isUnread = unreadCount > 0
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
        shape = RoundedCornerShape(20.dp),
        color = if (isUnread) Color.White else Color.White.copy(alpha = 0.7f),
        border = BorderStroke(
            width = if (isUnread) 1.5.dp else 1.dp,
            color = if (isUnread) AppGoldColor.copy(alpha = 0.4f) else AppDarkGreen.copy(alpha = 0.08f)
        ),
        shadowElevation = if (isUnread) 6.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar with premium border if unread
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(AppDarkGreen)
                    .then(
                        if (isUnread) Modifier.border(2.dp, AppGoldColor, CircleShape)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (conversation.avatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = conversation.avatarUrl,
                        contentDescription = otherName,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                } else {
                    Text(
                        initials,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppGoldColor,
                            fontFamily = FontFamily.Serif
                        )
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        otherName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (isUnread) FontWeight.ExtraBold else FontWeight.Bold,
                            color = AppDarkGreen,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Serif
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (conversation.timestamp.isNotEmpty()) {
                        Text(
                            conversation.timestamp,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isUnread) AppGoldColor else AppDarkGreen.copy(alpha = 0.4f),
                                fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp
                            )
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
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AppDarkGreen.copy(alpha = if (isUnread) 0.85f else 0.55f),
                            fontWeight = if (isUnread) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Serif
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (isUnread) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = AppDarkGreen,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    if (unreadCount > 9) "9+" else "$unreadCount",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AppGoldColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
