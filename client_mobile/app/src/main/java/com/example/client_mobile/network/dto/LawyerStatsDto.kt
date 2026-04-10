package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

// ─── GET /api/lawyers/me ──────────────────────────────────────────────────────
// Authenticated lawyer's full profile (Role: LAWYER only).
data class LawyerProfileDto(
    @SerializedName("id")               val id:              String  = "",
    @SerializedName("full_name")        val fullName:        String  = "",
    @SerializedName("email")            val email:           String  = "",
    @SerializedName("phone")            val phone:           String  = "",
    @SerializedName("address")          val address:         String  = "",
    @SerializedName("avatar_url")       val avatarUrl:       String  = "",
    @SerializedName("bio")              val bio:             String  = "",
    @SerializedName("speciality")       val speciality:      String  = "",
    @SerializedName("bar_association")  val barAssociation:  String  = "",
    @SerializedName("bar_number")       val barNumber:       String  = "",
    @SerializedName("years_experience") val yearsExperience: Int     = 0,
    @SerializedName("specializations")  val specializations: List<String> = emptyList(),
    @SerializedName("is_verified")      val isVerified:      Boolean = false,
    @SerializedName("is_available")     val isAvailable:     Boolean = true,
    @SerializedName("rating")           val rating:          Float   = 0f,
    @SerializedName("review_count")     val reviewCount:     Int     = 0,
    @SerializedName("client_count")     val clientCount:     Int     = 0,
    @SerializedName("status")           val status:          String  = "active",
    @SerializedName("created_at")       val createdAt:       String  = "",
    @SerializedName("updated_at")       val updatedAt:       String  = "",
    @SerializedName("role")             val role:            String  = "lawyer"
)

// ─── GET /api/lawyers/me/stats ────────────────────────────────────────────────
// Dashboard KPIs for the authenticated lawyer (Role: LAWYER only).
data class LawyerStatsDto(
    @SerializedName("total_clients")        val totalClients:       Int   = 0,
    @SerializedName("active_clients")       val activeClients:      Int   = 0,
    @SerializedName("audiences_today")      val audiencesToday:     Int   = 0,
    @SerializedName("new_requests")         val newRequests:        Int   = 0,
    @SerializedName("closed_cases")         val closedCases:        Int   = 0,
    /** Matches the 'dossiers_gagnes' key returned by the updated Mockable.io endpoint. */
    @SerializedName("dossiers_gagnes")      val dossiersGagnes:     Int   = 0,
    @SerializedName("total_revenue_month")  val totalRevenueMonth:  Float = 0f,
    @SerializedName("total_revenue_year")   val totalRevenueYear:   Float = 0f,
    @SerializedName("average_rating")       val averageRating:      Float = 0f
) {
    /** Effective 'Dossiers gagnés' — prefers the explicit field, falls back to closed_cases. */
    fun effectiveDossiersGagnes(): Int = if (dossiersGagnes > 0) dossiersGagnes else closedCases
}
