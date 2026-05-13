package com.example.client_mobile.network

import com.example.client_mobile.network.dto.HaqLawyerDetailDto
import com.example.client_mobile.network.dto.HaqLawyerPublicDto
import com.example.client_mobile.network.dto.PaginatedData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for public lawyer discovery.
 *
 * GET /lawyers       — browse/filter the lawyer directory.
 * GET /lawyers/{id}  — full profile of a specific lawyer.
 *
 * Auth is required per spec; the header is injected automatically by [AuthInterceptor].
 * All paths are relative to [NetworkModule.BASE_URL].
 *
 * Usage:
 *   val result: Resource<List<HaqLawyerPublicDto>> =
 *       safeApiCall { NetworkModule.lawyerApi.getLawyers() }
 */
interface LawyerApi {

    /**
     * GET /lawyers
     *
     * Returns a paginated list of active lawyers.
     * All query params are optional.
     *
     * @param domain    Filter by legal domain (e.g. "Droit Penal")
     * @param available Filter to available-only lawyers (pass true)
     * @param page      Page number (default 1)
     *
     * Response 200: data = List<HaqLawyerPublicDto>
     * Errors: 401 UNAUTHORIZED
     */
    @GET("lawyers")
    suspend fun getLawyers(
        @Query("domain")    domain:    String?  = null,
        @Query("available") available: Boolean? = null,
        @Query("page")      page:      Int      = 1
    ): Response<ApiResponse<PaginatedData<HaqLawyerPublicDto>>>

    /**
     * GET /lawyers/{lawyer_id}
     *
     * Returns the full public profile of a specific lawyer.
     *
     * Response 200: data = HaqLawyerDetailDto
     * Errors: 401 UNAUTHORIZED, 404 NOT_FOUND
     */
    @GET("lawyers/{id}")
    suspend fun getLawyerById(
        @Path("id") id: Int
    ): Response<ApiResponse<HaqLawyerDetailDto>>
}

