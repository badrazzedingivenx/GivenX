package com.example.client_mobile.screens.shared

import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val isLoading  = loginState is AuthViewModel.LoginUiState.Loading
    val authError  = (loginState as? AuthViewModel.LoginUiState.Error)?.message
    val context    = LocalContext.current

    LaunchedEffect(authError) {
        if (!authError.isNullOrBlank()) {
            Toast.makeText(context, authError, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(loginState) {
        if (loginState is AuthViewModel.LoginUiState.Success) {
            authViewModel.proceedToDashboard(
                onLawyer = onNavigateToLawyerHome,
                onClient = onNavigateToUserHome
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter            = painterResource(id = R.drawable.background_app),
            contentDescription = null,
            modifier           = Modifier.fillMaxSize(),
            contentScale       = ContentScale.Crop
        )

        // Centered glassmorphism card
        Box(
            modifier         = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(40.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.52f),
                                Color.White.copy(alpha = 0.68f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(40.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 36.dp)
            ) {
                Column(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo
                    Image(
                        painter            = painterResource(id = R.drawable.logo_app),
                        contentDescription = "Logo",
                        modifier           = Modifier.size(220.dp),
                        contentScale       = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text       = "Connexion",
                        fontSize   = 28.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color      = AppDarkGreen
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Email field
                    CustomInputField(
                        value         = email,
                        onValueChange = { email = it; emailError = false },
                        placeholder   = "E-mail",
                        leadingIcon   = Icons.Default.Email,
                        isError       = emailError,
                        errorMessage  = "Veuillez saisir votre e-mail",
                        keyboardType  = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password field
                    CustomInputField(
                        value              = password,
                        onValueChange      = { password = it; passwordError = false },
                        placeholder        = "Mot de passe",
                        leadingIcon        = Icons.Default.Lock,
                        isError            = passwordError,
                        errorMessage       = "Veuillez saisir votre mot de passe",
                        isPassword         = true,
                        isPasswordVisible  = isPasswordVisible,
                        onVisibilityToggle = { isPasswordVisible = !isPasswordVisible }
                    )

                    // Forgot password
                    Text(
                        text       = "Mot de passe oublié ?",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Serif,
                        color      = AppDarkGreen,
                        modifier   = Modifier
                            .align(Alignment.End)
                            .padding(top = 10.dp)
                            .clickable { /* Action */ }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Inline error banner
                    ErrorBanner(message = authError)

                    if (authError != null) Spacer(modifier = Modifier.height(8.dp))

                    // Login button — uses the standardised AppButton
                    AppButton(
                        text      = "Se connecter",
                        onClick   = {
                            emailError    = email.isBlank()
                            passwordError = password.isBlank()
                            if (emailError || passwordError) return@AppButton
                            authViewModel.login(email, password)
                        },
                        isLoading = isLoading
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    // Sign-up link
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text       = "Pas encore de compte ? ",
                            fontSize   = 12.sp,
                            fontFamily = FontFamily.Serif,
                            color      = AppDarkGreen.copy(alpha = 0.55f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text       = "Créer un compte",
                            fontSize   = 12.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color      = AppDarkGreen,
                            modifier   = Modifier.clickable { onNavigateToSignup(userType) }
                        )
                    }
                }
            }
        }
    }
}

// ─── Custom Input Field ───────────────────────────────────────────────────────
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
    onVisibilityToggle: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = {
                Text(
                    placeholder,
                    color      = AppDarkGreen.copy(alpha = 0.40f),
                    fontFamily = FontFamily.Serif,
                    fontSize   = 14.sp
                )
            },
            leadingIcon   = {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint     = if (isError) Color(0xFFD32F2F) else AppDarkGreen.copy(alpha = 0.65f),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon  = {
                if (isPassword && onVisibilityToggle != null) {
                    IconButton(onClick = onVisibilityToggle) {
                        Icon(
                            imageVector        = if (isPasswordVisible) Icons.Default.Visibility
                                                 else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint               = AppDarkGreen.copy(alpha = 0.45f)
                        )
                    }
                }
            },
            visualTransformation = if (isPassword && !isPasswordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else keyboardType
            ),
            modifier    = Modifier.fillMaxWidth(),
            shape       = RoundedCornerShape(20.dp),
            singleLine  = true,
            isError     = isError,
            colors      = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = Color.White.copy(alpha = 0.96f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.90f),
                errorContainerColor     = Color.White.copy(alpha = 0.96f),
                focusedBorderColor      = AppDarkGreen.copy(alpha = 0.70f),
                unfocusedBorderColor    = AppDarkGreen.copy(alpha = 0.20f),
                errorBorderColor        = Color(0xFFD32F2F)
            )
        )
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text     = errorMessage,
                color    = Color(0xFFD32F2F),
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(start = 16.dp, top = 3.dp)
            )
        }
    }
}
