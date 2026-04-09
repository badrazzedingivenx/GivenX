package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val isMe: Boolean = false
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun LiveSessionsScreen(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: LiveViewModel = viewModel()
) {
    var activeSession by remember { mutableStateOf<LiveSession?>(null) }
    val apiLives by viewModel.lives.collectAsStateWithLifecycle()

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
        LazyColumn(
            modifier        = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding  = PaddingValues(vertical = 12.dp)
        ) {
            item {
                val displayName = UserSession.name.split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: ""
                Text(
                    text = if (displayName.isNotEmpty()) "Bonjour, $displayName 👋" else "Bonjour 👋",
                    fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                    fontSize = 22.sp, color = AppDarkGreen
                )
                Text("Sessions en direct", fontFamily = FontFamily.Serif,
                    fontSize = 13.sp, color = AppDarkGreen.copy(alpha = 0.55f))
            }

            // ── Live now section ──────────────────────────────────────────
            item { SectionHeader(title = "🔴 En direct") }
            items(allLive) { session ->
                LiveSessionCard(session = session, onClick = { activeSession = session })
            }

            item { Spacer(Modifier.height(8.dp)) }
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

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ── Video area ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .background(Brush.verticalGradient(listOf(AppDarkGreen, Color(0xFF0D1F17)))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(modifier = Modifier.size(80.dp), shape = CircleShape,
                    color = AppGoldColor.copy(alpha = 0.18f), border = BorderStroke(2.dp, AppGoldColor)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null,
                            tint = AppGoldColor, modifier = Modifier.size(40.dp))
                    }
                }
                Text(session.lawyerName, fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                Text(session.topic, fontFamily = FontFamily.Serif, fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.70f))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(7.dp).background(Color(0xFFD32F2F), CircleShape))
                    Text("EN DIRECT", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                        fontSize = 11.sp, color = Color(0xFFD32F2F))
                    Text("· ${session.viewers} spectateurs", fontFamily = FontFamily.Serif,
                        fontSize = 11.sp, color = Color.White.copy(alpha = 0.60f))
                }
            }
        }

        // ── Top bar ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onLeave) {
                Icon(Icons.Default.Close, contentDescription = "Quitter",
                    tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFD32F2F)) {
                Text("LIVE", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                    fontSize = 11.sp, color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
        }

        // ── Chat section ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.46f)
                .align(Alignment.BottomCenter)
                .background(Color(0xFF0D1F17))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Messages
            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatMessages.reversed()) { msg ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(6.dp).offset(y = 6.dp)
                            .background(AppGoldColor, CircleShape))
                        Column {
                            Text(msg.author, fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AppGoldColor)
                            Text(msg.text, fontFamily = FontFamily.Serif, fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Input row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value         = chatInput,
                    onValueChange = { chatInput = it },
                    modifier      = Modifier.weight(1f),
                    placeholder   = { Text("Posez votre question…", fontFamily = FontFamily.Serif,
                        fontSize = 12.sp, color = Color.White.copy(alpha = 0.40f)) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(20.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor    = AppGoldColor,
                        unfocusedBorderColor  = Color.White.copy(alpha = 0.20f),
                        focusedTextColor      = Color.White,
                        unfocusedTextColor    = Color.White,
                        cursorColor           = AppGoldColor,
                        focusedContainerColor = Color.White.copy(alpha = 0.07f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
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
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Envoyer",
                        tint = AppGoldColor, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}
