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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLawyer by remember { mutableStateOf(false) }
    
    var isPasswordVisible by remember { mutableStateOf(false) }
    
    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val registerState by authViewModel.registerState.collectAsStateWithLifecycle()
    val isLoading = registerState is AuthViewModel.RegisterUiState.Loading
    val authError = (registerState as? AuthViewModel.RegisterUiState.Error)?.message

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

    AppScaffold(
        showBackground = true
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Inscription",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Créez votre compte premium",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // User Type Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val selectedColor = MaterialTheme.colorScheme.primary
                        val unselectedColor = Color.Transparent
                        val selectedTextColor = Color.White
                        val unselectedTextColor = MaterialTheme.colorScheme.primary

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (!isLawyer) selectedColor else unselectedColor)
                                .clickable { isLawyer = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Client",
                                color = if (!isLawyer) selectedTextColor else unselectedTextColor,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isLawyer) selectedColor else unselectedColor)
                                .clickable { isLawyer = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Avocat",
                                color = if (isLawyer) selectedTextColor else unselectedTextColor,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    CustomInputField(
                        value = firstName,
                        onValueChange = { firstName = it; firstNameError = false },
                        placeholder = "Prénom",
                        leadingIcon = Icons.Default.Person,
                        isError = firstNameError,
                        errorMessage = "Veuillez saisir votre prénom"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomInputField(
                        value = lastName,
                        onValueChange = { lastName = it; lastNameError = false },
                        placeholder = "Nom",
                        leadingIcon = Icons.Default.Person,
                        isError = lastNameError,
                        errorMessage = "Veuillez saisir votre nom"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomInputField(
                        value = email,
                        onValueChange = { email = it; emailError = false },
                        placeholder = "E-mail",
                        leadingIcon = Icons.Default.Email,
                        isError = emailError,
                        errorMessage = "Veuillez saisir un e-mail valide",
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomInputField(
                        value = password,
                        onValueChange = { password = it; passwordError = false },
                        placeholder = "Mot de passe",
                        leadingIcon = Icons.Default.Lock,
                        isError = passwordError,
                        errorMessage = "Mot de passe (min 6 caractères)",
                        isPassword = true,
                        isPasswordVisible = isPasswordVisible,
                        onVisibilityToggle = { isPasswordVisible = !isPasswordVisible }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    authError?.let { err ->
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    LegalButton(
                        text = "S'inscrire",
                        onClick = {
                            firstNameError = firstName.isBlank()
                            lastNameError = lastName.isBlank()
                            emailError = email.isBlank() || !email.contains("@")
                            passwordError = password.length < 6
                            
                            if (firstNameError || lastNameError || emailError || passwordError) return@LegalButton
                            
                            authViewModel.registerNewUser(
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                password = password,
                                role = if (isLawyer) "lawyer" else "user"
                            )
                        },
                        enabled = !isLoading
                    )

                    if (isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.clickable { onNavigateBack() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Déjà un compte ? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            "Se connecter",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }
        }
    }
}
