package com.example.client_mobile.network

import com.example.client_mobile.network.dto.AuthResponse
import com.example.client_mobile.network.dto.LoginRequest
import com.example.client_mobile.network.dto.RegisterRequest
import com.example.client_mobile.network.dto.UpdateProfileRequest
import com.example.client_mobile.network.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

/**
 * REST endpoints for authentication and the current user's profile.
 *
 * POST /api/auth/login    – returns a JWT
 * POST /api/auth/register – creates an account, returns a JWT
 * GET  /api/auth/me       – returns the logged-in user (requires JWT)
 */
interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    /** Protected endpoint – the AuthInterceptor adds the JWT automatically. */
    @GET("auth/me")
    suspend fun getMe(): Response<UserDto>

    /** Updates the current user's profile. Protected endpoint. */
    @PATCH("auth/me")
    suspend fun updateMe(@Body request: UpdateProfileRequest): Response<UserDto>
}
