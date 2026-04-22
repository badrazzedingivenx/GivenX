package com.example.client_mobile.data.model.dto

import com.google.gson.annotations.SerializedName

/**
 * JSON shape for a single dossier.
 * Supports both camelCase keys and snake_case keys (production MySQL server)
 * via Gson @SerializedName alternate values.
 */
data class DossierDto(
    @SerializedName(value = "id")                                         val id:              String = "",
    @SerializedName(value = "caseNumber",      alternate = ["case_number"]) val caseNumber:      String = "",
    @SerializedName(value = "category")                                    val category:        String = "",
    @SerializedName(value = "status")                                      val status:          String = "",
    @SerializedName(value = "openingDate",     alternate = ["opening_date"]) val openingDate:    String = "",
    @SerializedName(value = "lawyerId",        alternate = ["lawyer_id"])   val lawyerId:       String = "",
    @SerializedName(value = "lawyerName",      alternate = ["lawyer_name"]) val lawyerName:     String = "",
    @SerializedName(value = "lawyerSpecialty", alternate = ["lawyer_specialty"]) val lawyerSpecialty: String = "",
    @SerializedName(value = "clientName",      alternate = ["client_name"]) val clientName:     String = "",
    @SerializedName(value = "progress")                                    val progress:        Int    = 0
)

/** Request body for PATCH /api/dossiers/{id}/status */
data class UpdateDossierStatusRequest(
    @SerializedName("status") val status: String
)
