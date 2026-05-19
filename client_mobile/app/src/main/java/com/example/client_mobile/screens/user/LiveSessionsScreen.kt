package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// ─── Model ────────────────────────────────────────────────────────────────────

data class LiveSession(
    val id: Int,
    val lawyerName: String,
    val specialty: String,
    val topic: String,
    val viewers: Int,
    val isLive: Boolean = true
)

data class LiveChatMessage(
    val author: String,
    val text: String,
    val isMe: Boolean = false,
    val id: String = java.util.UUID.randomUUID().toString()
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveSessionsScreen(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: LiveViewModel = viewModel()
) {
    var activeSession by remember { mutableStateOf<LiveSession?>(null) }
    val apiLives      by viewModel.lives.collectAsStateWithLifecycle()
    val isRefreshing  by viewModel.isRefreshing.collectAsStateWithLifecycle()

    val creatorLive = CreatorRepository.liveSessions.map { cl ->
        LiveSession(
            id         = cl.id.toInt(),
            lawyerName = cl.lawyerName,
            specialty  = cl.specialty,
            topic      = cl.topic,
            viewers    = cl.viewers,
            isLive     = cl.isLive
        )
    }
    val allLive = creatorLive + (apiLives ?: emptyList())

    if (activeSession != null) {
        LiveRoomView(
            session  = activeSession!!,
            onLeave  = { activeSession = null }
        )
    } else {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh    = { viewModel.fetch() },
            modifier     = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier            = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding      = PaddingValues(vertical = 12.dp)
            ) {
                item {
                    val displayName = UserSession.name.split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: ""
                    Text(
                        text = if (displayName.isNotEmpty()) "Bonjour, $displayName 👋" else "Bonjour 👋",
                        fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                        fontSize = 22.sp, color = AppDarkGreen
                    )
                    Text(
                        "Sessions en direct",
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.sp,
                        color = AppDarkGreen.copy(alpha = 0.55f)
                    )
                }

                // ── Live now section ──────────────────────────────────────
                item { SectionHeader(title = "🔴 En direct") }
                items(allLive) { session ->
                    LiveSessionCard(session = session, onClick = { activeSession = session })
                }

                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

// ─── Session Card ─────────────────────────────────────────────────────────────

@Composable
private fun LiveSessionCard(session: LiveSession, onClick: () -> Unit) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(20.dp),
        color           = if (session.isLive) AppDarkGreen else Color.White,
        border          = BorderStroke(0.5.dp, if (session.isLive) AppGoldColor.copy(alpha = 0.55f) else AppDarkGreen.copy(alpha = 0.10f)),
        shadowElevation = if (session.isLive) 6.dp else 2.dp,
        onClick         = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar
            Surface(
                modifier = Modifier.size(52.dp),
                shape    = CircleShape,
                color    = AppGoldColor.copy(alpha = 0.18f),
                border   = BorderStroke(1.5.dp, AppGoldColor)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null,
                        tint = AppGoldColor, modifier = Modifier.size(24.dp))
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(session.lawyerName, fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold, fontSize = 14.sp,
                        color = if (session.isLive) Color.White else AppDarkGreen)
                    if (session.isLive) {
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFD32F2F)) {
                            Text("LIVE", fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
                Text(session.topic, fontFamily = FontFamily.Serif, fontSize = 12.sp,
                    color = if (session.isLive) Color.White.copy(alpha = 0.75f) else AppDarkGreen.copy(alpha = 0.65f),
                    lineHeight = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.People, contentDescription = null,
                        tint = AppGoldColor, modifier = Modifier.size(13.dp))
                    Text("${session.viewers} spectateurs", fontFamily = FontFamily.Serif,
                        fontSize = 11.sp, color = AppGoldColor)
                }
            }

            Icon(Icons.Default.PlayCircle, contentDescription = "Rejoindre",
                tint = AppGoldColor, modifier = Modifier.size(30.dp))
        }
    }
}

// ─── Live Room View ───────────────────────────────────────────────────────────

@Composable
private fun LiveRoomView(session: LiveSession, onLeave: () -> Unit) {
    var chatInput by remember { mutableStateOf("") }
    val chatMessages = remember {
        mutableStateListOf(
            LiveChatMessage("Karim B.", "Maître, que faire si l'employeur refuse de payer ?"),
            LiveChatMessage("Sara A.",  "Merci pour ces explications !"),
            LiveChatMessage("Ahmed Z.", "Peut-on porter plainte après 6 mois ?"),
        )
    }

    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Auto-scroll to newly added messages
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    var offsetY by remember { mutableStateOf(0f) }

    Dialog(
        onDismissRequest = onLeave,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            // ── Swipeable Container ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(0, offsetY.toInt()) }
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                if (offsetY > 300f) {
                                    onLeave()
                                } else {
                                    offsetY = 0f
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            offsetY += dragAmount
                            if (offsetY < 0f) offsetY = 0f
                        }
                    }
            ) {
                // ── Video area (Full Screen Edge-to-Edge) ──────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(AppDarkGreen, Color(0xFF0D1F17)))),
                    contentAlignment = Alignment.Center
                ) {
                    // Empty center for true immersive experience, content strictly relegated to overlays
                }

                // ── Chat & Input Overlay (Bottom) ──────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.40f)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                            )
                        )
                        .padding(horizontal = 16.dp)
                        .navigationBarsPadding() // Clear the system navigation bar space
                        .imePadding() // Adjust layout correctly when keyboard pops up
                        .padding(bottom = 24.dp) // Provide 24dp spacing floating above the bottom edge natively
                ) {
                    // Messages Overlay
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        reverseLayout = true,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 8.dp)
                    ) {
                        items(chatMessages.toList(), key = { it.id }) { msg ->
                            var visible by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) { visible = true }
                            androidx.compose.animation.AnimatedVisibility(
                                visible = visible,
                                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(initialOffsetY = { it / 2 })
                            ) {
                                Surface(
                                    color = Color.Black.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.padding(end = 50.dp) // Prevent full width stretch
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Box(modifier = Modifier.size(6.dp).offset(y = 6.dp)
                                            .background(AppGoldColor, CircleShape))
                                        Column {
                                            Text(msg.author, fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AppGoldColor)
                                            Text(msg.text, fontFamily = FontFamily.Serif, fontSize = 13.sp,
                                                color = Color.White.copy(alpha = 0.95f))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Input Box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value         = chatInput,
                            onValueChange = { chatInput = it },
                            modifier      = Modifier.weight(1f),
                            placeholder   = { Text("Add a comment...", fontFamily = FontFamily.Serif,
                                fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f)) },
                            singleLine    = true,
                            shape         = RoundedCornerShape(24.dp),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor    = Color.White.copy(alpha = 0.4f),
                                unfocusedBorderColor  = Color.White.copy(alpha = 0.2f),
                                focusedTextColor      = Color.White,
                                unfocusedTextColor    = Color.White,
                                cursorColor           = Color.White,
                                focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                                unfocusedContainerColor = Color.Black.copy(alpha = 0.3f)
                            )
                        )
                        IconButton(
                            onClick = {
                                if (chatInput.isNotBlank()) {
                                    chatMessages.add(0, LiveChatMessage(
                                        author = UserSession.name.split(" ").firstOrNull() ?: "Moi",
                                        text   = chatInput.trim(),
                                        isMe   = true
                                    ))
                                    chatInput = ""
                                }
                            },
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                .size(48.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Envoyer",
                                tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            } // End Swipeable Container

            // ── Top close button (Fixed always visible) ─ Outside Swipe Container ──
            IconButton(
                onClick = onLeave,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 16.dp, end = 16.dp)
                    .background(Color.Black.copy(alpha = 0.35f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Quitter",
                    tint = Color.White, modifier = Modifier.size(24.dp))
            }

            // ── Top Left Block (IG Style Profile + Live + Count) ── Outside Swipe ───
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(top = 16.dp, start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Profile Avatar
                Surface(
                    modifier = Modifier.size(34.dp),
                    shape = CircleShape,
                    color = AppGoldColor.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, AppGoldColor)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null,
                            tint = AppGoldColor, modifier = Modifier.size(20.dp))
                    }
                }

                // Name
                Text(session.lawyerName, fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)

                // LIVE Badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFE1306C) // Instagram Style gradient color basis
                ) {
                    Text("LIVE", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                        fontSize = 10.sp, color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }

                // Viewer Count
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.40f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                        Text("${session.viewers}", fontFamily = FontFamily.Serif,
                            fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        } // End Root Box
    } // End Dialog
}