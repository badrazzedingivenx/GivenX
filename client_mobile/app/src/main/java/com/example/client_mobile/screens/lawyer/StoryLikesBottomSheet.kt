package com.example.client_mobile.screens.lawyer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.client_mobile.network.MainRepository
import com.example.client_mobile.network.dto.StoryInteractorDto
import com.example.client_mobile.screens.shared.AppCreamBg
import com.example.client_mobile.screens.shared.AppDarkGreen
import com.example.client_mobile.screens.shared.AppGoldColor
import kotlinx.coroutines.launch

enum class StoryInteractionMode { Likes, Replies }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryInteractionSheet(
    storyId: String,
    mode: StoryInteractionMode,
    onDismiss: () -> Unit,
    onNavigateToChat: (String) -> Unit = {}
) {
    val repo = MainRepository
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var interactors by remember { mutableStateOf<List<StoryInteractorDto>>(emptyList()) }

    LaunchedEffect(storyId, mode) {
        isLoading = true
        interactors = when (mode) {
            StoryInteractionMode.Likes   -> repo.getStoryLikes(storyId)
            StoryInteractionMode.Replies -> repo.getStoryReplies(storyId)
        }
        isLoading = false
    }

    ModalBottomSheet(
        onDismissRequest  = onDismiss,
        containerColor    = AppCreamBg,
        shape             = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle        = {
            Box(
                Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .background(AppDarkGreen.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(Modifier.fillMaxWidth().navigationBarsPadding()) {
            // Header
            Row(
                modifier         = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val icon = if (mode == StoryInteractionMode.Likes) Icons.Default.Favorite
                           else Icons.Default.ChatBubbleOutline
                val tint = if (mode == StoryInteractionMode.Likes) Color(0xFFE91E63)
                           else Color(0xFF1976D2)
                val title = if (mode == StoryInteractionMode.Likes) "J'aimes sur la story"
                            else "Réponses à la story"

                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                Text(
                    text       = title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 17.sp,
                    color      = AppDarkGreen
                )
                if (!isLoading && interactors.isNotEmpty()) {
                    Spacer(Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AppGoldColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text     = "${interactors.size}",
                            fontSize = 12.sp,
                            color    = AppGoldColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.08f), thickness = 1.dp)

            // Content
            when {
                isLoading -> {
                    Box(
                        Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppDarkGreen, strokeWidth = 2.dp)
                    }
                }
                interactors.isEmpty() -> {
                    Box(
                        Modifier.fillMaxWidth().height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text     = if (mode == StoryInteractionMode.Likes)
                                           "Aucun j'aime pour l'instant"
                                       else
                                           "Aucune réponse pour l'instant",
                            color    = AppDarkGreen.copy(alpha = 0.45f),
                            fontSize = 14.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding      = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        modifier            = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                    ) {
                        items(interactors, key = { it.id }) { person ->
                            InteractorRow(
                                person       = person,
                                mode         = mode,
                                onNavigateToChat = onNavigateToChat
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InteractorRow(
    person: StoryInteractorDto,
    mode: StoryInteractionMode,
    onNavigateToChat: (String) -> Unit
) {
    val initials = person.fullName
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")
        .ifEmpty { "?" }

    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Avatar
        Box(
            modifier         = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(AppDarkGreen.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            if (!person.avatarUrl.isNullOrBlank()) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(person.avatarUrl)
                        .allowHardware(false)
                        .crossfade(true)
                        .build(),
                    contentDescription = person.fullName,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    loading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color       = AppGoldColor,
                                strokeWidth = 1.5.dp,
                                modifier    = Modifier.size(18.dp)
                            )
                        }
                    },
                    error = {
                        Text(
                            text       = initials,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp,
                            color      = AppDarkGreen
                        )
                    }
                )
            } else {
                Text(
                    text       = initials,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp,
                    color      = AppDarkGreen
                )
            }
        }

        // Name
        Column(Modifier.weight(1f)) {
            Text(
                text       = person.fullName.ifBlank { "Client" },
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp,
                color      = AppDarkGreen,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
        }

        // For replies: "Ouvrir" action button
        if (mode == StoryInteractionMode.Replies) {
            TextButton(
                onClick = {
                    // Construct conversation ID using same convention as ConversationRepository
                    val lawyerId = com.example.client_mobile.network.TokenManager.getLawyerId()
                    val convId = "${person.fullName.trim().replace(" ", "_")}_$lawyerId"
                    onNavigateToChat(convId)
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF1976D2))
            ) {
                Text("Ouvrir", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }

    HorizontalDivider(
        modifier  = Modifier.padding(start = 76.dp, end = 20.dp),
        color     = AppDarkGreen.copy(alpha = 0.06f),
        thickness = 1.dp
    )
}
