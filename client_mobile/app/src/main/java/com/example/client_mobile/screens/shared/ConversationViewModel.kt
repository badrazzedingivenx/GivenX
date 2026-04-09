package com.example.client_mobile.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Fetches the authenticated user's conversation list from GET /api/messages
 * and merges the results into [ConversationRepository], which is observed directly
 * by [MessagesInboxScreen].
 *
 * The ViewModel is scoped to the screen or the NavGraph host — Compose will
 * return the same instance across recompositions via `viewModel()`.
 */
class ConversationViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetch()
    }

    fun fetch() {
        if (!TokenManager.isLoggedIn()) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.mockApi.getMessages()
                if (response.isSuccessful) {
                    val dtos = response.body() ?: emptyList()
                    ConversationRepository.replaceFromApi(
                        dtos.map { dto ->
                            Conversation(
                                id             = dto.id,
                                otherPartyName = dto.otherPartyName,
                                lastMessage    = dto.lastMessage,
                                timestamp      = dto.timestamp,
                                unreadCount    = dto.unreadCount,
                                avatarUrl      = dto.avatarUrl
                            )
                        }
                    )
                }
            } catch (_: Exception) {
                // Network unavailable — keep any locally-created conversations
            } finally {
                _isLoading.value = false
            }
        }
    }
}
