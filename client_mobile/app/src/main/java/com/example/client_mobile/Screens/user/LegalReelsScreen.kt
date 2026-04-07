package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Model ────────────────────────────────────────────────────────────────────

data class LegalReel(
    val id: Int,
    val lawyerName: String,
    val specialty: String,
    val title: String,
    val likes: Int,
    val views: String,
    val isLiked: Boolean = false,
    val isLive: Boolean = false
)

private val sampleReels = listOf(
    LegalReel(1, "Maître Sara Benali",     "Droit Famille",   "Comment protéger vos droits lors d'un divorce ?",         1240, "8.2k"),
    LegalReel(2, "Maître Khalid Tazi",     "Droit Affaires",  "Les 5 erreurs à éviter quand vous créez une entreprise",   987, "5.4k"),
    LegalReel(3, "Maître Nadia Mansouri",  "Droit du Travail","Licenciement abusif : vos recours légaux expliqués",        643, "3.1k", isLive = true),
    LegalReel(4, "Maître Youssef El Fassi","Droit Immobilier","Bail commercial : ce que vous devez savoir avant de signer",432, "2.7k"),
    LegalReel(5, "Maître Amina Chraibi",   "Droit Pénal",     "Garde à vue : vos droits minute par minute",              2103, "12k"),
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun LegalReelsScreen(paddingValues: PaddingValues = PaddingValues()) {
    val reels = remember { mutableStateListOf(*sampleReels.toTypedArray()) }

    LazyColumn(
        modifier            = Modifier.fillMaxSize().padding(paddingValues),
        contentPadding      = PaddingValues(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        itemsIndexed(reels, key = { _, r -> r.id }) { index, reel ->
            ReelCard(
                reel    = reel,
                onLike  = {
                    val delta = if (reel.isLiked) -1 else 1
                    reels[index] = reel.copy(isLiked = !reel.isLiked, likes = reel.likes + delta)
                }
            )
        }
    }
}

// ─── Reel Card ────────────────────────────────────────────────────────────────

@Composable
private fun ReelCard(reel: LegalReel, onLike: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(520.dp)
    ) {
        // ── Background gradient (simulates video) ──────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            AppDarkGreen.copy(alpha = 0.92f),
                            Color(0xFF0D1F17),
                            Color.Black.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        // ── Play area center icon ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 140.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.size(72.dp),
                shape    = CircleShape,
                color    = Color.White.copy(alpha = 0.14f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Lire",
                        tint = Color.White, modifier = Modifier.size(40.dp))
                }
            }
        }

        // ── Live badge ─────────────────────────────────────────────────────
        if (reel.isLive) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFD32F2F)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier
                        .size(6.dp)
                        .background(Color.White, CircleShape))
                    Text("LIVE", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                        fontSize = 11.sp, color = Color.White)
                }
            }
        }

        // ── Views badge ────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Default.Visibility, contentDescription = null,
                tint = Color.White.copy(alpha = 0.80f), modifier = Modifier.size(13.dp))
            Text(reel.views, fontFamily = FontFamily.Serif, fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.80f))
        }

        // ── Bottom overlay ─────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.80f)))
                )
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Lawyer row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.size(38.dp),
                    shape    = CircleShape,
                    color    = AppGoldColor.copy(alpha = 0.18f),
                    border   = BorderStroke(1.5.dp, AppGoldColor)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null,
                            tint = AppGoldColor, modifier = Modifier.size(18.dp))
                    }
                }
                Column {
                    Text(reel.lawyerName, fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Text(reel.specialty, fontFamily = FontFamily.Serif,
                        fontSize = 11.sp, color = AppGoldColor)
                }
            }

            // Title
            Text(reel.title, fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                color = Color.White, lineHeight = 20.sp)

            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.clickable { onLike() }
                ) {
                    Icon(
                        if (reel.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "J'aime",
                        tint   = if (reel.isLiked) AppGoldColor else Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Text("${reel.likes}", fontFamily = FontFamily.Serif,
                        fontSize = 12.sp, color = Color.White)
                }

                // Share
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Partager",
                        tint = Color.White, modifier = Modifier.size(20.dp))
                    Text("Partager", fontFamily = FontFamily.Serif,
                        fontSize = 12.sp, color = Color.White)
                }

                // Consult Now CTA
                Surface(
                    shape  = RoundedCornerShape(20.dp),
                    color  = AppDarkGreen,
                    border = BorderStroke(1.dp, AppGoldColor),
                    onClick = {}
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null,
                            tint = AppGoldColor, modifier = Modifier.size(14.dp))
                        Text("Consulter", fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AppGoldColor)
                    }
                }
            }
        }

        // ── Progress bar (simulated) ───────────────────────────────────────
        LinearProgressIndicator(
            progress = { 0.42f },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.BottomCenter),
            color           = AppGoldColor,
            trackColor      = Color.White.copy(alpha = 0.20f)
        )
    }

    HorizontalDivider(color = Color.Black, thickness = 2.dp)
}
