package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
fun CreeUserScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    var fullNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

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
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.50f),
                                Color.White.copy(alpha = 0.65f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(50.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment =Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_app),
                        contentDescription = "Logo",
                        modifier = Modifier.size(235.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "S'inscrire",
                        fontSize = 30.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = darkGreen
                    )

                    Spacer(modifier = Modifier.height(25.dp))
                    CustomInputFieldUser(
                        value = fullName,
                        onValueChange = { 
                            fullName = it
                            fullNameError = false 
                        },
                        placeholder = "Nom complet",
                        leadingIcon = Icons.Default.Person,
                        isError = fullNameError,
                        errorMessage = "Veuillez saisir votre nom complet"
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    CustomInputFieldUser(
                        value = email,
                        onValueChange = { 
                            email = it
                            emailError = false 
                        },
                        placeholder = "E-mail",
                        leadingIcon = Icons.Default.Email,
                        isError = emailError,
                        errorMessage = "Veuillez saisir votre e-mail"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomInputFieldUser(
                        value = password,
                        onValueChange = { 
                            password = it
                            passwordError = false 
                        },
                        placeholder = "Mot de passe",
                        leadingIcon = Icons.Default.Lock,
                        isError = passwordError,
                        errorMessage = "Veuillez saisir votre mot de passe",
                        isPassword = true,
                        isPasswordVisible = isPasswordVisible,
                        onVisibilityToggle = { isPasswordVisible = !isPasswordVisible }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomInputFieldUser(
                        value = confirmPassword,
                        onValueChange = { 
                            confirmPassword = it
                            confirmPasswordError = false 
                        },
                        placeholder = "Confirmer le mot de passe",
                        leadingIcon = Icons.Default.Lock,
                        isError = confirmPasswordError,
                        errorMessage = "Veuillez confirmer votre mot de passe",
                        isPassword = true,
                        isPasswordVisible = isConfirmPasswordVisible,
                        onVisibilityToggle = { isConfirmPasswordVisible = !isConfirmPasswordVisible }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    authError?.let { err ->
                        Text(
                            text = err,
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                    }
                    Button(
                        onClick = {
                            fullNameError = fullName.isBlank()
                            emailError = email.isBlank()
                            passwordError = password.isBlank()
                            confirmPasswordError = confirmPassword.isBlank() || confirmPassword != password
                            
                            if (!fullNameError && !emailError && !passwordError && !confirmPasswordError) {
                                authViewModel.register(
                                    fullName = fullName,
                                    email    = email,
                                    password = password,
                                    role     = "user"
                                )
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = darkGreen),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "S'inscrire",
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "J'ai déjà un compte, ",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Serif,
                            color = Color.Gray
                        )
                        Text(
                            text = "Connect",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = darkGreen,
                            modifier = Modifier.clickable { onNavigateToLogin() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomInputFieldUser(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isError: Boolean,
    errorMessage: String = "",
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onVisibilityToggle: (() -> Unit)? = null
) {
    val darkGreen = Color(0xFF1B3124)
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray, fontFamily = FontFamily.Serif) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = darkGreen) },
            trailingIcon = {
                if (isPassword && onVisibilityToggle != null) {
                    IconButton(onClick = onVisibilityToggle) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }
            },
            visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(25.dp),
            singleLine = true,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.95f), 
                unfocusedContainerColor = Color.White.copy(alpha = 0.95f),
                errorContainerColor = Color.White.copy(alpha = 0.95f),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = Color.Red
            )
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(start = 16.dp, top = 2.dp)
            )
        }
    }
}
