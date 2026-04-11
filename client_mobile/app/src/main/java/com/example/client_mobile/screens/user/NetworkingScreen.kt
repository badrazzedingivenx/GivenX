package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// ─── Networking Screen ────────────────────────────────────────────────────────
@Composable
fun NetworkingScreen(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: LawyerViewModel   = viewModel()
) {
    val lawyers by viewModel.lawyers.collectAsStateWithLifecycle()

    LazyColumn(
        modifier            = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // ── Search bar ────────────────────────────────────────────────────────
        item {
            Surface(
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(16.dp),
                color         = AppDarkGreen.copy(alpha = 0.05f),
                border        = BorderStroke(1.dp, AppDarkGreen.copy(alpha = 0.10f))
            ) {
                Row(
                    modifier          = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null,
                        tint = AppDarkGreen.copy(alpha = 0.45f), modifier = Modifier.size(20.dp))
                    Text(
                        "Rechercher un avocat, une expertise…",
                        fontSize = 14.sp,
                        color    = AppDarkGreen.copy(alpha = 0.45f),
                        fontFamily = FontFamily.Serif
                    )
                }
            }
        }

        // ── Suggestions ───────────────────────────────────────────────────────
        item {
            NetworkingSectionHeader(
                icon  = Icons.Default.AutoAwesome,
                title = "Suggestions pour vous"
            )
        }

        when {
            lawyers == null -> item { NetworkingSkeletonRow() }
            lawyers!!.isEmpty() -> item { NetworkingEmptyHint("Aucun avocat disponible pour le moment.") }
            else -> item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding        = PaddingValues(horizontal = 2.dp)
                ) {
                    items(lawyers!!.take(6)) { card ->
                        LawyerSuggestionCard(card)
                    }
                }
            }
        }

        // ── Domaines populaires ───────────────────────────────────────────────
        item {
            NetworkingSectionHeader(
                icon  = Icons.Default.WorkspacePremium,
                title = "Domaines juridiques"
            )
        }

        item {
            val domains = listOf(
                "Droit de la famille"   to Icons.Default.FamilyRestroom,
                "Droit des affaires"    to Icons.Default.Business,
                "Droit du travail"      to Icons.Default.Work,
                "Droit immobilier"      to Icons.Default.HomeWork,
                "Droit pénal"           to Icons.Default.Gavel,
                "Droit administratif"   to Icons.Default.AccountBalance
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                domains.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { (label, icon) ->
                            DomainChip(label = label, icon = icon, modifier = Modifier.weight(1f))
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        // ── Annuaire des professionnels ────────────────────────────────────────
        item {
            NetworkingSectionHeader(
                icon  = Icons.Default.PeopleAlt,
                title = "Annuaire des avocats"
            )
        }

        when {
            lawyers == null -> items(3) { NetworkingListSkeleton() }
            lawyers!!.isEmpty() -> item { NetworkingEmptyHint("Aucun professionnel disponible.") }
            else -> items(lawyers!!) { card ->
                LawyerDirectoryCard(card)
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

// ─── Sub-components ───────────────────────────────────────────────────────────

@Composable
private fun NetworkingSectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier              = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(icon, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(18.dp))
        Text(
            title,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp,
            color      = AppDarkGreen
        )
    }
}

@Composable
private fun LawyerSuggestionCard(card: MatchCard) {
    Surface(
        modifier      = Modifier.width(160.dp),
        shape         = RoundedCornerShape(18.dp),
        color         = Color.White,
        shadowElevation = 3.dp,
        border        = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.08f))
    ) {
        Column(
            modifier            = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Avatar circle
            Box(
                modifier         = Modifier
                    .size(52.dp)
                    .background(AppDarkGreen.copy(alpha = 0.10f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    card.name.take(1).uppercase(),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 22.sp,
                    color      = AppDarkGreen
                )
            }
            Text(
                card.name,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize   = 13.sp,
                color      = AppDarkGreen,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                card.specialty,
                fontSize = 11.sp,
                color    = AppDarkGreen.copy(alpha = 0.60f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            // Rating
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = null,
                    tint = AppGoldColor, modifier = Modifier.size(13.dp))
                Text(
                    String.format("%.1f", card.rating),
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color      = AppDarkGreen
                )
            }
            if (card.isVerified) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = AppGoldColor.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier              = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null,
                            tint = AppGoldColor, modifier = Modifier.size(11.dp))
                        Text("Vérifié", fontSize = 10.sp, color = AppGoldColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun LawyerDirectoryCard(card: MatchCard) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(16.dp),
        color           = Color.White,
        shadowElevation = 2.dp,
        border          = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.07f))
    ) {
        Row(
            modifier          = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar
            Box(
                modifier         = Modifier
                    .size(46.dp)
                    .background(AppDarkGreen.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    card.name.take(1).uppercase(),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 18.sp,
                    color      = AppDarkGreen
                )
            }
            // Info
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        card.name,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 14.sp,
                        color      = AppDarkGreen,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis,
                        modifier   = Modifier.weight(1f, fill = false)
                    )
                    if (card.isVerified) {
                        Icon(Icons.Default.Verified, contentDescription = "Vérifié",
                            tint = AppGoldColor, modifier = Modifier.size(14.dp))
                    }
                }
                Text(
                    card.specialty,
                    fontSize = 12.sp,
                    color    = AppDarkGreen.copy(alpha = 0.60f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.Star, contentDescription = null,
                            tint = AppGoldColor, modifier = Modifier.size(12.dp))
                        Text(String.format("%.1f", card.rating), fontSize = 11.sp, color = AppDarkGreen.copy(alpha = 0.70f))
                    }
                    Text("·", fontSize = 11.sp, color = AppDarkGreen.copy(alpha = 0.35f))
                    Icon(Icons.Default.LocationOn, contentDescription = null,
                        tint = AppDarkGreen.copy(alpha = 0.45f), modifier = Modifier.size(12.dp))
                    Text(card.city, fontSize = 11.sp, color = AppDarkGreen.copy(alpha = 0.60f), maxLines = 1)
                }
            }
            // CTA
            Surface(
                shape  = RoundedCornerShape(10.dp),
                color  = AppDarkGreen,
                modifier = Modifier.clickable { /* navigate to detail */ }
            ) {
                Text(
                    "Voir",
                    modifier   = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White,
                    fontFamily = FontFamily.Serif
                )
            }
        }
    }
}

@Composable
private fun DomainChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        color     = AppDarkGreen.copy(alpha = 0.05f),
        border    = BorderStroke(1.dp, AppDarkGreen.copy(alpha = 0.12f))
    ) {
        Row(
            modifier              = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(16.dp))
            Text(
                label,
                fontSize   = 11.sp,
                fontWeight = FontWeight.Medium,
                color      = AppDarkGreen,
                maxLines   = 2,
                modifier   = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NetworkingSkeletonRow() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(4) {
            Surface(
                modifier = Modifier.size(width = 160.dp, height = 140.dp),
                shape    = RoundedCornerShape(18.dp),
                color    = AppDarkGreen.copy(alpha = 0.05f)
            ) {}
        }
    }
}

@Composable
private fun NetworkingListSkeleton() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp),
        shape = RoundedCornerShape(16.dp),
        color = AppDarkGreen.copy(alpha = 0.04f)
    ) {}
}

@Composable
private fun NetworkingEmptyHint(message: String) {
    Box(
        modifier         = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            message,
            fontSize   = 14.sp,
            color      = AppDarkGreen.copy(alpha = 0.45f),
            fontFamily = FontFamily.Serif
        )
    }
}
