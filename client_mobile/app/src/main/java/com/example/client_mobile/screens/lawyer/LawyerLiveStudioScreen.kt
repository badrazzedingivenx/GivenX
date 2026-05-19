package com.example.client_mobile.screens.lawyer

import com.example.client_mobile.screens.shared.*

import androidx.compose.animation.core.*
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
            animation  = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 0.4f,
        animationSpec = infiniteRepeatable(
            animation  = tween(800, easing = EaseInOut),
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

    AppScaffold { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ── Camera / Stage (Immersive backdrop) ───────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.60f)
                    .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(AppDarkGreen, Color(0xFF0A1A10))
                        )
                    )
                    .padding(bottom = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Pattern / Simulation of front camera view
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape    = CircleShape,
                        color    = AppGoldColor.copy(alpha = 0.12f),
                        border   = BorderStroke(2.dp, AppGoldColor)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person, 
                                contentDescription = null,
                                tint = AppGoldColor, 
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    Text(
                        text = LawyerSession.fullName, 
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold, 
                        fontSize = 22.sp, 
                        color = Color.White
                    )
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = topic, 
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontFamily = FontFamily.Serif, 
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // ── Top Bar Overlay ───────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // LIVE badge with pulse
                Row(
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .scale(pulseScale)
                            .background(StatusRed.copy(alpha = pulseAlpha), CircleShape)
                    )
                    StatusChip(
                        label = "EN DIRECT",
                        containerColor = StatusRed,
                        textColor = Color.White
                    )
                }
                // Viewer count
                Surface(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.People, 
                            contentDescription = null,
                            tint = Color.White, 
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            "$viewerCount", 
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold, 
                            fontSize = 13.sp, 
                            color = Color.White
                        )
                    }
                }
            }

            // ── Camera controls bar (Floating above chat) ─────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.60f)
                    .padding(bottom = 20.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.Bottom
            ) {
                // Mute
                StudioControlButton(
                    icon    = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    label   = if (isMuted) "Muet" else "Micro",
                    tint    = if (isMuted) StatusRed else Color.White,
                    onClick = { isMuted = !isMuted }
                )
                Spacer(Modifier.width(20.dp))
                // Flip camera
                StudioControlButton(
                    icon    = Icons.Default.FlipCameraAndroid,
                    label   = "Caméra",
                    tint    = Color.White,
                    onClick = { isFrontCamera = !isFrontCamera }
                )
                Spacer(Modifier.width(20.dp))
                // End live
                Surface(
                    shape   = RoundedCornerShape(24.dp),
                    color   = StatusRed,
                    onClick = {
                        CreatorRepository.endLive(liveId)
                        onEndLive()
                    },
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.StopCircle, 
                            contentDescription = null,
                            tint = Color.White, 
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "QUITTER", 
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold, 
                            fontSize = 13.sp, 
                            color = Color.White
                        )
                    }
                }
            }

            // ── Chat section ──────────────────────────────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.40f)
                    .align(Alignment.BottomCenter),
                color    = Color.White,
                shape    = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                shadowElevation = 16.dp,
                border = BorderStroke(1.dp, AppGoldColor.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Conversation en direct", 
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold, 
                            fontSize   = 16.sp, 
                            color      = AppDarkGreen
                        )
                        
                        Surface(
                            color = AppGoldColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.3f))
                        ) {
                            Text(
                                "MODÉRÉ",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AppGoldColor
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))

                    LazyColumn(
                        modifier     = Modifier.weight(1f),
                        reverseLayout = true,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(comments.reversed()) { msg ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.size(32.dp),
                                    shape = CircleShape,
                                    color = AppGoldColor.copy(alpha = 0.1f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            msg.author.take(1),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AppGoldColor
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        msg.author, 
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 12.sp, 
                                        color = AppGoldColor
                                    )
                                    Text(
                                        msg.text, 
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 14.sp, 
                                        color = AppDarkGreen.copy(alpha = 0.85f),
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value         = commentInput,
                            onValueChange = { commentInput = it },
                            modifier      = Modifier.weight(1f),
                            placeholder   = { 
                                Text(
                                    "Répondre à l'audience…", 
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 14.sp, 
                                    color = AppDarkGreen.copy(alpha = 0.4f)
                                ) 
                            },
                            singleLine    = true,
                            shape         = RoundedCornerShape(28.dp),
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor      = AppGoldColor,
                                unfocusedBorderColor    = AppDarkGreen.copy(alpha = 0.15f),
                                focusedTextColor        = AppDarkGreen,
                                unfocusedTextColor      = AppDarkGreen,
                                cursorColor             = AppGoldColor,
                                focusedContainerColor   = AppDarkGreen.copy(alpha = 0.03f),
                                unfocusedContainerColor = AppDarkGreen.copy(alpha = 0.03f)
                            )
                        )
                        IconButton(
                            onClick = {
                                if (commentInput.isNotBlank()) {
                                    comments.add(0, LiveStudioChatMessage("Maître (Vous)", commentInput.trim()))
                                    commentInput = ""
                                }
                            },
                            modifier = Modifier
                                .background(AppDarkGreen, CircleShape)
                                .size(48.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send, 
                                contentDescription = "Envoyer",
                                tint = AppGoldColor, 
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, 
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape    = CircleShape,
            color    = AppDarkGreen.copy(alpha = 0.5f),
            border   = BorderStroke(1.dp, AppGoldColor.copy(alpha = 0.4f)),
            onClick  = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
            }
        }
        Text(
            label, 
            fontFamily = FontFamily.Serif, 
            fontSize = 11.sp, 
            color = Color.White.copy(alpha = 0.90f),
            fontWeight = FontWeight.Medium
        )
    }
}
