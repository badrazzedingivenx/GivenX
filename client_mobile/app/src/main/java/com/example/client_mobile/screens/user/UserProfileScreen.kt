package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

// ─── User Profile Screen ──────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    onBack: () -> Unit = {},
    onLogOut: () -> Unit = {},
    onNavigateToEdit: () -> Unit = {},
    onNavigateToDocuments: () -> Unit = {},
    userViewModel: UserViewModel = viewModel()
) {
    val profile by userViewModel.profile.collectAsStateWithLifecycle()

    // Derived display values – fall back to UserSession while the VM is loading
    val userName    = profile?.let { "${it.firstName} ${it.lastName}".trim() }
                        .takeIf { !it.isNullOrBlank() } ?: UserSession.name
    val userEmail   = profile?.email?.takeIf { it.isNotBlank() }   ?: UserSession.email
    val userPhone   = profile?.phone?.takeIf { it.isNotBlank() }   ?: UserSession.phone
    val userAddress = profile?.address?.takeIf { it.isNotBlank() } ?: UserSession.address
    val photoUrl    = profile?.photoUrl

    var biometricEnabled by remember { mutableStateOf(false) }
    var showLogOutDialog  by remember { mutableStateOf(false) }
    var showDeleteDialog  by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mon Profil",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
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
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Modifier",
                            tint = AppGoldColor
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
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // ── Profile Header Card ───────────────────────────────────────
                item {
                    ProfileHeaderCard(userName = userName, photoUrl = photoUrl)
                }

                // ── Account Information ───────────────────────────────────────
                item {
                    SectionHeader(title = "Informations Personnelles")
                }
                item {
                    DashCard {
                        ProfileInfoRow(Icons.Default.Person, "Nom complet", userName)
                        ProfileDivider()
                        ProfileInfoRow(Icons.Default.Email, "Adresse e-mail", userEmail)
                        ProfileDivider()
                        ProfileInfoRow(Icons.Default.Phone, "Téléphone", userPhone)
                        ProfileDivider()
                        ProfileInfoRow(Icons.Default.LocationOn, "Adresse", userAddress, isLast = true)
                    }
                }

                // ── Security & Settings ───────────────────────────────────────
                item {
                    SectionHeader(title = "Sécurité & Paramètres")
                }
                item {
                    DashCard {
                        BiometricToggleRow(
                            enabled = biometricEnabled,
                            onToggle = { biometricEnabled = it }
                        )
                        ProfileDivider()
                        ProfileActionRow(
                            icon = Icons.Default.Lock,
                            label = "Changer le mot de passe",
                            onClick = {}
                        )
                        ProfileDivider()
                        ProfileActionRow(
                            icon = Icons.Default.Notifications,
                            label = "Notifications",
                            onClick = {},
                            isLast = true
                        )
                    }
                }

                // ── Legal Tech Features ───────────────────────────────────────
                item {
                    SectionHeader(title = "Espace Juridique")
                }
                item {
                    DashCard {
                        LegalFeatureRow(
                            icon = Icons.Default.Folder,
                            title = "Coffre-fort Numérique",
                            subtitle = "Mes documents & pièces",
                            onClick = onNavigateToDocuments
                        )
                        ProfileDivider()
                        LegalFeatureRow(
                            icon = Icons.Default.CreditCard,
                            title = "Moyens de Paiement",
                            subtitle = "Cartes & historique",
                            onClick = {},
                            isLast = true
                        )
                    }
                }

                // ── Membership Badge ──────────────────────────────────────────
                item {
                    MembershipBanner()
                }

                // ── Footer Actions ────────────────────────────────────────────
                item {
                    Spacer(modifier = Modifier.height(4.dp))
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
                            text = "Se Déconnecter",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFE53935)
                        )
                    }
                }
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Supprimer mon compte",
                            fontFamily = FontFamily.Serif,
                            fontSize = 13.sp,
                            color = Color.Gray.copy(alpha = 0.60f),
                            modifier = Modifier
                                .clickable { showDeleteDialog = true }
                                .padding(vertical = 8.dp, horizontal = 12.dp)
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }

    // ── Log Out Confirmation Dialog ───────────────────────────────────────────
    if (showLogOutDialog) {
        ProfileConfirmDialog(
            title = "Se déconnecter ?",
            message = "Vous serez redirigé vers l'écran de connexion.",
            confirmLabel = "Déconnecter",
            confirmColor = Color(0xFFE53935),
            onConfirm = {
                showLogOutDialog = false
                onLogOut()
            },
            onDismiss = { showLogOutDialog = false }
        )
    }

    // ── Delete Account Dialog ─────────────────────────────────────────────────
    if (showDeleteDialog) {
        ProfileConfirmDialog(
            title = "Supprimer le compte ?",
            message = "Cette action est irréversible. Toutes vos données seront définitivement supprimées.",
            confirmLabel = "Supprimer",
            confirmColor = Color(0xFFE53935),
            onConfirm = { showDeleteDialog = false },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

// ─── Profile Header Card ──────────────────────────────────────────────────────
@Composable
private fun ProfileHeaderCard(userName: String, photoUrl: String? = null) {
    val initials = userName
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
        Box(modifier = Modifier.fillMaxWidth()) {
            // Decorative circles
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = Color(0xFFD4AF37).copy(alpha = 0.07f),
                    radius = 180.dp.toPx(),
                    center = Offset(size.width * 0.9f, -size.height * 0.10f)
                )
                drawCircle(
                    color = Color(0xFFD4AF37).copy(alpha = 0.05f),
                    radius = 120.dp.toPx(),
                    center = Offset(0f, size.height * 1.10f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar with edit overlay
                Box(contentAlignment = Alignment.BottomEnd) {
                    if (!photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Photo de profil",
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            AppGoldColor.copy(alpha = 0.25f),
                                            AppGoldColor.copy(alpha = 0.08f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                                color = AppGoldColor
                            )
                        }
                    }

                    // Edit badge
                    Surface(
                        modifier = Modifier.size(28.dp),
                        shape = CircleShape,
                        color = AppGoldColor,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Modifier photo",
                                tint = AppDarkGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // Name & role
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = userName,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = AppGoldColor.copy(alpha = 0.18f),
                        border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.60f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = AppGoldColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Client Premium",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = AppGoldColor
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Profile Info Row ─────────────────────────────────────────────────────────
@Composable
private fun ProfileInfoRow(
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
                fontSize = 14.sp,
                color = AppDarkGreen
            )
        }
    }
}

// ─── Biometric Toggle Row ─────────────────────────────────────────────────────
@Composable
private fun BiometricToggleRow(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(38.dp),
            shape = RoundedCornerShape(12.dp),
            color = AppDarkGreen.copy(alpha = 0.07f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = AppGoldColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Connexion Biométrique",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = AppDarkGreen
            )
            Text(
                text = "Empreinte / Face ID",
                fontFamily = FontFamily.Serif,
                fontSize = 11.sp,
                color = AppDarkGreen.copy(alpha = 0.50f)
            )
        }
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AppDarkGreen,
                checkedBorderColor = AppGoldColor.copy(alpha = 0.60f),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = AppDarkGreen.copy(alpha = 0.18f),
                uncheckedBorderColor = AppDarkGreen.copy(alpha = 0.15f)
            )
        )
    }
}

// ─── Profile Action Row ───────────────────────────────────────────────────────
@Composable
private fun ProfileActionRow(
    icon: ImageVector,
    label: String,
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
        Text(
            text = label,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = AppDarkGreen,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = AppDarkGreen.copy(alpha = 0.30f),
            modifier = Modifier.size(18.dp)
        )
    }
}

// ─── Legal Feature Row ────────────────────────────────────────────────────────
@Composable
private fun LegalFeatureRow(
    icon: ImageVector,
    title: String,
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
                text = title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = AppDarkGreen
            )
            Text(
                text = subtitle,
                fontFamily = FontFamily.Serif,
                fontSize = 11.sp,
                color = AppDarkGreen.copy(alpha = 0.50f)
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = AppDarkGreen.copy(alpha = 0.30f),
            modifier = Modifier.size(18.dp)
        )
    }
}

// ─── Membership Banner ────────────────────────────────────────────────────────
@Composable
private fun MembershipBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = AppDarkGreen,
        border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.45f)),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = AppGoldColor,
                modifier = Modifier.size(36.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Abonnement Premium",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Text(
                    text = "Valide jusqu'au 31 Déc 2026",
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    color = AppGoldColor.copy(alpha = 0.80f)
                )
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AppGoldColor.copy(alpha = 0.18f),
                border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.60f))
            ) {
                Text(
                    text = "Actif",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = AppGoldColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

// ─── Row Divider ─────────────────────────────────────────────────────────────
@Composable
private fun ProfileDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 12.dp),
        color = AppDarkGreen.copy(alpha = 0.07f)
    )
}

// ─── Confirmation Dialog ──────────────────────────────────────────────────────
@Composable
private fun ProfileConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    confirmColor: Color,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = Color.White,
        title = {
            Text(
                title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = AppDarkGreen,
                fontSize = 18.sp
            )
        },
        text = {
            Text(
                message,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp,
                color = AppDarkGreen.copy(alpha = 0.65f)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Annuler",
                    fontFamily = FontFamily.Serif,
                    color = AppDarkGreen.copy(alpha = 0.60f)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = confirmColor)
            ) {
                Text(
                    confirmLabel,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    )
}
