package com.example.client_mobile.screens.shared

import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─── Data Models ──────────────────────────────────────────────────────────────
data class ChatMessage(
    val id: String,
    val content: String,
    val senderName: String,
    val timestamp: String,
    val isFromUser: Boolean
)

data class Conversation(
    val id: String,
    val otherPartyName: String,
    val lastMessage: String = "",
    val timestamp: String = "",
    val unreadCount: Int = 0,
    val avatarUrl: String = ""
)

// ─── Conversation Repository ──────────────────────────────────────────────────
object ConversationRepository {

    val conversations = mutableStateListOf<Conversation>()

    // Separate message lists per conversation (observed directly in ChatScreen)
    private val messageMap = mutableMapOf<String, androidx.compose.runtime.snapshots.SnapshotStateList<ChatMessage>>()

    fun getMessages(conversationId: String): androidx.compose.runtime.snapshots.SnapshotStateList<ChatMessage> {
        return messageMap.getOrPut(conversationId) { mutableStateListOf() }
    }

    /** Find existing conversation or create a new one locally. */
    fun getOrCreate(
        lawyerId: String,
        lawyerName: String,
        clientName: String
    ): Conversation {
        val id = "${clientName.replace(" ", "_")}_$lawyerId"
        return conversations.find { it.id == id } ?: Conversation(
            id = id,
            otherPartyName = lawyerName
        ).also { conversations.add(it) }
    }

    fun sendUserMessage(conversationId: String, content: String, senderName: String) {
        val time = currentTime()
        getMessages(conversationId).add(
            ChatMessage(
                id = System.currentTimeMillis().toString(),
                content = content,
                senderName = senderName,
                timestamp = time,
                isFromUser = true
            )
        )
        updateMeta(conversationId, content, time)
    }

    fun sendLawyerMessage(conversationId: String, content: String, senderName: String) {
        val time = currentTime()
        getMessages(conversationId).add(
            ChatMessage(
                id = System.currentTimeMillis().toString(),
                content = content,
                senderName = senderName,
                timestamp = time,
                isFromUser = false
            )
        )
        updateMeta(conversationId, content, time)
    }

    fun markRead(conversationId: String) {
        val idx = conversations.indexOfFirst { it.id == conversationId }
        if (idx >= 0) conversations[idx] = conversations[idx].copy(unreadCount = 0)
    }

    /**
     * Merges API conversations into the list.
     * API items are placed at the front; any locally-created conversation whose id
     * is not yet in the API response is appended after.
     */
    fun replaceFromApi(items: List<Conversation>) {
        val apiIds = items.map { it.id }.toSet()
        val localOnly = conversations.filter { it.id !in apiIds }
        conversations.clear()
        conversations.addAll(items)
        conversations.addAll(localOnly)
    }

    /**
     * Replaces (or seeds) the message list for [conversationId] with [items].
     * Locally-sent messages that are not yet returning from the API are kept.
     */
    fun replaceMessagesFromApi(conversationId: String, items: List<ChatMessage>) {
        val existing = getMessages(conversationId)
        val apiIds   = items.map { it.id }.toSet()
        val localOnly = existing.filter { it.id !in apiIds }
        existing.clear()
        existing.addAll(items)
        existing.addAll(localOnly)
    }

    private fun updateMeta(conversationId: String, content: String, time: String) {
        val idx = conversations.indexOfFirst { it.id == conversationId }
        if (idx >= 0) {
            conversations[idx] = conversations[idx].copy(
                lastMessage = content,
                timestamp = time
            )
        }
    }

    private fun currentTime(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
}
