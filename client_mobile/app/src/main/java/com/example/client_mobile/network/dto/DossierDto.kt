package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Maps the `dossiers` table returned by the API.
 *
 * Breaking change: the backend now returns a nested `lawyer` object instead of
 * (or in addition to) the flat `lawyer_name` / `lawyer_specialty` fields.
 * The helper functions below resolve the correct value from either shape,
 * so the rest of the app is insulated from the API migration.
 *
 * All String fields default to `""` so that legacy callers never receive null.
 */
data class DossierDto(
    @SerializedName("id")
    val id: String = "",

    @SerializedName(value = "case_number", alternate = ["caseNumber"])
    val caseNumber: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName(value = "opening_date", alternate = ["openingDate"])
    val openingDate: String? = null,

    // Flat FK — still present in DB; kept for cases where the API omits the nested object
    @SerializedName(value = "lawyer_id", alternate = ["lawyerId"])
    val lawyerId: String? = null,

    // Flat denormalised fields — still present in DB
    @SerializedName(value = "lawyer_name", alternate = ["lawyerName"])
    val lawyerNameFlat: String? = null,

    @SerializedName(value = "lawyer_specialty", alternate = ["lawyerSpecialty"])
    val lawyerSpecialtyFlat: String? = null,

    @SerializedName(value = "client_name", alternate = ["clientName"])
    val clientName: String? = null,

    @SerializedName("progress")
    val progress: Int = 0,

    // ── New: nested lawyer object ─────────────────────────────────────────────
    // The refactored API now embeds the full lawyer record directly.
    // avatar_url inside this object is an absolute URL.
    @SerializedName("lawyer")
    val lawyer: LawyerDto? = null
) {
    /** Resolves lawyer ID: nested object → flat field → empty string. */
    fun effectiveLawyerId(): String = lawyer?.id ?: lawyerId ?: ""

    /** Resolves lawyer display name: nested object → flat field → empty string. */
    fun effectiveLawyerName(): String =
        lawyer?.name?.takeIf { it.isNotBlank() } ?: lawyerNameFlat ?: ""

    /** Resolves lawyer specialty: nested object → flat field → empty string. */
    fun effectiveLawyerSpecialty(): String =
        lawyer?.speciality?.takeIf { it.isNotBlank() } ?: lawyerSpecialtyFlat ?: ""

    /** Resolves lawyer avatar URL (absolute). Returns empty string when absent. */
    fun effectiveLawyerAvatarUrl(): String = lawyer?.avatarUrl ?: ""
}

/** Request body for PATCH /api/dossiers/{id}/status */
data class UpdateDossierStatusRequest(
    @SerializedName("status") val status: String
)
