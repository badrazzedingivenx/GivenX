package com.example.client_mobile.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client_mobile.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvocatProfile(
    fullName: String = "Maître Yassine",
    speciality: String = "Droit Pénal",
    email: String = "avocat@contact.com",
    phone: String = "+212 6 00 00 00 00",
    address: String = "Rabat, Maroc",
    onBack: () -> Unit
) {
    val goldColor = Color(0xFFD4AF37)
    val darkGreen = Color(0xFF1B3124)
    val scrollState = rememberScrollState()

    var isEditing by remember { mutableStateOf(false) }

    // Editable states
    var nameState by remember { mutableStateOf(fullName) }
    var specState by remember { mutableStateOf(speciality) }
    var emailState by remember { mutableStateOf(email) }
    var phoneState by remember { mutableStateOf(phone) }
    var addressState by remember { mutableStateOf(address) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mon Profil", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = darkGreen) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = darkGreen)
                    }
                },
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = null,
                            tint = darkGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
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
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.7f)))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Using logo_user which is safe and exists
                Box(contentAlignment = Alignment.BottomEnd) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_user),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(3.dp, goldColor, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = darkGreen,
                        border = BorderStroke(1.dp, goldColor)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = goldColor, modifier = Modifier.padding(6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = nameState,
                        onValueChange = { nameState = it },
                        label = { Text("Nom complet") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = specState,
                        onValueChange = { specState = it },
                        label = { Text("Spécialité") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp)
                    )
                } else {
                    Text(text = nameState, fontSize = 24.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = darkGreen)
                    Text(text = specState, fontSize = 16.sp, fontFamily = FontFamily.Serif, color = goldColor)
                }

                Spacer(modifier = Modifier.height(32.dp))

                EditableProfileItem("E-mail", emailState, Icons.Default.Email, darkGreen, isEditing) { emailState = it }
                EditableProfileItem("Téléphone", phoneState, Icons.Default.Phone, darkGreen, isEditing) { phoneState = it }
                EditableProfileItem("Adresse", addressState, Icons.Default.LocationOn, darkGreen, isEditing) { addressState = it }

                Spacer(modifier = Modifier.height(32.dp))

                if (isEditing) {
                    Button(
                        onClick = { isEditing = false },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = darkGreen),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Text("Enregistrer", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                    }
                } else {
                    Button(
                        onClick = { onBack() },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Se déconnecter", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun EditableProfileItem(label: String, value: String, icon: ImageVector, themeColor: Color, isEditing: Boolean, onValueChange: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.9f),
        border = BorderStroke(0.5.dp, themeColor.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = themeColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, fontSize = 12.sp, color = Color.Gray, fontFamily = FontFamily.Serif)
                if (isEditing) {
                    TextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent, focusedContainerColor = Color.Transparent)
                    )
                } else {
                    Text(text = value, fontSize = 15.sp, color = themeColor, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                }
            }
        }
    }
}
