package com.example.client_mobile.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.AuthRepository
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.network.dto.LawyerProfileDto
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    // ── Navigation Events ─────────────────────────────────────────────────────

    sealed class AuthNavEvent {
        object NavigateToClientDashboard : AuthNavEvent()
        object NavigateToLawyerDashboard : AuthNavEvent()
    }

    private val _navEvent = MutableSharedFlow<AuthNavEvent>()
    val navEvent = _navEvent.asSharedFlow()

    private fun handleLoginSuccess(role: String) {
        viewModelScope.launch {
            val normalizedRole = role.lowercase().trim()
            if (normalizedRole == "lawyer" || normalizedRole == "avocat") {
                _navEvent.emit(AuthNavEvent.NavigateToLawyerDashboard)
            } else {
                _navEvent.emit(AuthNavEvent.NavigateToClientDashboard)
            }
        }
    }

    fun login(email: String, password: String) {
        if (_loginState.value is LoginUiState.Loading) return
        val lowerEmail = email.lowercase().trim()
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            try {
                AuthRepository.login(lowerEmail, password)
                
                // Get the actual role that was saved after being verified by the server
                val resolvedRole = TokenManager.getUserType() 
                
                // Sync sessions
                syncSessions()

                _loginState.value = LoginUiState.Success(resolvedRole)
                handleLoginSuccess(resolvedRole)
            } catch (e: Exception) {
                _loginState.value = LoginUiState.Error(
                    e.message ?: "Identifiants incorrects. Veuillez réessayer."
                )
            }
        }
    }

    private fun syncSessions() {
        val fullName  = TokenManager.getFullName()
        val avatarUrl = TokenManager.getAvatarUrl()
        val email     = TokenManager.getEmail() ?: ""
        val role      = TokenManager.getUserType()

        if (fullName.isNotBlank())  UserSession.name      = fullName
        if (avatarUrl.isNotBlank()) UserSession.avatarUrl = avatarUrl
        UserSession.email = email

        if (role == "lawyer") {
            LawyerSession.fullName = fullName
            LawyerSession.avatarUrl = avatarUrl
            LawyerSession.email = email
            val lawyerJson = TokenManager.getLawyerJson()
            if (lawyerJson != null) {
                try {
                    val dto = Gson().fromJson(lawyerJson, LawyerProfileDto::class.java)
                    LawyerSession.title = dto.speciality
                } catch (_: Exception) {}
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
                AuthRepository.register(
                    fullName = "$firstName $lastName".trim(),
                    email = email,
                    password = password,
                    role = role
                )
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
