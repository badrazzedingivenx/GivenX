package com.example.client_mobile.network

import com.example.client_mobile.network.dto.AuthResponse
import com.example.client_mobile.network.dto.LawyerProfileDto
import com.example.client_mobile.network.dto.LoginRequest
import com.example.client_mobile.network.dto.RegisterLawyerRequest
import com.example.client_mobile.network.dto.RegisterRequest
import com.example.client_mobile.network.dto.SignupRequest
import com.example.client_mobile.network.dto.UpdateLawyerProfileRequest
import com.example.client_mobile.network.dto.UpdateProfileRequest
import com.example.client_mobile.network.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

/**
 * REST endpoints for authentication and authenticated profile management.
 *
 * POST /api/auth/login             – authenticate, get JWT
 * POST /api/auth/register-user     – register new client account
 * POST /api/auth/register-lawyer   – register new lawyer account
 * GET  /api/users/me               – client's own profile (JWT required)
 * PUT  /api/users/me               – update client profile
 * GET  /api/lawyers/me             – lawyer's own profile (JWT required)
 * PUT  /api/lawyers/me             – update lawyer profile
 */
interface AuthApiService {

    /** POST /api/login */
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    /** POST /api/register */
    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    /** POST /api/auth/register-user — register a new client account. */
    @POST("api/auth/register-user")
    suspend fun signup(@Body request: SignupRequest): Response<AuthResponse>

    /** POST /api/auth/register-lawyer — register a new lawyer account. */
    @POST("api/auth/register-lawyer")
    suspend fun signupLawyer(@Body request: RegisterLawyerRequest): Response<AuthResponse>

    /** GET /api/auth/me — client's own profile. JWT added by interceptor. */
    @GET("api/auth/me")
    suspend fun getMe(): Response<UserDto>

    /** PUT /api/auth/me — update client profile fields. */
    @PUT("api/auth/me")
    suspend fun updateMe(@Body request: UpdateProfileRequest): Response<UserDto>

    /** GET /api/lawyers/me — lawyer's own full profile. */
    @GET("api/auth/me")
    suspend fun getLawyerMe(): Response<LawyerProfileDto>

    /** PUT /api/auth/me — update lawyer profile fields. */
    @PUT("api/auth/me")
    suspend fun updateLawyerMe(@Body request: UpdateLawyerProfileRequest): Response<LawyerProfileDto>
}
