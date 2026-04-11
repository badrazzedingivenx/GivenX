package com.example.client_mobile.network

import com.example.client_mobile.network.dto.AppointmentDto
import com.example.client_mobile.network.dto.BillingSummaryDto
import com.example.client_mobile.network.dto.ConversationApiDto
import com.example.client_mobile.network.dto.CreateDocumentRequest
import com.example.client_mobile.network.dto.DocumentApiDto
import com.example.client_mobile.network.dto.DossierDto
import com.example.client_mobile.network.dto.LawyerDto
import com.example.client_mobile.network.dto.LawyerProfileDto
import com.example.client_mobile.network.dto.LawyerStatsDto
import com.example.client_mobile.network.dto.LiveDto
import com.example.client_mobile.network.dto.ReelDto
import com.example.client_mobile.network.dto.RenameDocumentRequest
import com.example.client_mobile.network.dto.SaveConsultationRequest
import com.example.client_mobile.network.dto.SendMessageRequest
import com.example.client_mobile.network.dto.SendMessageResponseDto
import com.example.client_mobile.network.dto.StoryDto
import com.example.client_mobile.network.dto.UpdateDossierStatusRequest
import com.example.client_mobile.network.dto.UpdateProfileRequest
import com.example.client_mobile.network.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Unified REST API interface for the GivenX / HAQ backend.
 *
 * Base URL: [RetrofitClient.BASE_URL]
 *
 * All list endpoints return the [ApiResponse] envelope:
 *   { "success": true, "data": [...], "total": 42 }
 *
 * Authentication: every request automatically carries  
 *   Authorization: Bearer <token>  via RetrofitClient's auth interceptor.
 */
interface HaqApiService {

    // ── Lawyers ───────────────────────────────────────────────────────────────

    /**
     * GET /api/lawyers
     * Optional: ?domaine=Droit+Civil  ?q=dupont  ?page=1  ?limit=50
     */
    @GET("api/lawyers")
    suspend fun getLawyers(
        @Query("domaine") domaine: String? = null,
        @Query("q")       query:   String? = null,
        @Query("page")    page:    Int     = 1,
        @Query("limit")   limit:   Int     = 50
    ): Response<ApiResponse<List<LawyerDto>>>

    /**
     * GET /api/lawyers/{id}
     */
    @GET("api/lawyers/{id}")
    suspend fun getLawyerById(
        @Path("id") id: String
    ): Response<ApiResponse<LawyerDto>>

    /** GET /api/lawyers/me — returns the authenticated lawyer's profile. */
    @GET("api/lawyers/me")
    suspend fun getLawyerProfile(): Response<ApiResponse<LawyerProfileDto>>

    /** PATCH /api/lawyers/me — update the authenticated lawyer's profile. */
    @PATCH("api/lawyers/me")
    suspend fun updateLawyerProfile(
        @Body profile: LawyerProfileDto
    ): Response<ApiResponse<LawyerProfileDto>>

    /** GET /api/lawyers/me/stats — returns dashboard KPIs for the lawyer. */
    @GET("api/lawyers/me/stats")
    suspend fun getLawyerStats(): Response<ApiResponse<LawyerStatsDto>>

    // ── User Profile ─────────────────────────────────────────────────────────

    /** GET /api/users/me — authenticated user's full profile. */
    @GET("api/users/me")
    suspend fun getUserProfile(): Response<ApiResponse<UserDto>>

    /** PUT /api/users/me — update authenticated user's profile. */
    @PUT("api/users/me")
    suspend fun updateUserProfile(
        @Body request: UpdateProfileRequest
    ): Response<ApiResponse<UserDto>>

    // ── Dossiers ──────────────────────────────────────────────────────────────

    /**
     * GET /api/dossiers/{userId}
     * Returns all dossiers belonging to the given user.
     * The JWT in the Authorization header is validated server-side to ensure
     * the caller can only read their own dossiers.
     */
    @GET("api/dossiers/{userId}")
    suspend fun getDossiers(
        @Path("userId") userId: String
    ): Response<ApiResponse<List<DossierDto>>>

    /**
     * GET /api/dossiers/me
     * Convenience endpoint: returns the current user's dossiers using
     * the JWT alone (no explicit userId needed).
     */
    @GET("api/dossiers/me")
    suspend fun getMyDossiers(): Response<ApiResponse<List<DossierDto>>>

    /**
     * GET /api/dossiers/detail/{id}
     * Returns a single dossier by its database ID.
     */
    @GET("api/dossiers/{id}")
    suspend fun getDossierById(
        @Path("id") id: String
    ): Response<ApiResponse<DossierDto>>
    /**
     * PATCH /api/dossiers/{id}/status
     * Updates the status of a dossier (lawyer only — enforced server-side via JWT).
     */
    @PATCH("api/dossiers/{id}/status")
    suspend fun updateDossierStatus(
        @Path("id") id: String,
        @Body request: UpdateDossierStatusRequest
    ): Response<ApiResponse<DossierDto>>
    // ── Consultations ─────────────────────────────────────────────────────────

    /**
     * POST /api/consultations
     * Creates a consultation record when a user matches a lawyer.
     */
    @POST("api/consultations")
    suspend fun saveConsultation(
        @Body request: SaveConsultationRequest
    ): Response<ApiResponse<Unit>>

    // ── Appointments ──────────────────────────────────────────────────────────

    /** GET /api/appointments/me — returns all appointments for the current user. */
    @GET("api/appointments/me")
    suspend fun getMyAppointments(): Response<ApiResponse<List<AppointmentDto>>>

    // ── Billing ───────────────────────────────────────────────────────────────

    /** GET /api/billing/me — returns billing summary + invoice list for current user. */
    @GET("api/billing/me")
    suspend fun getMyBilling(): Response<ApiResponse<BillingSummaryDto>>

    // ── Documents ─────────────────────────────────────────────────────────────

    /** GET /api/documents/vault — returns all documents owned by the current user. */
    @GET("api/documents/vault")
    suspend fun getMyDocuments(): Response<ApiResponse<List<DocumentApiDto>>>

    /** POST /api/documents — upload / register a new document record. */
    @POST("api/documents")
    suspend fun createDocument(
        @Body request: CreateDocumentRequest
    ): Response<ApiResponse<DocumentApiDto>>

    /** PATCH /api/documents/{id} — rename an existing document. */
    @PATCH("api/documents/{id}")
    suspend fun renameDocument(
        @Path("id") id: String,
        @Body request: RenameDocumentRequest
    ): Response<ApiResponse<DocumentApiDto>>

    /** DELETE /api/documents/{id} — remove a document record. */
    @DELETE("api/documents/{id}")
    suspend fun deleteDocument(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>

    // ── Messages ──────────────────────────────────────────────────────────────

    /** GET /api/conversations — all conversations for the authenticated user or lawyer. */
    @GET("api/conversations")
    suspend fun getMessages(): Response<ApiResponse<List<ConversationApiDto>>>

    /** POST /api/conversations/{id}/messages — send a text message in a conversation. */
    @POST("api/conversations/{id}/messages")
    suspend fun sendMessage(
        @Path("id") conversationId: String,
        @Body request: SendMessageRequest
    ): Response<ApiResponse<SendMessageResponseDto>>

    // ── Reels ──────────────────────────────────────────────────────

    /** GET /api/reels — [ { "id": "reel_001", "lawyerName": "...", "likes": 542, "caption": "..." } ] */
    @GET("api/reels")
    suspend fun getReels(): Response<ApiResponse<List<ReelDto>>>

    // ── Stories ────────────────────────────────────────────────────

    /** GET /api/stories — [ { "id": "story_001", "lawyerName": "...", "imageUrl": "..." } ] */
    @GET("api/stories")
    suspend fun getStories(): Response<ApiResponse<List<StoryDto>>>

    // ── Lives ──────────────────────────────────────────────────────

    /** GET /api/lives — [ { "id": "live_001", "title": "...", "viewersCount": 124 } ] */
    @GET("api/lives")
    suspend fun getLives(): Response<ApiResponse<List<LiveDto>>>
}
