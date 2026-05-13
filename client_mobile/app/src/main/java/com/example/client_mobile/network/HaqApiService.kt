package com.example.client_mobile.network

import com.example.client_mobile.network.dto.ChatMessageApiDto
import com.example.client_mobile.network.dto.LikeResponseDto
import com.example.client_mobile.network.dto.NotificationDto
import com.example.client_mobile.network.dto.AppointmentDto
import com.example.client_mobile.network.dto.AppointmentsPageDto
import com.example.client_mobile.network.dto.BillingSummaryDto
import com.example.client_mobile.network.dto.ConversationApiDto
import com.example.client_mobile.network.dto.CreateDocumentRequest
import com.example.client_mobile.network.dto.DocumentApiDto
import com.example.client_mobile.network.dto.DossierDto
import com.example.client_mobile.network.dto.LawyerDto
import com.example.client_mobile.network.dto.LawyersResponseDto
import com.example.client_mobile.network.dto.LawyerProfileDto
import com.example.client_mobile.network.dto.LawyerStatsDto
import com.example.client_mobile.network.dto.LiveDto
import com.example.client_mobile.network.dto.LivesResponseDto
import com.example.client_mobile.network.dto.LegalPostDto
import com.example.client_mobile.network.dto.ReelDto
import com.example.client_mobile.network.dto.ReelsResponseDto
import com.example.client_mobile.network.dto.RecentConsultationDto
import com.example.client_mobile.network.dto.RevenueMonthDto
import com.example.client_mobile.network.dto.RenameDocumentRequest
import com.example.client_mobile.network.dto.SaveConsultationRequest
import com.example.client_mobile.network.dto.SendMessageRequest
import com.example.client_mobile.network.dto.SendMessageResponseDto
import com.example.client_mobile.network.dto.StoryDto
import com.example.client_mobile.network.dto.StoriesResponseDto
import com.example.client_mobile.network.dto.UpdateDossierStatusRequest
import com.example.client_mobile.network.dto.UpdateProfileRequest
import com.example.client_mobile.network.dto.UserDto
import com.example.client_mobile.network.dto.Profile
import com.example.client_mobile.network.dto.Specialty
import com.example.client_mobile.network.dto.Consultation
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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
    @GET("lawyers")
    suspend fun getLawyers(
        @Query("domaine") domaine: String? = null,
        @Query("q")       query:   String? = null,
        @Query("page")    page:    Int     = 1,
        @Query("limit")   limit:   Int     = 50
    ): Response<ApiResponse<LawyersResponseDto>>

    /**
     * GET /lawyers/{id}
     */
    @GET("lawyers/{id}")
    suspend fun getLawyerById(
        @Path("id") id: String
    ): Response<ApiResponse<LawyerDto>>

    /** GET /lawyers/me — returns the authenticated lawyer's profile. */
    @GET("lawyers/me")
    suspend fun getLawyerProfile(): Response<ApiResponse<LawyerProfileDto>>

    /** PATCH /lawyers/me — update the authenticated lawyer's profile. */
    @PATCH("lawyers/me")
    suspend fun updateLawyerProfile(
        @Body profile: LawyerProfileDto
    ): Response<ApiResponse<LawyerProfileDto>>

    /** GET /lawyers/me/stats — returns dashboard KPIs for the lawyer. */
    @GET("lawyers/me/stats")
    suspend fun getLawyerStats(): Response<ApiResponse<LawyerStatsDto>>

    /** GET /lawyers/me/revenue/monthly — returns 6-month revenue chart data. */
    @GET("lawyers/me/revenue/monthly")
    suspend fun getLawyerRevenueMonthly(): Response<ApiResponse<List<RevenueMonthDto>>>

    /**
     * GET /appointments
     * Returns a paginated appointments list for the authenticated user/lawyer.
     * Shape: { "data": { "appointments": [...], "pagination": {...} } }
     */
    @GET("appointments")
    suspend fun getAppointments(
        @Query("limit")   limit:  Int? = null,
        @Query("sort_by") sortBy: String? = null
    ): Response<ApiResponse<AppointmentsPageDto>>

    // ── User Profile ─────────────────────────────────────────────────────────

    /** GET /users/me — authenticated user's full profile (Client or Lawyer). */
    @GET("users/me")
    suspend fun getUserProfile(): Response<ApiResponse<UserDto>>

    /** PUT /users/me — update authenticated user's profile. */
    @PUT("users/me")
    suspend fun updateUserProfile(
        @Body request: UpdateProfileRequest
    ): Response<ApiResponse<UserDto>>

    // ── Dossiers ──────────────────────────────────────────────────────────────

    /**
     * GET /dossiers/{userId}
     * Returns all dossiers belonging to the given user.
     * The JWT in the Authorization header is validated server-side to ensure
     * the caller can only read their own dossiers.
     */
    @GET("dossiers/{userId}")
    suspend fun getDossiers(
        @Path("userId") userId: String
    ): Response<ApiResponse<List<DossierDto>>>

    /**
     * GET /documents
     * Returns documents owned by the authenticated user (replaces legacy dossiers/me).
     */
    @GET("documents")
    suspend fun getMyDossiers(): Response<ApiResponse<List<DossierDto>>>

    /**
     * GET /dossiers/detail/{id}
     * Returns a single dossier by its database ID.
     */
    @GET("dossiers/{id}")
    suspend fun getDossierById(
        @Path("id") id: String
    ): Response<ApiResponse<DossierDto>>
    /**
     * PATCH /dossiers/{id}/status
     * Updates the status of a dossier (lawyer only — enforced server-side via JWT).
     */
    @PATCH("dossiers/{id}/status")
    suspend fun updateDossierStatus(
        @Path("id") id: String,
        @Body request: UpdateDossierStatusRequest
    ): Response<ApiResponse<DossierDto>>
    // ── Consultations ─────────────────────────────────────────────────────────

    /**
     * POST /consultations
     * Creates a consultation record when a user matches a lawyer.
     */
    @POST("consultations")
    suspend fun saveConsultation(
        @Body request: SaveConsultationRequest
    ): Response<ApiResponse<Unit>>

    // ── Appointments ──────────────────────────────────────────────────────────

    /** GET /appointments — returns all appointments for the current user. */
    @GET("appointments")
    suspend fun getMyAppointments(): Response<ApiResponse<List<AppointmentDto>>>

    // ── Billing ───────────────────────────────────────────────────────────────

    /** GET /payments — returns payment history for the current user (replaces billing/me). */
    @GET("payments")
    suspend fun getMyBilling(): Response<ApiResponse<BillingSummaryDto>>

    // ── Documents ─────────────────────────────────────────────────────────────

    /** GET /documents — returns all documents owned by the current user. */
    @GET("documents")
    suspend fun getMyDocuments(): Response<ApiResponse<List<DocumentApiDto>>>

    /** POST /documents — upload / register a new document record. */
    @POST("documents")
    suspend fun createDocument(
        @Body request: CreateDocumentRequest
    ): Response<ApiResponse<DocumentApiDto>>

    /** PATCH /documents/{id} — rename an existing document. */
    @PATCH("documents/{id}")
    suspend fun renameDocument(
        @Path("id") id: String,
        @Body request: RenameDocumentRequest
    ): Response<ApiResponse<DocumentApiDto>>

    /** DELETE /documents/{id} — remove a document record. */
    @DELETE("documents/{id}")
    suspend fun deleteDocument(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>

    // ── Messages ──────────────────────────────────────────────────────────────

    /** GET /conversations — all conversations for the authenticated user or lawyer. */
    @GET("conversations")
    suspend fun getMessages(): Response<ApiResponse<List<ConversationApiDto>>>

    /** GET /conversations/{id}/messages — historical messages for a conversation. */
    @GET("conversations/{id}/messages")
    suspend fun getChatDetails(
        @Path("id") conversationId: String
    ): Response<ApiResponse<List<ChatMessageApiDto>>>

    /** POST /conversations/{id}/messages — send a text message in a conversation. */
    @POST("conversations/{id}/messages")
    suspend fun sendMessage(
        @Path("id") conversationId: String,
        @Body request: SendMessageRequest
    ): Response<ApiResponse<SendMessageResponseDto>>

    // ── Reels ──────────────────────────────────────────────────────

    /** GET /reels — shape: {"data":{"reels":[...],"pagination":{...}}} */
    @GET("reels")
    suspend fun getReels(): Response<ApiResponse<ReelsResponseDto>>
    /** POST /reels — multipart upload of a video file with an optional caption. */
    @Multipart
    @POST("reels")
    suspend fun uploadReel(
        @Part video: MultipartBody.Part,
        @Part("title") title: RequestBody
    ): Response<ApiResponse<ReelDto>>
    // ── Stories ────────────────────────────────────────────────────
    /** GET /stories — shape: {"data":{"stories":[...]}} */
    @GET("stories")
    suspend fun getStories(): Response<ApiResponse<StoriesResponseDto>>

    /** POST /stories — multipart upload of an image/video file. */
    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part media: MultipartBody.Part
    ): Response<ApiResponse<StoryDto>>

    // ── Legal Feed ─────────────────────────────────────────────────
    /** GET /reels — returns the social legal feed posts (legal-feed renamed to reels). */
    @GET("reels")
    suspend fun getLegalFeed(): Response<ApiResponse<ReelsResponseDto>>

    // ── Lives ──────────────────────────────────────────────────────

    /** GET /lives — shape: {"data":{"lives":[...],"pagination":{...}}} */
    @GET("lives")
    suspend fun getLives(): Response<ApiResponse<LivesResponseDto>>

    /** POST /reels/{id}/like — toggle like on a reel. */
    @POST("reels/{id}/like")
    suspend fun likeReel(
        @Path("id") reelId: String
    ): Response<ApiResponse<LikeResponseDto>>

    /** GET /notifications — current user's notifications. */
    @GET("notifications")
    suspend fun getNotifications(): Response<ApiResponse<List<com.example.client_mobile.network.dto.NotificationDto>>>

    // ── Payments ─────────────────────────────────────────────────────────────

    /** GET /payments?clientId={id} or /payments?lawyerId={id} — returns payments. */
    @GET("payments")
    suspend fun getPayments(
        @Query("clientId") clientId: Int? = null,
        @Query("lawyerId") lawyerId: Int? = null
    ): Response<ApiResponse<List<com.example.client_mobile.network.dto.PaymentDto>>>

    // ── Legal Services (Legacy support/migration) ─────────────────────────────

    @GET("profiles")
    suspend fun getProfiles(
        @Query("role") role: String? = null
    ): Response<ApiResponse<List<Profile>>>

    @POST("consultations")
    suspend fun createConsultation(
        @Body consultation: Consultation
    ): Response<ApiResponse<Consultation>>

    @GET("consultations")
    suspend fun getConsultations(
        @Query("client_id") clientId: Int? = null,
        @Query("lawyer_id") lawyerId: Int? = null
    ): Response<ApiResponse<List<Consultation>>>

    @GET("specialties")
    suspend fun getSpecialties(): Response<ApiResponse<List<Specialty>>>

    @PUT("consultations/{id}")
    suspend fun updateConsultation(
        @Path("id") id: Int,
        @Body consultation: Consultation
    ): Response<ApiResponse<Consultation>>
}
