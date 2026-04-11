package com.example.client_mobile.network

import com.example.client_mobile.network.dto.AppointmentDto
import com.example.client_mobile.network.dto.BillingSummaryDto
import com.example.client_mobile.network.dto.ChatMessageApiDto
import com.example.client_mobile.network.dto.ConversationApiDto
import com.example.client_mobile.network.dto.DocumentApiDto
import com.example.client_mobile.network.dto.DossierDto
import com.example.client_mobile.network.dto.LawyerDto
import com.example.client_mobile.network.dto.LawyerProfileDto
import com.example.client_mobile.network.dto.LawyerStatsDto
import com.example.client_mobile.network.dto.LikeResponseDto
import com.example.client_mobile.network.dto.LiveDto
import com.example.client_mobile.network.dto.NotificationDto
import com.example.client_mobile.network.dto.RecentConsultationDto
import com.example.client_mobile.network.dto.ReelDto
import com.example.client_mobile.network.dto.SendMessageRequest
import com.example.client_mobile.network.dto.SendMessageResponseDto
import com.example.client_mobile.network.dto.StoryDto
import com.example.client_mobile.network.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface whose return types match the Postman Mock Server exactly.
 *
 * The mock server returns raw JSON with no ApiResponse wrapper, and field names
 * are camelCase (matching the @SerializedName values in each DTO).
 *
 * Enabled via RetrofitClient.mockApi when USE_MOCK_SERVER = true.
 *
 * Endpoints (all are GET, no auth needed on the public mock):
 *   GET /api/auth/me           → UserDto
 *   GET /api/appointments/me   → List<AppointmentDto>
 *   GET /api/billing/me        → BillingSummaryDto
 *   GET /api/documents/me      → List<DocumentApiDto>
 */
interface MockApiService {

    // ── User ──────────────────────────────────────────────────────────────────

    /** GET /api/users/me — { "id": 1, "firstName": "Tarik", ... } */
    @GET("api/users/me")
    suspend fun getMe(): Response<UserDto>

    // ── Appointments ──────────────────────────────────────────────────────────

    /** GET /api/appointments/me — [ { "id": "appt_001", "lawyerName": "...", "date": "...", "time": "..." } ] */
    @GET("api/appointments/me")
    suspend fun getAppointments(): Response<List<AppointmentDto>>

    // ── Billing ───────────────────────────────────────────────────────────────

    /** GET /api/billing/me — { "paidAmount": 1240.5, "pendingAmount": 315.75 } */
    @GET("api/billing/me")
    suspend fun getBilling(): Response<BillingSummaryDto>

    // ── Documents ─────────────────────────────────────────────────────────────

    /** GET /api/documents/me — [ { "id": "doc_001", "title": "...", "uploadDate": "..." } ] */
    @GET("api/documents/me")
    suspend fun getDocuments(): Response<List<DocumentApiDto>>

    // ── Dossiers ──────────────────────────────────────────────────────────────

    /** GET /api/dossiers/me — [ { "id": "...", "caseNumber": "...", ... } ] */
    @GET("api/dossiers/me")
    suspend fun getDossiers(): Response<List<DossierDto>>

    // ── Lawyers ───────────────────────────────────────────────────────────────

    /** GET /api/lawyers — [ { "id": "...", "name": "...", "specialty": "...", ... } ] */
    @GET("api/lawyers")
    suspend fun getLawyers(@Query("limit") limit: Int? = null): Response<List<LawyerDto>>

    // ── Stories ────────────────────────────────────────────────────

    /** GET /api/stories — [ { "id": "story_001", "lawyerName": "...", "imageUrl": "..." } ] */
    @GET("api/stories")
    suspend fun getStories(): Response<List<StoryDto>>

    // ── Reels ──────────────────────────────────────────────────────

    /** GET /api/reels — [ { "id": "reel_001", "lawyerName": "...", "likes": 542, "caption": "..." } ] */
    @GET("api/reels")
    suspend fun getReels(): Response<List<ReelDto>>

    // ── Lives ──────────────────────────────────────────────────────

    /** GET /api/lives — [ { "id": "live_001", "title": "...", "viewersCount": 124 } ] */
    @GET("api/live-sessions")
    suspend fun getLives(): Response<List<LiveDto>>

    // ── Notifications ──────────────────────────────────────────────

    /** GET /api/notifications — [ { "id": "notif_001", "title": "...", "isRead": false } ] */
    @GET("api/notifications")
    suspend fun getNotifications(): Response<List<NotificationDto>>

    // ── Lawyer (authenticated, Role: LAWYER) ──────────────────────────────────

    /** GET /api/lawyers/me — authenticated lawyer's full profile */
    @GET("api/lawyers/me")
    suspend fun getLawyerProfile(): Response<LawyerProfileDto>

    /**
     * GET /api/lawyers/me/stats — dashboard KPIs:
     * total_clients, audiences_today, total_revenue_month, new_requests, closed_cases
     */
    @GET("api/lawyers/me/stats")
    suspend fun getLawyerStats(): Response<LawyerStatsDto>

    /**
     * GET /api/avocat/consultations/recent — recent consultations list.
     * Configure this path on Mockable.io to return a JSON array of consultation objects.
     * If the path is not configured on the mock server this call returns a non-2xx
     * response, which DashboardRepository handles gracefully (empty state).
     */
    @GET("api/avocat/consultations/recent")
    suspend fun getRecentConsultations(): Response<List<RecentConsultationDto>>

    // ── Messages ──────────────────────────────────────────────────────────────

    /** GET /api/messages — all conversations for the authenticated user or lawyer. */
    @GET("api/conversations")
    suspend fun getMessages(): Response<List<ConversationApiDto>>

    /** GET /api/conversations/{id}/messages — message thread for one conversation. */
    @GET("api/conversations/{id}/messages")
    suspend fun getChatDetails(@Path("id") id: String): Response<List<ChatMessageApiDto>>

    // ── Social interactions ────────────────────────────────────────────────────

    /**
     * POST /api/reels/{id}/like — toggle like on a reel.
     * Response: { "is_liked": true, "likes_count": 543 }
     */
    @POST("api/reels/{id}/like")
    suspend fun likeReel(@Path("id") id: String): Response<LikeResponseDto>

    /**
     * POST /api/conversations/{id}/messages — send a text message in a conversation.
     * Body: { "content": "...", "type": "text" }
     */
    @POST("api/conversations/{id}/messages")
    suspend fun sendMessage(
        @retrofit2.http.Path("id") conversationId: String,
        @Body request: SendMessageRequest
    ): Response<SendMessageResponseDto>
}
