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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserProfileScreen(
    onBack: () -> Unit = {},
    userViewModel: UserViewModel = viewModel()
) {
    val profile by userViewModel.profile.collectAsStateWithLifecycle()
    val isSaving by userViewModel.isSaving.collectAsStateWithLifecycle()
    val updateSuccess by userViewModel.updateSuccess.collectAsStateWithLifecycle()
    val errorMessage by userViewModel.errorMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            snackbarHostState.showSnackbar("Profil mis à jour")
            userViewModel.clearUpdateSuccess()
            onBack()
        }
    }

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(errorMessage!!)
            userViewModel.clearError()
        }
    }

    var fullName by remember(profile) { mutableStateOf(profile?.effectiveFullName() ?: "") }
    val email = profile?.email ?: ""
    var phone by remember(profile) { mutableStateOf(profile?.phone ?: "") }
    var address by remember(profile) { mutableStateOf(profile?.address ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> if (uri != null) selectedImageUri = uri }

    var fullNameError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        fullNameError = if (fullName.isBlank()) "Le nom est requis." else ""
        phoneError = when {
            phone.isBlank() -> "Le numéro est requis."
            phone.replace(" ", "").length < 8 -> "Numéro trop court (min. 8 chiffres)."
            else -> ""
        }
        return fullNameError.isEmpty() && phoneError.isEmpty()
    }

    AppScaffold(
        topBar = {
            StandardTopBar(
                title = "Modifier le Profil",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { if (validate()) showSaveDialog = true }) {
                        Icon(Icons.Default.Check, contentDescription = "Sauvegarder", tint = AppGoldColor)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.clickable { galleryLauncher.launch("image/*") }
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Photo de profil",
                                modifier = Modifier.size(88.dp).clip(CircleShape).border(2.dp, AppGoldColor, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val initialsStr = fullName.trim().split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("")
                            Box(
                                modifier = Modifier.size(88.dp).clip(CircleShape).background(
                                    Brush.radialGradient(listOf(AppDarkGreen.copy(alpha = 0.18f), AppDarkGreen.copy(alpha = 0.07f)))
                                ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = initialsStr.ifEmpty { "?" }, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 30.sp, color = AppDarkGreen)
                            }
                        }
                        Surface(modifier = Modifier.size(28.dp), shape = CircleShape, color = AppGoldColor, shadowElevation = 3.dp) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Changer", tint = AppDarkGreen, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            item { SectionHeader(title = "Informations Personnelles") }
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.10f)),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        LegalInputField(value = fullName, onValueChange = { fullName = it; fullNameError = "" }, label = "Nom Complet", leadingIcon = Icons.Default.Person, isError = fullNameError.isNotEmpty(), errorMessage = fullNameError)
                        LegalInputField(value = email, onValueChange = {}, label = "Adresse e-mail (non modifiable)", leadingIcon = Icons.Default.Email, enabled = false)
                        LegalInputField(value = phone, onValueChange = { phone = it; phoneError = "" }, label = "Numéro de téléphone", leadingIcon = Icons.Default.Phone, isError = phoneError.isNotEmpty(), errorMessage = phoneError, keyboardType = KeyboardType.Phone)
                        LegalInputField(value = address, onValueChange = { address = it }, label = "Adresse postale", leadingIcon = Icons.Default.LocationOn)
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = AppDarkGreen.copy(alpha = 0.06f),
                    border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.12f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(18.dp))
                        Text(text = "Pour modifier votre mot de passe, rendez-vous dans Sécurité & Paramètres.", fontFamily = FontFamily.Serif, fontSize = 12.sp, color = AppDarkGreen.copy(alpha = 0.65f), lineHeight = 18.sp)
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { if (validate()) showSaveDialog = true },
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppDarkGreen),
                        border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.50f))
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AppGoldColor, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Sauvegarder", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                        }
                    }
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, AppDarkGreen.copy(alpha = 0.35f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppDarkGreen)
                    ) {
                        Text("Annuler", fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }

        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                shape = RoundedCornerShape(22.dp),
                containerColor = Color.White,
                title = { Text("Confirmer ?", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = AppDarkGreen, fontSize = 17.sp) },
                text = { Text("Vos informations seront mises à jour.", fontFamily = FontFamily.Serif, fontSize = 13.sp, color = AppDarkGreen.copy(alpha = 0.65f)) },
                dismissButton = { TextButton(onClick = { showSaveDialog = false }) { Text("Annuler", fontFamily = FontFamily.Serif, color = AppDarkGreen.copy(alpha = 0.55f)) } },
                confirmButton = {
                    Button(
                        onClick = {
                            showSaveDialog = false
                            userViewModel.saveProfile(fullName, phone, address)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppDarkGreen)
                    ) {
                        Text("Sauvegarder", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            )
        }
    }
}