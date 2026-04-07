package com.example.client_mobile.screens.lawyer

import com.example.client_mobile.screens.shared.*

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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

// ─── Edit Lawyer Profile Screen ───────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditLawyerProfileScreen(
    initialName: String = "Maître Yassine El Amrani",
    initialTitle: String = "Avocat au Barreau de Casablanca",
    initialEmail: String = "y.elamrani@cabinetyassine.ma",
    initialPhone: String = "+212 6 61 23 45 67",
    initialAddress: String = "34, Bd Zerktouni, Casablanca",
    initialBio: String = "Maître El Amrani est spécialisé en droit pénal avec plus de 12 ans d'expérience. Il intervient devant les tribunaux de grande instance, cours d'appel et la Cour de cassation.",
    initialSpecs: List<String> = listOf("Droit Pénal", "Droit Civil", "Droit des Affaires", "Droit Fiscal", "Contentieux Commercial"),
    initialImageUri: Uri? = null,
    onBack: () -> Unit = {},
    onSave: (name: String, title: String, email: String, phone: String, address: String, bio: String, specs: List<String>, imageUri: Uri?) -> Unit = { _, _, _, _, _, _, _, _ -> }
) {
    // ── Editable state ────────────────────────────────────────────────────────
    var name    by remember { mutableStateOf(initialName) }
    var title   by remember { mutableStateOf(initialTitle) }
    var email   by remember { mutableStateOf(initialEmail) }
    var phone   by remember { mutableStateOf(initialPhone) }
    var address by remember { mutableStateOf(initialAddress) }
    var bio     by remember { mutableStateOf(initialBio) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(initialImageUri) }

    val specs = remember { mutableStateListOf(*initialSpecs.toTypedArray()) }
    var newSpec by remember { mutableStateOf("") }

    // ── Image Picker Launcher ─────────────────────────────────────────────────
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // ── Validation errors ─────────────────────────────────────────────────────
    var nameError  by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf("") }

    var showSaveDialog by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        nameError  = if (name.isBlank()) "Le nom est requis." else ""
        titleError = if (title.isBlank()) "Le titre professionnel est requis." else ""
        emailError = when {
            email.isBlank()                   -> "L'e-mail est requis."
            !email.contains("@") ||
            !email.contains(".")              -> "Adresse e-mail invalide."
            else                              -> ""
        }
        phoneError = when {
            phone.isBlank()                   -> "Le numéro est requis."
            phone.replace(" ", "").length < 8 -> "Numéro trop court (min. 8 chiffres)."
            else                              -> ""
        }
        return nameError.isEmpty() && titleError.isEmpty() &&
               emailError.isEmpty() && phoneError.isEmpty()
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
                            modifier = Modifier.clickable { launcher.launch("image/*") }
                        ) {
                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Photo de profil",
                                    modifier = Modifier
                                        .size(88.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                val initials = name
                                    .removePrefix("Maître ")
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
                                                    AppGoldColor.copy(alpha = 0.20f),
                                                    AppGoldColor.copy(alpha = 0.06f)
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

                // ── Identité Professionnelle ──────────────────────────────────
                item { SectionHeader(title = "Identité Professionnelle") }
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
                                value = title,
                                onValueChange = { title = it; titleError = "" },
                                label = "Titre professionnel",
                                leadingIcon = Icons.Default.Gavel,
                                isError = titleError.isNotEmpty(),
                                errorMessage = titleError
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
                                label = "Adresse du cabinet",
                                leadingIcon = Icons.Default.Business
                            )
                        }
                    }
                }

                // ── Biographie ────────────────────────────────────────────────
                item { SectionHeader(title = "Biographie") }
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White.copy(alpha = 0.92f),
                        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.10f)),
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            ProfileTextField(
                                value = bio,
                                onValueChange = { bio = it },
                                label = "À propos de vous",
                                leadingIcon = Icons.Default.FormatQuote,
                                singleLine = false,
                                minLines = 4,
                                maxLines = 8
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${bio.length} caractères",
                                fontFamily = FontFamily.Serif,
                                fontSize = 11.sp,
                                color = AppDarkGreen.copy(alpha = 0.40f)
                            )
                        }
                    }
                }

                // ── Domaines d'Expertise ──────────────────────────────────────
                item { SectionHeader(title = "Domaines d'Expertise") }
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
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Existing chips
                            if (specs.isNotEmpty()) {
                                FlowRowSpecChips(
                                    specs = specs,
                                    onRemove = { specs.remove(it) }
                                )
                            } else {
                                Text(
                                    "Aucun domaine ajouté.",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 13.sp,
                                    color = AppDarkGreen.copy(alpha = 0.40f)
                                )
                            }

                            HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.08f))

                            // Add new spec
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newSpec,
                                    onValueChange = { newSpec = it },
                                    placeholder = {
                                        Text(
                                            "Nouveau domaine…",
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 13.sp,
                                            color = AppDarkGreen.copy(alpha = 0.38f)
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AppDarkGreen,
                                        unfocusedBorderColor = AppDarkGreen.copy(alpha = 0.25f),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White.copy(alpha = 0.80f),
                                        cursorColor = AppDarkGreen
                                    )
                                )
                                Surface(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            val trimmed = newSpec.trim()
                                            if (trimmed.isNotEmpty() && !specs.contains(trimmed)) {
                                                specs.add(trimmed)
                                                newSpec = ""
                                            }
                                        },
                                    shape = RoundedCornerShape(14.dp),
                                    color = AppDarkGreen,
                                    border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.50f))
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Ajouter",
                                            tint = AppGoldColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
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
                    "Vos informations professionnelles seront mises à jour.",
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
                        onSave(name, title, email, phone, address, bio, specs.toList(), selectedImageUri)
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

// ─── Specialization Chips with Remove ────────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowSpecChips(
    specs: List<String>,
    onRemove: (String) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        specs.forEach { spec ->
            SpecEditChip(label = spec, onRemove = { onRemove(spec) })
        }
    }
}

@Composable
private fun SpecEditChip(label: String, onRemove: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = AppDarkGreen,
        border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 6.dp, top = 7.dp, bottom = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = Color.White,
                maxLines = 1
            )
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .clickable { onRemove() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Supprimer",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(11.dp)
                )
            }
        }
    }
}
