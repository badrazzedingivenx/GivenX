package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

// ─── GET /api/messages ────────────────────────────────────────────────────────
data class ConversationApiDto(
    @SerializedName("id")             val id:             String = "",
    @SerializedName("otherPartyName") val otherPartyName: String = "",
    @SerializedName("lastMessage")    val lastMessage:    String = "",
    @SerializedName("timestamp")      val timestamp:      String = "",
    @SerializedName("unreadCount")    val unreadCount:    Int    = 0,
    @SerializedName("avatarUrl")      val avatarUrl:      String = ""
)

// ─── GET /api/messages/{id} ───────────────────────────────────────────────────
data class ChatMessageApiDto(
    @SerializedName("senderId") val senderId: String = "",
    @SerializedName("text")     val text:     String = "",
    @SerializedName("time")     val time:     String = ""
)
