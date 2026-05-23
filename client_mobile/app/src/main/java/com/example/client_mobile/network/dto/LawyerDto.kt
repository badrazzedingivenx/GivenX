package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

// ─── Lawyer ───────────────────────────────────────────────────────────────────

/**
 * Maps the `lawyers` table returned by the API.
 *
 * Breaking changes vs. old schema:
 *  - `speciality`  is now the primary key (DB column name); `specialty` kept as alternate.
 *  - `city` and `location` are now **separate** fields (both exist as DB columns).
 *  - `years_experience` is the primary key for experience.
 *  - `review_count` is the primary key for reviewCount.
 *  - `avatar_url` now carries an **absolute URL** — pass directly to Coil, no base-URL prefix.
 *  - `is_verified` / `is_available` are primary keys (tinyint → Boolean via Gson).
 *  - Added `barNumber` (`bar_number`), `userId` (`user_id`), `profileId` (`profile_id`).
 */
data class LawyerDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    // DB column: `speciality` — alternate keeps old camelCase responses working
    @SerializedName(value = "speciality", alternate = ["specialty"])
    val speciality: String? = null,

    // Separate columns in the `lawyers` table
    @SerializedName("location")
    val location: String? = null,

    @SerializedName("city")
    val city: String? = null,

    @SerializedName(value = "years_experience", alternate = ["yearsExperience", "experience"])
    val yearsExperience: Int? = null,

    @SerializedName("rating")
    val rating: Float? = null,

    @SerializedName(value = "review_count", alternate = ["reviewCount"])
    val reviewCount: Int? = null,

    @SerializedName("bio")
    val bio: String? = null,

    @SerializedName(value = "is_verified", alternate = ["isVerified"])
    val isVerified: Boolean? = null,

    @SerializedName(value = "is_available", alternate = ["isAvailable"])
    val isAvailable: Boolean? = null,

    // DB column: `domaine`
    @SerializedName("domaine")
    val domaine: String? = null,

    // Absolute URL — use directly with Coil, no base-URL construction needed
    @SerializedName(value = "avatar_url", alternate = ["avatarUrl"])
    val avatarUrl: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName(value = "bar_number", alternate = ["barNumber"])
    val barNumber: String? = null,

    @SerializedName(value = "user_id", alternate = ["userId"])
    val userId: String? = null,

    @SerializedName(value = "profile_id", alternate = ["profileId"])
    val profileId: String? = null
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
