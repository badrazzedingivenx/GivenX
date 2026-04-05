package com.example.client_mobile.Screens

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
    val lawyerId: String,
    val lawyerName: String,
    val lawyerSpecialty: String,
    val clientName: String,
    val lastMessage: String = "",
    val lastTimestamp: String = "",
    val unreadCountForLawyer: Int = 0,
    val unreadCountForUser: Int = 0
)

// ─── Conversation Repository ──────────────────────────────────────────────────
object ConversationRepository {

    val conversations = mutableStateListOf<Conversation>()

    // Separate message lists per conversation (observed directly in ChatScreen)
    private val messageMap = mutableMapOf<String, androidx.compose.runtime.snapshots.SnapshotStateList<ChatMessage>>()

    fun getMessages(conversationId: String): androidx.compose.runtime.snapshots.SnapshotStateList<ChatMessage> {
        return messageMap.getOrPut(conversationId) { mutableStateListOf() }
    }

    /** Find existing conversation or create a new one. */
    fun getOrCreate(
        lawyerId: String,
        lawyerName: String,
        lawyerSpecialty: String,
        clientName: String
    ): Conversation {
        val id = "${clientName.replace(" ", "_")}_$lawyerId"
        return conversations.find { it.id == id } ?: Conversation(
            id = id,
            lawyerId = lawyerId,
            lawyerName = lawyerName,
            lawyerSpecialty = lawyerSpecialty,
            clientName = clientName
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
        updateMeta(conversationId, content, time, incrementLawyerUnread = true)
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
        updateMeta(conversationId, content, time, incrementLawyerUnread = false)
    }

    fun markReadByLawyer(conversationId: String) {
        val idx = conversations.indexOfFirst { it.id == conversationId }
        if (idx >= 0) conversations[idx] = conversations[idx].copy(unreadCountForLawyer = 0)
    }

    fun markReadByUser(conversationId: String) {
        val idx = conversations.indexOfFirst { it.id == conversationId }
        if (idx >= 0) conversations[idx] = conversations[idx].copy(unreadCountForUser = 0)
    }

    private fun updateMeta(
        conversationId: String,
        content: String,
        time: String,
        incrementLawyerUnread: Boolean
    ) {
        val idx = conversations.indexOfFirst { it.id == conversationId }
        if (idx >= 0) {
            val old = conversations[idx]
            conversations[idx] = old.copy(
                lastMessage = content,
                lastTimestamp = time,
                unreadCountForLawyer = if (incrementLawyerUnread) old.unreadCountForLawyer + 1 else old.unreadCountForLawyer,
                unreadCountForUser = if (!incrementLawyerUnread) old.unreadCountForUser + 1 else old.unreadCountForUser
            )
        }
    }

    private fun currentTime(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
}
