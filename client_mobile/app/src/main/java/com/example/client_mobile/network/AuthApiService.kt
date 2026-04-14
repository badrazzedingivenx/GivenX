package com.example.client_mobile.network

import com.example.client_mobile.network.dto.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthApiService {

    /** 
     * json-server "Login" 
     * Returns a list: if empty, login failed.
     */
    @GET("users")
    suspend fun login(
        @Query("email") email: String,
        @Query("password") password: String
    ): Response<ApiResponse<List<UserDto>>>

    /** Fetch profile by userId */
    @GET("profiles")
    suspend fun getProfileByUserId(@Query("userId") userId: Int): Response<ApiResponse<List<ProfileDto>>>

    /** Fetch Lawyer details with profile info */
    @GET("lawyers")
    suspend fun getLawyerByProfileId(
        @Query("profileId") profileId: Int,
        @Query("_expand") expand: String = "profile"
    ): Response<ApiResponse<List<LawyerDataDto>>>

    /** Fetch Client details */
    @GET("clients")
    suspend fun getClientByProfileId(@Query("profileId") profileId: Int): Response<ApiResponse<List<ClientDataDto>>>

    @retrofit2.http.PATCH("profiles/{id}")
    suspend fun patchProfile(
        @retrofit2.http.Path("id") id: Int,
        @retrofit2.http.Body updates: Map<String, String>
    ): Response<ApiResponse<ProfileDto>>
}
