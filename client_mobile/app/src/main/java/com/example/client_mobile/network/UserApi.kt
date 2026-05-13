package com.example.client_mobile.network

import com.example.client_mobile.network.dto.HaqProfileData
import com.example.client_mobile.network.dto.HaqUpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

/**
 * Retrofit interface for profile management.
 *
 * PUT /profile — single endpoint for updating any authenticated user's profile
 * (both CLIENT and LAWYER roles).  GET /auth/me lives in [AuthApi].
 *
 * All paths are relative to [NetworkModule.BASE_URL].
 * Authorization header is injected automatically by [AuthInterceptor].
 *
 * Usage:
 *   val result: Resource<HaqProfileData> =
 *       safeApiCall { NetworkModule.userApi.updateProfile(req) }
 */
interface UserApi {

    /**
     * PUT /profile
     *
     * Updates the authenticated user's profile.
     * All request fields are optional — include only the fields you want to change.
     *
     * Response 200: data = HaqProfileData (updated profile)
     * Errors: 401 UNAUTHORIZED, 422 UNPROCESSABLE_ENTITY (validation)
     */
    @PUT("profile")
    suspend fun updateProfile(
        @Body request: HaqUpdateProfileRequest
    ): Response<ApiResponse<HaqProfileData>>
}

