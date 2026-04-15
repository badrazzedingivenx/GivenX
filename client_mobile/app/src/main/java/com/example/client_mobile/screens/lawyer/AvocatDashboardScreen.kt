package com.example.client_mobile.screens.lawyer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client_mobile.network.dto.LawyerProfileDto
import com.example.client_mobile.network.dto.LawyerStatsDto
import com.example.client_mobile.network.dto.RecentConsultationDto
import com.example.client_mobile.network.dto.RevenueMonthDto
import com.example.client_mobile.screens.shared.*

// ─── Fallback chart data when the API hasn't returned data yet
private val FallbackRevenue = listOf(
    RevenueMonthDto("Jan", 8400f),
    RevenueMonthDto("Fév", 11200f),
    RevenueMonthDto("Mar",  9600f),
    RevenueMonthDto("Avr", 14800f),
    RevenueMonthDto("Mai", 12400f),
    RevenueMonthDto("Jun", 16200f)
)

// ─── Date formatter ──────────────────────────────────────────────────────────
/**
 * Converts an ISO date string (e.g. "2026-03-15" or "2026-03-15T10:00:00Z")
 * to a clean French short-date (e.g. "15 Mars").
 * Falls back to the raw string on any parse error.
 */
private fun formatConsultDate(raw: String): String {
    if (raw.isBlank()) return ""
    val months = listOf(
        "Jan", "Fév", "Mar", "Avr", "Mai", "Juin",
        "Juil", "Août", "Sep", "Oct", "Nov", "Déc"
    )
    return try {
        val datePart = raw.take(10)          // "2026-03-15"
        val parts    = datePart.split("-")   // ["2026", "03", "15"]
        val day      = parts[2].trimStart('0').ifEmpty { parts[2] }
        val month    = months.getOrNull(parts[1].toInt() - 1) ?: parts[1]
        "$day $month"
    } catch (_: Exception) { raw }
}

// ─── Main Screen ──────────────────────────────────────────────────────────────
@Composable
fun AvocatDashboardScreen(
    paddingValues: PaddingValues,
    profile: LawyerProfileDto? = null,
    stats: LawyerStatsDto? = null,
    revenueMonthly: List<RevenueMonthDto> = emptyList(),
    recentConsultations: List<RecentConsultationDto> = emptyList(),
    consultationsError: Boolean = false,
    onNavigateToRequests: () -> Unit = {},
    onNavigateToPayments: () -> Unit = {},
    onNavigateToCreator: () -> Unit = {},
    onRetryConsultations: () -> Unit = {}
) {
    val isLoading = stats == null

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { DashHeader(profile = profile, isLoading = isLoading) }
        item { DashStatsGrid(stats = stats, isLoading = isLoading) }
        item { DashRevenueCard(revenue = revenueMonthly, isLoading = isLoading) }
        item {
            DashConsultationsCard(
                consultations      = recentConsultations,
                isLoading          = isLoading,
                consultationsError = consultationsError,
                onRetry            = onRetryConsultations,
                onViewAll          = onNavigateToPayments
            )
        }
        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ─── Header ───────────────────────────────────────────────────────────────────
@Composable
private fun DashHeader(profile: LawyerProfileDto?, isLoading: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                SkeletonBox(modifier = Modifier.width(80.dp).height(12.dp))
                Spacer(Modifier.height(6.dp))
                SkeletonBox(modifier = Modifier.width(160.dp).height(22.dp))
            } else {
                Text(
                    text = "Bonjour, Maître",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = AppSubtitleGray,
                        fontSize = 14.sp
                    )
                )
                val displayName = profile?.fullName?.ifBlank { null }
                    ?: LawyerSession.fullName.ifBlank { "Avocat" }
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    ),
                    color = AppDarkGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ─── Stats Grid (2 × 2) ───────────────────────────────────────────────────────
@Composable
private fun DashStatsGrid(stats: LawyerStatsDto?, isLoading: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashStatCard(
                modifier  = Modifier.weight(1f),
                icon      = Icons.Default.Payments,
                label     = "Revenus",
                value     = stats?.totalRevenueMonth?.let { "%.0f MAD".format(it) },
                change    = stats?.revenueChange  ?: 0f,
                isLoading = isLoading
            )
            DashStatCard(
                modifier  = Modifier.weight(1f),
                icon      = Icons.Default.Groups,
                label     = "Clients actifs",
                value     = stats?.activeClients?.toString(),
                change    = stats?.clientsChange  ?: 0f,
                isLoading = isLoading
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashStatCard(
                modifier  = Modifier.weight(1f),
                icon      = Icons.Default.Star,
                label     = "Note moyenne",
                value     = stats?.averageRating?.let { "%.1f ★".format(it) },
                change    = stats?.ratingChange   ?: 0f,
                isLoading = isLoading
            )
            DashStatCard(
                modifier  = Modifier.weight(1f),
                icon      = Icons.Default.Visibility,
                label     = "Nouvelles demandes",
                value     = stats?.newRequests?.toString(),
                change    = stats?.requestsChange ?: 0f,
                isLoading = isLoading
            )
        }
    }
}

@Composable
private fun DashStatCard(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    value: String?,
    change: Float,
    isLoading: Boolean
) {
    LegalDashboardCard(modifier = modifier) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(AppDarkGreen.copy(alpha = 0.10f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AppDarkGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
                // % change badge
                if (!isLoading && change != 0f) {
                    val positive = change > 0f
                    StatusChip(
                        label = "${if (positive) "+" else ""}${"%.1f".format(change)}%",
                        containerColor = if (positive) StatusGreenBg else StatusRedBg,
                        textColor = if (positive) StatusGreen else StatusRed
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            if (isLoading) {
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.60f).height(22.dp))
                Spacer(Modifier.height(4.dp))
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.38f).height(10.dp))
            } else {
                Text(
                    text = value ?: "—",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    ),
                    color = AppDarkGreen,
                    maxLines = 1
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AppSubtitleGray
                    ),
                    maxLines = 1
                )
            }
    }
}

// ─── Revenue Bar Chart ────────────────────────────────────────────────────────
@Composable
private fun DashRevenueCard(revenue: List<RevenueMonthDto>, isLoading: Boolean) {
    val displayRevenue = revenue.ifEmpty { FallbackRevenue }
    val isFallback = revenue.isEmpty()
    val maxVal = displayRevenue.maxOfOrNull { it.amount }.takeIf { it != null && it > 0f } ?: 1f

    DashCard(modifier = Modifier.fillMaxWidth()) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Revenus — 6 mois",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    ),
                    color = AppDarkGreen
                )
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AppGoldColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        "MAD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppGoldColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            // Bars
            if (isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    listOf(0.60f, 0.80f, 0.50f, 1.00f, 0.70f, 0.90f).forEach { frac ->
                        SkeletonBox(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(frac),
                            shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                        )
                    }
                }
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    val count = displayRevenue.size
                    if (count < 2) return@Canvas
                    val stepX = size.width / (count - 1).toFloat()
                    val points = displayRevenue.mapIndexed { i, m ->
                        Offset(
                            x = i * stepX,
                            y = size.height - (m.amount / maxVal) * size.height
                        )
                    }
                    // Gradient fill under the line
                    val fillPath = Path().apply {
                        moveTo(points.first().x, size.height)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, size.height)
                        close()
                    }
                    drawPath(
                        path  = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                AppDarkGreen.copy(alpha = 0.22f),
                                AppGoldColor.copy(alpha  = 0.06f)
                            ),
                            startY = 0f,
                            endY   = size.height
                        )
                    )
                    // Line segments
                    for (i in 0 until count - 1) {
                        drawLine(
                            color       = AppDarkGreen.copy(alpha = 0.6f),
                            start       = points[i],
                            end         = points[i + 1],
                            strokeWidth = 2.5.dp.toPx(),
                            cap         = StrokeCap.Round
                        )
                    }
                    // Point markers — white ring + gold fill
                    points.forEach { pt ->
                        drawCircle(color = Color.White, radius = 4.dp.toPx(),   center = pt)
                        drawCircle(color = AppGoldColor,   radius = 2.5.dp.toPx(), center = pt)
                    }
                }
                Spacer(Modifier.height(8.dp))
                // Month labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    displayRevenue.forEach { m ->
                        Text(
                            text = m.month.take(3),
                            modifier = Modifier.weight(1f),
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                if (isFallback) {
                    Text(
                        "* Données approximatives. Synchronisez pour actualiser.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
    }
}

// ─── Recent Consultations ─────────────────────────────────────────────────────
@Composable
private fun DashConsultationsCard(
    consultations: List<RecentConsultationDto>,
    isLoading: Boolean,
    consultationsError: Boolean = false,
    onRetry: () -> Unit = {},
    onViewAll: () -> Unit
) {
    LegalDashboardCard(modifier = Modifier.fillMaxWidth()) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Consultations récentes",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    ),
                    color = AppDarkGreen
                )
                if (!isLoading && consultations.isNotEmpty()) {
                    TextButton(
                        onClick = onViewAll,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Voir tout", fontSize = 12.sp, color = AppGoldColor)
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = AppGoldColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            when {
                isLoading -> repeat(3) { i ->
                    ConsultationSkeletonRow()
                    if (i < 2) HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = Color(0xFFE2E8F0)
                    )
                }
                consultationsError -> {
                    // Track in-flight retry so the button shows a spinner
                    var isRetrying by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                "Impossible de charger",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                "Vérifiez votre connexion internet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                textAlign = TextAlign.Center
                            )
                            // Gold « Réessayer » button
                            OutlinedButton(
                                onClick = {
                                    isRetrying = true
                                    onRetry()
                                },
                                enabled = !isRetrying,
                                shape = RoundedCornerShape(50.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp, AppGoldColor
                                ),
                                contentPadding = PaddingValues(
                                    horizontal = 20.dp, vertical = 8.dp
                                )
                            ) {
                                if (isRetrying) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = AppGoldColor
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = AppGoldColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "Réessayer",
                                    fontSize = 13.sp,
                                    color = AppGoldColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    // Reset local spinner when the error clears (retry succeeded)
                    LaunchedEffect(consultationsError) {
                        if (!consultationsError) isRetrying = false
                    }
                }
                consultations.isEmpty() -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            "Aucune consultation récente",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
                else -> consultations.forEachIndexed { i, c ->
                    ConsultationRow(c)
                    if (i < consultations.lastIndex) HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
    }
}

@Composable
private fun ConsultationRow(consultation: RecentConsultationDto) {
    val statusText = consultation.status.ifBlank { "—" }
    val (statusColor, statusBg) = when {
        statusText.contains("term",    ignoreCase = true) ||
        statusText.contains("compl",   ignoreCase = true) -> StatusGreen to StatusGreenBg
        statusText.contains("attente", ignoreCase = true) ||
        statusText.contains("pend",    ignoreCase = true)  -> AppGoldColor to AppGoldColor.copy(alpha = 0.12f)
        else                                               -> StatusRed to StatusRedBg
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Avatar initials circle ─────────────────────────────────────
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(AppDarkGreen.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = consultation.clientName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppDarkGreen
            )
        }
        // ── Name + case + status pill ───────────────────────────────
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = consultation.clientName.ifBlank { "Client" },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                ),
                color = AppDarkGreen,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            // Show "Case · Date" when both fields are available; fall back gracefully
            val consultSubtitle = buildString {
                if (consultation.legalCase.isNotBlank()) append(consultation.legalCase)
                val fd = formatConsultDate(consultation.date)
                if (fd.isNotBlank()) {
                    if (isNotEmpty()) append("  ·  ")
                    append(fd)
                }
            }.ifBlank { "—" }
            Text(
                text = consultSubtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AppSubtitleGray
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Status pill
                StatusChip(
                    label = statusText,
                    containerColor = statusBg,
                    textColor = statusColor
                )
                // Price
                if (consultation.price > 0f) {
                    Text(
                        text = "· %.0f MAD".format(consultation.price),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
        // ── Gold action button (Video/Call) ──────────────────────────
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AppGoldColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.VideoCall,
                contentDescription = "Démarrer la consultation",
                tint = AppGoldColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ConsultationSkeletonRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar skeleton
        SkeletonBox(modifier = Modifier.size(42.dp), shape = CircleShape)
        // Text lines + pill skeleton
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SkeletonBox(modifier = Modifier.fillMaxWidth(0.55f).height(12.dp))
            SkeletonBox(modifier = Modifier.fillMaxWidth(0.40f).height(10.dp))
            SkeletonBox(
                modifier = Modifier.width(52.dp).height(8.dp),
                shape = RoundedCornerShape(50.dp)
            )
        }
        // Action button skeleton
        SkeletonBox(modifier = Modifier.size(36.dp), shape = CircleShape)
    }
}
