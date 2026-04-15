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
import com.example.client_mobile.screens.shared.LawyerSession
import com.example.client_mobile.screens.shared.SkeletonBox

// ─── Branding tokens ──────────────────────────────────────────────────────────
// Primary: deep forest green  — #2E5D46
// Accent:  muted gold          — #C5A059
// Background: soft slate-50    — #F8FAFC
private val BrandGreen     = Color(0xFF2E5D46)   // Deep forest green — headings, icons
private val AppDarkGreen   = Color(0xFF1B3124)   // App dark green   — API metric values
private val ConsultGreen   = Color(0xFF1B3A2C)   // Client name color
private val BrandGreenMid  = Color(0xFF3D7A5F)   // Lighter green     — chart line
private val BrandGold      = Color(0xFFC5A059)   // Muted gold        — badges, accents, "Voir tout"
private val BrandGoldLight = Color(0xFFFBF5E8)   // Gold-50           — gold badge bg
private val DashSlate50    = Color(0xFFF8FAFC)   // Slate-50          — page background
private val DashSlate800   = Color(0xFF1E293B)   // Slate-800         — headings
private val DashSlate400   = Color(0xFF64748B)   // Slate-500         — secondary labels
private val DashGreen      = Color(0xFF166534)   // Green-900         — Terminé text
private val DashGreenBg    = Color(0xFFDCFCE7)   // Green-100         — Terminé bg
private val DashAmber      = Color(0xFF92400E)   // Yellow-800        — En attente text
private val DashAmberBg    = Color(0xFFFEFCE8)   // Yellow-50         — En attente bg
private val DashAmberBorder= Color(0xFFFDE68A)   // Yellow-200        — En attente border
private val DashRoseBg     = Color(0xFFFFF1F2)   // Rose-50           — negative trend bg
private val DashRose600    = Color(0xFFE11D48)   // Rose-600          — negative trend text
private val DashRed        = Color(0xFFDC2626)   // Red-600           — destructive / declined

// Fallback chart data when the API hasn't returned data yet
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
            .padding(paddingValues)
            .background(DashSlate50),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    color = DashSlate400
                )
                val displayName = profile?.fullName?.ifBlank { null }
                    ?: LawyerSession.fullName.ifBlank { "Avocat" }
                Text(
                    text = displayName,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = DashSlate800,
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
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        // Left accent stripe (border-l-4 green)
        Box {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                    .background(BrandGreen)
            )
            Column(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(BrandGreen.copy(alpha = 0.10f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
                // % change badge — hidden when 0
                if (!isLoading && change != 0f) {
                    val positive = change > 0f
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = if (positive) DashGreenBg else DashRoseBg
                    ) {
                        Text(
                            text = "${if (positive) "+" else ""}${"%.1f".format(change)}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (positive) DashGreen else DashRose600,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            if (isLoading) {
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.60f).height(22.dp))
                Spacer(Modifier.height(2.dp))
                SkeletonBox(modifier = Modifier.fillMaxWidth(0.38f).height(10.dp))
            } else {
                Text(
                    text = value ?: "—",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = AppDarkGreen,
                    maxLines = 1
                )
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = DashSlate400,
                    maxLines = 1
                )
            }
            }  // Column
        }  // Box
    }  // Surface
}

// ─── Revenue Bar Chart ────────────────────────────────────────────────────────
@Composable
private fun DashRevenueCard(revenue: List<RevenueMonthDto>, isLoading: Boolean) {
    val displayRevenue = revenue.ifEmpty { FallbackRevenue }
    val isFallback = revenue.isEmpty()
    val maxVal = displayRevenue.maxOfOrNull { it.amount }.takeIf { it != null && it > 0f } ?: 1f

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Box {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                    .background(BrandGold)
            )
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Revenus — 6 mois",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DashSlate800
                )
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BrandGoldLight
                ) {
                    Text(
                        "MAD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandGold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

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
                                BrandGreen.copy(alpha = 0.22f),
                                BrandGold.copy(alpha  = 0.06f)
                            ),
                            startY = 0f,
                            endY   = size.height
                        )
                    )
                    // Line segments
                    for (i in 0 until count - 1) {
                        drawLine(
                            color       = BrandGreenMid,
                            start       = points[i],
                            end         = points[i + 1],
                            strokeWidth = 2.5.dp.toPx(),
                            cap         = StrokeCap.Round
                        )
                    }
                    // Point markers — white ring + gold fill
                    points.forEach { pt ->
                        drawCircle(color = Color.White, radius = 4.dp.toPx(),   center = pt)
                        drawCircle(color = BrandGold,   radius = 2.5.dp.toPx(), center = pt)
                    }
                }
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
                            color = DashSlate400,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                if (isFallback) {
                    Text(
                        "* Données approximatives. Synchronisez pour actualiser.",
                        fontSize = 10.sp,
                        color = DashSlate400.copy(alpha = 0.70f),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }  // Column
        }  // Box
    }  // Surface (Revenue)
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Box {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                    .background(BrandGreen)
            )
        Column(modifier = Modifier.padding(20.dp)) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Consultations récentes",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ConsultGreen
                )
                if (!isLoading && consultations.isNotEmpty()) {
                    TextButton(
                        onClick = onViewAll,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Voir tout", fontSize = 12.sp, color = BrandGold)
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = BrandGold,
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
                                tint = DashSlate400,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                "Impossible de charger",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF64748B),
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                "Vérifiez votre connexion internet.",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
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
                                    1.dp, BrandGold
                                ),
                                contentPadding = PaddingValues(
                                    horizontal = 20.dp, vertical = 8.dp
                                )
                            ) {
                                if (isRetrying) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = BrandGold
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = BrandGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "Réessayer",
                                    fontSize = 13.sp,
                                    color = BrandGold,
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
                            tint = DashSlate400,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            "Aucune consultation récente",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B),
                            fontFamily = FontFamily.Serif
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
        }  // Column
        }  // Box
    }  // Surface (Consultations)
}

@Composable
private fun ConsultationRow(consultation: RecentConsultationDto) {
    val statusText = consultation.status.ifBlank { "—" }
    val (statusColor, statusBg, statusBorder) = when {
        statusText.contains("term",    ignoreCase = true) ||
        statusText.contains("compl",   ignoreCase = true) -> Triple(DashGreen,  DashGreenBg,  Color.Transparent)
        statusText.contains("attente", ignoreCase = true) ||
        statusText.contains("pend",    ignoreCase = true)  -> Triple(BrandGold,  BrandGold.copy(alpha = 0.12f), BrandGold.copy(alpha = 0.35f))
        else                                               -> Triple(DashRed,    DashRoseBg,   Color.Transparent)
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
                .background(BrandGreen.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = consultation.clientName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = BrandGreen
            )
        }
        // ── Name + case + status pill ───────────────────────────────
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = consultation.clientName.ifBlank { "Client" },
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = ConsultGreen,
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
                fontSize = 11.sp,
                color = DashSlate400,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Status pill
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = statusBg,
                    border = if (statusBorder != Color.Transparent)
                        androidx.compose.foundation.BorderStroke(0.5.dp, statusBorder) else null
                ) {
                    Text(
                        text = statusText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
                // Price
                if (consultation.price > 0f) {
                    Text(
                        text = "· %.0f MAD".format(consultation.price),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DashSlate400
                    )
                }
            }
        }
        // ── Gold action button (Video/Call) ──────────────────────────
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(BrandGold.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.VideoCall,
                contentDescription = "Démarrer la consultation",
                tint = BrandGold,
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
