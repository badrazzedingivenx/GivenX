package com.example.client_mobile.network

import com.example.client_mobile.network.dto.DossierDto
import com.example.client_mobile.services.DossierData

/**
 * Repository that fetches dossiers from the REST API and maps them to the
 * domain model [DossierData] — the same type consumed by the UI.
 *
 * Replaces all Firestore calls previously in [com.example.client_mobile.services.DossierService].
 */
object DossierApiRepository {

    // ── Mapping ───────────────────────────────────────────────────────────────

    private fun DossierDto.toDomain() = DossierData(
        id              = id,
        caseNumber      = caseNumber.ifBlank { id },
        category        = category,
        status          = status,
        openingDate     = openingDate,
        lawyerId        = lawyerId,
        lawyerName      = lawyerName,
        lawyerSpecialty = lawyerSpecialty,
        progress        = progress
    )

    // ── List fetch ────────────────────────────────────────────────────────────

    /**
     * Returns the current user's dossiers via the /dossiers/me endpoint
     * (JWT-based, no explicit userId required).
     * Falls back to the userId-based endpoint if [userId] is provided.
     */
    suspend fun getDossiersForCurrentUser(userId: String? = null): List<DossierData> = try {
        val response = if (userId.isNullOrBlank()) {
            RetrofitClient.haqApi.getMyDossiers()
        } else {
            RetrofitClient.haqApi.getDossiers(userId)
        }
        if (response.isSuccessful) {
            response.body()?.data?.map { it.toDomain() } ?: emptyList()
        } else {
            emptyList()
        }
    } catch (_: Exception) {
        emptyList()
    }

    // ── Single fetch ──────────────────────────────────────────────────────────

    /**
     * Returns a single dossier by ID, or null if not found / on error.
     * The server performs ownership validation via the JWT.
     */
    suspend fun getDossierById(id: String): DossierData? = try {
        val response = RetrofitClient.haqApi.getDossierById(id)
        if (response.isSuccessful) response.body()?.data?.toDomain() else null
    } catch (_: Exception) {
        null
    }
}
