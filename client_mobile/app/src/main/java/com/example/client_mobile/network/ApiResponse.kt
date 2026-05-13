package com.example.client_mobile.network

import com.google.gson.annotations.SerializedName

/**
 * Generic envelope that wraps every REST API response.
 *
 * Success shape  : { "success": true,  "data": <T>,    "message": "OK" }
 * Error shape    : { "success": false, "data": null,   "message": "Unauthorized",
 *                    "error": "TOKEN_EXPIRED" }
 * List shape     : { "success": true,  "data": [...],  "total": 42 }
 *
 * Usage with Retrofit:
 *   @GET("lawyers")
 *   suspend fun getLawyers(): Response<ApiResponse<List<LawyerDto>>>
 *
 * Then in the repository:
 *   val body = response.body()
 *   if (response.isSuccessful && body?.success == true) {
 *       val lawyers = body.data   // typed as List<LawyerDto>?
 *   } else {
 *       val msg = body?.errorMessage()
 *   }
 */
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean                       = false,
    @SerializedName("data")    val data:    T?                            = null,
    @SerializedName("message") val message: String                        = "",
    @SerializedName("error")   val error:   String                        = "",
    // 422 Unprocessable Entity — map of field → list of validation messages
    @SerializedName("errors")  val errors:  Map<String, List<String>>?    = null,
    @SerializedName("total")   val total:   Int                           = 0
) {
    /** Returns a human-readable error string, falling back through available fields. */
    fun errorMessage(): String = message.ifBlank { error }.ifBlank { "Erreur inconnue" }

    /** Flattens validation errors into a single string for display. */
    fun validationMessage(): String =
        errors?.values?.flatten()?.joinToString("\n") ?: errorMessage()

    /** True only when the HTTP call succeeded AND the payload signals success. */
    val isSuccess: Boolean get() = success && data != null
}
