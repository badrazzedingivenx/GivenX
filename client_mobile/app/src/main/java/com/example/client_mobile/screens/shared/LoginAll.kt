package com.example.client_mobile.screens.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.client_mobile.R

@Composable
fun LoginScreen(
    userType: String,
    onNavigateToSignup: (String) -> Unit,
    onNavigateToLawyerHome: () -> Unit,
    onNavigateToUserHome: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val loginState by authViewModel.loginState.collectAsStateWithLifecycle()
    val isLoading = loginState is AuthViewModel.LoginUiState.Loading
    val authError = (loginState as? AuthViewModel.LoginUiState.Error)?.message

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is AuthViewModel.LoginUiState.Success -> {
                authViewModel.resetState()
                if (state.userType == "lawyer") onNavigateToLawyerHome()
                else onNavigateToUserHome()
            }
            else -> Unit
        }
    }

    val darkGreen = Color(0xFF1B3124)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
                    .fillMaxWidth(0.88f)
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
                    .padding(horizontal = 24.dp, vertical = 36.dp)
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
                        text = "Connexion",
                        fontSize = 32.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = darkGreen
                    )
                    Spacer(modifier = Modifier.height(35.dp))
                    CustomInputField(
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
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomInputField(
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
                    Text(
                        text = "Mot de passe oublié ?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = darkGreen,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 10.dp)
                            .clickable { /* Action */ }
                    )
                    Spacer(modifier = Modifier.height(35.dp))
                    // Auth error banner
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
                            emailError = email.isBlank()
                            passwordError = password.isBlank()
                            if (emailError || passwordError) return@Button
                            authViewModel.login(email, password, userType)
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
                                text = "Se connecter",
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Si vous n'avez pas de compte, ",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Serif,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Créez un compte",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = darkGreen,
                            modifier = Modifier.clickable { onNavigateToSignup(userType) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomInputField(
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
