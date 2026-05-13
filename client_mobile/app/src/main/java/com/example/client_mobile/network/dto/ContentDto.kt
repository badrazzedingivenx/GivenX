package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

// ─── Consultation ─────────────────────────────────────────────────────────────
data class SaveConsultationRequest(
    @SerializedName("user_id")          val userId:          String = "",
    @SerializedName("lawyer_id")        val lawyerId:        String = "",
    @SerializedName("lawyer_name")      val lawyerName:      String = "",
    @SerializedName("lawyer_specialty") val lawyerSpecialty: String = ""
)

// ─── Story ────────────────────────────────────────────────────────────────────
// Matches: GET /api/stories
// Response shape: {"success":true,"data":{"stories":[...]}}
// [{"id":"story_001","lawyerName":"Me. Yassine Alaoui","lawyerAvatar":"...","imageUrl":"...","expiresAt":"..."}]
data class StoryDto(
    @SerializedName("id")             val id:             String  = "",
    @SerializedName("lawyerName")     val lawyerName:     String  = "",
    @SerializedName("lawyerAvatar")   val lawyerAvatar:   String  = "",
    @SerializedName("imageUrl")       val imageUrl:       String  = "",
    @SerializedName("expiresAt")      val expiresAt:      String  = "",
    @SerializedName("isLive")         val isLive:         Boolean = false,
    @SerializedName("hasUnseenStory") val hasUnseenStory: Boolean = false,
    // Analytics fields (defaults apply if absent)
    @SerializedName("views")          val views:          Int     = 0,
    @SerializedName("timeLeft")       val timeLeft:       String  = ""
)

// ─── Reel ─────────────────────────────────────────────────────────────────────
// Matches: GET /api/reels
// [{"id":"reel_001","videoUrl":"...","lawyerName":"Me. Karim Bennani","likes":542,"caption":"..."}]
data class ReelDto(
    @SerializedName("id")         val id:         String = "",
    @SerializedName("videoUrl")   val videoUrl:   String = "",
    @SerializedName("lawyerName") val lawyerName: String = "",
    @SerializedName("likes")      val likes:      Int    = 0,
    @SerializedName("caption")    val caption:    String = "",
    // Analytics fields (defaults apply if absent)
    @SerializedName("title")      val title:      String = "",
    @SerializedName("views")      val views:      Int    = 0,
    @SerializedName("duration")   val duration:   String = "",
    /** "up" | "down" | "" */
    @SerializedName("trend")      val trend:      String = ""
)

// ─── Like response ────────────────────────────────────────────────────────────
// Matches: POST /api/reels/{id}/like → { "is_liked": true, "likes_count": 543 }
data class LikeResponseDto(
    @SerializedName("is_liked")    val isLiked:    Boolean = false,
    @SerializedName("likes_count") val likesCount: Int     = 0
)

// ─── Send Message ─────────────────────────────────────────────────────────────
// Body for POST /api/messages/send
data class SendMessageRequest(
    @SerializedName("conversationId") val conversationId: String,
    @SerializedName("content")        val content:        String,
    @SerializedName("type")           val type:           String = "text"
)

// Response from POST /api/messages/send
data class SendMessageResponseDto(
    @SerializedName("id")         val id:         String = "",
    @SerializedName("senderId")   val senderId:   String = "",
    @SerializedName("text")       val text:       String = "",
    @SerializedName("time")       val time:       String = "",
    @SerializedName("success")    val success:    Boolean = true
)

// ─── Search result entries ────────────────────────────────────────────────────
// Used by GET /api/lawyers?search=... and future unified-search endpoint
data class LawyerSearchResultDto(
    @SerializedName("id")         val id:         String = "",
    @SerializedName("name")       val name:       String = "",
    @SerializedName("specialty")  val specialty:  String = "",
    @SerializedName("avatarUrl")  val avatarUrl:  String = "",
    @SerializedName("rating")     val rating:     Float  = 0f,
    @SerializedName("domaine")    val domaine:    String = ""
)

// ─── Notification ─────────────────────────────────────────────────────────────
// Matches: GET /api/notifications
// [{"id":"notif_001","title":"...","description":"...","time":"2026-04-09T08:15:00Z","isRead":false}]
data class NotificationDto(
    @SerializedName("id")          val id:          String  = "",
    @SerializedName("title")       val title:       String  = "",
    @SerializedName("description") val description: String  = "",
    @SerializedName("time")        val time:        String  = "",
    @SerializedName("isRead")      val isRead:      Boolean = false,
    @SerializedName("type")        val type:        String  = "CASE_UPDATE"
)

// ─── Live ─────────────────────────────────────────────────────────────────────
// Matches: GET /api/lives
// [{"id":"live_001","title":"Live: Droit de travail...","lawyerName":"...","viewersCount":124,"thumbnail":"..."}]
data class LiveDto(
    @SerializedName("id")           val id:           String = "",
    @SerializedName("title")        val title:        String = "",
    @SerializedName("lawyerName")   val lawyerName:   String = "",
    @SerializedName("viewersCount") val viewersCount: Int    = 0,
    @SerializedName("thumbnail")    val thumbnail:    String = "",
    // Analytics fields (defaults apply if absent)
    @SerializedName("participants") val participants: Int    = 0,
    /** "LIVE" | "Scheduled" | "" */
    @SerializedName("status")       val status:       String = ""
)

// ─── Legal Feed Post ─────────────────────────────────────────────────────────
// Matches: GET /api/legal-feed
// [{"lawyer_id":"1","lawyer_name":"John Doe","avatar_url":"...","post_image_url":"...","legal_text":"...","date":"..." }]
data class LegalPostDto(
    @SerializedName("lawyer_id")      val lawyerId:     String  = "",
    @SerializedName("lawyer_name")    val lawyerName:   String  = "",
    @SerializedName("avatar_url")     val avatarUrl:    String  = "",
    @SerializedName("post_image_url") val postImageUrl: String  = "",
    @SerializedName("legal_text")     val legalText:    String  = "",
    @SerializedName("date")           val date:         String  = "",
    @SerializedName("likes_count")    val likesCount:   Int     = 0,
    @SerializedName("is_liked")       val isLiked:      Boolean = false,
    @SerializedName("is_verified")    val isVerified:   Boolean = true,
    @SerializedName("comments_count") val commentsCount: Int    = 0
)

// ─── Paginated response wrappers ──────────────────────────────────────────────
// The Hostinger API wraps lists inside a nested object within "data".
// These wrappers match the exact server shapes so Gson can deserialise them.

/** GET /stories → {"success":true,"data":{"stories":[...]}} */
data class StoriesResponseDto(
    @SerializedName("stories") val stories: List<StoryDto> = emptyList()
)

/** GET /reels → {"success":true,"data":{"reels":[...],"pagination":{...}}} */
data class ReelsResponseDto(
    @SerializedName("reels")      val reels:      List<ReelDto>     = emptyList(),
    @SerializedName("pagination") val pagination: PaginationMeta?   = null
)

/** GET /lives → {"success":true,"data":{"lives":[...],"pagination":{...}}} */
data class LivesResponseDto(
    @SerializedName("lives")      val lives:      List<LiveDto>     = emptyList(),
    @SerializedName("pagination") val pagination: PaginationMeta?   = null
)
