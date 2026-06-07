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
// Matches: GET /api/stories  → {"success":true,"data":{"stories":[...]}}
//
// Breaking changes vs. old DTO:
//  - `media_url`   replaces `imageUrl`  (DB column name; absolute URL)
//  - `is_live`     replaces `isLive`    (DB column name)
//  - `time_left`   replaces `timeLeft`  (DB column name)
//  - `expires_at`  replaces `expiresAt` (DB column name)
//  - `lawyer_name` replaces `lawyerName` (DB column name; also resolved from nested `lawyer`)
//  - `lawyer` nested object carries the absolute `avatar_url`
data class StoryDto(
    @SerializedName("id")
    val id: String = "",

    // DB column: `lawyer_name` — also resolved from nested lawyer object
    @SerializedName(value = "lawyer_name", alternate = ["lawyerName"])
    val lawyerNameFlat: String = "",

    // DB column: `media_url` — absolute URL, pass directly to Coil
    @SerializedName(value = "media_url", alternate = ["mediaUrl", "imageUrl"])
    val mediaUrl: String = "",

    @SerializedName("caption")
    val caption: String? = null,

    // DB column: `expires_at`
    @SerializedName(value = "expires_at", alternate = ["expiresAt"])
    val expiresAt: String? = null,

    // DB column: `is_live` (tinyint → Boolean)
    @SerializedName(value = "is_live", alternate = ["isLive"])
    val isLive: Boolean = false,

    @SerializedName("hasUnseenStory")
    val hasUnseenStory: Boolean = false,

    @SerializedName("views")
    val views: Int = 0,

    // DB column: `time_left`
    @SerializedName(value = "time_left", alternate = ["timeLeft"])
    val timeLeft: String = "",

    @SerializedName(value = "is_liked", alternate = ["isLiked"])
    val isLiked: Boolean = false,

    @SerializedName(value = "likes_count", alternate = ["likesCount"])
    val likesCount: Int = 0,

    @SerializedName(value = "replies_count", alternate = ["repliesCount"])
    val repliesCount: Int = 0,

    // Nested lawyer object — avatar_url inside is an absolute URL
    @SerializedName("lawyer")
    val lawyer: LawyerDto? = null,

    @SerializedName("user")
    val user: HaqUserDto? = null
) {
    /** Resolved display name: nested lawyer → flat field → nested user → fallback */
    val authorName: String get() = lawyer?.name?.takeIf { it.isNotBlank() }
        ?: lawyerNameFlat.ifBlank { user?.fullName ?: "Avocat" }

    /** Resolved avatar URL (absolute): nested lawyer → nested user → empty */
    val authorAvatarUrl: String get() = lawyer?.avatarUrl?.takeIf { it.isNotBlank() }
        ?: user?.avatar ?: ""
}

// Body for POST /stories/{id}/reply
data class StoryReplyRequest(
    @SerializedName("message") val message: String
)

// Response from POST /stories/{id}/reply
data class StoryReplyResponseDto(
    @SerializedName(value = "conversation_id", alternate = ["conversationId"]) val conversationId: String = "",
    @SerializedName(value = "message_id",      alternate = ["messageId"])      val messageId:      String = ""
)

// A client who liked or replied to a story — returned by GET /stories/{id}/likes and /replies
data class StoryInteractorDto(
    @SerializedName("id")
    val id: String = "",
    @SerializedName(value = "full_name", alternate = ["fullName", "name"])
    val fullName: String = "",
    @SerializedName(value = "avatar_url", alternate = ["avatarUrl"])
    val avatarUrl: String? = null
)

// ─── Reel ─────────────────────────────────────────────────────────────────────
// Matches: GET /api/reels  → {"success":true,"data":{"reels":[...],"pagination":{...}}}
//
// Breaking changes vs. old DTO:
//  - `video_url`     replaces `videoUrl`   (DB column; absolute URL)
//  - `thumbnail_url` is now a first-class field (DB column; absolute URL)
//  - `likes_count`   replaces `likes`      (DB column)
//  - `views_count`   replaces `views`      (DB column)
//  - `lawyer_name`   replaces `lawyerName` (DB column; also resolved from nested `lawyer`)
//  - `domain`        added                 (DB column)
data class ReelDto(
    @SerializedName("id")
    val id: String = "",

    // DB column: `video_url` — absolute URL, pass directly to ExoPlayer
    @SerializedName(value = "video_url", alternate = ["videoUrl"])
    val videoUrl: String = "",

    // DB column: `thumbnail_url` — absolute URL, pass directly to Coil
    @SerializedName(value = "thumbnail_url", alternate = ["thumbnailUrl"])
    val thumbnailUrl: String? = null,

    // DB column: `lawyer_name`
    @SerializedName(value = "lawyer_name", alternate = ["lawyerName"])
    val lawyerNameFlat: String = "",

    // DB column: `likes_count`
    @SerializedName(value = "likes_count", alternate = ["likes", "likesCount"])
    val likesCount: Int = 0,

    @SerializedName("caption")
    val caption: String = "",

    @SerializedName("title")
    val title: String = "",

    // DB column: `views_count`
    @SerializedName(value = "views_count", alternate = ["views", "viewsCount"])
    val viewsCount: Int = 0,

    @SerializedName("duration")
    val duration: String = "",

    @SerializedName(value = "duration_sec", alternate = ["durationSec"])
    val durationSec: Int? = null,

    /** "up" | "down" | null */
    @SerializedName("trend")
    val trend: String? = null,

    @SerializedName("domain")
    val domain: String? = null,

    @SerializedName("status")
    val status: String? = null,

    // Nested lawyer object — avatar_url inside is an absolute URL
    @SerializedName("lawyer")
    val lawyer: LawyerDto? = null,

    @SerializedName("user")
    val user: HaqUserDto? = null
) {
    /** Resolved display name: nested lawyer → flat field → nested user → fallback */
    val authorName: String get() = lawyer?.name?.takeIf { it.isNotBlank() }
        ?: lawyerNameFlat.ifBlank { user?.fullName ?: "Avocat" }

    /** Resolved avatar URL (absolute): nested lawyer → nested user → empty */
    val authorAvatarUrl: String get() = lawyer?.avatarUrl?.takeIf { it.isNotBlank() }
        ?: user?.avatar ?: ""
}

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
// Matches: GET /api/lives  → live_sessions table
//
// Breaking changes vs. old DTO:
//  - `topic`          replaces `title`        (DB column)
//  - `viewer_count`   replaces `viewersCount`  (DB column)
//  - `thumbnail_url`  replaces `thumbnail`     (DB column; absolute URL)
//  - `lawyer_name`    replaces `lawyerName`    (DB column)
//  - Added: `description`, `domain`, `playback_url`, `stream_key`, `rtmp_url`,
//           `started_at`, `scheduled_at`, `duration_sec`, nested `lawyer`
data class LiveDto(
    @SerializedName("id")
    val id: String = "",

    // DB column: `topic`
    @SerializedName(value = "topic", alternate = ["title"])
    val topic: String = "",

    @SerializedName("description")
    val description: String? = null,

    // DB column: `lawyer_name`
    @SerializedName(value = "lawyer_name", alternate = ["lawyerName"])
    val lawyerNameFlat: String = "",

    // DB column: `viewer_count`
    @SerializedName(value = "viewer_count", alternate = ["viewersCount", "viewerCount"])
    val viewerCount: Int = 0,

    // DB column: `thumbnail_url` — absolute URL
    @SerializedName(value = "thumbnail_url", alternate = ["thumbnail", "thumbnailUrl"])
    val thumbnailUrl: String? = null,

    @SerializedName("participants")
    val participants: Int = 0,

    /** "live" | "scheduled" | "ended" */
    @SerializedName("status")
    val status: String = "",

    @SerializedName("domain")
    val domain: String? = null,

    // Streaming fields
    @SerializedName(value = "playback_url", alternate = ["playbackUrl"])
    val playbackUrl: String? = null,

    @SerializedName(value = "stream_url", alternate = ["streamUrl"])
    val streamUrl: String? = null,

    @SerializedName(value = "stream_key", alternate = ["streamKey"])
    val streamKey: String? = null,

    @SerializedName(value = "rtmp_url", alternate = ["rtmpUrl"])
    val rtmpUrl: String? = null,

    @SerializedName(value = "started_at", alternate = ["startedAt"])
    val startedAt: String? = null,

    @SerializedName(value = "scheduled_at", alternate = ["scheduledAt"])
    val scheduledAt: String? = null,

    @SerializedName(value = "duration_sec", alternate = ["durationSec"])
    val durationSec: Int? = null,

    // Nested lawyer object — avatar_url inside is an absolute URL
    @SerializedName("lawyer")
    val lawyer: LawyerDto? = null
) {
    /** Resolved display name: nested lawyer → flat field → fallback */
    val authorName: String get() = lawyer?.name?.takeIf { it.isNotBlank() }
        ?: lawyerNameFlat.ifBlank { "Avocat" }

    /** Resolved avatar URL (absolute): nested lawyer → empty */
    val authorAvatarUrl: String get() = lawyer?.avatarUrl ?: ""
}

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

/** Grouped stories for Instagram-style "one circle per author" UI */
data class UserStoriesGroup(
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val stories: List<StoryDto>
)

/** Groups a flat list of StoryDto by author identity (nested lawyer/user ids or name). */
fun List<StoryDto>.groupByAuthor(): List<UserStoriesGroup> =
    groupBy { story ->
        story.lawyer?.id ?: story.user?.id?.toString() ?: story.authorName
    }.map { (id, storiesList) ->
        val first = storiesList.first()
        UserStoriesGroup(
            authorId = id,
            authorName = first.authorName,
            authorAvatarUrl = first.authorAvatarUrl,
            stories = storiesList
        )
    }

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
