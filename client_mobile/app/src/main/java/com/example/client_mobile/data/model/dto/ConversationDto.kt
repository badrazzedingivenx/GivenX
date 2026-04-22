package com.example.client_mobile.data.model.dto

import com.google.gson.annotations.SerializedName

// ─── Conversation participant (nested in real API response) ───────────────────
data class ConvoParticipant(
    @SerializedName("id")         val id:        String = "",
    @SerializedName("full_name")  val fullName:  String = "",
    @SerializedName("speciality") val speciality:String = "",
    @SerializedName("avatar_url") val avatarUrl: String = ""
)

// ─── Last message summary (nested in real API response) ──────────────────────
data class LastMessageSummaryDto(
    @SerializedName("content")   val content:  String = "",
    @SerializedName("sent_at")   val sentAt:   String = "",
    @SerializedName("sender_id") val senderId: String = ""
)

// ─── GET /api/conversations ───────────────────────────────────────────────────
// Supports both real API (nested lawyer/client objects) and legacy keys.
data class ConversationApiDto(
    @SerializedName("id")                  val id:              String               = "",
    // ── Real API fields ────────────────────────────────────────────────────────
    @SerializedName("lawyer")              val lawyer:          ConvoParticipant?    = null,
    @SerializedName("client")              val client:          ConvoParticipant?    = null,
    @SerializedName("last_message")        val lastMessageObj:  LastMessageSummaryDto? = null,
    @SerializedName("unread_count_user")   val unreadCountUser:   Int                = 0,
    @SerializedName("unread_count_lawyer") val unreadCountLawyer: Int                = 0,
    @SerializedName("created_at")          val createdAt:       String               = "",
    // ── Legacy flat fields (backward compat) ──────────────────────────────────
    @SerializedName("otherPartyName")      val otherPartyName:  String               = "",
    @SerializedName("lastMessage")         val lastMessage:     String               = "",
    @SerializedName("timestamp")           val timestamp:       String               = "",
    @SerializedName("unreadCount")         val unreadCount:     Int                  = 0,
    @SerializedName("avatarUrl")           val avatarUrl:       String               = ""
) {
    /** Display name for the other party. Chooses real API or flat fields. */
    fun displayName(isLawyer: Boolean): String = when {
        otherPartyName.isNotBlank() -> otherPartyName
        isLawyer                    -> client?.fullName ?: ""
        else                        -> lawyer?.fullName ?: ""
    }

    /** Avatar URL for the other party. */
    fun displayAvatar(isLawyer: Boolean): String = when {
        avatarUrl.isNotBlank() -> avatarUrl
        isLawyer               -> client?.avatarUrl ?: ""
        else                   -> lawyer?.avatarUrl ?: ""
    }

    /** Last message preview text. */
    fun displayLastMessage(): String = when {
        lastMessage.isNotBlank()     -> lastMessage
        else                         -> lastMessageObj?.content ?: ""
    }

    /** Timestamp string (ISO8601 or human-readable). */
    fun displayTimestamp(): String = when {
        timestamp.isNotBlank() -> timestamp
        else                   -> lastMessageObj?.sentAt?.take(10) ?: createdAt.take(10)
    }

    /** Unread count for the current user role. */
    fun displayUnreadCount(isLawyer: Boolean): Int = when {
        unreadCount > 0 -> unreadCount
        isLawyer        -> unreadCountLawyer
        else            -> unreadCountUser
    }
}

// ─── GET /api/conversations/{id}/messages ────────────────────────────────────
data class ChatMessageApiDto(
    // Real API fields
    @SerializedName("id")           val id:          String  = "",
    @SerializedName("content")      val content:     String  = "",
    @SerializedName("sender_id")    val senderId:    String  = "",
    @SerializedName("sender_name")  val senderName:  String  = "",
    @SerializedName("is_from_user") val isFromUser:  Boolean = true,
    @SerializedName("sent_at")      val sentAt:      String  = "",
    // Legacy flat fields
    @SerializedName("text")         val text:        String  = "",
    @SerializedName("time")         val time:        String  = ""
) {
    fun effectiveContent(): String  = content.ifBlank { text }
    fun effectiveTime():    String  = sentAt.ifBlank { time }
}
