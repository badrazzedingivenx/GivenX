package com.example.client_mobile.screens.shared

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client_mobile.R

// ─── Constants & Colors ───────────────────────────────────────────────────────
val AppDarkGreen       = Color(0xFF1B4332)
val AppGoldColor       = Color(0xFFD4AF37)
val AppGoldGradient    = Brush.linearGradient(listOf(Color(0xFFD4AF37), Color(0xFFF1D592)))
val AppTopBarGradient  = Brush.verticalGradient(listOf(AppDarkGreen, AppDarkGreen.copy(alpha = 0.9f)))

val StatusGreen        = Color(0xFF2D6A4F)
val StatusGreenBg      = Color(0xFFD8F3DC)
val StatusOrange       = Color(0xFFD97706)
val StatusOrangeBg     = Color(0xFFFEF3C7)
val StatusRed          = Color(0xFFDC2626)
val StatusRedBg        = Color(0xFFFEE2E2)
val StatusBlue         = Color(0xFF2563EB)
val StatusBlueBg       = Color(0xFFDBEAFE)
val StatusGray         = Color(0xFF4B5563)
val StatusGrayBg       = Color(0xFFF3F4F6)
val AppGoldBg          = Color(0xFFFFFBEB)
val AppSubtitleGray    = Color(0xFF6B7280)

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
        containerColor = if (showBackground) Color(0xFFF9FAFB) else Color.Transparent
    ) { paddingValues ->
        content(paddingValues)
    }
}

@Composable
fun BaseScreen(
    title: String? = null,
    onBack: (() -> Unit)? = null,
    isLawyer: Boolean = false,
    onNotifications: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    titleContent: (@Composable () -> Unit)? = null,
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    AppScaffold(
        topBar = {
            if (titleContent != null) {
                StandardTopBar(
                    title = titleContent,
                    onBack = onBack,
                    actions = {
                        if (actions != null) {
                            actions()
                        } else if (onNotifications != null) {
                            TopBarActions(onNotifications = onNotifications)
                        }
                    }
                )
            } else if (title != null) {
                StandardTopBar(
                    title = title,
                    onBack = onBack,
                    onNotifications = onNotifications,
                    actions = actions
                )
            }
        },
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        content = content
    )
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier
    Surface(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        shadowElevation = 2.dp,
        border = if (containerColor == Color.White) null else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun DashCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = Color.White,
    content: @Composable (ColumnScope.() -> Unit)
) {
    AppCard(modifier, onClick, containerColor, content)
}

@Composable
fun DarkDashCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    AppCard(
        modifier = modifier,
        onClick = onClick,
        containerColor = AppDarkGreen,
        content = content
    )
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AppDarkGreen,
                fontSize = 18.sp
            )
        )
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                modifier = Modifier.clickable(onClick = onActionClick),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = AppGoldColor,
                    fontWeight = FontWeight.SemiBold
                )
            )
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
    val containerHeight = 120.dp
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(containerHeight)
    ) {
        // The Custom Rounded Background with Gradient
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
            shadowElevation = 10.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppDarkGreen.copy(alpha = 0.92f))
            ) {
                // Subtle decorative element
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .offset(x = (-50).dp, y = (-50).dp)
                        .background(Color.White.copy(alpha = 0.03f), CircleShape)
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(start = 16.dp, top = 20.dp, end = 16.dp)
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
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Center Content (Logo/Title)
                    Box(
                        modifier = Modifier.align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        title()
                    }

                    // Actions (Right)
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
    title: String,
    onBack: (() -> Unit)? = null,
    onNotifications: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    StandardTopBar(
        title = {
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
        },
        onBack = onBack,
        actions = {
            if (actions != null) {
                actions()
            } else if (onNotifications != null) {
                TopBarActions(onNotifications = onNotifications)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardTopBar(
    onBack: (() -> Unit)? = null,
    onNotifications: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    StandardTopBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "GivenX Logo",
                modifier = Modifier.height(75.dp).padding(top = 10.dp),
                contentScale = ContentScale.Fit
            )
        },
        onBack = onBack,
        actions = {
            if (actions != null) {
                actions()
            } else if (onNotifications != null) {
                TopBarActions(onNotifications = onNotifications)
            }
        }
    )
}

// ─── Shared Top Bar Action Slot ────────────────────────────────────────────────
/**
 * Reusable action cluster: [Bell + badge] · [Avatar: photo→initials→icon, gold ring + online dot]
 * Used identically on every main dashboard (Client & Lawyer).
 */
@Composable
fun RowScope.TopBarActions(
    unreadCount:     Int? = null,
    photoUrl:        String? = null,
    initials:        String? = null,
    onNotifications: () -> Unit,
    onProfile:       () -> Unit = {}
) {
    val isLawyer = com.example.client_mobile.network.TokenManager.getUserType() == "lawyer"
    val lawyerStatsState = if (isLawyer) {
        val vm: com.example.client_mobile.screens.lawyer.LawyerDashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        vm.stats.collectAsState().value
    } else null
    
    // Auto-fetch notifications to keep badge updated
    val notifViewModel: NotificationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    val dynamicUnreadCount = unreadCount ?: if (isLawyer) {
        lawyerStatsState?.newRequests ?: NotificationRepository.lawyerNotifications.count { !it.isRead }
    } else {
        NotificationRepository.userNotifications.count { !it.isRead }
    }

    // ── Notification bell ──────────────────────────────────────────────────────
    IconButton(onClick = onNotifications) {
        BadgedBox(
            badge = {
                if (dynamicUnreadCount > 0) {
                    Badge(
                        containerColor = Color(0xFFD32F2F),
                        modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                    ) {
                        Text(
                            text       = if (dynamicUnreadCount > 9) "9+" else "$dynamicUnreadCount",
                            color      = Color.White,
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector        = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint               = Color.White,
                modifier           = Modifier.size(26.dp)
            )
        }
    }
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
    object Home     : LawyerTab("home",     Icons.Default.Notifications, "Home")
    object Messages : LawyerTab("messages", Icons.Default.Notifications, "Messages")
    object Clients  : LawyerTab("clients",  Icons.Default.Notifications, "Clients")
    object Profile  : LawyerTab("profile",  Icons.Default.Notifications, "Profile")
    object Creator  : LawyerTab("creator",  Icons.Default.Notifications, "Studio")
}

sealed class UserTab(val route: String, val icon: ImageVector, val label: String) {
    object Home       : UserTab("home",       Icons.Default.Notifications, "Home")
    object Networking : UserTab("networking", Icons.Default.Notifications, "Réseau")
    object Messages   : UserTab("messages",   Icons.Default.Notifications, "Messages")
    object Profile    : UserTab("profile",    Icons.Default.Notifications, "Profil")
}

sealed class MainTab(val route: String, val icon: ImageVector, val label: String) {
    object Feed      : MainTab("Feed",      Icons.Default.Notifications, "Fil")
    object Dashboard : MainTab("Dashboard", Icons.Default.Notifications, "Tableau")
    object Messages  : MainTab("Messages",  Icons.Default.Notifications, "Messages")
    object Profile   : MainTab("Profile",   Icons.Default.Notifications, "Profil")
}

@Composable
fun MainNavBottomBar(currentRoute: String?, onTabSelected: (MainTab) -> Unit) {
    // Implementation here
}

@Composable
fun LawyerNavBottomBar(currentRoute: String?, onTabSelected: (LawyerTab) -> Unit) {
    // Implementation here
}

@Composable
fun UserNavBottomBar(currentRoute: String?, onTabSelected: (UserTab) -> Unit) {
    // Implementation here
}

@Composable
fun AppBottomNavigation(currentRoute: String?, onNavigate: (String) -> Unit) {
    // Implementation here
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    // Implementation here
}

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
    // Implementation here
}
