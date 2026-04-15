package com.example.client_mobile.screens.shared

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.MainRepository
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Fetches historical messages for [conversationId] from
 * GET /api/messages/{id} and seeds [ConversationRepository].
 *
 * Outgoing messages are sent via POST /api/messages/send through [MainRepository],
 * which inserts them optimistically into [ConversationRepository] before the
 * API round-trip completes.
 */
class ChatViewModel(private val conversationId: String) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun clearError() { _errorMessage.value = null }

    init {
        Log.d("ChatViewModel", "Initializing for conversationId: $conversationId")
        fetchMessages()
    }

    fun fetchMessages() {
        if (!TokenManager.isLoggedIn() || conversationId.isBlank()) {
            Log.d("ChatViewModel", "Fetch skipped: loggedIn=${TokenManager.isLoggedIn()}, id=$conversationId")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("ChatViewModel", "Fetching messages for $conversationId...")
            try {
                val response = RetrofitClient.haqApi.getChatDetails(conversationId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val messages = response.body()?.data?.mapIndexed { index, dto ->
                        ChatMessage(
                            id         = dto.id.ifBlank { "${dto.senderId}_${dto.effectiveTime()}_$index" },
                            content    = dto.effectiveContent(),
                            senderName = dto.senderName.ifBlank { dto.senderId },
                            timestamp  = dto.effectiveTime(),
                            isFromUser = dto.isFromUser
                        )
                    } ?: emptyList()
                    ConversationRepository.replaceMessagesFromApi(conversationId, messages)
                } else {
                    _errorMessage.value = "Impossible de charger les messages."
                }
            } catch (_: Exception) {
                _errorMessage.value = "Erreur réseau. Vérifiez votre connexion."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Sends [text] via POST /api/messages/send through [MainRepository].
     * The local conversation list is updated optimistically — no need to
     * call [ConversationRepository] directly from the UI.
     */
    fun send(text: String, senderName: String, isFromUser: Boolean) {
        val trimmed = text.trim()
        if (trimmed.isBlank() || conversationId.isBlank()) return
        viewModelScope.launch {
            MainRepository.sendMessage(
                conversationId = conversationId,
                content        = trimmed,
                senderName     = senderName,
                isFromUser     = isFromUser
            )
        }
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    class Factory(private val conversationId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ChatViewModel(conversationId) as T
    }
}
