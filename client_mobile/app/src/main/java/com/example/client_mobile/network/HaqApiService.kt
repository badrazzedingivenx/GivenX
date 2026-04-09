package com.example.client_mobile.network

import com.example.client_mobile.network.dto.AppointmentDto
import com.example.client_mobile.network.dto.BillingSummaryDto
import com.example.client_mobile.network.dto.CreateDocumentRequest
import com.example.client_mobile.network.dto.DocumentApiDto
import com.example.client_mobile.network.dto.DossierDto
import com.example.client_mobile.network.dto.LawyerDto
import com.example.client_mobile.network.dto.RenameDocumentRequest
import com.example.client_mobile.network.dto.SaveConsultationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
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
    ): Response<ApiResponse<List<LawyerDto>>>

    /**
     * GET /api/lawyers/{id}
     */
    @GET("lawyers/{id}")
    suspend fun getLawyerById(
        @Path("id") id: String
    ): Response<ApiResponse<LawyerDto>>

    // ── Dossiers ──────────────────────────────────────────────────────────────

    /**
     * GET /api/dossiers/{userId}
     * Returns all dossiers belonging to the given user.
     * The JWT in the Authorization header is validated server-side to ensure
     * the caller can only read their own dossiers.
     */
    @GET("dossiers/{userId}")
    suspend fun getDossiers(
        @Path("userId") userId: String
    ): Response<ApiResponse<List<DossierDto>>>

    /**
     * GET /api/dossiers/me
     * Convenience endpoint: returns the current user's dossiers using
     * the JWT alone (no explicit userId needed).
     */
    @GET("dossiers/me")
    suspend fun getMyDossiers(): Response<ApiResponse<List<DossierDto>>>

    /**
     * GET /api/dossiers/detail/{id}
     * Returns a single dossier by its database ID.
     */
    @GET("dossiers/detail/{id}")
    suspend fun getDossierById(
        @Path("id") id: String
    ): Response<ApiResponse<DossierDto>>

    // ── Consultations ─────────────────────────────────────────────────────────

    /**
     * POST /api/consultations
     * Creates a consultation record when a user matches a lawyer.
     */
    @POST("consultations")
    suspend fun saveConsultation(
        @Body request: SaveConsultationRequest
    ): Response<ApiResponse<Unit>>

    // ── Appointments ──────────────────────────────────────────────────────────

    /** GET /api/appointments/me — returns all appointments for the current user. */
    @GET("appointments/me")
    suspend fun getMyAppointments(): Response<ApiResponse<List<AppointmentDto>>>

    // ── Billing ───────────────────────────────────────────────────────────────

    /** GET /api/billing/me — returns billing summary + invoice list for current user. */
    @GET("billing/me")
    suspend fun getMyBilling(): Response<ApiResponse<BillingSummaryDto>>

    // ── Documents ─────────────────────────────────────────────────────────────

    /** GET /api/documents/me — returns all documents owned by the current user. */
    @GET("documents/me")
    suspend fun getMyDocuments(): Response<ApiResponse<List<DocumentApiDto>>>

    /** POST /api/documents — upload / register a new document record. */
    @POST("documents")
    suspend fun createDocument(
        @Body request: CreateDocumentRequest
    ): Response<ApiResponse<DocumentApiDto>>

    /** PATCH /api/documents/{id} — rename an existing document. */
    @PATCH("documents/{id}")
    suspend fun renameDocument(
        @Path("id") id: String,
        @Body request: RenameDocumentRequest
    ): Response<ApiResponse<DocumentApiDto>>

    /** DELETE /api/documents/{id} — remove a document record. */
    @DELETE("documents/{id}")
    suspend fun deleteDocument(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>
}
