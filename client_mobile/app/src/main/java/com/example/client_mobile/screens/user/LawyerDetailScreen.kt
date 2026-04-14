package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

// ─── Lawyer Detail Screen ─────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerDetailScreen(
    lawyerId: String = "",
    onBack: () -> Unit = {},
    onNavigateToChat: (String) -> Unit = {},
    lawyerListViewModel: LawyerListViewModel = viewModel()
) {
    val lawyers by lawyerListViewModel.lawyers.collectAsStateWithLifecycle()
    val lawyer  = lawyers?.firstOrNull { it.id == lawyerId }

    var showBookingDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Fiche Avocat",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppDarkGreen
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = AppDarkGreen
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val conv = ConversationRepository.getOrCreate(
                            lawyerId   = lawyerId,
                            lawyerName = lawyer?.name ?: "",
                            clientName = UserSession.name
                        )
                        onNavigateToChat(conv.id)
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "Message",
                            tint = AppGoldColor
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        DashBoardBackground {
            if (lawyers == null) {
                // Still loading from Firestore
                Box(
                    Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppDarkGreen, strokeWidth = 2.5.dp)
                }
            } else if (lawyer == null) {
                Box(
                    Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.PersonOff,
                            contentDescription = null,
                            tint = AppDarkGreen.copy(alpha = 0.30f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            "Avocat introuvable",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = AppDarkGreen
                        )
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = AppDarkGreen),
                            shape = RoundedCornerShape(14.dp)
                        ) { Text("Retour", fontFamily = FontFamily.Serif, color = Color.White) }
                    }
                }
            } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // ── Header Card ───────────────────────────────────────────────
                item { LawyerDetailHeader(lawyer = lawyer) }

                // ── Specialization Chips ──────────────────────────────────────
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            DomainChip(label = lawyer.domaine)
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        item { DomainChip(label = lawyer.specialty) }
                    }
                }

                // ── Bio ───────────────────────────────────────────────────────
                item { SectionHeader(title = "À Propos") }
                item {
                    DashCard {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(36.dp),
                                shape = RoundedCornerShape(11.dp),
                                color = AppDarkGreen
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        tint = AppGoldColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Text(
                                "Biographie",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AppDarkGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            lawyer.bio,
                            fontFamily = FontFamily.Serif,
                            fontSize = 13.sp,
                            color = AppDarkGreen.copy(alpha = 0.72f),
                            lineHeight = 20.sp
                        )
                    }
                }

                // ── Location ──────────────────────────────────────────────────
                item { SectionHeader(title = "Localisation") }
                item { LocationCard(city = lawyer.city) }

                // ── Reviews ───────────────────────────────────────────────────
                item { SectionHeader(title = "Avis Clients", actionLabel = "${lawyer.reviewCount} avis") }
                item {
                    DashCard {
                        if (sampleReviews.isEmpty()) {
                            Text(
                                "Aucun avis pour le moment.",
                                fontFamily = FontFamily.Serif,
                                fontSize = 13.sp,
                                color = AppDarkGreen.copy(alpha = 0.45f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            sampleReviews.forEach { review ->
                                ReviewItem(review = review)
                                if (review != sampleReviews.last()) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        color = AppDarkGreen.copy(alpha = 0.07f)
                                    )
                                }
                            }
                        }
                    }
                }

                // ── CTA Buttons ───────────────────────────────────────────────
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                val conv = ConversationRepository.getOrCreate(
                                    lawyerId   = lawyerId,
                                    lawyerName = lawyer?.name ?: "",
                                    clientName = UserSession.name
                                )
                                onNavigateToChat(conv.id)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppDarkGreen),
                            border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.50f))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = AppGoldColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Envoyer un Message",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                        OutlinedButton(
                            onClick = { showBookingDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, AppDarkGreen.copy(alpha = 0.40f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppDarkGreen)
                        ) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Prendre RDV",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
            } // end else (lawyer found)
        }
    }

    // ── Booking Dialog ────────────────────────────────────────────────────────
    if (showBookingDialog) {
        AlertDialog(
            onDismissRequest = { showBookingDialog = false },
            shape = RoundedCornerShape(22.dp),
            containerColor = Color.White,
            icon = {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = AppGoldColor,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    "Prendre Rendez-vous",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkGreen,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
            "Votre demande de rendez-vous avec ${lawyer?.name ?: ""} sera envoyée. Vous recevrez une confirmation sous 24h.",
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    color = AppDarkGreen.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center
                )
            },
            dismissButton = {
                TextButton(onClick = { showBookingDialog = false }) {
                    Text("Annuler", fontFamily = FontFamily.Serif, color = AppDarkGreen.copy(alpha = 0.55f))
                }
            },
            confirmButton = {
                Button(
                    onClick = { showBookingDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppDarkGreen)
                ) {
                    Text("Confirmer", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        )
    }
}

// ─── Header Card ─────────────────────────────────────────────────────────────
@Composable
private fun LawyerDetailHeader(lawyer: LawyerItem) {
    val initials = lawyer.name
        .removePrefix("Maître ")
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = AppDarkGreen,
        border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.45f)),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar + verified
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(AppGoldColor.copy(alpha = 0.15f))
                        .border(2.dp, AppGoldColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (lawyer.avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model             = lawyer.avatarUrl,
                            contentDescription = lawyer.name,
                            modifier          = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale      = ContentScale.Crop
                        )
                    } else {
                        Text(
                            initials,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = AppGoldColor
                        )
                    }
                }
                if (lawyer.isVerified) {
                    Surface(
                        modifier = Modifier.size(28.dp),
                        shape = CircleShape,
                        color = Color(0xFF34A853),
                        border = BorderStroke(2.dp, AppDarkGreen)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    lawyer.name,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    lawyer.specialty,
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    color = AppGoldColor
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.55f),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        lawyer.city,
                        fontFamily = FontFamily.Serif,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.60f)
                    )
                }
            }

            HorizontalDivider(color = AppGoldColor.copy(alpha = 0.20f))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DetailStat(value = "${lawyer.yearsExp} ans", label = "Expérience")
                Box(modifier = Modifier.size(1.dp, 32.dp).background(AppGoldColor.copy(alpha = 0.25f)))
                DetailStat(value = "${lawyer.reviewCount}+", label = "Clients satisfaits")
                Box(modifier = Modifier.size(1.dp, 32.dp).background(AppGoldColor.copy(alpha = 0.25f)))
                DetailStat(value = "★ ${lawyer.rating}", label = "Note", valueColor = AppGoldColor)
            }
        }
    }
}

@Composable
private fun DetailStat(value: String, label: String, valueColor: Color = Color.White) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(value, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = valueColor)
        Text(label, fontFamily = FontFamily.Serif, fontSize = 10.sp, color = Color.White.copy(alpha = 0.55f))
    }
}

// ─── Domain Chip ─────────────────────────────────────────────────────────────
@Composable
private fun DomainChip(label: String) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = AppDarkGreen,
        border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.50f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(Icons.Default.Gavel, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(13.dp))
            Text(label, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.White)
        }
    }
}

// ─── Location Card ────────────────────────────────────────────────────────────
@Composable
private fun LocationCard(city: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(22.dp),
        color = AppDarkGreen.copy(alpha = 0.07f),
        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.15f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = AppGoldColor,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = city,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = AppDarkGreen
                )
                Text(
                    text = "Carte interactive — bientôt disponible",
                    fontFamily = FontFamily.Serif,
                    fontSize = 11.sp,
                    color = AppDarkGreen.copy(alpha = 0.45f)
                )
            }
        }
    }
}

// ─── Review Item ─────────────────────────────────────────────────────────────
data class ReviewItem(val name: String, val rating: Int, val comment: String, val date: String)

private val sampleReviews = emptyList<ReviewItem>()

@Composable
private fun ReviewItem(review: ReviewItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AppDarkGreen.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                review.name.first().uppercaseChar().toString(),
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = AppDarkGreen
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(review.name, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AppDarkGreen)
                Text(review.date, fontFamily = FontFamily.Serif, fontSize = 10.sp, color = AppDarkGreen.copy(alpha = 0.40f))
            }
            Row {
                repeat(review.rating) {
                    Text("★", fontSize = 12.sp, color = AppGoldColor)
                }
                repeat(5 - review.rating) {
                    Text("★", fontSize = 12.sp, color = AppDarkGreen.copy(alpha = 0.18f))
                }
            }
            Text(review.comment, fontFamily = FontFamily.Serif, fontSize = 12.sp, color = AppDarkGreen.copy(alpha = 0.65f), lineHeight = 18.sp)
        }
    }
}
