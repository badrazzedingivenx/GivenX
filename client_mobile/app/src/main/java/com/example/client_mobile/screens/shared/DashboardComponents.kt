package com.example.client_mobile.screens.shared

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client_mobile.R
import com.example.client_mobile.network.dto.StoryDto
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle

// ─── Brand Tokens ─────────────────────────────────────────────────────────────
val AppDarkGreen = Color(0xFF0F291E) // Deeper green for premium feel
val AppGoldColor = Color(0xFFD4AF37) // Metallic Gold
val RingGold = Color(0xFFC5A059) // Slightly softer gold for borders
val AppGoldGradient = androidx.compose.ui.graphics.Brush.verticalGradient(
    colors = listOf(Color(0xFFD4AF37), Color(0xFFC5A059))
)
val AppTopBarGradient = androidx.compose.ui.graphics.Brush.verticalGradient(
    colors = listOf(Color(0xFF1B4332), Color(0xFF0F291E))
)

// ─── Status Colors ────────────────────────────────────────────────────────────
val StatusGreen      = Color(0xFF1B4332)   // Green-600
val StatusGreenBg    = Color(0xFFECFDF5)   // Green-50
val StatusOrange     = Color(0xFFD97706)   // Amber-600
val StatusOrangeBg   = Color(0xFFFFF3E0)   // Orange-50
val StatusRed        = Color(0xFFDC2626)   // Red-600
val StatusRedBg      = Color(0xFFFFF1F2)   // Rose-50
val StatusBlue      = Color(0xFF2563EB)   // Blue-600
val StatusBlueBg    = Color(0xFFEFF6FF)   // Blue-50
val StatusGray      = Color(0xFF4B5563)   // Gray-600
val StatusGrayBg    = Color(0xFFF3F4F6)   // Gray-100
val AppGoldBg       = Color(0xFFC5A059).copy(alpha = 0.12f)
val AppSubtitleGray = Color(0xFF4A4A4A)
val AppCreamBg      = Color(0xFFF7F1EA)

// ─── Base Screen Template ─────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    showBackground: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        snackbarHost = snackbarHost,
        containerColor = AppCreamBg,
        content = content
    )
}

/**
 * Base screen with optional standardized TopBar and BottomBar.
 */
@Composable
fun BaseScreen(
    title: String? = null,
    titleContent: (@Composable () -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    onNotifications: (() -> Unit)? = null,
    showBottomBar: Boolean = true,
    floatingActionButton: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    actions: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    AppScaffold(
        topBar = {
            when {
                titleContent != null -> StandardTopBar(
                    title = titleContent,
                    onBack = onBack,
                    actions = actions ?: {}
                )
                title != null -> StandardTopBar(
                    title = title,
                    onBack = onBack,
                    actions = actions ?: {}
                )
            }
        },
        floatingActionButton = floatingActionButton,
        snackbarHost = snackbarHost,
        content = content
    )
}

// ─── Shared UI Components ──────────────────────────────────────────────────────

/**
 * Standard High-End Card for the Application
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        content = {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    )
}

@Composable
fun DashCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    AppCard(
        modifier = modifier,
        onClick = onClick,
        containerColor = containerColor,
        content = content
    )
}

@Composable
fun DarkDashCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AppDarkGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        content = content
    )
}

@Composable
fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AppDarkGreen,
                fontSize = 18.sp,
                fontFamily = FontFamily.Serif
            )
        )
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = AppGoldColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatusChip(
    label: String,
    containerColor: Color,
    textColor: Color
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(50.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = AppGoldColor.copy(alpha = 0.15f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = AppGoldColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = AppDarkGreen
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SkeletonBox(
    modifier: Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.LightGray.copy(alpha = alpha))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardTopBar(
    title: @Composable () -> Unit,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val containerHeight = 44.dp + statusBarTop

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(containerHeight)
    ) {
        // The Custom Rounded Background with Gradient
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppTopBarGradient)
            ) {
                // Subtle decorative element
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .offset(x = (-40).dp, y = (-40).dp)
                        .background(Color.White.copy(alpha = 0.03f), CircleShape)
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, top = statusBarTop, end = 16.dp)
                ) {
                    // Navigation Icon (Left)
                    if (onBack != null) {
                        Surface(
                            modifier = Modifier.align(Alignment.CenterStart),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.1f)
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Center Content (Logo/Title)
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        title()
                    }

                    // Actions (Right)
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        actions()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardTopBar(
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    StandardTopBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.simple_app),
                contentDescription = "GivenX Logo",
                modifier = Modifier
                    .fillMaxHeight(0.85f)
                    .wrapContentWidth(),
                contentScale = ContentScale.Fit
            )
        },
        onBack = onBack,
        actions = actions
    )
}

// ─── Shared Top Bar Action Slot ────────────────────────────────────────────────
/**
 * Reusable action cluster: [Bell + badge] · [Avatar: photo→initials→icon, gold ring + online dot]
 * Used identically on every main dashboard (Client & Lawyer).
 */
@Composable
fun RowScope.TopBarActions(
    unreadCount:     Int,
    photoUrl:        String?,
    initials:        String?,
    onNotifications: () -> Unit,
    onProfile:       () -> Unit
) {
    // ── Notification bell ──────────────────────────────────────────────────────
    IconButton(onClick = onNotifications) {
        BadgedBox(
            badge = {
                if (unreadCount > 0) Badge(containerColor = Color(0xFFD32F2F)) {
                    Text(
                        text       = if (unreadCount > 99) "99+" else "$unreadCount",
                        color      = Color.White,
                        fontSize   = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        ) {
            Icon(
                imageVector        = Icons.Filled.Notifications,
                contentDescription = "Notifications",
                tint               = Color.White,
                modifier           = Modifier.size(24.dp)
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    showLogo: Boolean = false
) {
    StandardTopBar(
        title = {
            if (showLogo) {
                Image(
                    painter = painterResource(id = R.drawable.simple_app),
                    contentDescription = "GivenX Logo",
                    modifier = Modifier
                        .fillMaxHeight(0.85f)
                        .wrapContentWidth(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp,
                        letterSpacing = 2.sp
                    )
                )
            }
        },
        onBack = onBack,
        actions = actions
    )
}

// ─── Standardized Legal Button ────────────────────────────────────────────────
@Composable
fun LegalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppGoldColor,
            contentColor = AppDarkGreen
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )
    }
}

@Composable
fun LegalDashboardCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    AppCard(
        modifier = modifier,
        onClick = onClick,
        containerColor = containerColor,
        content = content
    )
}

// ─── Common Input Field (New Standard) ──────────────────────────────────────────

@Composable
fun LegalInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = RoundedCornerShape(16.dp),
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            )
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

// ─── App Navigation Tokens ──────────────────────────────────────────────────────

sealed class LawyerTab(val route: String, val icon: ImageVector, val label: String) {
    object Home : LawyerTab("lawyer_home", Icons.Default.Home, "Accueil")
    object Messages : LawyerTab("lawyer_messages", Icons.AutoMirrored.Filled.Chat, "Messages")
    object Clients : LawyerTab("lawyer_clients", Icons.Default.Groups, "Clients")
    object Profile : LawyerTab("lawyer_profile", Icons.Default.Person, "Profil")
    object Creator : LawyerTab("lawyer_creator", Icons.Default.AutoAwesome, "Créateur")
}

sealed class UserTab(val route: String, val icon: ImageVector, val label: String) {
    object Home : UserTab("user_home", Icons.Default.Home, "Accueil")
    object Networking : UserTab("user_networking", Icons.Default.PeopleAlt, "Réseau")
    object Messages : UserTab("user_messages", Icons.AutoMirrored.Filled.Chat, "Messages")
    object Profile : UserTab("user_profile", Icons.Default.Person, "Profil")
}

// ─── StoriesRow (Moved from Feed) ───────────────────────────────────────────────────

@Composable
fun StoriesRow(
    stories: List<StoryDto>?,
    modifier: Modifier = Modifier,
    onStoryClick: (Int) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        androidx.compose.foundation.lazy.LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when {
                stories == null -> {
                    items(5) { StoryShimmerItem() }
                }
                stories.isNotEmpty() -> {
                    itemsIndexed(stories, key = { _, it -> it.id }) { index, story ->
                        StoryRingItem(story = story, onClick = { onStoryClick(index) })
                    }
                }
                else -> {
                    items(5) { StoryShimmerItem() }
                }
            }
        }
    }
}

@Composable
private fun StoryRingItem(story: StoryDto, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier
            .width(72.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier.size(68.dp)
        ) {
            // Unseen Story Border Logic
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val strokePx = 3.dp.toPx()
                val brush = if (story.hasUnseenStory) {
                    androidx.compose.ui.graphics.Brush.sweepGradient(
                        colors = listOf(AppGoldColor, RingGold, AppGoldColor),
                        center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
                    )
                } else {
                    androidx.compose.ui.graphics.SolidColor(Color.Gray.copy(alpha = 0.3f))
                }
                
                drawArc(
                    brush      = brush,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter  = false,
                    topLeft    = androidx.compose.ui.geometry.Offset(strokePx / 2, strokePx / 2),
                    size       = androidx.compose.ui.geometry.Size(size.width - strokePx, size.height - strokePx),
                    style      = androidx.compose.ui.graphics.drawscope.Stroke(width = strokePx)
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
                    coil.compose.AsyncImage(
                        model             = story.lawyerAvatar,
                        contentDescription = story.lawyerName,
                        contentScale      = androidx.compose.ui.layout.ContentScale.Crop,
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

            if (story.isLive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .background(Color(0xFFFF0000), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        "LIVE",
                        color      = Color.White,
                        fontSize   = 7.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        
        val parts = story.lawyerName.split(" ", limit = 2)
        val prefix = if(parts.isNotEmpty()) parts[0] else "Me."
        val lastName = if(parts.size > 1) parts[1] else ""
        
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = androidx.compose.ui.text.font.FontWeight.Normal)) {
                    append("$prefix ")
                }
                withStyle(SpanStyle(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)) {
                    append(lastName)
                }
            },
            modifier  = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize  = 12.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
            color     = AppDarkGreen,
            maxLines  = 1,
            overflow  = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

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

// ─── Premium Bottom Bar ───────────────────────────────────────────────────────
private val SlateGrey   = Color(0xFF8E8E93)
private val ForestGreen = Color(0xFF1A3C34)
private val FabGold     = Color(0xFFC5A059)

sealed class MainTab(val route: String, val icon: ImageVector, val label: String) {
    object Accueil  : MainTab("feed",     Icons.Default.Home,                   "Accueil")
    object Messages : MainTab("messages", Icons.AutoMirrored.Filled.Chat,       "Messages")
    object Reels    : MainTab("reels",    Icons.Default.Movie,                  "Reels")
    object Dossiers : MainTab("dossiers", Icons.Default.Work,                   "Dossiers")
    object Profile  : MainTab("profile",  Icons.Default.Person,                 "Profil")
}

/**
 * Creates a [Shape] with a smooth concave notch centered at the top edge,
 * sized to cradle the center FAB.
 */
private fun createBarNotchShape(fabRadiusPx: Float, notchMarginPx: Float): Shape {
    return object : Shape {
        override fun createOutline(
            size: androidx.compose.ui.geometry.Size,
            layoutDirection: androidx.compose.ui.unit.LayoutDirection,
            density: androidx.compose.ui.unit.Density
        ): Outline {
            val cx = size.width / 2f
            val totalR = fabRadiusPx + notchMarginPx
            val halfW = totalR + notchMarginPx * 2
            val depth = totalR

            return Outline.Generic(Path().apply {
                moveTo(0f, 0f)
                lineTo(cx - halfW, 0f)
                cubicTo(
                    cx - halfW + notchMarginPx * 2, 0f,
                    cx - totalR, depth * 0.85f,
                    cx, depth
                )
                cubicTo(
                    cx + totalR, depth * 0.85f,
                    cx + halfW - notchMarginPx * 2, 0f,
                    cx + halfW, 0f
                )
                lineTo(size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            })
        }
    }
}

/**
 * Premium bottom bar with a gold center FAB (Reels) and smooth notch cutout.
 * Layout: [Accueil] [Messages] ── ●Reels● ── [Dossiers] [Profil]
 *
 * Lawyer mode: long-press the center FAB to reveal Camera + Creator Studio mini-FABs.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HaqqiPremiumBottomBar(
    currentRoute: String?,
    onTabSelected: (MainTab) -> Unit,
    onReelsClick: () -> Unit,
    isLawyer: Boolean = false,
    onRecordReel: () -> Unit = {},
    onCreatorStudio: () -> Unit = {}
) {
    val density = LocalDensity.current
    val fabDiameter = 58.dp
    val barHeight = 64.dp
    val notchMargin = 8.dp

    val fabRadiusPx = with(density) { (fabDiameter / 2).toPx() }
    val notchMarginPx = with(density) { notchMargin.toPx() }

    val barShape = remember(fabRadiusPx, notchMarginPx) {
        createBarNotchShape(fabRadiusPx, notchMarginPx)
    }

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(barHeight + fabDiameter / 2),
        contentAlignment = Alignment.BottomCenter
    ) {
        // ── Bar with notch ──────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .align(Alignment.BottomCenter),
            color = Color(0xFFFAF9F7),
            shadowElevation = 12.dp,
            shape = barShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Accueil, Messages
                PremiumNavItem(
                    icon = MainTab.Accueil.icon,
                    label = MainTab.Accueil.label,
                    selected = currentRoute == MainTab.Accueil.route,
                    onClick = { onTabSelected(MainTab.Accueil) },
                    modifier = Modifier.weight(1f)
                )
                PremiumNavItem(
                    icon = MainTab.Messages.icon,
                    label = MainTab.Messages.label,
                    selected = currentRoute == MainTab.Messages.route,
                    onClick = { onTabSelected(MainTab.Messages) },
                    modifier = Modifier.weight(1f)
                )

                // Center gap for the FAB
                Spacer(modifier = Modifier.weight(1.2f))

                // Right: Dossiers, Profil
                PremiumNavItem(
                    icon = MainTab.Dossiers.icon,
                    label = MainTab.Dossiers.label,
                    selected = currentRoute == MainTab.Dossiers.route,
                    onClick = { onTabSelected(MainTab.Dossiers) },
                    modifier = Modifier.weight(1f)
                )
                PremiumNavItem(
                    icon = MainTab.Profile.icon,
                    label = MainTab.Profile.label,
                    selected = currentRoute == MainTab.Profile.route,
                    onClick = { onTabSelected(MainTab.Profile) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── Expandable mini-FABs (Lawyer only) ─────────────────────────────
        if (isLawyer) {
            AnimatedVisibility(
                visible = expanded,
                enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-52).dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Camera mini-FAB
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = CircleShape,
                        color = AppDarkGreen,
                        border = BorderStroke(1.5.dp, FabGold),
                        shadowElevation = 6.dp,
                        onClick = {
                            expanded = false
                            onRecordReel()
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Record Reel",
                                tint = FabGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    // Creator Studio mini-FAB
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = CircleShape,
                        color = AppDarkGreen,
                        border = BorderStroke(1.5.dp, FabGold),
                        shadowElevation = 6.dp,
                        onClick = {
                            expanded = false
                            onCreatorStudio()
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Creator Studio",
                                tint = FabGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── Gold center FAB (Reels) ─────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Surface(
                modifier = Modifier
                    .size(fabDiameter)
                    .combinedClickable(
                        onClick = {
                            expanded = false
                            onReelsClick()
                        },
                        onLongClick = if (isLawyer) {
                            { expanded = !expanded }
                        } else null
                    ),
                shape = CircleShape,
                color = FabGold,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = "Reels",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Reels",
                color = FabGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
private fun PremiumNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(if (selected) 1.1f else 1f, label = "nav_scale")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 6.dp)
            .scale(scale)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) ForestGreen else SlateGrey,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (selected) ForestGreen else SlateGrey,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
            maxLines = 1
        )
        if (selected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(AppGoldColor)
            )
        }
    }
}

// ─── Legal Input Field (Standardized) ──────────────────────────────────────────

@Composable
fun CustomLegalInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontFamily = FontFamily.Serif, fontSize = 13.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = AppDarkGreen,
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White.copy(alpha = 0.95f),
                focusedBorderColor = AppDarkGreen,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = Color.Red
            )
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(start = 16.dp, top = 3.dp)
            )
        }
    }
}