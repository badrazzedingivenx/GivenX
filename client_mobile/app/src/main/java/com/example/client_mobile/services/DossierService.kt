package com.example.client_mobile.services

import com.example.client_mobile.network.DossierApiRepository
import com.example.client_mobile.network.TokenManager

data class DossierData(
    val id: String = "",
    val caseNumber: String = "",
    val category: String = "",
    val status: String = "",
    val openingDate: String = "",
    val lawyerId: String = "",
    val lawyerName: String = "",
    val lawyerSpecialty: String = "",
    /** 0–100 progress value (maps to CaseStep activeIndex in UI) */
    val progress: Int = 0
)

/**
 * Thin facade over [DossierApiRepository].
 * ViewModels call DossierApiRepository directly; this object exists for
 * backward compatibility in case other code still references DossierService.
 */
object DossierService {

    /**
     * Returns all dossiers for the currently logged-in user via REST API.
     * Returns an empty list if not authenticated or on network error.
     */
    suspend fun getDossiersForCurrentUser(): List<DossierData> =
        DossierApiRepository.getDossiersForCurrentUser(TokenManager.getUserId())

    /**
     * Returns a single dossier by its API ID.
     * Returns null if not found or on error.
     */
    suspend fun getDossierById(dossierDocId: String): DossierData? =
        DossierApiRepository.getDossierById(dossierDocId)
}

