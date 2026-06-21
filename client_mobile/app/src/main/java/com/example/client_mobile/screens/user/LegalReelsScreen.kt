package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*
import com.example.client_mobile.network.TokenManager

import android.view.ViewGroup
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

// ─── Brand Tokens ─────────────────────────────────────────────────────────────
private val ReelGold   = Color(0xFFC5A059)
private val ReelGreen  = Color(0xFF1A3C34)

// ─── Model ────────────────────────────────────────────────────────────────────

data class LegalReel(
    val id: Int,
    val lawyerName: String,
    val specialty: String,
    val title: String,
    val likes: Int,
    val comments: Int = 0,
    val shares: Int = 0,
    val views: String,
    val videoUrl: String = "",
    val isLiked: Boolean = false,
    val isLive: Boolean = false
)


// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalReelsScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onBack: () -> Unit = {},
    onReserveClicked: (lawyerId: String, lawyerName: String) -> Unit = { _, _ -> },
    viewModel: ReelViewModel = viewModel()
) {
    val apiReels     by viewModel.reels.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isError      by viewModel.isError.collectAsStateWithLifecycle()

    val allReels = remember { mutableStateListOf<LegalReel>() }

    LaunchedEffect(apiReels, CreatorRepository.reels.size) {
        val loaded = apiReels ?: return@LaunchedEffect
        val creatorMapped = CreatorRepository.reels.map { cr ->
            LegalReel(
                id         = cr.id.toInt(),
                lawyerName = cr.lawyerName,
                specialty  = cr.specialty,
                title      = cr.title,
                likes      = cr.likes,
                views      = "${cr.views}",
                isLiked    = cr.isLiked
            )
        }
        allReels.clear()
        allReels.addAll(creatorMapped + loaded)
    }

    // ── Error state ───────────────────────────────────────────────────────────
    if (isError && allReels.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ReelGreen)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            NoConnectionScreen(onRetry = { viewModel.refresh() })
        }
        return
    }

    // ── Loading state ─────────────────────────────────────────────────────────
    if (apiReels == null && allReels.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ReelGreen)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ReelGold, strokeWidth = 3.dp)
        }
        return
    }

    // ── Main Content ──────────────────────────────────────────────────────────
    val pagerState = rememberPagerState { allReels.size }
    var isMuted by remember { mutableStateOf(false) }

    // (Internal ReservationSheet has been hoisted out to MainDashboardHost)

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh    = { viewModel.refresh() },
            modifier     = Modifier.fillMaxSize()
        ) {
            VerticalPager(
                state    = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val reel = allReels.getOrNull(page) ?: return@VerticalPager
                val isActive = pagerState.currentPage == page

                ReelPage(
                    reel     = reel,
                    isActive = isActive,
                    isMuted  = isMuted,
                    onBookConsultation = {
                        onReserveClicked(reel.id.toString(), reel.lawyerName)
                    },
                    onLike   = {
                        val apiIdx = (apiReels ?: emptyList()).indexOfFirst { it.id == reel.id }
                        if (apiIdx >= 0) {
                            viewModel.toggleLike(apiIdx)
                        } else {
                            val idx = allReels.indexOf(reel)
                            if (idx >= 0) {
                                val delta = if (reel.isLiked) -1 else 1
                                allReels[idx] = reel.copy(
                                    isLiked = !reel.isLiked,
                                    likes   = reel.likes + delta
                                )
                            }
                        }
                    }
                )
            }
        }

        // ── Top Overlay Bar ───────────────────────────────────────────────────
        ReelsTopBar(
            onBack  = onBack,
            isMuted = isMuted,
            onToggleMute = { isMuted = !isMuted },
            modifier = Modifier
                .zIndex(10f)
                .statusBarsPadding()
                .align(Alignment.TopCenter)
        )
    }
}

// ─── Top Overlay Bar ──────────────────────────────────────────────────────────

@Composable
private fun ReelsTopBar(
    onBack: () -> Unit,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back arrow
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Retour",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Center title
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "●",
                color    = ReelGold,
                fontSize = 10.sp,
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                "Expert Quick-Tips",
                color      = Color.White,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 17.sp
            )
        }

        // Mute/Unmute
        IconButton(onClick = onToggleMute) {
            Icon(
                if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = if (isMuted) "Activer le son" else "Couper le son",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ─── Reel Page ────────────────────────────────────────────────────────────────

@Composable
private fun ReelPage(
    reel: LegalReel,
    isActive: Boolean,
    isMuted: Boolean,
    onBookConsultation: () -> Unit,
    onLike: () -> Unit
) {
    val context = LocalContext.current

    // ── Fix 3: Explicit 1.0f playback speed + play/pause state ─────────────
    var isPlaying by remember { mutableStateOf(true) }
    // Pulse icon state: null = hidden, true = play icon, false = pause icon
    var pulseIcon by remember { mutableStateOf<Boolean?>(null) }
    val pulseAlpha by animateFloatAsState(
        targetValue  = if (pulseIcon != null) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label        = "pulseAlpha"
    )

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode         = Player.REPEAT_MODE_ONE
            volume             = 0f
            // Explicitly reset speed=1.0f AND pitch=1.0f to guarantee normal playback.
            // PlaybackParameters is the canonical approach; setPlaybackSpeed() only
            // sets speed but does not guarantee the pitch multiplier is reset.
            playbackParameters = PlaybackParameters(1.0f)
        }
    }

    // Manage playback and mute state
    LaunchedEffect(reel.videoUrl, isActive) {
        if (reel.videoUrl.isNotBlank()) {
            exoPlayer.setMediaItem(MediaItem.fromUri(reel.videoUrl))
            exoPlayer.prepare()
            isPlaying = isActive
            exoPlayer.playWhenReady = isActive
        } else {
            isPlaying = false
            exoPlayer.pause()
        }
    }

    LaunchedEffect(isMuted, isActive) {
        exoPlayer.volume = if (isMuted || !isActive) 0f else 1f
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Fix 4: Video area — tap to toggle play/pause ──────────────────
        val interactionSource = remember { MutableInteractionSource() }
        if (reel.videoUrl.isNotBlank()) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player        = exoPlayer
                        useController = false
                        layoutParams  = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(interactionSource = interactionSource, indication = null) {
                        isPlaying = !isPlaying
                        exoPlayer.playWhenReady = isPlaying
                        pulseIcon = isPlaying  // true=play icon, false=pause icon
                    }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                ReelGreen,
                                Color(0xFF0D1F17),
                                Color.Black.copy(alpha = 0.95f)
                            )
                        )
                    )
            )
        }

        // ── Top + bottom gradient scrim ───────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                    )
                )
        )

        // ── Fix 4: Animated play/pause flash icon (center) ───────────────
        // Shown briefly when user taps to toggle playback, then auto-hides.
        if (pulseIcon != null) {
            LaunchedEffect(pulseIcon) {
                kotlinx.coroutines.delay(700)
                pulseIcon = null
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 180.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(76.dp)
                        .scale(pulseAlpha),
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.45f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector        = if (pulseIcon == true) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (pulseIcon == true) "Lecture" else "Pause",
                            tint               = Color.White,
                            modifier           = Modifier.size(44.dp)
                        )
                    }
                }
            }
        }

        // Static play icon shown only when there is no video at all
        if (reel.videoUrl.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 200.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape    = CircleShape,
                    color    = Color.White.copy(alpha = 0.12f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Lire",
                            tint     = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }

        // ── Right sidebar (interaction column) ────────────────────────────
        ReelSidebar(
            reel   = reel,
            onLike = onLike,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp, bottom = 40.dp)
                .offset(y = 60.dp)
        )

        // ── Bottom info card ──────────────────────────────────────────────
        ReelBottomCard(
            reel = reel,
            onBookConsultation = onBookConsultation,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp)
                .padding(bottom = 100.dp)
        )
    }
}

// ─── Right Sidebar (Interaction Column) ───────────────────────────────────────

@Composable
private fun ReelSidebar(
    reel: LegalReel,
    onLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ── Lawyer avatar with gold '+' badge ─────────────────────────────
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape    = CircleShape,
                color    = ReelGreen,
                shadowElevation = 4.dp,
                border   = androidx.compose.foundation.BorderStroke(2.dp, ReelGold)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint     = ReelGold,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            // Gold '+' badge
            Surface(
                modifier = Modifier
                    .size(20.dp)
                    .offset(x = 2.dp, y = 2.dp),
                shape = CircleShape,
                color = ReelGold
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Suivre",
                        tint     = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ── Like ──────────────────────────────────────────────────────────
        val likeScale by animateFloatAsState(
            targetValue = if (reel.isLiked) 1.2f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "like_bounce"
        )
        SidebarAction(
            icon     = if (reel.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            count    = formatCount(reel.likes),
            tint     = if (reel.isLiked) Color(0xFFFF4458) else Color.White,
            onClick  = onLike,
            iconScale = likeScale
        )

        // ── Comment ───────────────────────────────────────────────────────
        SidebarAction(
            icon    = Icons.Default.ChatBubbleOutline,
            count   = formatCount(reel.comments),
            tint    = Color.White,
            onClick = {}
        )

        // ── Share ─────────────────────────────────────────────────────────
        SidebarAction(
            icon    = Icons.Default.Share,
            count   = formatCount(reel.shares),
            tint    = Color.White,
            onClick = {}
        )
    }
}

@Composable
private fun SidebarAction(
    icon: ImageVector,
    count: String,
    tint: Color,
    onClick: () -> Unit,
    iconScale: Float = 1f
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = tint,
            modifier           = Modifier
                .size(30.dp)
                .scale(iconScale)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text       = count,
            color      = Color.White,
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
            textAlign  = TextAlign.Center
        )
    }
}

// ─── Bottom Info Card (Glass Layer) ───────────────────────────────────────────

@Composable
private fun ReelBottomCard(
    reel: LegalReel,
    onBookConsultation: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(28.dp),
        color    = Color.Black.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ── Row 1: Lawyer name + verified + specialty chip ────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // lawyerName from API already includes full title, e.g. "Maître Youssef El Alami"
                    Text(
                        text       = reel.lawyerName,
                        color      = Color.White,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis,
                        modifier   = Modifier.widthIn(max = 180.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Gold verified badge
                    Surface(
                        modifier = Modifier.size(18.dp),
                        shape    = CircleShape,
                        color    = ReelGold
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Vérifié",
                                tint     = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Fix 1: Specialty badge — constrained width + single line with ellipsis
                if (reel.specialty.isNotBlank()) {
                    Surface(
                        shape    = RoundedCornerShape(16.dp),
                        color    = Color.Transparent,
                        border   = androidx.compose.foundation.BorderStroke(1.dp, ReelGold),
                        modifier = Modifier.widthIn(max = 140.dp)
                    ) {
                        Text(
                            text       = reel.specialty,
                            color      = ReelGold,
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Serif,
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis,
                            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // ── Fix 2: Expandable caption with "Voir plus" / "Voir moins" ─
            if (reel.title.isNotBlank()) {
                var isExpanded by remember(reel.id) { mutableStateOf(false) }
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Balance,
                        contentDescription = null,
                        tint     = ReelGold,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Column {
                        Text(
                            text       = reel.title,
                            color      = Color.White.copy(alpha = 0.90f),
                            fontFamily = FontFamily.Serif,
                            fontSize   = 13.sp,
                            lineHeight = 18.sp,
                            maxLines   = if (isExpanded) Int.MAX_VALUE else 2,
                            overflow   = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis
                        )
                        Text(
                            text     = if (isExpanded) "Voir moins" else "Voir plus...",
                            color    = ReelGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                    isExpanded = !isExpanded
                                }
                                .padding(top = 2.dp)
                        )
                    }
                }
            }

            // ── CTA Button ────────────────────────────────────────────────
            if (TokenManager.getUserType() != "lawyer") {
                Button(
                    onClick  = onBookConsultation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape  = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ReelGold,
                        contentColor   = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Réserver une Consultation",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 14.sp
                    )
                }
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000     -> String.format("%.1fk", count / 1_000.0)
        else               -> "$count"
    }
}


