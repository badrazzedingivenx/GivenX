package com.example.client_mobile.screens.shared

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

// ─── Design Tokens ────────────────────────────────────────────────────────────
private val InboxBackground      = Color(0xFFF5F7F6)   // Very light warm-green tinted gray
private val CardWhite            = Color(0xFFFFFFFF)
private val NameColor            = Color(0xFF0F291E)    // AppDarkGreen alias – strong hierarchy
private val SnippetColor         = Color(0xFF7A8C84)    // Medium gray-green
private val TimeColor            = Color(0xFFADB8B3)    // Light gray
private val UnreadDotBg          = Color(0xFF0F291E)    // AppDarkGreen badge
private val UnreadTimeColor      = Color(0xFFD4AF37)    // AppGoldColor for unread time
private val AvatarRingUnread     = Color(0xFFD4AF37)    // Gold ring when unread
private val AvatarBg             = Color(0xFF1B4332)    // Rich dark-green avatar background
private val AvatarInitialColor   = Color(0xFFD4AF37)    // Gold initials
private val SearchBarBg          = Color(0xFFEEF1EF)    // Slightly darker than page bg
private val SectionLabelColor    = Color(0xFF4A5D55)    // Muted green-gray

// ─── Messages Inbox Screen ─────────────────────────────────────────────────────
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

    Scaffold(
        topBar = {
            StandardTopBar(title = "Messages")
        },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { localPadding ->
        // ── No-connection full-screen state ──────────────────────────────────────
        if (isError && conversations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = localPadding.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    )
            ) {
                NoConnectionScreen(
                    onRetry  = { conversationViewModel.refresh() },
                    modifier = Modifier.fillMaxSize()
                )
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier  = Modifier.align(Alignment.BottomCenter)
                )
            }
            return@Scaffold
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(InboxBackground)
        ) {
            // ── Loading skeleton ──────────────────────────────────────────────────
            if (isLoading && conversations.isEmpty()) {
                Box(modifier = Modifier.padding(top = localPadding.calculateTopPadding())) {
                    InboxSkeleton()
                }
            }

            // ── Pull-to-refresh + list ────────────────────────────────────────────
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh    = { conversationViewModel.refresh() },
                modifier     = Modifier
                    .fillMaxSize()
                    .padding(
                        top = localPadding.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    )
            ) {
                MessagesInboxContent(
                    conversations    = conversations,
                    isLawyer         = isLawyer,
                    onNavigateToChat = onNavigateToChat
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier  = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}


// ─── Skeleton Loading ─────────────────────────────────────────────────────────
@Composable
private fun InboxSkeleton() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(16.dp)) }
        // Search bar skeleton
        item {
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape    = RoundedCornerShape(16.dp)
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
        // Card skeletons
        items(6) {
            SkeletonConversationCard()
        }
    }
}

@Composable
private fun SkeletonConversationCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton_card")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue  = 0.55f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation         = 2.dp,
                shape             = RoundedCornerShape(20.dp),
                ambientColor      = Color(0x14000000),
                spotColor         = Color(0x14000000)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(CardWhite)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = alpha))
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray.copy(alpha = alpha))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(11.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray.copy(alpha = alpha * 0.7f))
                )
            }
        }
    }
}

// ─── Inner content (list + empty state) ──────────────────────────────────────
@Composable
private fun MessagesInboxContent(
    conversations: List<Conversation>,
    isLawyer: Boolean,
    onNavigateToChat: (String) -> Unit
) {
    if (conversations.isEmpty()) {
        InboxEmptyState(isLawyer = isLawyer)
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(Modifier.height(12.dp)) }

            // ── Section label ─────────────────────────────────────────────────
            item {
                Text(
                    text = "Messages",
                    fontSize    = 22.sp,
                    fontWeight  = FontWeight.Bold,
                    color       = NameColor,
                    letterSpacing = 0.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${conversations.size} conversation${if (conversations.size > 1) "s" else ""}",
                    fontSize  = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color     = SectionLabelColor
                )
                Spacer(Modifier.height(12.dp))
            }

            // ── Conversation cards ────────────────────────────────────────────
            itemsIndexed(conversations, key = { _, c -> c.id }) { index, conv ->
                val enterAlpha by animateFloatAsState(
                    targetValue   = 1f,
                    animationSpec = tween(
                        durationMillis = 320,
                        delayMillis    = (index * 50).coerceAtMost(300),
                        easing         = FastOutSlowInEasing
                    ),
                    label = "card_alpha_$index"
                )
                ConversationCard(
                    conversation = conv,
                    isLawyer     = isLawyer,
                    modifier     = Modifier.graphicsLayer(alpha = enterAlpha),
                    onClick      = { onNavigateToChat(conv.id) }
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// ─── Empty State ──────────────────────────────────────────────────────────────
@Composable
private fun InboxEmptyState(isLawyer: Boolean) {
    Box(
        modifier          = Modifier.fillMaxSize(),
        contentAlignment  = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon container
            Box(
                modifier         = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1B4332).copy(alpha = 0.12f),
                                Color(0xFF0F291E).copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.ChatBubbleOutline,
                    contentDescription = null,
                    tint               = AppDarkGreen.copy(alpha = 0.35f),
                    modifier           = Modifier.size(40.dp)
                )
            }
            Text(
                text       = "Aucune conversation",
                fontSize   = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color      = NameColor
            )
            Text(
                text    = if (isLawyer)
                    "Les messages de vos clients apparaîtront ici."
                else
                    "Consultez un avocat et commencez une discussion.",
                fontSize    = 14.sp,
                color       = SnippetColor,
                lineHeight  = 20.sp
            )
        }
    }
}

// ─── Conversation Card ────────────────────────────────────────────────────────
@Composable
private fun ConversationCard(
    conversation: Conversation,
    isLawyer: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val otherName   = conversation.otherPartyName
    val unreadCount = conversation.unreadCount
    val isUnread    = unreadCount > 0

    val initials = otherName
        .removePrefix("Maître ")
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")

    // Outer shadow box wrapped around the card for the diffuse elevation effect
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation         = if (isUnread) 8.dp else 3.dp,
                shape             = RoundedCornerShape(20.dp),
                // Very soft, dispersed shadow – low opacity, high spread
                ambientColor      = if (isUnread)
                    Color(0xFF0F291E).copy(alpha = 0.10f)
                else
                    Color(0xFF000000).copy(alpha = 0.06f),
                spotColor         = if (isUnread)
                    Color(0xFFD4AF37).copy(alpha = 0.10f)
                else
                    Color(0xFF000000).copy(alpha = 0.06f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(CardWhite)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
    ) {
        // Unread left accent bar
        if (isUnread) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFD4AF37), Color(0xFFC5A059))
                        )
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start  = if (isUnread) 20.dp else 16.dp,
                    end    = 16.dp,
                    top    = 16.dp,
                    bottom = 16.dp
                ),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Avatar ────────────────────────────────────────────────────────
            Box(
                modifier         = Modifier.size(54.dp),
                contentAlignment = Alignment.Center
            ) {
                // Gold ring for unread
                if (isUnread) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    colors = listOf(
                                        AvatarRingUnread,
                                        AvatarRingUnread.copy(alpha = 0.4f),
                                        AvatarRingUnread
                                    )
                                )
                            )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(if (isUnread) 48.dp else 54.dp)
                        .clip(CircleShape)
                        .background(AvatarBg),
                    contentAlignment = Alignment.Center
                ) {
                    if (conversation.avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model              = conversation.avatarUrl,
                            contentDescription = otherName,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Text(
                            text       = initials,
                            fontSize   = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color      = AvatarInitialColor
                        )
                    }
                }
            }

            // ── Text content ──────────────────────────────────────────────────
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Name + Date row
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.Top
                ) {
                    Text(
                        text       = otherName,
                        fontSize   = 15.sp,
                        fontWeight = if (isUnread) FontWeight.ExtraBold else FontWeight.SemiBold,
                        color      = NameColor,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis,
                        modifier   = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    if (conversation.timestamp.isNotEmpty()) {
                        Text(
                            text       = conversation.timestamp,
                            fontSize   = 11.sp,
                            fontWeight = if (isUnread) FontWeight.SemiBold else FontWeight.Normal,
                            color      = if (isUnread) UnreadTimeColor else TimeColor
                        )
                    }
                }

                // Snippet + Badge row
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text     = if (conversation.lastMessage.isNotEmpty())
                            conversation.lastMessage
                        else "Nouvelle conversation",
                        fontSize   = 13.sp,
                        fontWeight = if (isUnread) FontWeight.Medium else FontWeight.Normal,
                        color      = if (isUnread) SnippetColor.copy(alpha = 0.9f) else SnippetColor,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis,
                        modifier   = Modifier.weight(1f)
                    )

                    if (isUnread) {
                        Spacer(Modifier.width(10.dp))
                        // Unread count badge
                        Box(
                            modifier         = Modifier
                                .defaultMinSize(minWidth = 22.dp, minHeight = 22.dp)
                                .clip(CircleShape)
                                .background(UnreadDotBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text       = if (unreadCount > 9) "9+" else "$unreadCount",
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color      = AvatarInitialColor, // gold text on dark bg
                                modifier   = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
