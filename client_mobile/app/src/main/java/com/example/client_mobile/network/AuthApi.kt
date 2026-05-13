package com.example.client_mobile.network

import com.example.client_mobile.network.dto.HaqAuthData
import com.example.client_mobile.network.dto.HaqLoginRequest
import com.example.client_mobile.network.dto.HaqProfileData
import com.example.client_mobile.network.dto.HaqRegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit interface for the HAQ Authentication API.
 *
 * All paths are relative to [NetworkModule.BASE_URL]
 * (https://lavender-spoonbill-389199.hostingersite.com/api/v1/).
 *
 * Unauthenticated endpoints (login, register) pass through [AuthInterceptor]
 * which simply adds nothing when no token is stored.
 *
 * Usage:
 *   val result = safeApiCall { NetworkModule.authApi.login(req) }
 */
interface AuthApi {

    /**
     * POST /auth/login
     *
     * Authenticates any user/lawyer and returns a JWT token.
     * No token required.
     *
     * Response 200: data = HaqAuthData (access_token + user)
     * Errors: 401 INVALID_CREDENTIALS, 403 FORBIDDEN
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: HaqLoginRequest
    ): Response<ApiResponse<HaqAuthData>>

    /**
     * POST /auth/register
     *
     * Registers a CLIENT or LAWYER based on the [role] field.
     * No token required.
     *
     * Response 201: data = HaqAuthData (access_token + user)
     * Errors: 422 UNPROCESSABLE_ENTITY (validation), 409 duplicate email
     */
    @POST("auth/register")
    suspend fun register(
        @Body request: HaqRegisterRequest
    ): Response<ApiResponse<HaqAuthData>>

    /**
     * POST /auth/logout
     *
     * Invalidates the current session on the server.
     * Requires a valid access token (injected automatically).
     *
     * Response 200: no data — only success + message
     * Errors: 401 UNAUTHORIZED
     */
    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    /**
     * POST /auth/refresh
     *
     * Exchanges an expiring token for a fresh one.
     * Send the current (still-valid or recently-expired) token in the header.
     *
     * Response 200: data = HaqAuthData (new access_token)
     * Errors: 401 UNAUTHORIZED / TOKEN_EXPIRED
     */
    @POST("auth/refresh")
    suspend fun refresh(): Response<ApiResponse<HaqAuthData>>

    /**
     * GET /auth/me
     *
     * Returns the full profile of the currently authenticated user or lawyer.
     * Requires a valid access token.
     *
     * Response 200: data = HaqProfileData
     * Errors: 401 UNAUTHORIZED
     */
    @GET("auth/me")
    suspend fun me(): Response<ApiResponse<HaqProfileData>>
}

