package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

// ─── Model ────────────────────────────────────────────────────────────────────

data class LegalReel(
    val id: Int,
    val lawyerName: String,
    val specialty: String,
    val title: String,
    val likes: Int,
    val views: String,
    val videoUrl: String = "",
    val isLiked: Boolean = false,
    val isLive: Boolean = false
)


// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LegalReelsScreen(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: ReelViewModel = viewModel()
) {
    val apiReels     by viewModel.reels.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isError      by viewModel.isError.collectAsStateWithLifecycle()

    val allReels = remember { mutableStateListOf<LegalReel>() }

    // Merge API reels + lawyer-created reels whenever either changes
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

    // ── No-connection full-screen ─────────────────────────────────────────────
    if (isError && allReels.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black).padding(paddingValues)) {
            NoConnectionScreen(onRetry = { viewModel.refresh() })
        }
        return
    }

    // ── Loading skeleton ──────────────────────────────────────────────────────
    if (apiReels == null && allReels.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black).padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AppGoldColor)
        }
        return
    }

    // ── VerticalPager (TikTok-style full-screen reels) ───────────────────────
    val pagerState = rememberPagerState { allReels.size }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh    = { viewModel.refresh() },
            modifier     = Modifier.fillMaxSize()
        ) {
            VerticalPager(
                state    = pagerState,
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) { page ->
                val reel = allReels.getOrNull(page) ?: return@VerticalPager
                val isActive = pagerState.currentPage == page

                ReelPage(
                    reel     = reel,
                    isActive = isActive,
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
    }
}

// ─── Reel Page (full-screen, TikTok-style) ────────────────────────────────────

@Composable
private fun ReelPage(
    reel: LegalReel,
    isActive: Boolean,
    onLike: () -> Unit
) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    // Swap media when the reel changes; play only when this page is active
    LaunchedEffect(reel.videoUrl, isActive) {
        if (reel.videoUrl.isNotBlank()) {
            exoPlayer.setMediaItem(MediaItem.fromUri(reel.videoUrl))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = isActive
        } else {
            if (!isActive) exoPlayer.pause()
        }
    }

    // Release player when the page leaves composition
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Video background (or gradient fallback) ────────────────────────
        if (reel.videoUrl.isNotBlank()) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player       = exoPlayer
                        useController = false
                        layoutParams  = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Gradient shimmer when no videoUrl (creator reels, mock data)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                AppDarkGreen.copy(alpha = 0.92f),
                                Color(0xFF0D1F17),
                                Color.Black.copy(alpha = 0.95f)
                            )
                        )
                    )
            )
        }

        // ── Gradient overlay so text stays readable over video ─────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.65f)
                        )
                    )
                )
        )

        // ── Centre play-icon when no video ─────────────────────────────────
        if (reel.videoUrl.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 180.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape    = CircleShape,
                    color    = Color.White.copy(alpha = 0.14f)
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

        // ── Live badge ─────────────────────────────────────────────────────
        if (reel.isLive) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFD32F2F)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color.White, CircleShape)
                    )
                    Text(
                        "LIVE",
                        fontFamily  = FontFamily.Serif,
                        fontWeight  = FontWeight.Bold,
                        fontSize    = 11.sp,
                        color       = Color.White
                    )
                }
            }
        }

        // ── Views badge ────────────────────────────────────────────────────
        if (reel.views.isNotBlank()) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.Visibility,
                    contentDescription = null,
                    tint     = Color.White.copy(alpha = 0.80f),
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    reel.views,
                    fontFamily = FontFamily.Serif,
                    fontSize   = 12.sp,
                    color      = Color.White.copy(alpha = 0.80f)
                )
            }
        }

        // ── Lawyer name + title + actions (bottom overlay) ─────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.80f))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Lawyer name row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.size(38.dp),
                    shape    = CircleShape,
                    color    = AppGoldColor.copy(alpha = 0.18f),
                    border   = BorderStroke(1.5.dp, AppGoldColor)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint     = AppGoldColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Column {
                    Text(
                        reel.lawyerName,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 13.sp,
                        color      = Color.White
                    )
                    if (reel.specialty.isNotBlank()) {
                        Text(
                            reel.specialty,
                            fontFamily = FontFamily.Serif,
                            fontSize   = 11.sp,
                            color      = AppGoldColor
                        )
                    }
                }
            }

            // Caption / title
            if (reel.title.isNotBlank()) {
                Text(
                    reel.title,
                    fontFamily  = FontFamily.Serif,
                    fontWeight  = FontWeight.SemiBold,
                    fontSize    = 14.sp,
                    color       = Color.White,
                    lineHeight  = 20.sp
                )
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.clickable { onLike() }
                ) {
                    Icon(
                        if (reel.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "J'aime",
                        tint     = if (reel.isLiked) AppGoldColor else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        "${reel.likes}",
                        fontFamily = FontFamily.Serif,
                        fontSize   = 12.sp,
                        color      = Color.White
                    )
                }

                // Share button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Partager",
                        tint     = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "Partager",
                        fontFamily = FontFamily.Serif,
                        fontSize   = 12.sp,
                        color      = Color.White
                    )
                }

                // Consult CTA
                Surface(
                    shape   = RoundedCornerShape(20.dp),
                    color   = AppDarkGreen,
                    border  = BorderStroke(1.dp, AppGoldColor),
                    onClick = {}
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint     = AppGoldColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            "Consulter",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 12.sp,
                            color      = AppGoldColor
                        )
                    }
                }
            }
        }
    }

    HorizontalDivider(color = Color.Black, thickness = 2.dp)
}

