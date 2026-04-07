package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ─── Edit User Profile Screen ─────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserProfileScreen(
    initialName: String = "Karim Bennani",
    initialEmail: String = "karim.bennani@email.com",
    initialPhone: String = "+212 6 12 34 56 78",
    initialAddress: String = "12, Rue Hassan II, Casablanca",
    initialImageUri: Uri? = null,
    onBack: () -> Unit = {},
    onSave: (name: String, email: String, phone: String, address: String, imageUri: Uri?) -> Unit = { _, _, _, _, _ -> }
) {
    // ── Editable state ────────────────────────────────────────────────────────
    var name    by remember { mutableStateOf(initialName) }
    var email   by remember { mutableStateOf(initialEmail) }
    var phone   by remember { mutableStateOf(initialPhone) }
    var address by remember { mutableStateOf(initialAddress) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(initialImageUri) }

    // ── Image Picker Launcher ─────────────────────────────────────────────────
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    // ── Validation errors ─────────────────────────────────────────────────────
    var nameError    by remember { mutableStateOf("") }
    var emailError   by remember { mutableStateOf("") }
    var phoneError   by remember { mutableStateOf("") }

    var showSaveDialog by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        nameError  = if (name.isBlank()) "Le nom ne peut pas être vide." else ""
        emailError = when {
            email.isBlank()                    -> "L'e-mail est requis."
            !email.contains("@") ||
            !email.contains(".")               -> "Adresse e-mail invalide."
            else                               -> ""
        }
        phoneError = when {
            phone.isBlank()                    -> "Le numéro est requis."
            phone.replace(" ", "").length < 8  -> "Numéro trop court (min. 8 chiffres)."
            else                               -> ""
        }
        return nameError.isEmpty() && emailError.isEmpty() && phoneError.isEmpty()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Modifier le Profil",
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
                            contentDescription = "Annuler",
                            tint = AppDarkGreen
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (validate()) showSaveDialog = true
                    }) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Sauvegarder",
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // ── Avatar ────────────────────────────────────────────────────
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier.clickable { galleryLauncher.launch("image/*") }
                        ) {
                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Photo de profil",
                                    modifier = Modifier
                                        .size(88.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, AppGoldColor, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                val initials = name
                                    .split(" ")
                                    .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                                    .take(2)
                                    .joinToString("")
                                Box(
                                    modifier = Modifier
                                        .size(88.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    AppDarkGreen.copy(alpha = 0.18f),
                                                    AppDarkGreen.copy(alpha = 0.07f)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initials.ifEmpty { "?" },
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 30.sp,
                                        color = AppDarkGreen
                                    )
                                }
                            }
                            Surface(
                                modifier = Modifier.size(28.dp),
                                shape = CircleShape,
                                color = AppGoldColor,
                                shadowElevation = 3.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        contentDescription = "Changer la photo",
                                        tint = AppDarkGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Informations Personnelles ─────────────────────────────────
                item { SectionHeader(title = "Informations Personnelles") }

                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White.copy(alpha = 0.92f),
                        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.10f)),
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            ProfileTextField(
                                value = name,
                                onValueChange = { name = it; nameError = "" },
                                label = "Nom complet",
                                leadingIcon = Icons.Default.Person,
                                isError = nameError.isNotEmpty(),
                                errorMessage = nameError
                            )
                            ProfileTextField(
                                value = email,
                                onValueChange = { email = it; emailError = "" },
                                label = "Adresse e-mail",
                                leadingIcon = Icons.Default.Email,
                                isError = emailError.isNotEmpty(),
                                errorMessage = emailError,
                                keyboardType = KeyboardType.Email
                            )
                            ProfileTextField(
                                value = phone,
                                onValueChange = { phone = it; phoneError = "" },
                                label = "Numéro de téléphone",
                                leadingIcon = Icons.Default.Phone,
                                isError = phoneError.isNotEmpty(),
                                errorMessage = phoneError,
                                keyboardType = KeyboardType.Phone
                            )
                            ProfileTextField(
                                value = address,
                                onValueChange = { address = it },
                                label = "Adresse postale",
                                leadingIcon = Icons.Default.LocationOn
                            )
                        }
                    }
                }

                // ── Hint Card ─────────────────────────────────────────────────
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = AppDarkGreen.copy(alpha = 0.06f),
                        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = AppGoldColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Pour modifier votre mot de passe, rendez-vous dans Sécurité & Paramètres.",
                                fontFamily = FontFamily.Serif,
                                fontSize = 12.sp,
                                color = AppDarkGreen.copy(alpha = 0.65f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // ── Buttons ───────────────────────────────────────────────────
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { if (validate()) showSaveDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppDarkGreen),
                            border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.50f))
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = AppGoldColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Sauvegarder",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, AppDarkGreen.copy(alpha = 0.35f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppDarkGreen)
                        ) {
                            Text(
                                "Annuler",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }

    // ── Save Confirmation Dialog ───────────────────────────────────────────────
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            shape = RoundedCornerShape(22.dp),
            containerColor = Color.White,
            title = {
                Text(
                    "Confirmer les modifications ?",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkGreen,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    "Vos informations seront mises à jour.",
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    color = AppDarkGreen.copy(alpha = 0.65f)
                )
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Annuler", fontFamily = FontFamily.Serif, color = AppDarkGreen.copy(alpha = 0.55f))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSaveDialog = false
                        onSave(name, email, phone, address, selectedImageUri)
                        onBack()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppDarkGreen)
                ) {
                    Text(
                        "Sauvegarder",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        )
    }
}
