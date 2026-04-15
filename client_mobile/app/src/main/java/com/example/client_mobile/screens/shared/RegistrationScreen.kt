package com.example.client_mobile.screens.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.client_mobile.R

@Composable
fun RegistrationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLawyerHome: () -> Unit,
    onNavigateToUserHome: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var firstName by remember { mutableStateOf("") }
    var lastName  by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var isLawyer  by remember { mutableStateOf(false) }

    var isPasswordVisible by remember { mutableStateOf(false) }

    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError  by remember { mutableStateOf(false) }
    var emailError     by remember { mutableStateOf(false) }
    var passwordError  by remember { mutableStateOf(false) }

    val registerState by authViewModel.registerState.collectAsStateWithLifecycle()
    val isLoading  = registerState is AuthViewModel.RegisterUiState.Loading
    val authError  = (registerState as? AuthViewModel.RegisterUiState.Error)?.message

    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is AuthViewModel.RegisterUiState.Success -> {
                authViewModel.resetRegisterState()
                if (state.userType == "lawyer") onNavigateToLawyerHome()
                else onNavigateToUserHome()
            }
            else -> Unit
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

        // Scrollable centered card
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text       = "Inscription",
                        fontSize   = 28.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color      = AppDarkGreen
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Account type toggle ──────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(AppDarkGreen.copy(alpha = 0.08f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        listOf("Client" to false, "Avocat" to true).forEach { (label, isLawyerType) ->
                            val isSelected = isLawyer == isLawyerType
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (isSelected) AppDarkGreen else Color.Transparent)
                                    .clickable { isLawyer = isLawyerType },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text       = label,
                                    color      = if (isSelected) Color.White else AppDarkGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    fontSize   = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Form fields ──────────────────────────────────────────
                    CustomInputField(
                        value         = firstName,
                        onValueChange = { firstName = it; firstNameError = false },
                        placeholder   = "Prénom",
                        leadingIcon   = Icons.Default.Person,
                        isError       = firstNameError,
                        errorMessage  = "Veuillez saisir votre prénom"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomInputField(
                        value         = lastName,
                        onValueChange = { lastName = it; lastNameError = false },
                        placeholder   = "Nom",
                        leadingIcon   = Icons.Default.Person,
                        isError       = lastNameError,
                        errorMessage  = "Veuillez saisir votre nom"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomInputField(
                        value         = email,
                        onValueChange = { email = it; emailError = false },
                        placeholder   = "E-mail",
                        leadingIcon   = Icons.Default.Email,
                        isError       = emailError,
                        errorMessage  = "Veuillez saisir un e-mail valide",
                        keyboardType  = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomInputField(
                        value              = password,
                        onValueChange      = { password = it; passwordError = false },
                        placeholder        = "Mot de passe",
                        leadingIcon        = Icons.Default.Lock,
                        isError            = passwordError,
                        errorMessage       = "Mot de passe requis (6 caractères min)",
                        isPassword         = true,
                        isPasswordVisible  = isPasswordVisible,
                        onVisibilityToggle = { isPasswordVisible = !isPasswordVisible }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // ── Error banner ─────────────────────────────────────────
                    ErrorBanner(message = authError)
                    if (authError != null) Spacer(modifier = Modifier.height(8.dp))

                    // ── Submit button ────────────────────────────────────────
                    AppButton(
                        text      = "S'inscrire",
                        isLoading = isLoading,
                        onClick   = {
                            firstNameError = firstName.isBlank()
                            lastNameError  = lastName.isBlank()
                            emailError     = email.isBlank() || !email.contains("@")
                            passwordError  = password.length < 6
                            if (firstNameError || lastNameError || emailError || passwordError) return@AppButton
                            authViewModel.registerNewUser(
                                firstName = firstName,
                                lastName  = lastName,
                                email     = email,
                                password  = password,
                                role      = if (isLawyer) "lawyer" else "user"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Back to login link ───────────────────────────────────
                    Row(
                        modifier          = Modifier.clickable { onNavigateBack() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Déjà un compte ? ",
                            fontSize   = 13.sp,
                            color      = AppDarkGreen.copy(alpha = 0.55f),
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            "Se connecter",
                            fontSize   = 13.sp,
                            color      = AppDarkGreen,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }
            }
        }
    }
}
