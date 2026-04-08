package com.example.client_mobile.screens.lawyer

import com.example.client_mobile.screens.shared.*

import androidx.compose.animation.core.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Live Studio Screen ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerLiveStudioScreen(
    liveId: Long,
    topic: String = "Q&A en direct",
    onEndLive: () -> Unit = {}
) {
    var isMuted       by remember { mutableStateOf(false) }
    var isFrontCamera by remember { mutableStateOf(true) }
    var viewerCount   by remember { mutableIntStateOf(1) }
    var commentInput  by remember { mutableStateOf("") }

    val comments = remember {
        mutableStateListOf(
            LiveStudioChatMessage("Karim B.",  "Merci pour la session !"),
            LiveStudioChatMessage("Sara A.",   "Très utile, merci Maître 🙏"),
            LiveStudioChatMessage("Ahmed Z.",  "Question : combien de temps dure la procédure ?")
        )
    }

    // ── Pulse animation for LIVE dot ──────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "liveStudioPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 1.5f,
        animationSpec = infiniteRepeatable(
            animation  = tween(700, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 0.3f,
        animationSpec = infiniteRepeatable(
            animation  = tween(700, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Simulate increasing viewers
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        repeat(15) {
            kotlinx.coroutines.delay(2000)
            viewerCount += (1..8).random()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ── Camera / Stage ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.56f)
                .background(Brush.verticalGradient(listOf(AppDarkGreen, Color(0xFF0A1A10)))),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.size(90.dp),
                    shape    = CircleShape,
                    color    = AppGoldColor.copy(alpha = 0.15f),
                    border   = androidx.compose.foundation.BorderStroke(2.dp, AppGoldColor)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null,
                            tint = AppGoldColor, modifier = Modifier.size(44.dp))
                    }
                }
                Text(LawyerSession.fullName, fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                Text(topic, fontFamily = FontFamily.Serif, fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.65f))
            }
        }

        // ── Top bar ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // LIVE badge with pulse
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .scale(pulseScale)
                        .background(Color(0xFFD32F2F).copy(alpha = pulseAlpha), CircleShape)
                )
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFD32F2F)) {
                    Text("LIVE", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                        fontSize = 11.sp, color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }
            // Viewer count
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.People, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(16.dp))
                Text("$viewerCount", fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
            }
        }

        // ── Camera controls bar ───────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.56f)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.Bottom
        ) {
            // Mute
            StudioControlButton(
                icon    = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                label   = if (isMuted) "Muet" else "Son",
                tint    = if (isMuted) Color(0xFFD32F2F) else Color.White,
                onClick = { isMuted = !isMuted }
            )
            Spacer(Modifier.width(24.dp))
            // Flip camera
            StudioControlButton(
                icon    = Icons.Default.FlipCameraAndroid,
                label   = "Inverser",
                tint    = Color.White,
                onClick = { isFrontCamera = !isFrontCamera }
            )
            Spacer(Modifier.width(24.dp))
            // End live
            Surface(
                shape   = RoundedCornerShape(20.dp),
                color   = Color(0xFFD32F2F),
                onClick = {
                    CreatorRepository.endLive(liveId)
                    onEndLive()
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.StopCircle, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(18.dp))
                    Text("Terminer", fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                }
            }
        }

        // ── Chat section ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .align(Alignment.BottomCenter)
                .background(Color(0xFF0D1F17))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text("Commentaires", fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AppGoldColor)
            Spacer(Modifier.height(6.dp))

            LazyColumn(
                modifier     = Modifier.weight(1f),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(comments.reversed()) { msg ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(6.dp).offset(y = 6.dp)
                            .background(AppGoldColor, CircleShape))
                        Column {
                            Text(msg.author, fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AppGoldColor)
                            Text(msg.text, fontFamily = FontFamily.Serif,
                                fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value         = commentInput,
                    onValueChange = { commentInput = it },
                    modifier      = Modifier.weight(1f),
                    placeholder   = { Text("Répondre en direct…", fontFamily = FontFamily.Serif,
                        fontSize = 12.sp, color = Color.White.copy(alpha = 0.35f)) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(20.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = AppGoldColor,
                        unfocusedBorderColor    = Color.White.copy(alpha = 0.20f),
                        focusedTextColor        = Color.White,
                        unfocusedTextColor      = Color.White,
                        cursorColor             = AppGoldColor,
                        focusedContainerColor   = Color.White.copy(alpha = 0.07f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
                    )
                )
                IconButton(onClick = {
                    if (commentInput.isNotBlank()) {
                        comments.add(0, LiveStudioChatMessage("Vous", commentInput.trim()))
                        commentInput = ""
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Envoyer",
                        tint = AppGoldColor, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

// ─── Studio Control Button ────────────────────────────────────────────────────

@Composable
private fun StudioControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape    = CircleShape,
            color    = Color.White.copy(alpha = 0.15f),
            onClick  = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(22.dp))
            }
        }
        Text(label, fontFamily = FontFamily.Serif, fontSize = 10.sp, color = Color.White.copy(alpha = 0.70f))
    }
}
