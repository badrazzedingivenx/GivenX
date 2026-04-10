package com.example.client_mobile.network

import com.example.client_mobile.network.dto.LawyerDto
import com.example.client_mobile.network.dto.LawyerListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * REST endpoints for the lawyers resource.
 *
 * Base URL: RetrofitClient.BASE_URL  (e.g. https://votre-site.com/api/)
 *
 * Expected MySQL table columns (matching LawyerDto @SerializedName):
 *   id, name, specialty, location, experience, rating,
 *   compatibility, review_count, bio, is_verified, domaine
 */
interface LawyerApiService {

    /**
     * GET /api/lawyers
     *
     * Optional query params:
     *   ?domaine=Droit+Civil   – filter by practice area
     *   ?q=dupont              – full-text search on name / specialty
     *   ?page=1&limit=20       – pagination
     *
     * Returns: { "data": [...], "total": 42 }
     */
    @GET("api/lawyers")
    suspend fun getLawyers(
        @Query("domaine") domaine: String? = null,
        @Query("q")       query:   String? = null,
        @Query("page")    page:    Int     = 1,
        @Query("limit")   limit:   Int     = 50
    ): Response<LawyerListResponse>

    /**
     * GET /api/lawyers/{id}
     *
     * Returns a single lawyer object.
     */
    @GET("api/lawyers/{id}")
    suspend fun getLawyerById(
        @Path("id") id: String
    ): Response<LawyerDto>
}
