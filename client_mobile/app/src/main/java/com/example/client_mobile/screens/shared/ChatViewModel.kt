package com.example.client_mobile.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import kotlinx.coroutines.launch

/**
 * Fetches historical messages for [conversationId] from
 * GET /api/messages/{id} and seeds [ConversationRepository].
 * Local messages sent in the current session that have not yet returned from
 * the API are preserved (see [ConversationRepository.replaceMessagesFromApi]).
 */
class ChatViewModel(private val conversationId: String) : ViewModel() {

    init {
        fetchMessages()
    }

    fun fetchMessages() {
        if (!TokenManager.isLoggedIn() || conversationId.isBlank()) return
        viewModelScope.launch {
            try {
                val response = RetrofitClient.mockApi.getChatDetails(conversationId)
                if (response.isSuccessful) {
                    val messages = response.body()?.mapIndexed { index, dto ->
                        ChatMessage(
                            id         = "${dto.senderId}_${dto.time}_$index",
                            content    = dto.text,
                            senderName = dto.senderId,
                            timestamp  = dto.time,
                            isFromUser = dto.senderId.startsWith("user")
                        )
                    } ?: emptyList()
                    ConversationRepository.replaceMessagesFromApi(conversationId, messages)
                }
            } catch (_: Exception) {
                // Keep existing in-memory messages on failure
            }
        }
    }

    // ── Factory ───────────────────────────────────────────────────────────────

    class Factory(private val conversationId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ChatViewModel(conversationId) as T
    }
}
