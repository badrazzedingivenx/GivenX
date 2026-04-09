package com.example.client_mobile.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

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
                // Role is now persisted by AuthRepository from the server response.
                // Read it back from TokenManager so Success carries the server-confirmed role.
                _loginState.value = LoginUiState.Success(com.example.client_mobile.network.TokenManager.getUserType())
            } catch (e: Exception) {
                _loginState.value = LoginUiState.Error(
                    e.message ?: "Identifiants incorrects. Veuillez réessayer."
                )
            }
        }
    }

    fun logout() = AuthRepository.logout()

    fun resetState() {
        _loginState.value = LoginUiState.Idle
    }
}
