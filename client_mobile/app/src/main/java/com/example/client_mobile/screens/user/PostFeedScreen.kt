package com.example.client_mobile.screens.user

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.client_mobile.screens.shared.*
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

// ─── Screen ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostFeedScreen(
    paddingValues: PaddingValues    = PaddingValues(),
    isLawyer:      Boolean          = false,
    onCreatePost:  () -> Unit       = {},
    viewModel:     PostViewModel    = viewModel()
) {
    val posts        by viewModel.posts.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isError      by viewModel.isError.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            StandardTopBar(title = "Publications", onBack = null)
        },
        floatingActionButton = {
            if (isLawyer) {
                FloatingActionButton(
                    onClick        = onCreatePost,
                    containerColor = AppDarkGreen,
                    contentColor   = AppGoldColor,
                    shape          = CircleShape
                ) {
                    Icon(Icons.Default.Add, "Nouvelle publication", modifier = Modifier.size(26.dp))
                }
            }
        },
        containerColor      = AppCreamBg,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { localPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh    = { viewModel.refresh() },
            modifier     = Modifier.fillMaxSize()
        ) {
            when {
                // ── Loading skeleton ──────────────────────────────────────────
                posts == null -> LazyColumn(
                    modifier       = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top    = localPadding.calculateTopPadding() + 12.dp,
                        bottom = paddingValues.calculateBottomPadding() + 24.dp,
                        start  = 16.dp,
                        end    = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(4) { PostCardSkeleton() }
                }

                // ── Error ─────────────────────────────────────────────────────
                isError -> Box(
                    modifier         = Modifier
                        .fillMaxSize()
                        .padding(localPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.CloudOff, null,
                            tint     = AppDarkGreen.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "Impossible de charger les publications",
                            color = AppDarkGreen.copy(alpha = 0.6f)
                        )
                        OutlinedButton(onClick = { viewModel.refresh() }) {
                            Text("Réessayer", color = AppDarkGreen)
                        }
                    }
                }

                // ── Empty ─────────────────────────────────────────────────────
                posts!!.isEmpty() -> Box(
                    modifier         = Modifier
                        .fillMaxSize()
                        .padding(localPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier            = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Article, null,
                            tint     = AppGoldColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            "Aucune publication pour le moment",
                            color      = AppDarkGreen.copy(alpha = 0.6f),
                            fontFamily = FontFamily.Serif,
                            fontSize   = 16.sp
                        )
                        if (isLawyer) {
                            Button(
                                onClick = onCreatePost,
                                colors  = ButtonDefaults.buttonColors(containerColor = AppDarkGreen)
                            ) {
                                Text("Créer la première publication", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // ── Posts list ────────────────────────────────────────────────
                else -> LazyColumn(
                    modifier       = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top    = localPadding.calculateTopPadding() + 12.dp,
                        bottom = paddingValues.calculateBottomPadding() + 88.dp,
                        start  = 16.dp,
                        end    = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(posts!!, key = { it.id }) { post ->
                        PostCard(
                            post   = post,
                            onLike = { viewModel.toggleLike(post.id) }
                        )
                    }
                }
            }
        }
    }
}

// ─── Post card ────────────────────────────────────────────────────────────────
@Composable
private fun PostCard(post: PostItem, onLike: () -> Unit) {
    val context = LocalContext.current

    // Animated like scale
    var likeScale by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue    = likeScale,
        animationSpec  = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        finishedListener = { likeScale = 1f },
        label          = "likeScale"
    )

    DashCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Author row ────────────────────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!post.authorAvatar.isNullOrBlank()) {
                    AsyncImage(
                        model              = ImageRequest.Builder(context).data(post.authorAvatar).crossfade(true).build(),
                        contentDescription = post.authorName,
                        modifier           = Modifier.size(44.dp).clip(CircleShape),
                        contentScale       = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape    = CircleShape,
                        color    = AppGoldColor.copy(alpha = 0.15f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                post.authorName.take(1).uppercase(),
                                color      = AppGoldColor,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 16.sp
                            )
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        post.authorName,
                        color      = AppDarkGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 14.sp,
                        maxLines   = 1
                    )
                    Text(
                        relativeTime(post.createdAt),
                        color    = AppDarkGreen.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
            }

            // ── Content text (hashtags highlighted) ───────────────────────────
            if (post.content.isNotBlank()) {
                Text(
                    text = buildAnnotatedString {
                        post.content.split(" ").forEachIndexed { i, word ->
                            if (i > 0) append(" ")
                            if (word.startsWith("#")) {
                                withStyle(SpanStyle(color = AppGoldColor, fontWeight = FontWeight.SemiBold)) {
                                    append(word)
                                }
                            } else {
                                append(word)
                            }
                        }
                    },
                    color      = AppDarkGreen,
                    fontSize   = 14.sp,
                    lineHeight = 20.sp
                )
            }

            // ── Media ─────────────────────────────────────────────────────────
            if (!post.mediaUrl.isNullOrBlank()) {
                AsyncImage(
                    model              = ImageRequest.Builder(context).data(post.mediaUrl).crossfade(true).build(),
                    contentDescription = "Image de la publication",
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale       = ContentScale.Crop
                )
            }

            // ── Hashtag chips ─────────────────────────────────────────────────
            if (post.hashtags.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    post.hashtags.take(5).forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = AppGoldColor.copy(alpha = 0.10f)
                        ) {
                            Text(
                                tag,
                                modifier   = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                color      = AppGoldColor,
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.07f))

            // ── Action row ────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Like
                Row(
                    modifier          = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null
                    ) {
                        likeScale = 1.4f
                        onLike()
                    },
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector        = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "J'aime",
                        tint               = if (post.isLiked) Color(0xFFE53935) else AppDarkGreen.copy(alpha = 0.6f),
                        modifier           = Modifier.size(20.dp).scale(animatedScale)
                    )
                    Text(
                        formatCount(post.likesCount),
                        color      = AppDarkGreen.copy(alpha = 0.7f),
                        fontSize   = 13.sp,
                        fontWeight = if (post.isLiked) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Comments (display-only)
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.ModeComment, "Commentaires",
                        tint     = AppDarkGreen.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        formatCount(post.commentsCount),
                        color    = AppDarkGreen.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }

                // Share
                IconButton(
                    onClick  = { /* share sheet — future */ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send, "Partager",
                        tint     = AppDarkGreen.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ─── Skeleton ─────────────────────────────────────────────────────────────────
@Composable
private fun PostCardSkeleton() {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue  = 0.20f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeletonAlpha"
    )
    DashCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    Modifier.size(44.dp)
                        .clip(CircleShape)
                        .background(AppDarkGreen.copy(alpha = alpha))
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        Modifier.width(130.dp).height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(AppDarkGreen.copy(alpha = alpha))
                    )
                    Box(
                        Modifier.width(80.dp).height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(AppDarkGreen.copy(alpha = alpha / 2))
                    )
                }
            }
            Box(
                Modifier.fillMaxWidth().height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(AppDarkGreen.copy(alpha = alpha))
            )
            Box(
                Modifier.fillMaxWidth(0.7f).height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(AppDarkGreen.copy(alpha = alpha))
            )
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun relativeTime(iso: String): String {
    if (iso.isBlank()) return ""
    return try {
        val then = ZonedDateTime.parse(iso)
        val now  = ZonedDateTime.now()
        when (val mins = ChronoUnit.MINUTES.between(then, now)) {
            in 0..0   -> "À l'instant"
            in 1..59  -> "il y a ${mins}min"
            in 60..1439 -> "il y a ${mins / 60}h"
            else        -> "il y a ${mins / 1440}j"
        }
    } catch (_: Exception) { iso }
}

private fun formatCount(n: Int): String = when {
    n >= 1_000_000 -> "${n / 1_000_000}M"
    n >= 1_000     -> "${n / 1_000}k"
    else           -> "$n"
}
