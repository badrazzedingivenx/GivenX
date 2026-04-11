package com.example.client_mobile.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.AuthRepository
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.network.dto.LawyerProfileDto
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    // ── Login state ───────────────────────────────────────────────────────────

    sealed class LoginUiState {
        object Idle    : LoginUiState()
        object Loading : LoginUiState()
        data class Success(val userType: String) : LoginUiState()
        data class Error(val message: String)    : LoginUiState()
    }

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState

    fun login(email: String, password: String, userType: String) {
        if (_loginState.value is LoginUiState.Loading) return
        val lowerEmail = email.lowercase().trim()
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            try {
                AuthRepository.login(lowerEmail, password, userType)
                val fullName  = TokenManager.getFullName()
                val avatarUrl = TokenManager.getAvatarUrl()
                if (fullName.isNotBlank())  UserSession.name      = fullName
                if (avatarUrl.isNotBlank()) UserSession.avatarUrl = avatarUrl
                UserSession.email = TokenManager.getEmail() ?: ""
                // "lawyer" → LawyerDashboard, "user"/"CLIENT" → ClientDashboard
                val resolvedRole = TokenManager.getUserType() // "lawyer" | "user"
                // Populate LawyerSession immediately from cache so screens that
                // read LawyerSession.fullName directly (e.g. LiveStudioScreen) work.
                if (resolvedRole == "lawyer") {
                    if (fullName.isNotBlank())  LawyerSession.fullName = fullName
                    if (avatarUrl.isNotBlank()) LawyerSession.avatarUrl = avatarUrl
                    LawyerSession.email = TokenManager.getEmail() ?: ""
                    val lawyerJson = TokenManager.getLawyerJson()
                    if (lawyerJson != null) {
                        try {
                            val dto = Gson().fromJson(lawyerJson, LawyerProfileDto::class.java)
                            if (dto.speciality.isNotBlank()) LawyerSession.title   = dto.speciality
                            if (dto.phone.isNotBlank())      LawyerSession.phone   = dto.phone
                            if (dto.address.isNotBlank())    LawyerSession.address = dto.address
                            if (dto.bio.isNotBlank())        LawyerSession.bio     = dto.bio
                        } catch (_: Exception) { /* malformed cache — ignore */ }
                    }
                }
                _loginState.value = LoginUiState.Success(resolvedRole)
            } catch (e: Exception) {
                _loginState.value = LoginUiState.Error(
                    e.message ?: "Identifiants incorrects. Veuillez réessayer."
                )
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginUiState.Idle
    }

    /**
     * Centralised post-login navigation.
     * Call this from any Composable's LaunchedEffect instead of duplicating
     * the role check everywhere.
     *
     * Usage:
     *   LaunchedEffect(loginState) {
     *       if (loginState is LoginUiState.Success) {
     *           authViewModel.proceedToDashboard(
     *               onLawyer = onNavigateToLawyerHome,
     *               onClient = onNavigateToUserHome
     *           )
     *       }
     *   }
     */
    fun proceedToDashboard(
        onLawyer: () -> Unit,
        onClient: () -> Unit
    ) {
        val role = TokenManager.getUserType()   // "lawyer" | "user"
        resetState()
        if (role == "lawyer") onLawyer() else onClient()
    }

    // ── Register state ────────────────────────────────────────────────────────

    sealed class RegisterUiState {
        object Idle    : RegisterUiState()
        object Loading : RegisterUiState()
        data class Success(val userType: String) : RegisterUiState()
        data class Error(val message: String)    : RegisterUiState()
    }

    private val _registerState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val registerState: StateFlow<RegisterUiState> = _registerState

    fun registerNewUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        role: String
    ) {
        if (_registerState.value is RegisterUiState.Loading) return
        viewModelScope.launch {
            _registerState.value = RegisterUiState.Loading
            try {
                AuthRepository.registerUser(firstName, lastName, email, password, role)
                val savedName = TokenManager.getFullName()
                if (savedName.isNotBlank()) UserSession.name = savedName
                UserSession.email = TokenManager.getEmail() ?: ""
                _registerState.value = RegisterUiState.Success(TokenManager.getUserType())
            } catch (e: Exception) {
                _registerState.value = RegisterUiState.Error(
                    e.message ?: "Erreur lors de l'inscription. Veuillez réessayer."
                )
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        phone: String = "",
        role: String = "user",
        speciality: String = ""
    ) {
        if (_registerState.value is RegisterUiState.Loading) return
        viewModelScope.launch {
            _registerState.value = RegisterUiState.Loading
            try {
                AuthRepository.register(fullName, email, password, phone, role, speciality)
                val savedName = TokenManager.getFullName()
                val avatarUrl = TokenManager.getAvatarUrl()
                if (savedName.isNotBlank()) UserSession.name      = savedName
                if (avatarUrl.isNotBlank()) UserSession.avatarUrl = avatarUrl
                UserSession.email = TokenManager.getEmail() ?: ""
                _registerState.value = RegisterUiState.Success(TokenManager.getUserType())
            } catch (e: Exception) {
                _registerState.value = RegisterUiState.Error(
                    e.message ?: "Erreur lors de l'inscription. Veuillez réessayer."
                )
            }
        }
    }

    fun resetRegisterState() {
        _registerState.value = RegisterUiState.Idle
    }

    fun logout() = AuthRepository.logout()
}
