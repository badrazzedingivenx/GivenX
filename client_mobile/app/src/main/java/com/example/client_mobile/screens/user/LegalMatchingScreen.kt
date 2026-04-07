package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.abs

// ─── Model ────────────────────────────────────────────────────────────────────

data class MatchCard(
    val id: Int,
    val name: String,
    val specialty: String,
    val city: String,
    val rating: Float,
    val yearsExp: Int,
    val isVerified: Boolean,
    val tagline: String
)

private val sampleMatchCards = listOf(
    MatchCard(1, "Maître Sara Benali",        "Droit de la Famille", "Rabat",       4.9f, 11, true,  "Spécialiste en divorce & garde d'enfants"),
    MatchCard(2, "Maître Khalid Tazi",         "Droit des Affaires",  "Casablanca",  4.7f,  8, true,  "Expert en création de sociétés & contrats"),
    MatchCard(3, "Maître Nadia Mansouri",      "Droit du Travail",    "Marrakech",   4.8f, 14, true,  "Défense des droits des salariés"),
    MatchCard(4, "Maître Youssef El Fassi",    "Droit Immobilier",    "Fès",         4.6f,  6, false, "Litiges locatifs & transactions immobilières"),
    MatchCard(5, "Maître Amina Chraibi",       "Droit Pénal",         "Casablanca",  4.95f,18, true,  "Pénaliste reconnue devant la Cour de cassation"),
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalMatchingScreen(paddingValues: PaddingValues = PaddingValues()) {

    val cards = remember { mutableStateListOf(*sampleMatchCards.toTypedArray()) }
    var matchedName by remember { mutableStateOf<String?>(null) }
    val scope = coroutineScope()

    // Show "It's a Match!" overlay briefly
    matchedName?.let { name ->
        LaunchedEffect(name) {
            kotlinx.coroutines.delay(1800)
            matchedName = null
        }
        MatchOverlay(name = name)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        // ── Header ────────────────────────────────────────────────────────
        val displayName = UserSession.name.split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: ""
        Text(
            text = if (displayName.isNotEmpty()) "Bonjour, $displayName 👋" else "Bonjour 👋",
            fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
            fontSize = 22.sp, color = AppDarkGreen
        )
        Text(
            text = "Trouvez l'avocat idéal pour votre dossier",
            fontFamily = FontFamily.Serif, fontSize = 13.sp,
            color = AppDarkGreen.copy(alpha = 0.55f)
        )

        Spacer(Modifier.height(20.dp))

        if (cards.isEmpty()) {
            // ── Empty State ───────────────────────────────────────────────
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.HowToReg, contentDescription = null,
                        tint = AppGoldColor, modifier = Modifier.size(64.dp))
                    Text("Plus de profils disponibles", fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppDarkGreen)
                    Text("Revenez bientôt !", fontFamily = FontFamily.Serif,
                        fontSize = 13.sp, color = AppDarkGreen.copy(alpha = 0.55f))
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { cards.addAll(sampleMatchCards) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppDarkGreen)
                    ) { Text("Recharger", fontFamily = FontFamily.Serif, color = AppGoldColor, fontWeight = FontWeight.Bold) }
                }
            }
        } else {
            // ── Card Stack ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Background card (next one peeking behind)
                if (cards.size >= 2) {
                    PeekCard(cards[1])
                }
                // Top swipeable card
                SwipeableMatchCard(
                    card    = cards[0],
                    onPass  = { cards.removeAt(0) },
                    onMatch = { cards.removeAt(0); matchedName = cards.getOrNull(0)?.name ?: it }
                )
            }

            // ── Action Buttons ────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pass
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.5.dp, Color(0xFFE57373)),
                    shadowElevation = 4.dp,
                    onClick = { if (cards.isNotEmpty()) cards.removeAt(0) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Close, contentDescription = "Passer",
                            tint = Color(0xFFE57373), modifier = Modifier.size(26.dp))
                    }
                }
                // Info
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.dp, AppDarkGreen.copy(alpha = 0.20f)),
                    shadowElevation = 2.dp,
                    onClick = {}
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Info, contentDescription = "Info",
                            tint = AppDarkGreen.copy(alpha = 0.55f), modifier = Modifier.size(20.dp))
                    }
                }
                // Match
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    color = AppDarkGreen,
                    border = BorderStroke(1.5.dp, AppGoldColor),
                    shadowElevation = 6.dp,
                    onClick = {
                        if (cards.isNotEmpty()) {
                            val name = cards[0].name
                            cards.removeAt(0)
                            matchedName = name
                        }
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Favorite, contentDescription = "Matcher",
                            tint = AppGoldColor, modifier = Modifier.size(26.dp))
                    }
                }
            }
        }
    }
}

// ─── Swipeable Card ───────────────────────────────────────────────────────────

@Composable
private fun SwipeableMatchCard(
    card   : MatchCard,
    onPass : () -> Unit,
    onMatch: (String) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animOffsetX by animateFloatAsState(targetValue = offsetX, label = "cardSwipe")
    val rotation = (animOffsetX / 25f).coerceIn(-20f, 20f)

    val passAlpha  by animateFloatAsState(targetValue = if (offsetX < -40f) ((abs(offsetX) - 40f) / 120f).coerceIn(0f, 1f) else 0f, label = "passAlpha")
    val matchAlpha by animateFloatAsState(targetValue = if (offsetX >  40f) ((offsetX - 40f) / 120f).coerceIn(0f, 1f) else 0f, label = "matchAlpha")

    Box(
        modifier = Modifier
            .offset(x = animOffsetX.dp)
            .rotate(rotation)
            .pointerInput(card.id) {
                detectDragGestures(
                    onDragEnd = {
                        when {
                            offsetX < -150f -> onPass()
                            offsetX >  150f -> onMatch(card.name)
                        }
                        offsetX = 0f
                    },
                    onDrag = { _, dragAmount -> offsetX += dragAmount.x * 0.5f }
                )
            }
    ) {
        MatchCardContent(card = card)

        // Pass badge
        if (passAlpha > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(20.dp)
                    .background(Color(0xFFE57373).copy(alpha = passAlpha), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) { Text("PASSER", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp) }
        }
        // Match badge
        if (matchAlpha > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
                    .background(AppDarkGreen.copy(alpha = matchAlpha), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) { Text("MATCH ✨", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = AppGoldColor, fontSize = 14.sp) }
        }
    }
}

@Composable
private fun PeekCard(card: MatchCard) {
    Box(modifier = Modifier
        .offset(y = 14.dp)
        .fillMaxWidth(0.92f)
        .graphicsLayerAlpha(0.55f)
    ) {
        MatchCardContent(card = card)
    }
}

@Composable
private fun MatchCardContent(card: MatchCard) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(28.dp),
        color           = Color.White,
        border          = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.10f)),
        shadowElevation = 8.dp
    ) {
        Column {
            // ── Colored header banner ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(listOf(AppDarkGreen, AppDarkGreen.copy(alpha = 0.75f))),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape    = CircleShape,
                        color    = AppGoldColor.copy(alpha = 0.18f),
                        border   = BorderStroke(2.dp, AppGoldColor)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(36.dp))
                        }
                    }
                    if (card.isVerified) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(14.dp))
                            Text("Vérifié", fontFamily = FontFamily.Serif, fontSize = 11.sp, color = AppGoldColor)
                        }
                    }
                }
            }

            // ── Info section ───────────────────────────────────────────────
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(card.name, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = AppDarkGreen)
                Text(card.tagline, fontFamily = FontFamily.Serif, fontSize = 13.sp,
                    color = AppDarkGreen.copy(alpha = 0.60f))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusChip(label = card.specialty, containerColor = AppDarkGreen, textColor = AppGoldColor)
                    StatusChip(label = card.city, containerColor = AppDarkGreen.copy(alpha = 0.08f), textColor = AppDarkGreen)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(15.dp))
                        Text("${card.rating}", fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = AppDarkGreen)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.WorkHistory, contentDescription = null, tint = AppDarkGreen.copy(alpha = 0.55f), modifier = Modifier.size(14.dp))
                        Text("${card.yearsExp} ans d'exp.", fontFamily = FontFamily.Serif, fontSize = 12.sp, color = AppDarkGreen.copy(alpha = 0.55f))
                    }
                }
            }
        }
    }
}

// ─── Match Overlay ────────────────────────────────────────────────────────────

@Composable
private fun MatchOverlay(name: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppDarkGreen.copy(alpha = 0.88f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(72.dp))
            Text("C'est un Match !", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                fontSize = 30.sp, color = AppGoldColor, textAlign = TextAlign.Center)
            Text(name, fontFamily = FontFamily.Serif, fontSize = 17.sp,
                color = Color.White, textAlign = TextAlign.Center)
            Text("a accepté votre demande", fontFamily = FontFamily.Serif,
                fontSize = 13.sp, color = Color.White.copy(alpha = 0.70f))
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

@Composable
private fun coroutineScope() = rememberCoroutineScope()

private fun Modifier.graphicsLayerAlpha(alphaValue: Float) = this.alpha(alphaValue)
