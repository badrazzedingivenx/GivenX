package com.example.client_mobile.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
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
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
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
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

// ─── Quick Action Button ──────────────────────────────────────────────────────
@Composable
fun QuickActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(10.dp),
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) AppGoldColor else Color.White.copy(alpha = 0.50f),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = label,
                        color = if (selected) AppGoldColor else Color.White.copy(alpha = 0.50f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
