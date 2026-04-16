package com.example.client_mobile.screens.user

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.example.client_mobile.R
import com.example.client_mobile.network.dto.LegalPostDto
import com.example.client_mobile.network.dto.StoryDto
import com.example.client_mobile.screens.shared.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Size
import com.example.client_mobile.network.TokenManager

// ─── Gradient ring colours ────────────────────────────────────────────────────
private val RingGold      = Color(0xFFC5A059)
private val RingGreen     = Color(0xFF1B4332)
private val FeedBackground = Color(0xFFF9F5F0)  // Warm Beige

// ─── Screen ───────────────────────────────────────────────────────────────────

/**
 * HaqqiSocialFeedScreen — Instagram-style legal social feed.
 * Primary entry point for both Lawyers and Clients after login.
 *
 * @param paddingValues  Scaffold inner padding from the host.
 * @param isLawyer       If true, shows the floating "Create Post" FAB.
 * @param onCreatePost   Called when the lawyer taps the FAB.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedTopBar(onNotifications: () -> Unit = {}) {
    StandardTopBar(
        onBack = null,
        actions = {
            TopBarActions(
                onNotifications = onNotifications,
                onProfile = {}
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HaqqiSocialFeedScreen(
    paddingValues: PaddingValues = PaddingValues(),
    isLawyer: Boolean = TokenManager.getUserType() == "lawyer",
    onCreatePost: () -> Unit = {},
    viewModel: SocialFeedViewModel = viewModel()
) {
    val posts        by viewModel.posts.collectAsStateWithLifecycle()
    val stories      by viewModel.stories.collectAsStateWithLifecycle()
    val likedIds     by viewModel.likedIds.collectAsStateWithLifecycle()
    val likeCountMap by viewModel.likeCount.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isError      by viewModel.isError.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { 
            FeedTopBar(onNotifications = {
                // Notifications can be triggered here
            }) 
        },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { localPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FeedBackground)
        ) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh    = { viewModel.refresh() },
                modifier     = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(
                        top = localPadding.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding() + 24.dp // Ensure we can scroll past the bottom bar
                    ),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // ── Stories row ───────────────────────────────────────────────
                item(key = "stories") {
                    StoriesSection(
                        stories       = stories,
                        modifier      = Modifier.background(Color.White)
                    )
                }

                // ── Divider ───────────────────────────────────────────────────
                item(key = "divider") {
                    HorizontalDivider(
                        color     = AppDarkGreen.copy(alpha = 0.06f),
                        thickness = 1.dp
                    )
                }

                // ── Feed posts ────────────────────────────────────────────────
                when {
                    // Loading skeleton
                    posts == null -> {
                        items(4, key = { "skeleton_$it" }) {
                            FeedPostSkeleton()
                        }
                    }

                    // Error state
                    isError && posts!!.isEmpty() -> {
                        item(key = "error") {
                            FeedErrorState(onRetry = { viewModel.refresh() })
                        }
                    }

                    // Empty state
                    posts!!.isEmpty() -> {
                        item(key = "empty") {
                            FeedEmptyState()
                        }
                    }

                    // Posts list
                    else -> {
                        items(
                            items = posts!!,
                            key   = { post -> "${post.lawyerId}_${post.date}" }
                        ) { post ->
                            val postKey  = "${post.lawyerId}_${post.date}"
                            val isLiked  = postKey in likedIds
                            val count    = likeCountMap[postKey] ?: post.likesCount
                            LegalPostCard(
                                post     = post,
                                isLiked  = isLiked,
                                likeCount = count,
                                onLike   = { viewModel.toggleLike(postKey, count) }
                            )
                            HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.05f))
                        }
                    }
                }

                item(key = "bottom_spacer") { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }

        // ── Lawyer-only FAB ───────────────────────────────────────────────────
        if (isLawyer) {
            FloatingActionButton(
                onClick           = onCreatePost,
                modifier          = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp),
                containerColor    = AppDarkGreen,
                contentColor      = AppGoldColor,
                shape             = CircleShape,
                elevation         = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = "Créer un post",
                    modifier           = Modifier.size(28.dp)
                )
            }
        }
        }
    }
}

// ─── Stories Section ──────────────────────────────────────────────────────────

@Composable
private fun StoriesSection(
    stories: List<StoryDto>?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 12.dp)) {
        LazyRow(
            contentPadding      = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when {
                // Shimmer placeholders while loading
                stories == null -> {
                    items(5) { StoryShimmerItem() }
                }
                // Real stories
                stories.isNotEmpty() -> {
                    items(stories, key = { it.id }) { story ->
                        StoryRingItem(story = story)
                    }
                }
                // Empty — still show 5 shimmer so the row doesn't collapse
                else -> {
                    items(5) { StoryShimmerItem() }
                }
            }
        }
    }
}

// ─── Story Ring Item ──────────────────────────────────────────────────────────

/** Circular avatar with animated Gold→Green gradient ring (drawn via Canvas). */
@Composable
private fun StoryRingItem(story: StoryDto) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier
            .width(72.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = {}
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier.size(68.dp)
        ) {
            // Gradient ring drawn manually so we can use sweepGradient
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokePx = 3.dp.toPx()
                val brush = Brush.sweepGradient(
                    colors = listOf(RingGold, RingGreen, RingGold),
                    center = Offset(size.width / 2f, size.height / 2f)
                )
                drawArc(
                    brush      = brush,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter  = false,
                    topLeft    = Offset(strokePx / 2, strokePx / 2),
                    size       = Size(size.width - strokePx, size.height - strokePx),
                    style      = Stroke(width = strokePx)
                )
            }

            // Avatar image
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(AppDarkGreen.copy(alpha = 0.07f))
            ) {
                if (story.lawyerAvatar.isNotBlank()) {
                    AsyncImage(
                        model             = story.lawyerAvatar,
                        contentDescription = story.lawyerName,
                        contentScale      = ContentScale.Crop,
                        modifier          = Modifier.fillMaxSize()
                    )
                } else {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector        = Icons.Default.Person,
                            contentDescription = null,
                            tint               = AppGoldColor,
                            modifier           = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // LIVE badge
            if (story.isLive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 2.dp)
                        .background(Color.Red, RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        "LIVE",
                        color      = Color.White,
                        fontSize   = 7.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text      = story.lawyerName.substringBefore(" ").take(10),
            fontSize  = 10.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            color     = AppDarkGreen,
            maxLines  = 1,
            overflow  = TextOverflow.Ellipsis
        )
    }
}

// ─── Story Shimmer ────────────────────────────────────────────────────────────

@Composable
private fun StoryShimmerItem() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier.width(72.dp)
    ) {
        SkeletonBox(
            modifier = Modifier.size(64.dp),
            shape    = CircleShape
        )
        Spacer(modifier = Modifier.height(6.dp))
        SkeletonBox(modifier = Modifier.width(48.dp).height(10.dp))
    }
}

// ─── Legal Post Card ──────────────────────────────────────────────────────────

@Composable
fun LegalPostCard(
    post      : LegalPostDto,
    isLiked   : Boolean = false,
    likeCount : Int     = post.likesCount,
    onLike    : () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val MAX_CAPTION_LINES = 3

    // Like button bounce animation
    val likeScale by animateFloatAsState(
        targetValue   = if (isLiked) 1.25f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label         = "like_scale"
    )

    Surface(
        modifier       = Modifier.fillMaxWidth(),
        color          = Color.White,
        tonalElevation = 0.dp
    ) {
        Column {
            // ── Header row ────────────────────────────────────────────────────
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier         = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AppDarkGreen.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (post.avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model              = post.avatarUrl,
                            contentDescription = post.lawyerName,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize()
                        )
                    } else {
                        // Initials fallback
                        val initials = post.lawyerName
                            .split(" ")
                            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                            .take(2)
                            .joinToString("")
                        Text(
                            text       = initials,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 15.sp,
                            color      = AppDarkGreen,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text       = post.lawyerName,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp,
                            color      = AppDarkGreen,
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis
                        )
                        // Verified badge — only for lawyers (isVerified from API)
                        if (post.isVerified) {
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(
                                imageVector        = Icons.Default.Verified,
                                contentDescription = "Avocat vérifié",
                                tint               = Color(0xFF2563EB),
                                modifier           = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text      = formatTimeAgo(post.date),
                        fontSize  = 11.sp,
                        fontFamily = FontFamily.Serif,
                        color     = Color.Gray
                    )
                }

                // Options menu placeholder
                Icon(
                    imageVector        = Icons.Default.MoreHoriz,
                    contentDescription = "Options",
                    tint               = AppDarkGreen.copy(alpha = 0.35f),
                    modifier           = Modifier.size(20.dp)
                )
            }

            // ── Post Image ────────────────────────────────────────────────────
            if (post.postImageUrl.isNotBlank()) {
                AsyncImage(
                    model              = ImageRequest.Builder(LocalContext.current)
                                            .data(post.postImageUrl)
                                            .crossfade(true)
                                            .build(),
                    contentDescription = "Publication de ${post.lawyerName}",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                )
            }

            // ── Interactions row ──────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Like
                IconButton(
                    onClick  = onLike,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector        = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "J'aime",
                        tint               = if (isLiked) RingGold else AppDarkGreen.copy(alpha = 0.65f),
                        modifier           = Modifier.size(24.dp).scale(likeScale)
                    )
                }
                if (likeCount > 0) {
                    Text(
                        text      = likeCount.toString(),
                        fontSize  = 13.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        color     = AppDarkGreen,
                        modifier  = Modifier.padding(end = 4.dp)
                    )
                }

                // Comment
                IconButton(
                    onClick  = {},
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Commenter",
                        tint               = AppDarkGreen.copy(alpha = 0.65f),
                        modifier           = Modifier.size(22.dp)
                    )
                }
                if (post.commentsCount > 0) {
                    Text(
                        text      = post.commentsCount.toString(),
                        fontSize  = 13.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        color     = AppDarkGreen,
                        modifier  = Modifier.padding(end = 4.dp)
                    )
                }

                // Share
                IconButton(
                    onClick  = {},
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Partager",
                        tint               = AppDarkGreen.copy(alpha = 0.65f),
                        modifier           = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bookmark
                Icon(
                    imageVector        = Icons.Default.BookmarkBorder,
                    contentDescription = "Sauvegarder",
                    tint               = AppDarkGreen.copy(alpha = 0.45f),
                    modifier           = Modifier.size(22.dp)
                )
            }

            // ── Caption ───────────────────────────────────────────────────────
            if (post.legalText.isNotBlank()) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text       = post.legalText,
                        fontFamily = FontFamily.Serif,
                        fontSize   = 13.sp,
                        lineHeight = 20.sp,
                        color      = AppDarkGreen,
                        textAlign  = TextAlign.Start,
                        maxLines   = if (expanded) Int.MAX_VALUE else MAX_CAPTION_LINES,
                        overflow   = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
                    )
                    if (!expanded && post.legalText.length > 120) {
                        Text(
                            text      = "Lire plus",
                            fontSize  = 12.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            color     = RingGold,
                            modifier  = Modifier
                                .padding(top = 2.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication        = null
                                ) { expanded = true }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// ─── Skeleton Placeholder ─────────────────────────────────────────────────────

@Composable
private fun FeedPostSkeleton() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color    = Color.White
    ) {
        Column(modifier = Modifier.padding(bottom = 12.dp)) {
            // Header shimmer
            Row(
                modifier          = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonBox(modifier = Modifier.size(44.dp), shape = CircleShape)
                Spacer(modifier = Modifier.width(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SkeletonBox(modifier = Modifier.width(130.dp).height(13.dp))
                    SkeletonBox(modifier = Modifier.width(70.dp).height(10.dp))
                }
            }
            // Image shimmer
            SkeletonBox(modifier = Modifier.fillMaxWidth().height(240.dp), shape = RoundedCornerShape(0.dp))
            Spacer(modifier = Modifier.height(10.dp))
            // Actions shimmer
            Row(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SkeletonBox(modifier = Modifier.size(24.dp), shape = CircleShape)
                SkeletonBox(modifier = Modifier.size(24.dp), shape = CircleShape)
                SkeletonBox(modifier = Modifier.size(24.dp), shape = CircleShape)
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Caption shimmer
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SkeletonBox(modifier = Modifier.fillMaxWidth().height(12.dp))
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.85f).height(12.dp))
            }
        }
    }
    HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.04f))
}

// ─── Error / Empty States ─────────────────────────────────────────────────────

@Composable
private fun FeedErrorState(onRetry: () -> Unit) {
    Box(
        modifier         = Modifier
            .fillMaxWidth()
            .height(380.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment  = Alignment.CenterHorizontally,
            verticalArrangement  = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.WifiOff,
                contentDescription = null,
                tint               = AppDarkGreen.copy(alpha = 0.30f),
                modifier           = Modifier.size(64.dp)
            )
            Text(
                text       = "Connexion impossible",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize   = 17.sp,
                color      = AppDarkGreen
            )
            Text(
                text      = "Vérifiez votre connexion internet\net réessayez.",
                fontFamily = FontFamily.Serif,
                fontSize   = 13.sp,
                color      = Color.Gray
            )
            Button(
                onClick  = onRetry,
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = AppDarkGreen,
                    contentColor   = Color.White
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Réessayer", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FeedEmptyState() {
    Box(
        modifier         = Modifier
            .fillMaxWidth()
            .height(380.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.Gavel,
                contentDescription = null,
                tint               = AppDarkGreen.copy(alpha = 0.25f),
                modifier           = Modifier.size(72.dp)
            )
            Text(
                text       = "Aucune publication pour l'instant",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                color      = AppDarkGreen
            )
            Text(
                text      = "Les avocats vérifiés partageront\nbientôt leurs conseils juridiques ici.",
                fontFamily = FontFamily.Serif,
                fontSize   = 13.sp,
                color      = Color.Gray
            )
        }
    }
}

// ─── Time Formatter ───────────────────────────────────────────────────────────

/**
 * Converts ISO-8601 date string to a human-readable relative label.
 * Falls back gracefully if parsing fails.
 */
private fun formatTimeAgo(dateStr: String): String {
    if (dateStr.isBlank()) return ""
    return try {
        val zdt  = ZonedDateTime.parse(dateStr)
        val now  = ZonedDateTime.now()
        val mins = ChronoUnit.MINUTES.between(zdt, now)
        when {
            mins < 1    -> "À l'instant"
            mins < 60   -> "Il y a ${mins}min"
            mins < 1440 -> "Il y a ${mins / 60}h"
            mins < 2880 -> "Hier"
            else        -> DateTimeFormatter.ofPattern("d MMM yyyy").format(zdt)
        }
    } catch (_: Exception) {
        dateStr
    }
}
