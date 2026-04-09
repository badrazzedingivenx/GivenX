package com.example.client_mobile.screens.lawyer

import com.example.client_mobile.screens.shared.*

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// ─── Lawyer Profile Screen ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvocatProfile(
    fullName: String = "",
    title: String = "",
    email: String = "",
    phone: String = "",
    address: String = "",
    bio: String = "",
    profileImageUri: Uri? = null,
    onBack: () -> Unit = {},
    onNavigateToEdit: () -> Unit = {},
    dashboardViewModel: LawyerDashboardViewModel = viewModel()
) {
    val lawyerProfile by dashboardViewModel.profile.collectAsStateWithLifecycle()

    // API fields override the passed-in fallback params
    val displayName    = lawyerProfile?.fullName?.takeIf { it.isNotBlank() }      ?: fullName
    val displayTitle   = lawyerProfile?.let {
        buildString {
            if (it.speciality.isNotBlank())     append(it.speciality)
            if (it.barAssociation.isNotBlank()) append(" — Barreau de ${it.barAssociation}")
        }.takeIf { s -> s.isNotBlank() }
    } ?: title
    val displayEmail   = lawyerProfile?.email?.takeIf { it.isNotBlank() }         ?: email
    val displayPhone   = lawyerProfile?.phone?.takeIf { it.isNotBlank() }         ?: phone
    val displayAddress = lawyerProfile?.address?.takeIf { it.isNotBlank() }       ?: address
    val displayBio     = lawyerProfile?.bio?.takeIf { it.isNotBlank() }           ?: bio
    val specializations = lawyerProfile?.specializations?.takeIf { it.isNotEmpty() }
        ?: emptyList()

    val yearsExp  = lawyerProfile?.yearsExperience ?: 0
    val clientCnt = lawyerProfile?.clientCount     ?: 0
    val rating    = lawyerProfile?.rating          ?: 0f

    var showLogOutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Profil Avocat",
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
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Partager",
                            tint = AppDarkGreen
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // ── Header Card ───────────────────────────────────────────────
                item {
                    LawyerHeaderCard(
                        fullName = displayName,
                        title = displayTitle,
                        profileImageUri = profileImageUri,
                        yearsExp = yearsExp,
                        casesWon = clientCnt,
                        rating = rating
                    )
                }

                // ── Specializations ───────────────────────────────────────────────
                if (specializations.isNotEmpty()) {
                    item { SectionHeader(title = "Domaines d'Expertise") }
                    item { SpecializationChipsRow(specializations = specializations) }
                }

                // ── Bio ──────────────────────────────────────────────────────────
                if (displayBio.isNotBlank()) {
                    item { SectionHeader(title = "À Propos") }
                    item { LawyerBioCard(bio = displayBio) }
                }

                // ── Cabinet Links ─────────────────────────────────────────────
                item { SectionHeader(title = "Mon Cabinet") }
                item {
                    DashCard {
                        LawyerActionRow(
                            icon = Icons.Default.Business,
                            label = "Mon Cabinet",
                            subtitle = displayAddress,
                            onClick = {}
                        )
                        LawyerDivider()
                        LawyerActionRow(
                            icon = Icons.Default.AttachMoney,
                            label = "Mes Honoraires",
                            subtitle = "Tarifs & facturation",
                            onClick = {}
                        )
                        LawyerDivider()
                        LawyerActionRow(
                            icon = Icons.Default.CalendarMonth,
                            label = "Disponibilité",
                            subtitle = "Gérer mes créneaux",
                            onClick = {}
                        )
                        LawyerDivider()
                        LawyerActionRow(
                            icon = Icons.Default.Gavel,
                            label = "Documents Juridiques",
                            subtitle = "Certificats & licences",
                            onClick = {},
                            isLast = true
                        )
                    }
                }

                // ── Contact Info ──────────────────────────────────────────────
                item { SectionHeader(title = "Contact") }
                item {
                    DashCard {
                        LawyerInfoRow(Icons.Default.Email, "E-mail", displayEmail)
                        LawyerDivider()
                        LawyerInfoRow(Icons.Default.Phone, "Téléphone", displayPhone)
                        LawyerDivider()
                        LawyerInfoRow(Icons.Default.LocationOn, "Adresse", displayAddress, isLast = true)
                    }
                }

                // ── CTA Buttons ───────────────────────────────────────────────
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppDarkGreen),
                            border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.50f))
                        ) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = AppGoldColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Consulter mon Agenda",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                        OutlinedButton(
                            onClick = onNavigateToEdit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, AppDarkGreen.copy(alpha = 0.35f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AppDarkGreen
                            )
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Modifier le Profil",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                // ── Log Out ───────────────────────────────────────────────────
                item {
                    Button(
                        onClick = { showLogOutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFF1F1)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.35f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Se Déconnecter",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFE53935)
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }

    if (showLogOutDialog) {
        AlertDialog(
            onDismissRequest = { showLogOutDialog = false },
            shape = RoundedCornerShape(22.dp),
            containerColor = Color.White,
            title = {
                Text(
                    "Se déconnecter ?",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkGreen,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    "Vous serez redirigé vers l'écran de connexion.",
                    fontFamily = FontFamily.Serif,
                    fontSize = 14.sp,
                    color = AppDarkGreen.copy(alpha = 0.65f)
                )
            },
            dismissButton = {
                TextButton(onClick = { showLogOutDialog = false }) {
                    Text(
                        "Annuler",
                        fontFamily = FontFamily.Serif,
                        color = AppDarkGreen.copy(alpha = 0.60f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showLogOutDialog = false; onBack() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text(
                        "Déconnecter",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        )
    }
}

// ─── Header Card ─────────────────────────────────────────────────────────────
@Composable
private fun LawyerHeaderCard(
    fullName: String,
    title: String,
    profileImageUri: Uri?,
    yearsExp: Int,
    casesWon: Int,
    rating: Float
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = AppDarkGreen,
        border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.45f)),
        shadowElevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Decorative circles
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = Color(0xFFD4AF37).copy(alpha = 0.08f),
                    radius = 200.dp.toPx(),
                    center = Offset(size.width * 0.92f, -size.height * 0.12f)
                )
                drawCircle(
                    color = Color(0xFFD4AF37).copy(alpha = 0.05f),
                    radius = 130.dp.toPx(),
                    center = Offset(0f, size.height * 1.10f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Avatar + verified badge
                Box(contentAlignment = Alignment.BottomEnd) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Photo de profil",
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .border(2.dp, AppGoldColor, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        val initials = fullName
                            .removePrefix("Maître ")
                            .split(" ")
                            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                            .take(2)
                            .joinToString("")
                        // Initials avatar
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            AppGoldColor.copy(alpha = 0.22f),
                                            AppGoldColor.copy(alpha = 0.06f)
                                        )
                                    )
                                )
                                .border(2.dp, AppGoldColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 34.sp,
                                color = AppGoldColor
                            )
                        }
                    }
                    // Verified badge
                    Surface(
                        modifier = Modifier.size(30.dp),
                        shape = CircleShape,
                        color = Color(0xFF34A853),
                        border = BorderStroke(2.dp, AppDarkGreen)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Vérifié",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Name & title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = fullName,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = title,
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.sp,
                        color = AppGoldColor.copy(alpha = 0.90f),
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }

                // Stats row
                HorizontalDivider(color = AppGoldColor.copy(alpha = 0.20f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LawyerStatItem(value = "${yearsExp} ans", label = "Expérience")
                    VerticalStatDivider()
                    LawyerStatItem(value = "$casesWon+", label = "Dossiers gagnés")
                    VerticalStatDivider()
                    LawyerStatItem(
                        value = "★ $rating",
                        label = "Satisfaction",
                        valueColor = AppGoldColor
                    )
                }
            }
        }
    }
}

@Composable
private fun LawyerStatItem(
    value: String,
    label: String,
    valueColor: Color = Color.White
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = value,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = valueColor
        )
        Text(
            text = label,
            fontFamily = FontFamily.Serif,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.55f),
            maxLines = 1
        )
    }
}

@Composable
private fun VerticalStatDivider() {
    Box(
        modifier = Modifier
            .height(32.dp)
            .width(1.dp)
            .background(AppGoldColor.copy(alpha = 0.25f))
    )
}

// ─── Specialization Chips Row ─────────────────────────────────────────────────
@Composable
private fun SpecializationChipsRow(specializations: List<String>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        items(specializations) { spec ->
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = AppDarkGreen,
                border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.55f)),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Gavel,
                        contentDescription = null,
                        tint = AppGoldColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = spec,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Color.White,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ─── Bio Card ─────────────────────────────────────────────────────────────────
@Composable
private fun LawyerBioCard(bio: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = 0.92f),
        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.10f)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
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
                    text = "Bio professionnelle",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = AppDarkGreen
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = bio,
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                color = AppDarkGreen.copy(alpha = 0.72f),
                lineHeight = 20.sp
            )
        }
    }
}

// ─── Action Row ───────────────────────────────────────────────────────────────
@Composable
private fun LawyerActionRow(
    icon: ImageVector,
    label: String,
    subtitle: String,
    onClick: () -> Unit,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = if (isLast) 0.dp else 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(14.dp),
            color = AppDarkGreen,
            border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.50f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppGoldColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = AppDarkGreen,
                maxLines = 1
            )
            Text(
                text = subtitle,
                fontFamily = FontFamily.Serif,
                fontSize = 11.sp,
                color = AppDarkGreen.copy(alpha = 0.50f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = AppDarkGreen.copy(alpha = 0.28f),
            modifier = Modifier.size(18.dp)
        )
    }
}

// ─── Info Row ─────────────────────────────────────────────────────────────────
@Composable
private fun LawyerInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isLast) 0.dp else 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(38.dp),
            shape = RoundedCornerShape(12.dp),
            color = AppDarkGreen.copy(alpha = 0.07f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppGoldColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontFamily = FontFamily.Serif,
                fontSize = 11.sp,
                color = AppDarkGreen.copy(alpha = 0.50f)
            )
            Text(
                text = value,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = AppDarkGreen,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─── Row Divider ─────────────────────────────────────────────────────────────
@Composable
private fun LawyerDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 12.dp),
        color = AppDarkGreen.copy(alpha = 0.07f)
    )
}
