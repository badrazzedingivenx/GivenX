package com.example.client_mobile.screens.shared

import androidx.compose.runtime.mutableStateListOf

enum class ConsultationStatus {
    ACTIVE,
    COMPLETED
}

data class Consultation(
    val id: String,
    val clientId: String,
    val lawyerId: String,
    val lawyerName: String,
    val avatarUrl: String,
    val isPaid: Boolean,
    var status: ConsultationStatus
)

/**
 * Local in-memory store for Consultations, since the mock backend doesn't support it yet.
 */
object ConsultationRepository {
    private val consultations = mutableStateListOf<Consultation>()

    fun addConsultation(consultation: Consultation) {
        // Prevent duplicates
        val existing = consultations.find { it.id == consultation.id }
        if (existing == null) {
            consultations.add(consultation)
        } else {
            existing.status = consultation.status
        }
    }

    fun getActiveConsultations(clientId: String): List<Consultation> {
        return consultations.filter { it.clientId == clientId && it.status == ConsultationStatus.ACTIVE }
    }

    fun hasPaidActiveConsultation(clientId: String, lawyerId: String): Boolean {
        return consultations.any { 
            it.clientId == clientId && 
            it.lawyerId == lawyerId && 
            it.isPaid && 
            it.status == ConsultationStatus.ACTIVE 
        }
    }

    fun closeConsultation(consultationId: String) {
        val consultation = consultations.find { it.id == consultationId }
        if (consultation != null) {
            consultations[consultations.indexOf(consultation)] = consultation.copy(status = ConsultationStatus.COMPLETED)
        }
    }

    fun clear() {
        consultations.clear()
    }
}
