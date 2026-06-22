package com.example.client_mobile.screens.shared

import android.util.Log
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
    val isFromUser: Boolean,
    val document: VaultDocument? = null
)

data class Conversation(
    val id: String,
    val otherPartyName: String,
    val lastMessage: String = "",
    val timestamp: String = "",
    val unreadCount: Int = 0,
    val avatarUrl: String = "",
    val lawyerId: String = "",
    val clientId: String = ""
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
        clientName: String,
        avatarUrl: String = ""
    ): Conversation {
        val id = "${clientName.replace(" ", "_")}_$lawyerId"
        // Prevent duplicate local conversations for the same lawyer
        return conversations.find { it.otherPartyName == lawyerName || it.id == id } ?: Conversation(
            id = id,
            otherPartyName = lawyerName,
            avatarUrl = avatarUrl,
            lawyerId = lawyerId,
            clientId = com.example.client_mobile.network.TokenManager.getUserIdInt().toString()
        ).also { conversations.add(it) }
    }

    fun removeConversation(conversationId: String) {
        conversations.removeAll { it.id == conversationId }
        messageMap.remove(conversationId)
    }

    fun sendUserMessage(conversationId: String, content: String, senderName: String, tempId: String? = null, document: VaultDocument? = null) {
        val time = currentTime()
        getMessages(conversationId).add(
            ChatMessage(
                id = tempId ?: System.currentTimeMillis().toString(),
                content = content,
                senderName = senderName,
                timestamp = time,
                isFromUser = true,
                document = document
            )
        )
        updateMeta(conversationId, content, time)
    }

    fun sendLawyerMessage(conversationId: String, content: String, senderName: String, tempId: String? = null, document: VaultDocument? = null) {
        val time = currentTime()
        getMessages(conversationId).add(
            ChatMessage(
                id = tempId ?: System.currentTimeMillis().toString(),
                content = content,
                senderName = senderName,
                timestamp = time,
                isFromUser = false,
                document = document
            )
        )
        updateMeta(conversationId, content, time)
    }

    fun replaceId(oldId: String, newId: String) {
        val idx = conversations.indexOfFirst { it.id == oldId }
        if (idx >= 0) {
            conversations[idx] = conversations[idx].copy(id = newId)
            messageMap[newId] = messageMap.remove(oldId) ?: mutableStateListOf()
        }
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
        val apiNames = items.map { it.otherPartyName }.toSet()
        val localOnly = conversations.filter { it.otherPartyName !in apiNames }
        
        val mergedItems = items.map { apiConv ->
            val localConv = conversations.find { it.otherPartyName == apiConv.otherPartyName || it.id == apiConv.id }
            if (localConv != null && localConv.timestamp > apiConv.timestamp && localConv.lastMessage.isNotBlank()) {
                // If local state has a newer message (e.g. just sent but not yet on server), merge it!
                apiConv.copy(lastMessage = localConv.lastMessage, timestamp = localConv.timestamp)
            } else {
                apiConv
            }
        }
        
        conversations.clear()
        conversations.addAll(mergedItems)
        conversations.addAll(localOnly)
    }

    /**
     * Replaces (or seeds) the message list for [conversationId] with [items].
     * Locally-sent messages that are not yet returning from the API are kept.
     */
    fun replaceMessagesFromApi(conversationId: String, items: List<ChatMessage>) {
        val list = getMessages(conversationId)
        
        // Rigorously deduplicate by ID, prioritizing server items over local ones
        val combined = (items + list).distinctBy { it.id }
        
        // Also distinct by content + time for optimistic dupes where the UUID didn't sync
        val fullyDeduplicated = combined.distinctBy { it.content + it.timestamp }
        
        list.clear()
        list.addAll(fullyDeduplicated)
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

    fun clear() {
        conversations.clear()
        messageMap.clear()
    }
}
