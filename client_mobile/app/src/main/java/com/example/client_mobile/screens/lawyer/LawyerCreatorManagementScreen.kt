package com.example.client_mobile.screens.lawyer

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.client_mobile.network.dto.LiveDto
import com.example.client_mobile.network.dto.ReelDto
import com.example.client_mobile.network.dto.StoryDto
import com.example.client_mobile.screens.shared.AppDarkGreen
import com.example.client_mobile.screens.shared.AppGoldColor
import com.example.client_mobile.screens.shared.CreatorRepository
import com.example.client_mobile.screens.shared.DashBoardBackground
import com.example.client_mobile.screens.shared.LawyerSession

// ── Branding tokens for the creator studio ─────────────────────────────────────
private val StudioIndigo     = Color(0xFF3730A3)  // Indigo-800
private val StudioIndigoDark = Color(0xFF1E1B4B)  // Indigo-950
private val StudioIndigoMid  = Color(0xFF4F46E5)  // Indigo-600
private val StudioAccent     = Color(0xFF818CF8)  // Indigo-400
private val StudioBg         = Color(0xFFF8FAFF)  // near-white blue tint
private val StudioGold       = AppGoldColor

private val IndigoGradient = Brush.linearGradient(
    colors = listOf(StudioIndigoMid, Color(0xFF7C3AED), StudioAccent)
)
private val GoldGradient = Brush.linearGradient(
    colors = listOf(StudioGold, Color(0xFFFFA000), StudioGold)
)

// ─── Creator Studio Screen ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerCreatorManagementScreen(
    onBack: () -> Unit,
    viewModel: CreatorViewModel = viewModel()
) {
    val reels        by viewModel.reels.collectAsStateWithLifecycle()
    val stories      by viewModel.stories.collectAsStateWithLifecycle()
    val lives        by viewModel.lives.collectAsStateWithLifecycle()
    val totalViews   by viewModel.totalViews.collectAsStateWithLifecycle()
    val totalLikes   by viewModel.totalLikes.collectAsStateWithLifecycle()
    val engPct       by viewModel.engagementPct.collectAsStateWithLifecycle()
    val aiInsight    by viewModel.aiInsight.collectAsStateWithLifecycle()
    val isLoading    by viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    // Combine API stories with locally-posted ones (from MediaPickerFlow)
    val localStories = CreatorRepository.stories
    val allStories: List<StoryUiModel> = remember(stories, localStories) {
        val api = stories.map { StoryUiModel(it.id, it.lawyerName, it.views, it.timeLeft, it.isLive) }
        val local = localStories.map { StoryUiModel(it.id.toString(), it.lawyerName, 0, "", false) }
        (local + api).distinctBy { it.id }
    }
    // Combine API reels with locally-uploaded ones
    val localReels = CreatorRepository.reels
    val allReels: List<ReelUiModel> = remember(reels, localReels) {
        val api = reels.map { ReelUiModel(it.id, it.title.ifBlank { it.caption.take(32) }, it.views, it.likes, it.duration, it.trend) }
        val local = localReels.map { ReelUiModel(it.id.toString(), it.title, it.views, it.likes, "", "") }
        (local + api).distinctBy { it.id }
    }
    // Live items
    val activeLives: List<LiveDto> = remember(lives, CreatorRepository.liveSessions) {
        val apiLives = lives
        val repoLives = CreatorRepository.liveSessions.map { cl ->
            LiveDto(id = cl.id.toString(), title = cl.topic, lawyerName = cl.lawyerName,
                viewersCount = cl.viewers, status = if (cl.isLive) "LIVE" else "Ended")
        }
        (repoLives + apiLives).distinctBy { it.id }
    }

    // Stat cards data
    val viewsLabel    = formatNumber(totalViews)
    val likesLabel    = formatNumber(totalLikes)
    val engLabel      = String.format("%.1f", engPct) + "%"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Studio Créateur",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp,
                            color      = StudioIndigoDark
                        )
                        Text(
                            LawyerSession.fullName.ifBlank { "Me. Yassine" },
                            fontFamily = FontFamily.Serif,
                            fontSize   = 12.sp,
                            color      = StudioIndigo.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = StudioIndigo
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser", tint = StudioIndigo)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = StudioBg
                )
            )
        },
        containerColor = StudioBg
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StudioIndigo)
            }
            return@Scaffold
        }

        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh    = { viewModel.refresh() },
            modifier     = Modifier.fillMaxSize().padding(padding)
        ) {
            LazyColumn(
                contentPadding     = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier           = Modifier.fillMaxSize()
            ) {
                // ── Stories row ───────────────────────────────────────────────
                item {
                    StudioSectionHeader(title = "Stories", icon = Icons.Default.AutoStories)
                    Spacer(Modifier.height(12.dp))
                    if (allStories.isEmpty()) {
                        EmptyInlineNote("Aucune story active. Publiez-en une via le bouton +.")
                    } else {
                        LazyRow(
                            contentPadding     = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(allStories, key = { it.id }) { story ->
                                StoryCircle(story)
                            }
                        }
                    }
                }

                // ── KPI row ───────────────────────────────────────────────────
                item {
                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KpiCard(modifier = Modifier.weight(1f), icon = Icons.Default.Visibility,
                            value = viewsLabel, label = "Total Vues", accentColor = StudioIndigoMid)
                        KpiCard(modifier = Modifier.weight(1f), icon = Icons.Default.Favorite,
                            value = likesLabel, label = "J'aimes", accentColor = Color(0xFFE91E63))
                        KpiCard(modifier = Modifier.weight(1f), icon = Icons.Default.ShowChart,
                            value = engLabel, label = "Engagement", accentColor = Color(0xFF4CAF50))
                    }
                }

                // ── AI Insight card ───────────────────────────────────────────
                item {
                    AiInsightCard(insight = aiInsight)
                }

                // ── Live banner ───────────────────────────────────────────────
                if (activeLives.isNotEmpty()) {
                    item {
                        StudioSectionHeader(
                            title = "Directs",
                            icon  = Icons.Default.LiveTv,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(Modifier.height(10.dp))
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            activeLives.forEach { live ->
                                LiveBannerCard(live)
                            }
                        }
                    }
                }

                // ── Reels list ────────────────────────────────────────────────
                item {
                    StudioSectionHeader(
                        title    = "Reels  (${allReels.size})",
                        icon     = Icons.Default.PlayCircle,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                if (allReels.isEmpty()) {
                    item {
                        Box(
                            Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyInlineNote("Aucun reel publié. Appuyez sur + pour en créer un.")
                        }
                    }
                } else {
                    items(allReels, key = { it.id }) { reel ->
                        ReelAnalyticsCard(
                            reel     = reel,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── UI Data Models (flattened for the UI layer) ────────────────────────────────

private data class StoryUiModel(
    val id:         String,
    val authorName: String,
    val views:      Int,
    val timeLeft:   String,
    val isLive:     Boolean
)

private data class ReelUiModel(
    val id:       String,
    val title:    String,
    val views:    Int,
    val likes:    Int,
    val duration: String,
    val trend:    String   // "up" | "down" | ""
)

// ── Stories circle ─────────────────────────────────────────────────────────────

@Composable
private fun StoryCircle(story: StoryUiModel) {
    val ringBrush = if (story.isLive) {
        Brush.linearGradient(listOf(Color.Red, Color(0xFFFF6B6B)))
    } else {
        IndigoGradient
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .drawBehind {
                    drawCircle(
                        brush  = ringBrush,
                        radius = size.minDimension / 2f,
                        style  = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                .padding(4.dp)
                .clip(CircleShape)
                .background(StudioIndigo.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = story.authorName.firstOrNull()?.uppercase() ?: "?",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize   = 22.sp,
                color      = StudioIndigo
            )
            if (story.isLive) {
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 2.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Red
                    ) {
                        Text(
                            "LIVE",
                            fontSize   = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = Color.White,
                            modifier   = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }
        Text(
            text     = story.authorName.substringAfterLast(" ").take(8),
            fontSize = 10.sp,
            color    = StudioIndigoDark.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (story.views > 0) {
            Text(
                text     = formatNumber(story.views) + " vues",
                fontSize = 9.sp,
                color    = StudioAccent
            )
        }
    }
}

// ── KPI Card ───────────────────────────────────────────────────────────────────

@Composable
private fun KpiCard(
    modifier:    Modifier,
    icon:        ImageVector,
    value:       String,
    label:       String,
    accentColor: Color
) {
    Surface(
        modifier  = modifier.height(96.dp),
        shape     = RoundedCornerShape(20.dp),
        color     = Color.White,
        shadowElevation = 4.dp,
        border    = BorderStroke(1.dp, accentColor.copy(alpha = 0.18f))
    ) {
        Column(
            modifier            = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(accentColor.copy(alpha = 0.10f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(
                    value,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 18.sp,
                    color      = StudioIndigoDark
                )
                Text(
                    label,
                    fontSize = 10.sp,
                    color    = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}

// ── AI Insight Card ────────────────────────────────────────────────────────────

@Composable
private fun AiInsightCard(insight: String) {
    Surface(
        modifier        = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape           = RoundedCornerShape(24.dp),
        color           = StudioIndigoDark,
        shadowElevation = 6.dp,
        border          = BorderStroke(1.dp, StudioAccent.copy(alpha = 0.30f))
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment  = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier         = Modifier
                        .size(38.dp)
                        .background(StudioAccent.copy(alpha = 0.18f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint     = StudioAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        "IA Insight",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 14.sp,
                        color      = Color.White
                    )
                    Text(
                        "Powered by Gemini",
                        fontSize = 10.sp,
                        color    = StudioAccent.copy(alpha = 0.75f)
                    )
                }
            }
            HorizontalDivider(color = StudioAccent.copy(alpha = 0.15f))
            Text(
                text       = insight.ifBlank { "Analyse en cours…" },
                fontFamily = FontFamily.Serif,
                fontSize   = 14.sp,
                lineHeight = 22.sp,
                color      = Color.White.copy(alpha = 0.88f)
            )
        }
    }
}

// ── Live Banner Card ───────────────────────────────────────────────────────────

@Composable
private fun LiveBannerCard(live: LiveDto) {
    val isLive   = live.status.equals("LIVE", ignoreCase = true) || live.viewersCount > 0
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by pulseAnim.animateFloat(
        initialValue   = 0.4f,
        targetValue    = 1.0f,
        animationSpec  = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(20.dp),
        color           = if (isLive) Color(0xFF1A0E2E) else Color.White,
        shadowElevation = 4.dp,
        border          = BorderStroke(
            1.dp,
            if (isLive) Color.Red.copy(alpha = 0.50f) else StudioAccent.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier           = Modifier.padding(16.dp),
            verticalAlignment  = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Thumbnail / indicator box
            Box(
                modifier         = Modifier
                    .size(56.dp)
                    .background(
                        if (isLive) Color.Red.copy(alpha = 0.15f) else StudioIndigo.copy(alpha = 0.10f),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.LiveTv,
                    contentDescription = null,
                    tint     = if (isLive) Color.Red.copy(pulseAlpha) else StudioAccent,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    live.title.ifBlank { "Direct sans titre" },
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp,
                    color      = if (isLive) Color.White else StudioIndigoDark,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis
                )
                if (live.lawyerName.isNotBlank()) {
                    Text(
                        live.lawyerName,
                        fontSize = 12.sp,
                        color    = if (isLive) Color.White.copy(0.6f) else Color.Gray
                    )
                }
            }
            // Status / viewers chip
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isLive) Color.Red.copy(pulseAlpha * 0.9f) else StudioAccent.copy(0.15f)
                ) {
                    Text(
                        if (isLive) "LIVE" else "Prévu",
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = if (isLive) Color.White else StudioIndigo,
                        modifier   = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                if (live.viewersCount > 0) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${live.viewersCount} 👁",
                        fontSize = 10.sp,
                        color    = if (isLive) Color.White.copy(0.7f) else Color.Gray
                    )
                }
            }
        }
    }
}

// ── Reel Analytics Card ────────────────────────────────────────────────────────

@Composable
private fun ReelAnalyticsCard(reel: ReelUiModel, modifier: Modifier = Modifier) {
    val trendColor = when (reel.trend.lowercase()) {
        "up"   -> Color(0xFF4CAF50)
        "down" -> Color(0xFFE53935)
        else   -> Color.Gray
    }
    val trendIcon: ImageVector? = when (reel.trend.lowercase()) {
        "up"   -> Icons.AutoMirrored.Filled.TrendingUp
        "down" -> Icons.AutoMirrored.Filled.TrendingDown
        else   -> null
    }
    Surface(
        modifier        = modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(20.dp),
        color           = Color.White,
        shadowElevation = 3.dp,
        border          = BorderStroke(0.5.dp, StudioIndigo.copy(alpha = 0.08f))
    ) {
        Row(
            modifier           = Modifier.padding(14.dp),
            verticalAlignment  = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Thumbnail placeholder + play icon
            Box(
                modifier         = Modifier
                    .size(width = 70.dp, height = 88.dp)
                    .background(IndigoGradient, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint     = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(32.dp)
                )
                if (reel.duration.isNotBlank()) {
                    Box(
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Black.copy(alpha = 0.55f)
                        ) {
                            Text(
                                reel.duration,
                                fontSize = 8.sp,
                                color    = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            // Text + stats
            Column(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    reel.title.ifBlank { "Reel #${reel.id.takeLast(4)}" },
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp,
                    color      = StudioIndigoDark,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // Views
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null,
                            tint = Color.Gray, modifier = Modifier.size(13.dp))
                        Text(formatNumber(reel.views), fontSize = 12.sp, color = Color.Gray)
                    }
                    // Likes
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null,
                            tint = Color(0xFFE91E63), modifier = Modifier.size(13.dp))
                        Text(formatNumber(reel.likes), fontSize = 12.sp, color = Color.Gray)
                    }
                }
                // Trend indicator
                if (trendIcon != null) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(trendIcon, contentDescription = null,
                            tint = trendColor, modifier = Modifier.size(15.dp))
                        Text(
                            if (reel.trend == "up") "En hausse" else "En baisse",
                            fontSize = 11.sp,
                            color    = trendColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// ── Section Header ─────────────────────────────────────────────────────────────

@Composable
private fun StudioSectionHeader(
    title:    String,
    icon:     ImageVector,
    modifier: Modifier = Modifier.padding(horizontal = 20.dp)
) {
    Row(
        modifier          = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = StudioIndigo, modifier = Modifier.size(18.dp))
        Text(
            title,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp,
            color      = StudioIndigoDark
        )
    }
}

// ── Empty inline note ──────────────────────────────────────────────────────────

@Composable
private fun EmptyInlineNote(message: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape    = RoundedCornerShape(14.dp),
        color    = StudioIndigo.copy(alpha = 0.05f),
        border   = BorderStroke(1.dp, StudioAccent.copy(alpha = 0.20f))
    ) {
        Row(
            modifier           = Modifier.padding(14.dp),
            verticalAlignment  = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = null,
                tint = StudioAccent, modifier = Modifier.size(18.dp))
            Text(
                message,
                fontSize   = 13.sp,
                color      = StudioIndigo.copy(alpha = 0.75f),
                lineHeight = 18.sp
            )
        }
    }
}

// ── Helpers ────────────────────────────────────────────────────────────────────

private fun formatNumber(n: Int): String = when {
    n >= 1_000_000 -> String.format("%.1fM", n / 1_000_000f)
    n >= 1_000     -> String.format("%.1fK", n / 1_000f)
    else           -> n.toString()
}

// ─── Legacy composables preserved for navigation backward-compat ──────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerCreatorManagementScreen(
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Stories", "Reels", "Lives")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Gestion Créateur",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = AppDarkGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = AppDarkGreen)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = AppDarkGreen,
                indicator = { tabPositions ->
                    with(TabRowDefaults) {
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AppGoldColor
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> StoriesList()
                    1 -> ReelsList()
                    2 -> LivesList()
                }
            }
        }
    }
}

@Composable
fun StoriesList() {
    val stories = CreatorRepository.stories
    if (stories.isEmpty()) {
        EmptyState(Icons.Default.History, "Aucune story publiée")
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(stories) { story ->
                CreatorContentCard(
                    title = "Story #${story.id.toString().takeLast(4)}",
                    subtitle = "Publié par ${story.lawyerName}",
                    icon = Icons.Default.History,
                    onDelete = { CreatorRepository.deleteStory(story.id) }
                )
            }
        }
    }
}

@Composable
fun ReelsList() {
    val reels = CreatorRepository.reels
    if (reels.isEmpty()) {
        EmptyState(Icons.Default.PlayCircle, "Aucun reel publié")
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(reels) { reel ->
                CreatorContentCard(
                    title = reel.title,
                    subtitle = "Reel • ${reel.specialty}",
                    icon = Icons.Default.PlayCircle,
                    onDelete = { CreatorRepository.deleteReel(reel.id) }
                )
            }
        }
    }
}

@Composable
fun LivesList() {
    val lives = CreatorRepository.liveSessions
    if (lives.isEmpty()) {
        EmptyState(Icons.Default.LiveTv, "Aucun direct enregistré")
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(lives) { live ->
                CreatorContentCard(
                    title = live.topic,
                    subtitle = if (live.isLive) "En direct actuellement" else "Terminé",
                    icon = Icons.Default.LiveTv,
                    onDelete = { CreatorRepository.deleteLive(live.id) },
                    isLive = live.isLive
                )
            }
        }
    }
}

@Composable
fun CreatorContentCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onDelete: () -> Unit,
    isLive: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(AppDarkGreen.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = AppDarkGreen)
                if (isLive) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.Red, CircleShape)
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppDarkGreen)
                Text(subtitle, fontSize = 13.sp, color = Color.Gray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.Red.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun EmptyState(icon: ImageVector, message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = Color.Gray, fontSize = 16.sp)
    }
}
