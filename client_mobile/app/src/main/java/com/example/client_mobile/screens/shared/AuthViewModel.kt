package com.example.client_mobile.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.AuthRepository
import com.example.client_mobile.network.TokenManager
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
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            try {
                AuthRepository.login(email, password, userType)
                val fullName  = TokenManager.getFullName()
                val avatarUrl = TokenManager.getAvatarUrl()
                if (fullName.isNotBlank())  UserSession.name      = fullName
                if (avatarUrl.isNotBlank()) UserSession.avatarUrl = avatarUrl
                UserSession.email = TokenManager.getEmail() ?: ""
                _loginState.value = LoginUiState.Success(TokenManager.getUserType())
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

    // ── Register state ────────────────────────────────────────────────────────

    sealed class RegisterUiState {
        object Idle    : RegisterUiState()
        object Loading : RegisterUiState()
        data class Success(val userType: String) : RegisterUiState()
        data class Error(val message: String)    : RegisterUiState()
    }

    private val _registerState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val registerState: StateFlow<RegisterUiState> = _registerState

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
