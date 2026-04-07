package com.example.client_mobile.screens.lawyer

import com.example.client_mobile.screens.shared.*

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    val goldColor = Color(0xFFD4AF37)
    val darkGreen = Color(0xFF1B3124)
    
    var showPermissionDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> 
        if (uri != null) {
            LawyerSession.profileImageUri = uri
        }
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
        val status = ContextCompat.checkSelfPermission(context, permission)
        if (status == PermissionChecker.PERMISSION_GRANTED) galleryLauncher.launch("image/*") else showPermissionDialog = true
    }

    if (showPermissionDialog) {
        PermissionRequestDialog(
            onDismiss = { showPermissionDialog = false },
            onConfirm = {
                showPermissionDialog = false
                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
                permissionLauncher.launch(permission)
            },
            darkGreen = darkGreen,
            goldColor = goldColor
        )
    }

    val scrollState = rememberScrollState()
    val selectedTab by remember { mutableIntStateOf(0) } // Home is 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_app),
                            contentDescription = "Logo",
                            modifier = Modifier.size(330.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            AvocatBottomBar(
                backgroundColor = darkGreen,
                selectedColor = goldColor,
                selectedTab = selectedTab,
                onTabSelected = { index ->
                    if (index == 3) {
                        onNavigateToProfile()
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background_app),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
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
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Bienvenue ${if (isMasculine) "Maître" else "Maîtresse"}",
                    fontSize = 28.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = darkGreen
                )
                Text(
                    text = "Gérez vos activités facilement",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Serif,
                    color = darkGreen.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Profile Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToProfile() },
                    shape = RoundedCornerShape(30.dp),
                    color = darkGreen,
                    border = BorderStroke(1.dp, goldColor.copy(alpha = 0.5f)),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.clickable { onProfileImageClick() },
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            if (profileImageUri != null) {
                                AsyncImage(
                                    model = profileImageUri,
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, goldColor, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.logo_user),
                                    contentDescription = "Default Profile",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, goldColor, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Surface(
                                modifier = Modifier.size(26.dp),
                                shape = CircleShape,
                                color = Color.White,
                                border = BorderStroke(1.dp, goldColor)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = darkGreen, modifier = Modifier.padding(5.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "${if (isMasculine) "Maître" else "Maîtresse"} $fullName",
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = speciality,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Serif,
                                color = goldColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatItemCompact(modifier = Modifier.weight(1f), Icons.Default.Groups, clientCount, "Clients", darkGreen, goldColor)
                    StatItemCompact(modifier = Modifier.weight(1f), Icons.Default.Email, messageCount, "Messages", darkGreen, goldColor)
                    StatItemCompact(modifier = Modifier.weight(1f), Icons.Default.Description, demandeCount, "Demandes", darkGreen, goldColor)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                    ActionMenuItemAvocat(Icons.Default.Assignment, "Voir les demandes", "Consulter et répondre aux nouvelles demandes", darkGreen, goldColor) {}
                    ActionMenuItemAvocat(Icons.Default.ChatBubble, "Messages", "Voir vos conversations", darkGreen, goldColor) {}
                    ActionMenuItemAvocat(Icons.Default.Person, "Mon profil", "Gérer vos informations", darkGreen, goldColor) {
                        onNavigateToProfile()
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun PermissionRequestDialog(onDismiss: () -> Unit, onConfirm: () -> Unit, darkGreen: Color, goldColor: Color) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Accès à la galerie", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
        text = { Text("Nous avons besoin de votre autorisation pour accéder à vos photos.", fontFamily = FontFamily.Serif) },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = darkGreen)) {
                Text("Autoriser", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Plus tard", color = Color.Gray)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

@Composable
fun StatItemCompact(modifier: Modifier, icon: ImageVector, count: String, label: String, containerColor: Color, accentColor: Color) {
    Surface(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = BorderStroke(0.5.dp, accentColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
            Text(text = count, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = FontFamily.Serif)
            Text(text = label, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontFamily = FontFamily.Serif)
        }
    }
}

@Composable
fun ActionMenuItemAvocat(icon: ImageVector, title: String, subTitle: String, backgroundColor: Color, iconColor: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(80.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = BorderStroke(0.5.dp, iconColor.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(45.dp).background(Color.White.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Serif)
                Text(text = subTitle, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontFamily = FontFamily.Serif)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = iconColor.copy(alpha = 0.9f), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun AvocatBottomBar(backgroundColor: Color, selectedColor: Color, selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp).height(70.dp),
        shape = RoundedCornerShape(25.dp),
        color = backgroundColor,
        shadowElevation = 8.dp
    ) {
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
            BottomNavItemAvocat(Icons.Default.Home, "Accueil", selectedTab == 0, selectedColor) { onTabSelected(0) }
            BottomNavItemAvocat(Icons.Default.Chat, "Messages", selectedTab == 1, selectedColor) { onTabSelected(1) }
            BottomNavItemAvocat(Icons.Default.Groups, "Clients", selectedTab == 2, selectedColor) { onTabSelected(2) }
            BottomNavItemAvocat(Icons.Default.Person, "Profil", selectedTab == 3, selectedColor) { onTabSelected(3) }
        }
    }
}

@Composable
fun BottomNavItemAvocat(icon: ImageVector, label: String, selected: Boolean, selectedColor: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clip(CircleShape).clickable { onClick() }.padding(8.dp)) {
        Icon(icon, contentDescription = label, tint = if (selected) selectedColor else Color.White.copy(alpha = 0.5f), modifier = Modifier.size(24.dp))
        Text(text = label, color = if (selected) selectedColor else Color.White.copy(alpha = 0.5f), fontSize = 10.sp, fontFamily = FontFamily.Serif, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}
