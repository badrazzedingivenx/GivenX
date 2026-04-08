package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.CoroutineScope

// ─── Model ────────────────────────────────────────────────────────────────────

data class MatchCard(
    val id: Int,
    val name: String,
    val specialty: String,
    val city: String,
    val rating: Float,
    val yearsExp: Int,
    val isVerified: Boolean,
    val tagline: String,
    val matchPercent: Int = 90
)

private val sampleMatchCards = listOf(
    MatchCard(1, "Maître Sara Benali",       "Droit de la Famille", "Rabat",      4.9f, 11, true,  "Spécialiste en divorce & garde d'enfants",  96),
    MatchCard(2, "Maître Khalid Tazi",        "Droit des Affaires",  "Casablanca", 4.7f,  8, true,  "Expert en création de sociétés & contrats",  88),
    MatchCard(3, "Maître Nadia Mansouri",     "Droit du Travail",    "Marrakech",  4.8f, 14, true,  "Défense des droits des salariés",            92),
    MatchCard(4, "Maître Youssef El Fassi",   "Droit Immobilier",    "Fès",        4.6f,  6, false, "Litiges locatifs & transactions immobilières", 79),
    MatchCard(5, "Maître Amina Chraibi",      "Droit Pénal",         "Casablanca", 4.95f,18, true,  "Pénaliste reconnue devant la Cour de cassation", 98),
)

// gradient palette per specialty
private fun specialtyGradient(specialty: String): List<Color> = when {
    specialty.contains("Famille")    -> listOf(Color(0xFF1B3124), Color(0xFF2D5A40))
    specialty.contains("Affaires")   -> listOf(Color(0xFF1A2A3A), Color(0xFF1B3124))
    specialty.contains("Travail")    -> listOf(Color(0xFF1B3124), Color(0xFF3D2B00))
    specialty.contains("Immobilier") -> listOf(Color(0xFF22304A), Color(0xFF1B3124))
    specialty.contains("Pénal")      -> listOf(Color(0xFF2C1B1B), Color(0xFF1B3124))
    else                             -> listOf(Color(0xFF1B3124), Color(0xFF2D4A32))
}

// ─── Screen ───────────────────────────────────────────────────────────────────

private const val SWIPE_THRESHOLD = 160f

@Composable
fun LegalMatchingScreen(paddingValues: PaddingValues = PaddingValues()) {
    var currentIndex   by remember { mutableIntStateOf(0) }
    var matchedName    by remember { mutableStateOf<String?>(null) }
    val scope          = rememberCoroutineScope()

    // Animatables owned here so we fully control reset — never mutate the card list
    val offsetX        = remember { Animatable(0f) }
    val offsetY        = remember { Animatable(0f) }
    var isAnimatingOut by remember { mutableStateOf(false) }

    // Normalised [0,1] progress of the current swipe — drives peek card parallax
    val swipeProgress by remember {
        derivedStateOf { (abs(offsetX.value) / SWIPE_THRESHOLD).coerceIn(0f, 1f) }
    }

    // Fly the top card off-screen then advance the index
    fun doPass() {
        if (isAnimatingOut || currentIndex >= sampleMatchCards.size) return
        isAnimatingOut = true
        scope.launch {
            val j1 = launch { offsetX.animateTo(-1400f, tween(300, easing = FastOutLinearInEasing)) }
            val j2 = launch { offsetY.animateTo(offsetY.value + 40f, tween(270)) }
            j1.join(); j2.join()
            currentIndex++
            offsetX.snapTo(0f)
            offsetY.snapTo(0f)
            isAnimatingOut = false
        }
    }

    fun doMatch() {
        if (isAnimatingOut || currentIndex >= sampleMatchCards.size) return
        val name = sampleMatchCards[currentIndex].name
        isAnimatingOut = true
        scope.launch {
            val j1 = launch { offsetX.animateTo(1400f, tween(300, easing = FastOutLinearInEasing)) }
            val j2 = launch { offsetY.animateTo(offsetY.value + 40f, tween(270)) }
            j1.join(); j2.join()
            currentIndex++
            offsetX.snapTo(0f)
            offsetY.snapTo(0f)
            isAnimatingOut = false
            matchedName = name
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Header ────────────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                val displayName = UserSession.name.split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: ""
                Text(
                    text = if (displayName.isNotEmpty()) "Bonjour, $displayName" else "Bonjour",
                    fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                    fontSize = 24.sp, color = AppDarkGreen
                )
                Text(
                    text = "Trouvez l'avocat idéal pour votre dossier",
                    fontFamily = FontFamily.Serif, fontSize = 13.sp,
                    color = AppDarkGreen.copy(alpha = 0.50f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(2.dp)
                        .background(AppGoldColor, RoundedCornerShape(1.dp))
                )
            }

            if (currentIndex >= sampleMatchCards.size) {
                EmptyMatchState(onReload = { currentIndex = 0 })
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 3rd card — interpolates toward depth-1 style as the top card moves
                    sampleMatchCards.getOrNull(currentIndex + 2)?.let { card ->
                        PeekCardLayer(
                            card   = card,
                            scale  = lerp(0.86f, 0.93f, swipeProgress),
                            yOffDp = lerp(34f,   18f,   swipeProgress),
                            alpha  = lerp(0.30f, 0.52f, swipeProgress)
                        )
                    }
                    // 2nd card — scales up to full size as the top card is swiped away
                    sampleMatchCards.getOrNull(currentIndex + 1)?.let { card ->
                        PeekCardLayer(
                            card   = card,
                            scale  = lerp(0.93f, 1.00f, swipeProgress),
                            yOffDp = lerp(18f,   2f,    swipeProgress),
                            alpha  = lerp(0.62f, 1.00f, swipeProgress)
                        )
                    }
                    // Top swipeable card
                    SwipeableMatchCard(
                        card      = sampleMatchCards[currentIndex],
                        cardIndex = currentIndex,
                        offsetX   = offsetX,
                        offsetY   = offsetY,
                        isLocked  = isAnimatingOut,
                        scope     = scope,
                        onPass    = ::doPass,
                        onMatch   = ::doMatch
                    )
                }

                ActionButtonRow(
                    onPass  = ::doPass,
                    onInfo  = {},
                    onMatch = ::doMatch
                )
            }
        }

        // ── Match Overlay ─────────────────────────────────────────────────
        matchedName?.let { name ->
            LaunchedEffect(name) {
                delay(2200)
                matchedName = null
            }
            MatchOverlay(name = name)
        }
    }
}

// ─── Peek Card Layer (animated by swipeProgress) ─────────────────────────────

@Composable
private fun PeekCardLayer(
    card   : MatchCard,
    scale  : Float,
    yOffDp : Float,
    alpha  : Float
) {
    Surface(
        modifier = Modifier
            .offset(y = yOffDp.dp)
            .fillMaxWidth()
            .graphicsLayer {
                scaleX     = scale
                scaleY     = scale
                this.alpha = alpha
            },
        shape           = RoundedCornerShape(32.dp),
        color           = Color.White,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
                .background(Brush.verticalGradient(specialtyGradient(card.specialty)))
        )
    }
}

// ─── Swipeable Card ───────────────────────────────────────────────────────────

@Composable
private fun SwipeableMatchCard(
    card      : MatchCard,
    cardIndex : Int,
    offsetX   : Animatable<Float, AnimationVector1D>,
    offsetY   : Animatable<Float, AnimationVector1D>,
    isLocked  : Boolean,
    scope     : CoroutineScope,
    onPass    : () -> Unit,
    onMatch   : () -> Unit
) {
    // rememberUpdatedState ensures the gesture handler always sees the latest lock flag
    // without needing to restart the pointerInput block
    val lockedState = rememberUpdatedState(isLocked)

    // These read offsetX.value (a State) — the graphicsLayer re-executes on the render
    // thread without triggering a full recomposition of this composable
    val shadowPx = with(LocalDensity.current) { 18.dp.toPx() }

    val passAlpha  = if (offsetX.value < -30f) ((abs(offsetX.value) - 30f) / 130f).coerceIn(0f, 1f) else 0f
    val matchAlpha = if (offsetX.value >  30f) ((offsetX.value - 30f) / 130f).coerceIn(0f, 1f) else 0f
    val glowColor  = when {
        matchAlpha > 0f -> Color(0xFF34A853).copy(alpha = matchAlpha * 0.22f)
        passAlpha  > 0f -> Color(0xFFE53935).copy(alpha = passAlpha  * 0.22f)
        else            -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            // graphicsLayer runs on the render thread — translationX/Y/rotationZ updates
            // bypass composition entirely for maximum 60fps smoothness
            .graphicsLayer {
                translationX    = offsetX.value
                translationY    = offsetY.value
                rotationZ       = (offsetX.value / 18f).coerceIn(-22f, 22f)
                shadowElevation = shadowPx
                shape           = RoundedCornerShape(32.dp)
                clip            = true
            }
            // key = cardIndex: restarts the gesture block when a new card becomes active,
            // cancelling any in-progress drag from the previous card
            .pointerInput(cardIndex) {
                detectDragGestures(
                    onDragEnd = {
                        if (lockedState.value) return@detectDragGestures
                        when {
                            offsetX.value < -SWIPE_THRESHOLD -> onPass()
                            offsetX.value >  SWIPE_THRESHOLD -> onMatch()
                            else -> scope.launch {
                                // Spring snap-back — both axes animate in parallel
                                val springSpec = spring<Float>(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness    = Spring.StiffnessMediumLow
                                )
                                val j1 = launch { offsetX.animateTo(0f, springSpec) }
                                val j2 = launch { offsetY.animateTo(0f, springSpec) }
                                j1.join(); j2.join()
                            }
                        }
                    },
                    onDrag = { _, drag ->
                        if (!lockedState.value) {
                            // snapTo follows the finger with zero latency
                            scope.launch {
                                offsetX.snapTo(offsetX.value + drag.x * 0.62f)
                                offsetY.snapTo(offsetY.value + drag.y * 0.22f)
                            }
                        }
                    }
                )
            }
    ) {
        PremiumCardContent(card = card)

        // Colour glow overlay
        if (glowColor != Color.Transparent) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(32.dp))
                    .background(glowColor)
            )
        }

        // PASSER stamp
        AnimatedVisibility(
            visible  = passAlpha > 0.12f,
            modifier = Modifier.align(Alignment.TopStart).padding(22.dp),
            enter = fadeIn() + scaleIn(initialScale = 0.75f),
            exit  = fadeOut() + scaleOut()
        ) {
            StampBadge(text = "PASSER", color = Color(0xFFE53935), alpha = passAlpha)
        }

        // MATCH stamp
        AnimatedVisibility(
            visible  = matchAlpha > 0.12f,
            modifier = Modifier.align(Alignment.TopEnd).padding(22.dp),
            enter = fadeIn() + scaleIn(initialScale = 0.75f),
            exit  = fadeOut() + scaleOut()
        ) {
            StampBadge(text = "MATCH ✨", color = Color(0xFF34A853), alpha = matchAlpha, rotate = -12f)
        }

        // TOP MATCH badge
        if (card.matchPercent >= 90) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 14.dp)
                    .background(
                        Brush.horizontalGradient(listOf(AppGoldColor, Color(0xFFF5C842))),
                        RoundedCornerShape(50.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Text(
                    "⚡ TOP MATCH ${card.matchPercent}%",
                    fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                    fontSize = 11.sp, color = AppDarkGreen
                )
            }
        }
    }
}

// ─── Premium Card Content ─────────────────────────────────────────────────────

@Composable
private fun PremiumCardContent(card: MatchCard) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(32.dp),
        color    = Color.Transparent
    ) {
        Column {

            // ── Hero image area ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(
                        Brush.verticalGradient(specialtyGradient(card.specialty)),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
            ) {
                // Soft radial glow behind avatar
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.Center)
                        .background(
                            Brush.radialGradient(
                                listOf(AppGoldColor.copy(alpha = 0.18f), Color.Transparent)
                            ),
                            CircleShape
                        )
                        .blur(40.dp)
                )

                // Avatar circle
                Surface(
                    modifier = Modifier
                        .size(110.dp)
                        .align(Alignment.Center)
                        .offset(y = (-20).dp),
                    shape  = CircleShape,
                    color  = AppGoldColor.copy(alpha = 0.12f),
                    border = BorderStroke(2.5.dp, AppGoldColor)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Gavel,
                            contentDescription = null,
                            tint     = AppGoldColor,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                // Verified badge — bottom of avatar
                if (card.isVerified) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = 36.dp, y = 38.dp),
                        shape = CircleShape,
                        color = AppGoldColor
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Vérifié",
                            tint     = AppDarkGreen,
                            modifier = Modifier.size(22.dp).padding(4.dp)
                        )
                    }
                }

                // Bottom gradient fade → info area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xFF0E1F16).copy(alpha = 0.85f))
                            )
                        )
                )

                // Name & tagline on the gradient
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 22.dp, bottom = 18.dp, end = 56.dp)
                ) {
                    Text(
                        card.name,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 21.sp,
                        color      = Color.White
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        card.tagline,
                        fontFamily = FontFamily.Serif,
                        fontSize   = 12.sp,
                        color      = Color.White.copy(alpha = 0.72f)
                    )
                }

                // Rating pill — top right
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp),
                    shape = RoundedCornerShape(50.dp),
                    color = Color.Black.copy(alpha = 0.38f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null,
                            tint = AppGoldColor, modifier = Modifier.size(13.dp))
                        Text(
                            "${card.rating}",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 13.sp,
                            color      = Color.White
                        )
                    }
                }
            }

            // ── Info section (glassmorphism-style white panel) ──────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                color    = Color.White
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {

                    // Badges row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GlassBadge(
                            icon  = Icons.Default.Balance,
                            label = card.specialty
                        )
                        GlassBadge(
                            icon  = Icons.Default.LocationOn,
                            label = card.city
                        )
                        GlassBadge(
                            icon  = Icons.Default.WorkHistory,
                            label = "${card.yearsExp} ans"
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Match % progress bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "Compatibilité",
                            fontFamily = FontFamily.Serif,
                            fontSize   = 12.sp,
                            color      = Color.Gray,
                            modifier   = Modifier.width(90.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(7.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(AppDarkGreen.copy(alpha = 0.08f))
                        ) {
                            val pct = card.matchPercent / 100f
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(pct)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(AppDarkGreen, AppGoldColor)
                                        )
                                    )
                            )
                        }
                        Text(
                            "${card.matchPercent}%",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 12.sp,
                            color      = AppDarkGreen
                        )
                    }
                }
            }
        }
    }
}

// ─── Glass Badge ─────────────────────────────────────────────────────────────

@Composable
private fun GlassBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = AppDarkGreen.copy(alpha = 0.06f),
        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector     = icon,
                contentDescription = null,
                tint            = AppGoldColor,
                modifier        = Modifier.size(12.dp)
            )
            Text(
                text       = label,
                fontFamily = FontFamily.Serif,
                fontSize   = 11.sp,
                color      = AppDarkGreen,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ─── Stamp Badge ─────────────────────────────────────────────────────────────

@Composable
private fun StampBadge(text: String, color: Color, alpha: Float, rotate: Float = 12f) {
    Box(
        modifier = Modifier
            .rotate(rotate)
            .background(Color.Transparent)
            .padding(2.dp)
    ) {
        Text(
            text       = text,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 22.sp,
            color      = color.copy(alpha = alpha.coerceIn(0f, 1f)),
            modifier   = Modifier
                .background(
                    color.copy(alpha = alpha * 0.12f),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// ─── Action Button Row ────────────────────────────────────────────────────────

@Composable
private fun ActionButtonRow(
    onPass  : () -> Unit,
    onInfo  : () -> Unit,
    onMatch : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // Pass button — red elevated
        FloatingActionButton(
            onClick          = onPass,
            modifier         = Modifier.size(62.dp),
            shape            = CircleShape,
            containerColor   = Color.White,
            contentColor     = Color(0xFFE53935),
            elevation        = FloatingActionButtonDefaults.elevation(
                defaultElevation  = 8.dp,
                pressedElevation  = 2.dp,
                hoveredElevation  = 12.dp
            )
        ) {
            Icon(Icons.Default.Close, contentDescription = "Passer", modifier = Modifier.size(28.dp))
        }

        // Info button — small neutral
        FloatingActionButton(
            onClick        = onInfo,
            modifier       = Modifier.size(46.dp),
            shape          = CircleShape,
            containerColor = Color.White,
            contentColor   = AppDarkGreen.copy(alpha = 0.45f),
            elevation      = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp, pressedElevation = 1.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = "Info", modifier = Modifier.size(20.dp))
        }

        // Match button — gold/green elevated
        FloatingActionButton(
            onClick        = onMatch,
            modifier       = Modifier.size(62.dp),
            shape          = CircleShape,
            containerColor = AppDarkGreen,
            contentColor   = AppGoldColor,
            elevation      = FloatingActionButtonDefaults.elevation(
                defaultElevation = 10.dp,
                pressedElevation = 2.dp,
                hoveredElevation = 16.dp
            )
        ) {
            Icon(Icons.Default.Favorite, contentDescription = "Matcher", modifier = Modifier.size(28.dp))
        }
    }
}

// ─── Empty State ─────────────────────────────────────────────────────────────

@Composable
private fun EmptyMatchState(onReload: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                modifier = Modifier.size(84.dp),
                shape    = CircleShape,
                color    = AppDarkGreen.copy(alpha = 0.07f),
                border   = BorderStroke(1.5.dp, AppGoldColor.copy(alpha = 0.40f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.HowToReg,
                        contentDescription = null,
                        tint     = AppGoldColor,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Text(
                "Plus de profils disponibles",
                fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                fontSize = 18.sp, color = AppDarkGreen, textAlign = TextAlign.Center
            )
            Text(
                "Vous avez consulté tous les avocats.\nRevenez bientôt !",
                fontFamily = FontFamily.Serif, fontSize = 13.sp,
                color = AppDarkGreen.copy(alpha = 0.50f), textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onReload,
                shape   = RoundedCornerShape(16.dp),
                colors  = ButtonDefaults.buttonColors(containerColor = AppDarkGreen),
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = AppGoldColor,
                    modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Recharger", fontFamily = FontFamily.Serif,
                    color = AppGoldColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─── Match Overlay ────────────────────────────────────────────────────────────

@Composable
private fun MatchOverlay(name: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue   = 1f,
        targetValue    = 1.12f,
        animationSpec  = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label          = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(AppDarkGreen.copy(alpha = 0.95f), Color(0xFF0D1F14).copy(alpha = 0.97f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                tint     = AppGoldColor,
                modifier = Modifier.size(80.dp).scale(pulseScale)
            )
            Text(
                "C'est un Match !",
                fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                fontSize = 32.sp, color = AppGoldColor, textAlign = TextAlign.Center
            )
            Text(
                name,
                fontFamily = FontFamily.Serif, fontSize = 18.sp,
                color = Color.White, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium
            )
            Text(
                "a accepté votre demande de connexion",
                fontFamily = FontFamily.Serif, fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.60f), textAlign = TextAlign.Center
            )
        }
    }
}

