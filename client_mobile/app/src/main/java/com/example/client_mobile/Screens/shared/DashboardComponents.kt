package com.example.client_mobile.screens.shared

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
val AppDarkGreen = Color(0xFF1B3124)
val AppGoldColor = Color(0xFFD4AF37)

// ─── Shared Background ────────────────────────────────────────────────────────
@Composable
fun DashBoardBackground(content: @Composable BoxScope.() -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_app),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.72f))
        )
        content()
    }
}

// ─── Section Header ───────────────────────────────────────────────────────────
@Composable
fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = AppDarkGreen
        )
        if (actionLabel != null && onAction != null) {
            Text(
                text = actionLabel,
                fontSize = 13.sp,
                fontFamily = FontFamily.Serif,
                color = AppGoldColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onAction() }
            )
        }
    }
}

// ─── Light Card ───────────────────────────────────────────────────────────────
@Composable
fun DashCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "dashCardScale"
    )
    Surface(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .then(
                if (onClick != null)
                    Modifier.clickable(interactionSource = interactionSource, indication = null) { onClick() }
                else Modifier
            ),
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = 0.92f),
        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.10f)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp), content = content)
    }
}

// ─── Dark Card ────────────────────────────────────────────────────────────────
@Composable
fun DarkDashCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "darkDashCardScale"
    )
    Surface(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth()
            .then(
                if (onClick != null)
                    Modifier.clickable(interactionSource = interactionSource, indication = null) { onClick() }
                else Modifier
            ),
        shape = RoundedCornerShape(22.dp),
        color = AppDarkGreen,
        border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.40f)),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(18.dp), content = content)
    }
}

// ─── Status Chip ──────────────────────────────────────────────────────────────
@Composable
fun StatusChip(
    label: String,
    containerColor: Color,
    textColor: Color = Color.White
) {
    Surface(shape = RoundedCornerShape(50.dp), color = containerColor) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = textColor,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

// ─── Quick Action Button ──────────────────────────────────────────────────────
@Composable
fun QuickActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
        label = "quickActionScale"
    )
    Column(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(16.dp),
            color = AppDarkGreen,
            border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.50f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = AppGoldColor,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = FontFamily.Serif,
            color = AppDarkGreen,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

// ─── Compact Stat Tile ────────────────────────────────────────────────────────
@Composable
fun CompactStatTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    count: String,
    label: String
) {
    Surface(
        modifier = modifier.height(85.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.18f),
        border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.40f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                count,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                fontFamily = FontFamily.Serif
            )
            Text(
                label,
                color = Color.White.copy(alpha = 0.65f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Serif
            )
        }
    }
}

// ─── Client Bottom Bar ────────────────────────────────────────────────────────
@Composable
fun UserBottomBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf(
        Pair(Icons.Default.Home, "Accueil"),
        Pair(Icons.Default.Assignment, "Dossiers"),
        Pair(Icons.Default.Chat, "Messages"),
        Pair(Icons.Default.Person, "Profil")
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(70.dp),
        shape = RoundedCornerShape(25.dp),
        color = AppDarkGreen,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, (icon, label) ->
                val selected = selectedTab == index
                val iconScale by animateFloatAsState(
                    targetValue = if (selected) 1.20f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                    label = "userTabScale_$index"
                )
                val indicatorWidth by animateDpAsState(
                    targetValue = if (selected) 16.dp else 0.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                    label = "userTabIndicator_$index"
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) AppGoldColor else Color.White.copy(alpha = 0.50f),
                        modifier = Modifier.size(24.dp).scale(iconScale)
                    )
                    Text(
                        text = label,
                        color = if (selected) AppGoldColor else Color.White.copy(alpha = 0.50f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .width(indicatorWidth)
                            .clip(CircleShape)
                            .background(AppGoldColor)
                    )
                }
            }
        }
    }
}

// ─── Navigation Route Tokens ──────────────────────────────────────────────────
sealed class LawyerTab(val route: String, val icon: ImageVector, val label: String) {
    data object Home     : LawyerTab("lawyer_home",     Icons.Default.Home,   "Accueil")
    data object Messages : LawyerTab("lawyer_messages", Icons.Default.Chat,   "Messages")
    data object Clients  : LawyerTab("lawyer_clients",  Icons.Default.Groups, "Clients")
    data object Profile  : LawyerTab("lawyer_profile",  Icons.Default.Person, "Profil")
    companion object { val all get() = listOf(Home, Messages, Clients, Profile) }
}

sealed class UserTab(val route: String, val icon: ImageVector, val label: String) {
    data object Home     : UserTab("user_home",      Icons.Default.Home,          "Accueil")
    data object Cases    : UserTab("user_cases",     Icons.Default.Assignment,    "Dossiers")
    data object Matching : UserTab("user_matching",  Icons.Default.Favorite,      "Matching")
    data object Reels    : UserTab("user_reels",     Icons.Default.PlayCircle,    "Reels")
    data object Live     : UserTab("user_live",      Icons.Default.LiveTv,        "Live")
    data object Messages : UserTab("user_messages",  Icons.Default.Chat,          "Messages")
    data object Profile  : UserTab("user_profile",   Icons.Default.Person,        "Profil")
    companion object { val all get() = listOf(Home, Cases, Matching, Reels, Live) }
}

// ─── Lawyer Nav Bottom Bar ─────────────────────────────────────────────────────
@Composable
fun LawyerNavBottomBar(
    currentRoute: String?,
    onNavigateTo: (LawyerTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(70.dp),
        shape = RoundedCornerShape(25.dp),
        color = AppDarkGreen,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LawyerTab.all.forEach { tab ->
                val selected = currentRoute == tab.route
                val iconScale by animateFloatAsState(
                    targetValue = if (selected) 1.20f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                    label = "lawyerTabScale_${tab.route}"
                )
                val indicatorWidth by animateDpAsState(
                    targetValue = if (selected) 16.dp else 0.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                    label = "lawyerTabIndicator_${tab.route}"
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onNavigateTo(tab) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (selected) AppGoldColor else Color.White.copy(alpha = 0.50f),
                        modifier = Modifier.size(24.dp).scale(iconScale)
                    )
                    Text(
                        text = tab.label,
                        color = if (selected) AppGoldColor else Color.White.copy(alpha = 0.50f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .width(indicatorWidth)
                            .clip(CircleShape)
                            .background(AppGoldColor)
                    )
                }
            }
        }
    }
}

// ─── User Nav Bottom Bar ───────────────────────────────────────────────────────
@Composable
fun UserNavBottomBar(
    currentRoute: String?,
    onNavigateTo: (UserTab) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(70.dp),
        shape = RoundedCornerShape(25.dp),
        color = AppDarkGreen,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserTab.all.forEach { tab ->
                val selected = currentRoute == tab.route
                val iconScale by animateFloatAsState(
                    targetValue = if (selected) 1.20f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                    label = "userNavTabScale_${tab.route}"
                )
                val indicatorWidth by animateDpAsState(
                    targetValue = if (selected) 16.dp else 0.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                    label = "userNavTabIndicator_${tab.route}"
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onNavigateTo(tab) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (selected) AppGoldColor else Color.White.copy(alpha = 0.50f),
                        modifier = Modifier.size(24.dp).scale(iconScale)
                    )
                    Text(
                        text = tab.label,
                        color = if (selected) AppGoldColor else Color.White.copy(alpha = 0.50f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .width(indicatorWidth)
                            .clip(CircleShape)
                            .background(AppGoldColor)
                    )
                }
            }
        }
    }
}

// ─── Shared Profile Text Field ────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = "",
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 6,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(label, fontFamily = FontFamily.Serif, fontSize = 13.sp)
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (isError) MaterialTheme.colorScheme.error else AppGoldColor,
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            isError = isError,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppDarkGreen,
                unfocusedBorderColor = AppDarkGreen.copy(alpha = 0.28f),
                focusedLabelColor = AppDarkGreen,
                unfocusedLabelColor = AppDarkGreen.copy(alpha = 0.50f),
                focusedContainerColor = Color.White.copy(alpha = 0.95f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.88f),
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.05f),
                cursorColor = AppDarkGreen
            )
        )
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(start = 16.dp, top = 3.dp)
            )
        }
    }
}
