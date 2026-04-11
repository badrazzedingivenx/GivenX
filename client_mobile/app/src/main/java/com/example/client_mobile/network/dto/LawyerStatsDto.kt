package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

// ─── GET /api/lawyers/me ──────────────────────────────────────────────────────
// Authenticated lawyer's full profile (Role: LAWYER only).
data class LawyerProfileDto(
    @SerializedName(value = "id")                                                      val id:              String  = "",
    @SerializedName(value = "full_name",        alternate = ["fullName", "name"])      val fullName:        String  = "",
    @SerializedName(value = "email")                                                   val email:           String  = "",
    @SerializedName(value = "phone")                                                   val phone:           String  = "",
    @SerializedName(value = "address")                                                 val address:         String  = "",
    @SerializedName(value = "avatar_url",       alternate = ["avatarUrl"])             val avatarUrl:       String  = "",
    @SerializedName(value = "bio")                                                     val bio:             String  = "",
    @SerializedName(value = "speciality",       alternate = ["specialty"])             val speciality:      String  = "",
    @SerializedName(value = "bar_association",  alternate = ["barAssociation"])        val barAssociation:  String  = "",
    @SerializedName(value = "bar_number",       alternate = ["barNumber"])             val barNumber:       String  = "",
    @SerializedName(value = "years_experience", alternate = ["yearsExperience"])       val yearsExperience: Int     = 0,
    @SerializedName(value = "specializations")                                         val specializations: List<String> = emptyList(),
    @SerializedName(value = "is_verified",      alternate = ["isVerified"])            val isVerified:      Boolean = false,
    @SerializedName(value = "is_available",     alternate = ["isAvailable"])           val isAvailable:     Boolean = true,
    @SerializedName(value = "rating")                                                  val rating:          Float   = 0f,
    @SerializedName(value = "review_count",     alternate = ["reviewCount"])           val reviewCount:     Int     = 0,
    @SerializedName(value = "client_count",     alternate = ["clientCount"])           val clientCount:     Int     = 0,
    @SerializedName(value = "status")                                                  val status:          String  = "active",
    @SerializedName(value = "created_at",       alternate = ["createdAt"])             val createdAt:       String  = "",
    @SerializedName(value = "updated_at",       alternate = ["updatedAt"])             val updatedAt:       String  = "",
    @SerializedName(value = "role")                                                    val role:            String  = "lawyer"
)

// ─── GET /api/lawyers/me/stats ────────────────────────────────────────────────
// Dashboard KPIs for the authenticated lawyer (Role: LAWYER only).
data class LawyerStatsDto(
    @SerializedName(value = "total_clients",       alternate = ["totalClients"])      val totalClients:       Int   = 0,
    @SerializedName(value = "active_clients",      alternate = ["activeClients"])     val activeClients:      Int   = 0,
    @SerializedName(value = "audiences_today",     alternate = ["audiencesToday"])    val audiencesToday:     Int   = 0,
    @SerializedName(value = "new_requests",        alternate = ["newRequests"])       val newRequests:        Int   = 0,
    @SerializedName(value = "closed_cases",        alternate = ["closedCases"])       val closedCases:        Int   = 0,
    /** Matches the 'dossiers_gagnes' key returned by the updated Mockable.io endpoint. */
    @SerializedName(value = "dossiers_gagnes",     alternate = ["dossiersGagnes"])    val dossiersGagnes:     Int   = 0,
    @SerializedName(value = "total_revenue_month", alternate = ["totalRevenueMonth"]) val totalRevenueMonth:  Float = 0f,
    @SerializedName(value = "total_revenue_year",  alternate = ["totalRevenueYear"])  val totalRevenueYear:   Float = 0f,
    @SerializedName(value = "average_rating",      alternate = ["averageRating"])     val averageRating:      Float = 0f
) {
    /** Effective 'Dossiers gagnés' — prefers the explicit field, falls back to closed_cases. */
    fun effectiveDossiersGagnes(): Int = if (dossiersGagnes > 0) dossiersGagnes else closedCases
}
