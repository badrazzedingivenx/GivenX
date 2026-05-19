package com.example.client_mobile.services

import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.network.dto.SaveConsultationRequest

data class ConsultationData(
    val id: String = "",
    val userId: String = "",
    val lawyerId: String = "",
    val lawyerName: String = "",
    val lawyerSpecialty: String = "",
    val status: String = "En attente",
    val progress: Int = 0
)

object ConsultationService {

    /**
     * Persists a consultation via POST /consultations.
     * No-op if the user is not authenticated (no stored userId).
     */
    suspend fun saveConsultation(
        lawyerFirestoreId: String,
        lawyerName: String,
        lawyerSpecialty: String
    ) {
        val userId = TokenManager.getUserId() ?: return
        try {
            RetrofitClient.haqApi.saveConsultation(
                SaveConsultationRequest(
                    userId          = userId,
                    lawyerId        = lawyerFirestoreId,
                    lawyerName      = lawyerName,
                    lawyerSpecialty = lawyerSpecialty
                )
            )
        } catch (_: Exception) { /* fire-and-forget, never crash the UI */ }
    }
}
