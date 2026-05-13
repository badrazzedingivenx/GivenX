package com.example.client_mobile.network

import com.example.client_mobile.network.dto.HaqConsultationDto
import com.example.client_mobile.network.dto.HaqCreateConsultationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for consultation (appointment request) endpoints.
 *
 * GET    /consultations               — list the authenticated user's consultations
 * POST   /consultations               — client requests a consultation with a lawyer
 * DELETE /consultations/{id}          — cancel a consultation
 *
 * Auth header injected automatically by [AuthInterceptor].
 * All paths relative to [NetworkModule.BASE_URL].
 */
interface ConsultationApi {

    /**
     * GET /consultations
     *
     * Returns consultations for the authenticated user.
     * Lawyers see requests they received; clients see requests they sent.
     *
     * @param status Optional status filter (e.g. "pending", "accepted", "cancelled")
     * @param page   Page number
     */
    @GET("consultations")
    suspend fun getConsultations(
        @Query("status") status: String? = null,
        @Query("page")   page:   Int     = 1
    ): Response<ApiResponse<List<HaqConsultationDto>>>

    /**
     * POST /consultations
     *
     * Client-only. Requests a new consultation with a lawyer.
     *
     * Response 201: data = HaqConsultationDto (the created request)
     * Errors: 401 UNAUTHORIZED, 422 UNPROCESSABLE_ENTITY
     */
    @POST("consultations")
    suspend fun createConsultation(
        @Body request: HaqCreateConsultationRequest
    ): Response<ApiResponse<HaqConsultationDto>>

    /**
     * DELETE /consultations/{id}
     *
     * Cancels / deletes a consultation request.
     *
     * Response 200: empty data
     * Errors: 401 UNAUTHORIZED, 403 FORBIDDEN, 404 NOT_FOUND
     */
    @DELETE("consultations/{id}")
    suspend fun deleteConsultation(
        @Path("id") id: Int
    ): Response<ApiResponse<Unit>>
}
