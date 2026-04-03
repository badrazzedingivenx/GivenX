package com.example.client_mobile.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

// ─── About Screen ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "À Propos",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = AppDarkGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = AppDarkGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        DashBoardBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 36.dp)
            ) {
                // ── Header ───────────────────────────────────────────────────
                item { AboutHeaderSection() }

                // ── Mission ──────────────────────────────────────────────────
                item { AboutMissionSection() }

                // ── Values ───────────────────────────────────────────────────
                item { AboutValuesSection() }

                // ── Stats ────────────────────────────────────────────────────
                item { AboutStatsSection() }

                // ── Contact ──────────────────────────────────────────────────
                item { AboutContactSection() }
            }
        }
    }
}

// ─── Header ───────────────────────────────────────────────────────────────────
@Composable
private fun AboutHeaderSection() {
    DarkDashCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "Logo HAQ",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "La justice, accessible à tous.",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = AppGoldColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = "HAQ est une plateforme LegalTech marocaine qui connecte citoyens et avocats qualifiés pour un accès simple, rapide et transparent à la justice.",
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.72f),
                textAlign = TextAlign.Center,
                lineHeight = 21.sp
            )
        }
    }
}

// ─── Mission ──────────────────────────────────────────────────────────────────
@Composable
private fun AboutMissionSection() {
    SectionHeader(title = "Notre Mission")
    Spacer(modifier = Modifier.height(6.dp))
    DashCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = AppDarkGreen,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.EmojiObjects,
                        contentDescription = null,
                        tint = AppGoldColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Text(
                text = "Rendre la justice accessible à tous",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = AppDarkGreen,
                lineHeight = 23.sp
            )
            Text(
                text = "Nous croyons que chaque citoyen mérite un accès équitable et transparent au système juridique. Notre mission est de briser les barrières — géographiques, financières ou informationnelles — qui séparent les gens de la justice.",
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                color = AppDarkGreen.copy(alpha = 0.72f),
                lineHeight = 22.sp
            )
        }
    }
}

// ─── Values ───────────────────────────────────────────────────────────────────
@Composable
private fun AboutValuesSection() {
    SectionHeader(title = "Nos Valeurs")
    Spacer(modifier = Modifier.height(6.dp))
    DashCard {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            listOf(
                Triple(
                    Icons.Default.VerifiedUser,
                    "Intégrité",
                    "Nous opérons avec la plus haute rigueur éthique et professionnelle dans chaque interaction."
                ),
                Triple(
                    Icons.Default.Lightbulb,
                    "Innovation",
                    "Nous exploitons la technologie pour simplifier des processus juridiques complexes."
                ),
                Triple(
                    Icons.Default.Security,
                    "Sécurité",
                    "Vos données et dossiers sont protégés par les meilleurs standards de chiffrement."
                ),
                Triple(
                    Icons.Default.Accessibility,
                    "Accessibilité",
                    "Un service juridique de qualité, disponible pour tous, partout et à tout moment."
                )
            ).forEach { (icon, title, description) ->
                AboutValueItem(icon = icon, title = title, description = description)
            }
        }
    }
}

@Composable
private fun AboutValueItem(icon: ImageVector, title: String, description: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = RoundedCornerShape(13.dp),
            color = AppDarkGreen
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = AppGoldColor,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = AppDarkGreen
            )
            Text(
                text = description,
                fontFamily = FontFamily.Serif,
                fontSize = 12.sp,
                color = AppDarkGreen.copy(alpha = 0.62f),
                lineHeight = 18.sp
            )
        }
    }
}

// ─── Stats ────────────────────────────────────────────────────────────────────
@Composable
private fun AboutStatsSection() {
    SectionHeader(title = "HAQ en chiffres")
    Spacer(modifier = Modifier.height(6.dp))
    DarkDashCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf(
                Pair("240+", "Avocats"),
                Pair("5 000+", "Clients"),
                Pair("1 200+", "Dossiers"),
                Pair("4.8 ★", "Note")
            ).forEach { (value, label) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        value,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppGoldColor
                    )
                    Text(
                        label,
                        fontFamily = FontFamily.Serif,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.62f)
                    )
                }
            }
        }
    }
}

// ─── Contact ──────────────────────────────────────────────────────────────────
@Composable
private fun AboutContactSection() {
    SectionHeader(title = "Nous Contacter")
    Spacer(modifier = Modifier.height(6.dp))
    DashCard {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            listOf(
                Triple(Icons.Default.Email, "Email", "contact@haq.ma"),
                Triple(Icons.Default.Phone, "Téléphone", "+212 5XX-XXXXXX"),
                Triple(Icons.Default.LocationOn, "Adresse", "Casablanca, Maroc")
            ).forEach { (icon, label, info) ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = RoundedCornerShape(11.dp),
                        color = AppDarkGreen.copy(alpha = 0.08f),
                        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.12f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                icon,
                                contentDescription = label,
                                tint = AppDarkGreen,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            label,
                            fontFamily = FontFamily.Serif,
                            fontSize = 11.sp,
                            color = AppDarkGreen.copy(alpha = 0.50f)
                        )
                        Text(
                            info,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = AppDarkGreen
                        )
                    }
                }
            }
        }
    }
}
