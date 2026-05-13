package com.example.client_mobile.network

import com.example.client_mobile.network.dto.HaqCreateLiveSessionRequest
import com.example.client_mobile.network.dto.HaqLiveCommentDto
import com.example.client_mobile.network.dto.HaqLiveCommentRequest
import com.example.client_mobile.network.dto.HaqLiveSessionDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for live-session endpoints.
 *
 * GET    /live-sessions                          — browse sessions
 * POST   /live-sessions                          — lawyer creates a session
 * GET    /live-sessions/{id}                     — session detail
 * POST   /live-sessions/{id}/join                — client joins a session
 * POST   /live-sessions/{id}/comments            — post a comment
 * GET    /live-sessions/{id}/comments            — list comments
 * PATCH  /live-sessions/{id}/end                 — lawyer ends a session
 *
 * Auth header injected automatically by [AuthInterceptor].
 * All paths relative to [NetworkModule.BASE_URL].
 */
interface LiveSessionApi {

    /**
     * GET /live-sessions
     *
     * @param status   Filter by status ("scheduled", "live", "ended")
     * @param domain   Filter by legal domain
     * @param page     Page number
     * @param perPage  Items per page
     */
    @GET("live-sessions")
    suspend fun getLiveSessions(
        @Query("status")   status:  String? = null,
        @Query("domain")   domain:  String? = null,
        @Query("page")     page:    Int     = 1,
        @Query("per_page") perPage: Int     = 10
    ): Response<ApiResponse<List<HaqLiveSessionDto>>>

    /**
     * POST /live-sessions
     *
     * Lawyer-only. Creates a new live session.
     */
    @POST("live-sessions")
    suspend fun createLiveSession(
        @Body request: HaqCreateLiveSessionRequest
    ): Response<ApiResponse<HaqLiveSessionDto>>

    /**
     * GET /live-sessions/{id}
     *
     * Returns the full detail of a specific session.
     */
    @GET("live-sessions/{id}")
    suspend fun getLiveSessionById(
        @Path("id") id: Int
    ): Response<ApiResponse<HaqLiveSessionDto>>

    /**
     * POST /live-sessions/{id}/join
     *
     * Client joins an ongoing or scheduled session.
     * Returns updated session data with join confirmation.
     */
    @POST("live-sessions/{id}/join")
    suspend fun joinLiveSession(
        @Path("id") id: Int
    ): Response<ApiResponse<HaqLiveSessionDto>>

    /**
     * POST /live-sessions/{id}/comments
     *
     * Posts a comment to a live session.
     */
    @POST("live-sessions/{id}/comments")
    suspend fun postComment(
        @Path("id") id: Int,
        @Body request: HaqLiveCommentRequest
    ): Response<ApiResponse<HaqLiveCommentDto>>

    /**
     * GET /live-sessions/{id}/comments
     *
     * Lists all comments for the given session.
     */
    @GET("live-sessions/{id}/comments")
    suspend fun getComments(
        @Path("id") id: Int
    ): Response<ApiResponse<List<HaqLiveCommentDto>>>

    /**
     * PATCH /live-sessions/{id}/end
     *
     * Lawyer-only. Marks a session as ended.
     */
    @PATCH("live-sessions/{id}/end")
    suspend fun endLiveSession(
        @Path("id") id: Int
    ): Response<ApiResponse<HaqLiveSessionDto>>
}
