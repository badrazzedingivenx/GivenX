package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

// ─── Lawyer ───────────────────────────────────────────────────────────────────

/**
 * JSON shape for a single lawyer.
 * Supports both camelCase keys (Postman mock) and snake_case keys (real MySQL server)
 * via Gson @SerializedName alternate values.
 */
data class LawyerDto(
    @SerializedName(value = "id")                                          val id:            String  = "",
    @SerializedName(value = "name")                                        val name:          String  = "",
    @SerializedName(value = "specialty")                                   val specialty:     String  = "",
    @SerializedName(value = "location")                                    val location:      String  = "",
    @SerializedName(value = "experience")                                  val experience:    Int     = 0,
    @SerializedName(value = "rating")                                      val rating:        Float   = 0f,
    @SerializedName(value = "compatibility")                               val compatibility: Int     = 0,
    @SerializedName(value = "reviewCount",  alternate = ["review_count"])  val reviewCount:   Int     = 0,
    @SerializedName(value = "bio")                                         val bio:           String  = "",
    @SerializedName(value = "isVerified",   alternate = ["is_verified"])   val isVerified:    Boolean = true,
    @SerializedName(value = "domaine")                                     val domaine:       String  = "",
    @SerializedName(value = "avatarUrl",    alternate = ["avatar_url"])     val avatarUrl:     String  = ""
)

/** Standard list envelope: { "data": [...], "total": 42 } */
data class LawyerListResponse(
    @SerializedName("data")  val data:  List<LawyerDto> = emptyList(),
    @SerializedName("total") val total: Int             = 0
)
