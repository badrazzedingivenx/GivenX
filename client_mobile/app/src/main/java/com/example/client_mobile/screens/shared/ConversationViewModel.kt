package com.example.client_mobile.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConversationViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun clearError() {
        _errorMessage.value = null
        _isError.value = false
    }

    init { fetch() }

    /** Pull-to-refresh trigger. */
    fun refresh() {
        _isRefreshing.value = true
        fetch(isRefresh = true)
    }

    fun fetch(isRefresh: Boolean = false) {
        if (!TokenManager.isLoggedIn()) return
        if (!isRefresh) _isLoading.value = true
        _isError.value = false
        val isLawyer = TokenManager.getUserType() == "lawyer"
        viewModelScope.launch {
            try {
                // Try HaqApiService first, then fallback to mockApi
                val haqResponse = RetrofitClient.haqApi.getMessages()
                val dtos = if (haqResponse.isSuccessful && haqResponse.body()?.success == true) {
                    haqResponse.body()?.data ?: emptyList()
                } else {
                    val mockResponse = RetrofitClient.mockApi.getMessages()
                    if (mockResponse.isSuccessful) {
                        mockResponse.body() ?: emptyList()
                    } else {
                        null
                    }
                }

                if (dtos != null) {
                    ConversationRepository.replaceFromApi(
                        dtos.map { dto ->
                            Conversation(
                                id             = dto.id,
                                otherPartyName = dto.displayName(isLawyer),
                                lastMessage    = dto.displayLastMessage(),
                                timestamp      = dto.displayTimestamp(),
                                unreadCount    = dto.displayUnreadCount(isLawyer),
                                avatarUrl      = dto.displayAvatar(isLawyer)
                            )
                        }
                    )
                }
                // dtos == null means the API responded but had no usable data (e.g. 404).
                // Keep whatever is already in the repository and show an empty list — not
                // a "no connection" error, because we did reach the server.
            } catch (_: Exception) {
                // Genuine network failure (no internet, timeout, etc.)
                _isError.value = ConversationRepository.conversations.isEmpty()
                _errorMessage.value = "Erreur réseau. Vérifiez votre connexion."
            } finally {
                _isLoading.value = false
                _isRefreshing.value = false
            }
        }
    }
}
