package com.example.client_mobile.network

import com.example.client_mobile.network.dto.Consultation
import com.example.client_mobile.network.dto.Profile
import com.example.client_mobile.network.dto.Specialty
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit Service for handling Legal App resources (Profiles, Consultations, Specialties).
 * Compatible with json-server-auth.
 */
interface LegalApiService {

    // ── Profiles ─────────────────────────────────────────────────────────────

    /**
     * Fetch profiles with optional role filtering.
     * Example: GET /profiles?role=LAWYER
     */
    @GET("profiles")
    suspend fun getProfiles(
        @Query("role") role: String? = null
    ): Response<List<Profile>>

    /**
     * Create a new profile.
     * Note: json-server-auth usually uses /register for new users,
     * but standard CRUD can be done on /profiles if allowed.
     */
    @POST("profiles")
    suspend fun createProfile(@Body profile: Profile): Response<Profile>

    /**
     * Update an existing profile.
     */
    @PUT("profiles/{id}")
    suspend fun updateProfile(
        @Path("id") id: Int,
        @Body profile: Profile
    ): Response<Profile>

    /**
     * Delete a profile.
     */
    @DELETE("profiles/{id}")
    suspend fun deleteProfile(@Path("id") id: Int): Response<Unit>


    // ── Consultations ────────────────────────────────────────────────────────

    /**
     * Fetch consultations.
     * Use @Query for filtering.
     * Example: GET /consultations?client_id=1
     * Example: GET /consultations?lawyer_id=2
     */
    @GET("consultations")
    suspend fun getConsultations(
        @Query("client_id") clientId: Int? = null,
        @Query("lawyer_id") lawyerId: Int? = null
    ): Response<List<Consultation>>

    @POST("consultations")
    suspend fun createConsultation(@Body consultation: Consultation): Response<Consultation>

    @PUT("consultations/{id}")
    suspend fun updateConsultation(
        @Path("id") id: Int,
        @Body consultation: Consultation
    ): Response<Consultation>

    @DELETE("consultations/{id}")
    suspend fun deleteConsultation(@Path("id") id: Int): Response<Unit>


    // ── Specialties ─────────────────────────────────────────────────────────

    @GET("specialties")
    suspend fun getSpecialties(): Response<List<Specialty>>
}
