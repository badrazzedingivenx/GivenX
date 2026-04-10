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

    val darkGreen = Color(0xFF1B3124)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_app),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 40.dp),
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
                                Color.White.copy(alpha = 0.50f),
                                Color.White.copy(alpha = 0.65f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(40.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 30.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Inscription",
                        fontSize = 28.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = darkGreen
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // User Type Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(25.dp))
                            .background(Color.White.copy(alpha = 0.3f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val selectedColor = darkGreen
                        val unselectedColor = Color.Transparent
                        val selectedTextColor = Color.White
                        val unselectedTextColor = darkGreen

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (!isLawyer) selectedColor else unselectedColor)
                                .clickable { isLawyer = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Client",
                                color = if (!isLawyer) selectedTextColor else unselectedTextColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isLawyer) selectedColor else unselectedColor)
                                .clickable { isLawyer = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Avocat",
                                color = if (isLawyer) selectedTextColor else unselectedTextColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    CustomInputField(
                        value = firstName,
                        onValueChange = { firstName = it; firstNameError = false },
                        placeholder = "Prénom",
                        leadingIcon = Icons.Default.Person,
                        isError = firstNameError,
                        errorMessage = "Veuillez saisir votre prénom"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomInputField(
                        value = lastName,
                        onValueChange = { lastName = it; lastNameError = false },
                        placeholder = "Nom",
                        leadingIcon = Icons.Default.Person,
                        isError = lastNameError,
                        errorMessage = "Veuillez saisir votre nom"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomInputField(
                        value = email,
                        onValueChange = { email = it; emailError = false },
                        placeholder = "E-mail",
                        leadingIcon = Icons.Default.Email,
                        isError = emailError,
                        errorMessage = "Veuillez saisir un e-mail valide",
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomInputField(
                        value = password,
                        onValueChange = { password = it; passwordError = false },
                        placeholder = "Mot de passe",
                        leadingIcon = Icons.Default.Lock,
                        isError = passwordError,
                        errorMessage = "Mot de passe requis",
                        isPassword = true,
                        isPasswordVisible = isPasswordVisible,
                        onVisibilityToggle = { isPasswordVisible = !isPasswordVisible }
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    authError?.let { err ->
                        Text(
                            text = err,
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Button(
                        onClick = {
                            firstNameError = firstName.isBlank()
                            lastNameError = lastName.isBlank()
                            emailError = email.isBlank() || !email.contains("@")
                            passwordError = password.length < 6
                            
                            if (firstNameError || lastNameError || emailError || passwordError) return@Button
                            
                            authViewModel.registerNewUser(
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                password = password,
                                role = if (isLawyer) "lawyer" else "user"
                            )
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = darkGreen),
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("S'inscrire", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.clickable { onNavigateBack() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Déjà un compte ? ",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            "Se connecter",
                            fontSize = 13.sp,
                            color = darkGreen,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }
            }
        }
    }
}
