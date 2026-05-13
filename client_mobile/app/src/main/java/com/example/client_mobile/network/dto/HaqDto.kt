package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

// =============================================================================
//  HAQ API — Data Transfer Objects
//  Base URL : https://lavender-spoonbill-389199.hostingersite.com/api/v1/
//  All field names match exactly the JSON keys in HAQ_API_Guide.
// =============================================================================


// ── Shared ────────────────────────────────────────────────────────────────────

/**
 * Compact user object embedded inside session, message, comment responses.
 * Also returned as `user` inside the login response.
 */
data class HaqUserDto(
    @SerializedName("id")        val id:       Int    = 0,
    @SerializedName("full_name") val fullName: String = "",
    @SerializedName("email")     val email:    String = "",
    @SerializedName("role")      val role:     String = "", // "CLIENT" | "LAWYER" | "ADMIN"
    @SerializedName("status")    val status:   String = "",
    @SerializedName("avatar")    val avatar:   String? = null
)


// =============================================================================
//  AUTH  (/auth/*)
// =============================================================================

/** POST /auth/login */
data class HaqLoginRequest(
    @SerializedName("email")    val email:    String,
    @SerializedName("password") val password: String
)

/**
 * POST /auth/register
 * Single endpoint for both CLIENT and LAWYER — role is passed as a field.
 * phone is optional per spec.
 */
data class HaqRegisterRequest(
    @SerializedName("full_name")             val fullName:             String,
    @SerializedName("email")                 val email:                String,
    @SerializedName("password")              val password:             String,
    @SerializedName("password_confirmation") val passwordConfirmation: String,
    @SerializedName("role")                  val role:                 String, // "CLIENT" | "LAWYER"
    @SerializedName("phone")                 val phone:                String? = null
)

/**
 * `data` object returned by POST /auth/login, /auth/register, POST /auth/refresh.
 *
 * Server may return the token as "access_token" or "token" — both are handled.
 * The user object may be keyed as "user" or "profile" — both are handled.
 */
data class HaqAuthData(
    @SerializedName(value = "access_token", alternate = ["token", "accessToken"])
    val accessToken: String      = "",
    @SerializedName("token_type")   val tokenType:   String      = "Bearer",
    @SerializedName("expires_in")   val expiresIn:   Int         = 3600,
    @SerializedName(value = "user", alternate = ["profile", "data"])
    val user:        HaqUserDto? = null
)


// =============================================================================
//  PROFILE  (/profile  &  /auth/me)
// =============================================================================

/**
 * Full profile returned by GET /auth/me and PUT /profile.
 * Phone, bio, address, avatar are optional on the server side.
 */
data class HaqProfileData(
    @SerializedName("id")         val id:        Int     = 0,
    @SerializedName("full_name")  val fullName:  String  = "",
    @SerializedName("email")      val email:     String  = "",
    @SerializedName("role")       val role:      String  = "",
    @SerializedName("status")     val status:    String  = "",
    @SerializedName("phone")      val phone:     String? = null,
    @SerializedName("bio")        val bio:       String? = null,
    @SerializedName("address")    val address:   String? = null,
    @SerializedName("avatar")     val avatar:    String? = null,
    @SerializedName("created_at") val createdAt: String  = ""
)

/** Request body for PUT /profile — all fields are optional */
data class HaqUpdateProfileRequest(
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("phone")     val phone:    String? = null,
    @SerializedName("bio")       val bio:      String? = null,
    @SerializedName("address")   val address:  String? = null,
    @SerializedName("avatar")    val avatar:   String? = null
)


// =============================================================================
//  LAWYERS  (/lawyers/*)
// =============================================================================

/** Single lawyer item returned in the GET /lawyers list. */
data class HaqLawyerPublicDto(
    @SerializedName("id")        val id:        Int     = 0,
    @SerializedName("full_name") val fullName:  String  = "",
    @SerializedName("domain")    val domain:    String  = "",
    @SerializedName("avatar")    val avatar:    String? = null,
    @SerializedName("available") val available: Boolean = true,
    @SerializedName("rating")    val rating:    Float   = 0f,
    @SerializedName("bio")       val bio:       String  = ""
)

/** Full public profile returned by GET /lawyers/{lawyer_id}. */
data class HaqLawyerDetailDto(
    @SerializedName("id")                  val id:                 Int     = 0,
    @SerializedName("full_name")           val fullName:           String  = "",
    @SerializedName("domain")              val domain:             String  = "",
    @SerializedName("avatar")              val avatar:             String? = null,
    @SerializedName("available")           val available:          Boolean = true,
    @SerializedName("rating")              val rating:             Float   = 0f,
    @SerializedName("bio")                 val bio:                String  = "",
    @SerializedName("phone")               val phone:              String  = "",
    @SerializedName("address")             val address:            String  = "",
    @SerializedName("consultations_count") val consultationsCount: Int     = 0,
    @SerializedName("reviews_count")       val reviewsCount:       Int     = 0,
    @SerializedName("created_at")          val createdAt:          String  = ""
)


// =============================================================================
//  LIVE SESSIONS  (/live-sessions/*)
// =============================================================================

/** Request body for POST /live-sessions — LAWYER role only. */
data class HaqCreateLiveSessionRequest(
    @SerializedName("topic")       val topic:       String,
    @SerializedName("description") val description: String,
    @SerializedName("domain")      val domain:      String
)

/**
 * Session object returned by GET /live-sessions and GET /live-sessions/{id}.
 * When created (POST), streamKey, rtmpUrl, and playbackUrl are populated.
 */
data class HaqLiveSessionDto(
    @SerializedName("session_id")   val sessionId:   String      = "",
    @SerializedName("topic")        val topic:       String      = "",
    @SerializedName("description")  val description: String      = "",
    @SerializedName("domain")       val domain:      String      = "",
    @SerializedName("status")       val status:      String      = "", // "live" | "scheduled" | "ended"
    @SerializedName("stream_key")   val streamKey:   String      = "",
    @SerializedName("rtmp_url")     val rtmpUrl:     String      = "",
    @SerializedName("playback_url") val playbackUrl: String      = "",
    @SerializedName("lawyer")       val lawyer:      HaqUserDto? = null,
    @SerializedName("viewer_count") val viewerCount: Int         = 0,
    @SerializedName("created_at")   val createdAt:   String      = ""
)

/** POST /live-sessions/{session_id}/comments body. */
data class HaqLiveCommentRequest(
    @SerializedName("content") val content: String
)

/** Single comment on a live session. */
data class HaqLiveCommentDto(
    @SerializedName("id")         val id:        Int         = 0,
    @SerializedName("content")    val content:   String      = "",
    @SerializedName("user")       val user:      HaqUserDto? = null,
    @SerializedName("created_at") val createdAt: String      = ""
)


// =============================================================================
//  CONSULTATIONS  (/consultations/*)
// =============================================================================

/**
 * POST /consultations body — CLIENT role only.
 * type: "video" | "audio" | "chat"
 */
data class HaqCreateConsultationRequest(
    @SerializedName("lawyer_id")        val lawyerId:        Int,
    @SerializedName("date")             val date:            String,
    @SerializedName("time")             val time:            String,
    @SerializedName("type")             val type:            String,
    @SerializedName("subject")          val subject:         String,
    @SerializedName("duration_minutes") val durationMinutes: Int
)

/** Consultation item returned by GET /consultations. */
data class HaqConsultationDto(
    @SerializedName("id")               val id:              Int                 = 0,
    @SerializedName("lawyer_id")        val lawyerId:        Int                 = 0,
    @SerializedName("date")             val date:            String              = "",
    @SerializedName("time")             val time:            String              = "",
    @SerializedName("type")             val type:            String              = "",
    @SerializedName("subject")          val subject:         String              = "",
    @SerializedName("duration_minutes") val durationMinutes: Int                 = 0,
    @SerializedName("status")           val status:          String              = "",
    @SerializedName("lawyer")           val lawyer:          HaqLawyerPublicDto? = null,
    @SerializedName("created_at")       val createdAt:       String              = ""
)


// =============================================================================
//  CONVERSATIONS & MESSAGES  (/conversations/*)
// =============================================================================

/** POST /conversations body. */
data class HaqCreateConversationRequest(
    @SerializedName("recipient_id") val recipientId: Int
)

/** Single conversation item in GET /conversations. */
data class HaqConversationDto(
    @SerializedName("id")           val id:          Int            = 0,
    @SerializedName("participant")  val participant: HaqUserDto?    = null,
    @SerializedName("last_message") val lastMessage: HaqMessageDto? = null,
    @SerializedName("unread_count") val unreadCount: Int            = 0,
    @SerializedName("updated_at")   val updatedAt:   String         = ""
)

/** POST /conversations/{id}/messages body. */
data class HaqSendMessageRequest(
    @SerializedName("content") val content: String,
    @SerializedName("type")    val type:    String = "text"
)

/** Single message in a conversation. */
data class HaqMessageDto(
    @SerializedName("id")         val id:        Int         = 0,
    @SerializedName("content")    val content:   String      = "",
    @SerializedName("type")       val type:      String      = "text",
    @SerializedName("sender")     val sender:    HaqUserDto? = null,
    @SerializedName("is_mine")    val isMine:    Boolean     = false,
    @SerializedName("created_at") val createdAt: String      = ""
)


// =============================================================================
//  PAYMENTS  (/payments/*)
// =============================================================================

/**
 * POST /payments body.
 * method: "card" | "paypal"
 */
data class HaqCreatePaymentRequest(
    @SerializedName("consultation_id") val consultationId: Int,
    @SerializedName("method")          val method:         String
)

/** Payment item returned by GET /payments and GET /payments/{id}. */
data class HaqPaymentDto(
    @SerializedName("id")              val id:             Int    = 0,
    @SerializedName("consultation_id") val consultationId: Int    = 0,
    @SerializedName("method")          val method:         String = "",
    @SerializedName("amount")          val amount:         Float  = 0f,
    @SerializedName("status")          val status:         String = "", // "pending" | "completed" | "failed"
    @SerializedName("created_at")      val createdAt:      String = ""
)
