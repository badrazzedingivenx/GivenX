package com.example.client_mobile.screens.lawyer

import com.example.client_mobile.screens.shared.*

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.client_mobile.R

@Composable
fun CreeAvocatScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var speciality by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    val specialities = listOf(
        "Généraliste", "Droit des Affaires", "Droit Pénal", 
        "Droit de la Famille", "Droit du Travail", "Droit Immobilier", 
        "Droit Administratif", "Autre"
    )

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    var fullNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf(false) }
    var specialityError by remember { mutableStateOf(false) }

    val registerState by authViewModel.registerState.collectAsStateWithLifecycle()
    val isLoading = registerState is AuthViewModel.RegisterUiState.Loading
    val authError = (registerState as? AuthViewModel.RegisterUiState.Error)?.message

    LaunchedEffect(registerState) {
        if (registerState is AuthViewModel.RegisterUiState.Success) {
            authViewModel.resetRegisterState()
            onNavigateToHome()
        }
    }

    val darkGreen = Color(0xFF1B3124)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_app),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        AppScaffold(
            showBackground = true,
            containerColor = Color.Transparent
        ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White.copy(alpha = 0.85f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50.dp)
                    )
                    .padding(horizontal = 22.dp, vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_app),
                        contentDescription = "Logo",
                        modifier = Modifier.size(235.dp), 
                        contentScale = ContentScale.Fit
                    )

                    Text(
                        text = "S'inscrire",
                        fontSize = 28.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = darkGreen,
                        modifier = Modifier.offset(y = (-55).dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth().offset(y = (-50).dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CustomInputFieldAvocatCompact(
                            value = fullName,
                            onValueChange = { fullName = it; fullNameError = false },
                            placeholder = "Nom complet",
                            leadingIcon = Icons.Default.Person,
                            isError = fullNameError,
                            errorMessage = "Veuillez saisir votre nom complet"
                        )

                        CustomInputFieldAvocatCompact(
                            value = email,
                            onValueChange = { email = it; emailError = false },
                            placeholder = "E-mail",
                            leadingIcon = Icons.Default.Email,
                            isError = emailError,
                            errorMessage = "Veuillez saisir votre e-mail"
                        )

                        CustomInputFieldAvocatCompact(
                            value = phone,
                            onValueChange = { 
                                if (it.all { char -> char.isDigit() }) {
                                    phone = it
                                    phoneError = false
                                }
                            },
                            placeholder = "Téléphone",
                            leadingIcon = Icons.Default.Phone,
                            isError = phoneError,
                            errorMessage = "Veuillez saisir votre numéro",
                            keyboardType = KeyboardType.Number
                        )

                        CustomInputFieldAvocatCompact(
                            value = password,
                            onValueChange = { password = it; passwordError = false },
                            placeholder = "Mot de passe",
                            leadingIcon = Icons.Default.Lock,
                            isError = passwordError,
                            errorMessage = "Veuillez saisir votre mot de passe",
                            isPassword = true,
                            isPasswordVisible = isPasswordVisible,
                            onVisibilityToggle = { isPasswordVisible = !isPasswordVisible }
                        )

                        CustomInputFieldAvocatCompact(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; confirmPasswordError = false },
                            placeholder = "Confirmer le mot de passe",
                            leadingIcon = Icons.Default.Lock,
                            isError = confirmPasswordError,
                            errorMessage = "Veuillez confirmer votre mot de passe",
                            isPassword = true,
                            isPasswordVisible = isConfirmPasswordVisible,
                            onVisibilityToggle = { isConfirmPasswordVisible = !isConfirmPasswordVisible }
                        )

                        CustomInputFieldAvocatCompact(
                            value = address,
                            onValueChange = { address = it; addressError = false },
                            placeholder = "Adresse complète",
                            leadingIcon = Icons.Default.LocationOn,
                            isError = addressError,
                            errorMessage = "Veuillez saisir votre adresse"
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = speciality,
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = { Text("Sélectionnez votre spécialité", fontSize = 14.sp, fontFamily = FontFamily.Serif, color = Color.Gray) },
                                    leadingIcon = { Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = darkGreen) },
                                    trailingIcon = { 
                                        IconButton(onClick = { expanded = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                                    shape = RoundedCornerShape(25.dp),
                                    isError = specialityError,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White.copy(alpha = 0.9f),
                                        unfocusedContainerColor = Color.White.copy(alpha = 0.7f),
                                        errorContainerColor = Color.White.copy(alpha = 0.9f),
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        errorBorderColor = Color.Red
                                    )
                                )
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.fillMaxWidth(0.8f).background(Color.White)
                                ) {
                                    specialities.forEach { selection ->
                                        DropdownMenuItem(
                                            text = { Text(selection, fontFamily = FontFamily.Serif) },
                                            onClick = {
                                                speciality = selection
                                                expanded = false
                                                specialityError = false
                                            }
                                        )
                                    }
                                }
                            }
                            if (specialityError) {
                                Text(
                                    text = "Veuillez sélectionner votre spécialité",
                                    color = Color.Red,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Serif,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                    Button(
                        onClick = {
                            fullNameError = fullName.isBlank()
                            emailError = email.isBlank()
                            phoneError = phone.isBlank()
                            passwordError = password.isBlank()
                            confirmPasswordError = confirmPassword.isBlank() || confirmPassword != password
                            addressError = address.isBlank()
                            specialityError = speciality.isBlank()

                            if (!fullNameError && !emailError && !phoneError && !passwordError && !confirmPasswordError && !addressError && !specialityError) {
                                authViewModel.register(
                                    fullName   = fullName,
                                    email      = email,
                                    password   = password,
                                    phone      = phone,
                                    role       = "lawyer",
                                    speciality = speciality
                                )
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = darkGreen),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth().height(55.dp).offset(y = (-45).dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text("S'inscrire", fontSize = 18.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    authError?.let { err ->
                        Text(
                            text = err,
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-40).dp)
                                .padding(bottom = 4.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().offset(y = (-40).dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("J'ai déjà un compte, ", fontSize = 13.sp, fontFamily = FontFamily.Serif, color = Color.Gray)
                        Text("Connect", fontSize = 13.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = darkGreen, 
                            modifier = Modifier.clickable { onNavigateToLogin() })
                    }
                }
            }
        }
    }
    }
}

@Composable
fun CustomInputFieldAvocatCompact(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isError: Boolean,
    errorMessage: String = "",
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onVisibilityToggle: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val darkGreen = Color(0xFF1B3124)
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 14.sp, color = Color.Gray, fontFamily = FontFamily.Serif) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = darkGreen) },
            trailingIcon = {
                if (isPassword && onVisibilityToggle != null) {
                    IconButton(onClick = onVisibilityToggle) {
                        Icon(
                            if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, 
                            contentDescription = null, 
                            tint = Color.Gray
                        )
                    }
                }
            },
            visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(25.dp),
            singleLine = true,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.9f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.7f),
                errorContainerColor = Color.White.copy(alpha = 0.9f),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = Color.Red
            )
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 10.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
