package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

// ─── Story ────────────────────────────────────────────────────────────────────
// Matches: GET /api/stories
// [{"id":"story_001","lawyerName":"Me. Yassine Alaoui","lawyerAvatar":"...","imageUrl":"...","expiresAt":"..."}]
data class StoryDto(
    @SerializedName("id")           val id:           String = "",
    @SerializedName("lawyerName")   val lawyerName:   String = "",
    @SerializedName("lawyerAvatar") val lawyerAvatar: String = "",
    @SerializedName("imageUrl")     val imageUrl:     String = "",
    @SerializedName("expiresAt")    val expiresAt:    String = ""
)

// ─── Reel ─────────────────────────────────────────────────────────────────────
// Matches: GET /api/reels
// [{"id":"reel_001","videoUrl":"...","lawyerName":"Me. Karim Bennani","likes":542,"caption":"..."}]
data class ReelDto(
    @SerializedName("id")         val id:         String = "",
    @SerializedName("videoUrl")   val videoUrl:   String = "",
    @SerializedName("lawyerName") val lawyerName: String = "",
    @SerializedName("likes")      val likes:      Int    = 0,
    @SerializedName("caption")    val caption:    String = ""
)

// ─── Notification ─────────────────────────────────────────────────────────────
// Matches: GET /api/notifications
// [{"id":"notif_001","title":"...","description":"...","time":"2026-04-09T08:15:00Z","isRead":false}]
data class NotificationDto(
    @SerializedName("id")          val id:          String  = "",
    @SerializedName("title")       val title:       String  = "",
    @SerializedName("description") val description: String  = "",
    @SerializedName("time")        val time:        String  = "",
    @SerializedName("isRead")      val isRead:      Boolean = false
)

// ─── Live ─────────────────────────────────────────────────────────────────────
// Matches: GET /api/lives
// [{"id":"live_001","title":"Live: Droit de travail...","lawyerName":"...","viewersCount":124,"thumbnail":"..."}]
data class LiveDto(
    @SerializedName("id")           val id:           String = "",
    @SerializedName("title")        val title:        String = "",
    @SerializedName("lawyerName")   val lawyerName:   String = "",
    @SerializedName("viewersCount") val viewersCount: Int    = 0,
    @SerializedName("thumbnail")    val thumbnail:    String = ""
)
