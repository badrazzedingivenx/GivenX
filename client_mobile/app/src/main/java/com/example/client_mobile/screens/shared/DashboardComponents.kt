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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
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

// ─── Brand Tokens ─────────────────────────────────────────────────────────────
val AppDarkGreen = Color(0xFF0F291E) // Deeper green for premium feel
val AppGoldColor = Color(0xFFD4AF37) // Metallic Gold
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
    Box(modifier = modifier.fillMaxSize()) {
        if (showBackground) {
            // Layer 1: Background Image
            Image(
                painter = painterResource(id = R.drawable.background_app),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Layer 2: White Overlay (Only affects the background image)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.20f))
            )
        }

        // Layer 3: UI Content (Above the overlay)
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = topBar,
            bottomBar = bottomBar,
            floatingActionButton = floatingActionButton,
            snackbarHost = snackbarHost,
            containerColor = Color.Transparent,
            content = content
        )
    }
}

/**
 * Base screen with optional standardized TopBar and BottomBar.
 */
@Composable
fun BaseScreen(
    title: String? = null,
    onBack: (() -> Unit)? = null,
    showBottomBar: Boolean = true,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    AppScaffold(
        topBar = {
            if (title != null) {
                StandardTopBar(title = title, onBack = onBack)
            }
        },
        floatingActionButton = floatingActionButton,
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
                    .background(AppTopBarGradient)
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
                        .padding(start = 16.dp, top = 20.dp, end = 16.dp)
                ) {
                    // Navigation Icon (Left)
                    if (onBack != null) {
                        Surface(
                            modifier = Modifier.align(Alignment.CenterStart).padding(top = 20.dp),
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
                        Box(modifier = Modifier.padding(top = 20.dp)) {
                            title()
                        }
                    }

                    // Actions (Right)
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd).padding(top = 20.dp),
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
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    StandardTopBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.simple_app),
                contentDescription = "GivenX Logo",
                modifier = Modifier.height(70.dp),
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
                        text       = if (unreadCount > 9) "9+" else "$unreadCount",
                        color      = Color.White,
                        fontSize   = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        ) {
            Icon(
                imageVector        = Icons.Default.Notifications,
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
                    modifier = Modifier.height(70.dp),
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

@Composable
fun LawyerNavBottomBar(
    currentRoute: String?,
    onTabSelected: (LawyerTab) -> Unit
) {
    val tabs = listOf(
        LawyerTab.Home,
        LawyerTab.Messages,
        LawyerTab.Clients,
        LawyerTab.Creator,
        LawyerTab.Profile
    )
    
    Surface(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
            .height(72.dp),
        shape = RoundedCornerShape(28.dp),
        color = AppDarkGreen,
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                BottomNavItem(
                    icon = tab.icon,
                    label = tab.label,
                    selected = currentRoute == tab.route,
                    onClick = { onTabSelected(tab) }
                )
            }
        }
    }
}

@Composable
fun UserNavBottomBar(
    currentRoute: String?,
    onTabSelected: (UserTab) -> Unit
) {
    val tabs = listOf(
        UserTab.Home,
        UserTab.Messages,
        UserTab.Profile
    )

    Surface(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
            .height(72.dp),
        shape = RoundedCornerShape(28.dp),
        color = AppDarkGreen,
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                BottomNavItem(
                    icon = tab.icon,
                    label = tab.label,
                    selected = currentRoute == tab.route,
                    onClick = { onTabSelected(tab) }
                )
            }
        }
    }
}

// ─── App Bottom Navigation ────────────────────────────────────────────────────
@Composable
fun AppBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)
            .height(72.dp),
        shape = RoundedCornerShape(28.dp),
        color = AppDarkGreen,
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Accueil",
                selected = currentRoute == "home",
                onClick = { onNavigate("home") }
            )
            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.Chat,
                label = "Messages",
                selected = currentRoute == "messages",
                onClick = { onNavigate("messages") }
            )
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Profil",
                selected = currentRoute == "profile",
                onClick = { onNavigate("profile") }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (selected) 1.15f else 1f)
    val alpha by animateFloatAsState(if (selected) 1f else 0.5f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 4.dp, horizontal = 12.dp)
            .scale(scale)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) AppGoldColor else Color.White,
            modifier = Modifier.size(24.dp).graphicsLayer(alpha = alpha)
        )
        if (selected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(AppGoldColor)
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.5f),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
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
