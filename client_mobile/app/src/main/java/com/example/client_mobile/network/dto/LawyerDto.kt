package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

// ─── Lawyer ───────────────────────────────────────────────────────────────────

/**
 * JSON shape for a single lawyer.
 * Supports both camelCase keys and snake_case keys (production MySQL server)
 * via Gson @SerializedName alternate values.
 */
data class LawyerDto(
    @SerializedName(value = "id")                                                     val id:            String?  = null,
    @SerializedName(value = "name",        alternate = ["full_name"])                 val name:          String?  = null,
    @SerializedName(value = "specialty",   alternate = ["speciality"])               val specialty:     String?  = null,
    @SerializedName(value = "location",    alternate = ["city"])                     val location:      String?  = null,
    @SerializedName(value = "experience",  alternate = ["years_experience"])         val experience:    Int?     = null,
    @SerializedName(value = "rating")                                                 val rating:        Float?   = null,
    @SerializedName(value = "compatibility")                                          val compatibility: Int?     = null,
    @SerializedName(value = "reviewCount",  alternate = ["review_count"])            val reviewCount:   Int?     = null,
    @SerializedName(value = "bio")                                                    val bio:           String?  = null,
    @SerializedName(value = "isVerified",   alternate = ["is_verified"])             val isVerified:    Boolean? = null,
    @SerializedName(value = "isAvailable",  alternate = ["is_available"])            val isAvailable:   Boolean? = null,
    @SerializedName(value = "domaine")                                                val domaine:       String?  = null,
    @SerializedName(value = "avatarUrl",    alternate = ["avatar_url"])              val avatarUrl:     String?  = null,
    @SerializedName(value = "status")                                                 val status:        String?  = null
)

/** Standard list envelope: { "data": [...], "total": 42 } */
data class LawyerListResponse(
    @SerializedName("data")  val data:  List<LawyerDto> = emptyList(),
    @SerializedName("total") val total: Int             = 0
)

/**
 * Wrapper for GET /lawyers paginated response.
 * Shape: { "success":true, "data": { "data": [...], "pagination": { ... } } }
 * Access items via: response.body()?.data?.lawyers
 */
data class LawyersResponseDto(
    @SerializedName("data")       val lawyers:    List<LawyerDto>? = emptyList(),
    @SerializedName("pagination") val pagination: PaginationMeta?  = null
)

/**
 * Pagination metadata returned alongside paginated list responses.
 * Shape: { "total": 120, "page": 1, "limit": 50, "totalPages": 3 }
 */
data class PaginationMeta(
    @SerializedName("total")      val total:      Int = 0,
    @SerializedName("page")       val page:       Int = 1,
    @SerializedName("limit")      val limit:      Int = 50,
    @SerializedName("totalPages") val totalPages: Int = 1
)

/**
 * Generic wrapper for paginated API responses where the server returns:
 * { "data": { "data": [...items...], "pagination": { ... } } }
 *
 * Usage: ApiResponse<PaginatedData<LawyerDto>>
 * Extract the list via: response.body()?.data?.items
 */
data class PaginatedData<T>(
    @SerializedName("data")       val items:      List<T>         = emptyList(),
    @SerializedName("pagination") val pagination: PaginationMeta? = null
)
