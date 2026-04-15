package com.example.client_mobile.screens.lawyer

import com.example.client_mobile.screens.shared.*

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import coil.compose.AsyncImage
import com.example.client_mobile.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenAvocat(
    fullName: String = LawyerSession.fullName,
    speciality: String = LawyerSession.title,
    profileImageUri: Uri? = LawyerSession.profileImageUri,
    isMasculine: Boolean = true,
    clientCount: String = "28",
    messageCount: String = "12",
    demandeCount: String = "5",
    onNavigateToProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) LawyerSession.profileImageUri = uri
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> if (isGranted) galleryLauncher.launch("image/*") }

    val onProfileImageClick = {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED) {
            galleryLauncher.launch("image/*")
        } else {
            showPermissionDialog = true
        }
    }

    if (showPermissionDialog) {
        PermissionRequestDialog(
            onDismiss = { showPermissionDialog = false },
            onConfirm = {
                showPermissionDialog = false
                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    Manifest.permission.READ_MEDIA_IMAGES
                else
                    Manifest.permission.READ_EXTERNAL_STORAGE
                permissionLauncher.launch(permission)
            }
        )
    }

    val scrollState = rememberScrollState()
    // Home tab is always selected on this standalone screen (index 0)
    val selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Image(
                            painter      = painterResource(id = R.drawable.logo_app),
                            contentDescription = "Logo",
                            modifier     = Modifier.size(330.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            AvocatBottomBar(
                selectedTab    = selectedTab,
                onTabSelected  = { index -> if (index == 3) onNavigateToProfile() }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background
            Image(
                painter      = painterResource(id = R.drawable.background_app),
                contentDescription = null,
                modifier     = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.65f)))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // ── Welcome Heading ──────────────────────────────────────────
                Text(
                    text       = "Bienvenue ${if (isMasculine) "Maître" else "Maîtresse"}",
                    fontSize   = 28.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color      = AppDarkGreen
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text       = "Gérez vos activités facilement",
                    fontSize   = 14.sp,
                    fontFamily = FontFamily.Serif,
                    color      = AppDarkGreen.copy(alpha = 0.60f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── Profile Card ─────────────────────────────────────────────
                Surface(
                    modifier       = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToProfile() },
                    shape          = RoundedCornerShape(28.dp),
                    color          = AppDarkGreen,
                    border         = BorderStroke(1.dp, AppGoldColor.copy(alpha = 0.45f)),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier          = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar with camera overlay
                        Box(
                            modifier         = Modifier.clickable { onProfileImageClick() },
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            if (profileImageUri != null) {
                                AsyncImage(
                                    model              = profileImageUri,
                                    contentDescription = "Profile",
                                    modifier           = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, AppGoldColor, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter            = painterResource(id = R.drawable.logo_user),
                                    contentDescription = "Default Profile",
                                    modifier           = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, AppGoldColor, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            // Camera badge
                            Surface(
                                modifier  = Modifier.size(26.dp),
                                shape     = CircleShape,
                                color     = Color.White,
                                border    = BorderStroke(1.dp, AppGoldColor)
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint     = AppDarkGreen,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                text       = "${if (isMasculine) "Maître" else "Maîtresse"} $fullName",
                                fontSize   = 18.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color      = Color.White
                            )
                            Text(
                                text       = speciality,
                                fontSize   = 13.sp,
                                fontFamily = FontFamily.Serif,
                                color      = AppGoldColor,
                                fontWeight = FontWeight.Medium
                            )
                            // Subtle "Voir profil" hint
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text       = "Voir le profil",
                                    fontSize   = 11.sp,
                                    fontFamily = FontFamily.Serif,
                                    color      = Color.White.copy(alpha = 0.50f)
                                )
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint     = Color.White.copy(alpha = 0.50f),
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Stats Row ────────────────────────────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CompactStatTile(
                        modifier = Modifier.weight(1f),
                        icon     = Icons.Default.Groups,
                        count    = clientCount,
                        label    = "Clients"
                    )
                    CompactStatTile(
                        modifier = Modifier.weight(1f),
                        icon     = Icons.Default.Email,
                        count    = messageCount,
                        label    = "Messages"
                    )
                    CompactStatTile(
                        modifier = Modifier.weight(1f),
                        icon     = Icons.Default.Description,
                        count    = demandeCount,
                        label    = "Demandes"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Action Menu ──────────────────────────────────────────────
                SectionHeader(title = "Actions rapides")
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActionMenuItemAvocat(
                        icon      = Icons.AutoMirrored.Filled.Assignment,
                        title     = "Voir les demandes",
                        subTitle  = "Consulter et répondre aux nouvelles demandes",
                        onClick   = {}
                    )
                    ActionMenuItemAvocat(
                        icon      = Icons.Default.ChatBubble,
                        title     = "Messages",
                        subTitle  = "Voir vos conversations",
                        onClick   = {}
                    )
                    ActionMenuItemAvocat(
                        icon      = Icons.Default.Person,
                        title     = "Mon profil",
                        subTitle  = "Gérer vos informations",
                        onClick   = { onNavigateToProfile() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ─── Permission Dialog ────────────────────────────────────────────────────────
@Composable
fun PermissionRequestDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Accès à la galerie",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                color      = AppDarkGreen
            )
        },
        text = {
            Text(
                "Nous avons besoin de votre autorisation pour accéder à vos photos.",
                fontFamily = FontFamily.Serif,
                fontSize   = 14.sp,
                color      = AppDarkGreen.copy(alpha = 0.70f)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors  = ButtonDefaults.buttonColors(containerColor = AppDarkGreen),
                shape   = RoundedCornerShape(14.dp)
            ) {
                Text(
                    "Autoriser",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Plus tard",
                    fontFamily = FontFamily.Serif,
                    color      = AppDarkGreen.copy(alpha = 0.50f)
                )
            }
        },
        shape          = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

// ─── Action Menu Item ─────────────────────────────────────────────────────────
@Composable
fun ActionMenuItemAvocat(
    icon: ImageVector,
    title: String,
    subTitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier       = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 76.dp)
            .clickable { onClick() },
        shape          = RoundedCornerShape(22.dp),
        color          = AppDarkGreen,
        border         = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.30f)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon tile
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint     = AppGoldColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text       = title,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text       = subTitle,
                    color      = Color.White.copy(alpha = 0.55f),
                    fontSize   = 12.sp,
                    fontFamily = FontFamily.Serif
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint     = AppGoldColor.copy(alpha = 0.80f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ─── Avocat Bottom Bar (with animated indicator) ──────────────────────────────
@Composable
fun AvocatBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(
        Pair(Icons.Default.Home,                "Accueil"),
        Pair(Icons.AutoMirrored.Filled.Chat,    "Messages"),
        Pair(Icons.Default.Groups,              "Clients"),
        Pair(Icons.Default.Person,              "Profil")
    )

    Surface(
        modifier       = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(70.dp),
        shape          = RoundedCornerShape(28.dp),
        color          = AppDarkGreen,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier              = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, (icon, label) ->
                val selected = selectedTab == index
                val indicatorWidth by animateDpAsState(
                    targetValue   = if (selected) 16.dp else 0.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness    = Spring.StiffnessMedium
                    ),
                    label = "avocatTabIndicator_$index"
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector        = icon,
                        contentDescription = label,
                        tint               = if (selected) AppGoldColor else Color.White.copy(alpha = 0.50f),
                        modifier           = Modifier.size(24.dp)
                    )
                    Text(
                        text       = label,
                        color      = if (selected) AppGoldColor else Color.White.copy(alpha = 0.50f),
                        fontSize   = 10.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    // Animated selection indicator pill
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
