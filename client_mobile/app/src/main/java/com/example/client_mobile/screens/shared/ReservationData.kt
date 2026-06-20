package com.example.client_mobile.screens.shared

/**
 * Consultation mode options with their associated pricing in DH.
 */
enum class ConsultationMode(val label: String, val price: Int, val icon: String) {
    VIDEO("Consultation Vidéo", 350, "videocam"),
    CABINET("Consultation en Cabinet", 700, "business"),
    MESSAGE("Message Prioritaire", 150, "chat")
}

/**
 * Immutable snapshot of all reservation + payment data.
 * Passed to the parent via `onPaymentValidated` — the parent decides
 * what to do with it (API call, grant messaging access, etc.).
 */
data class ReservationData(
    val nom: String,
    val contact: String,
    val domaine: String,
    val description: String,
    val mode: ConsultationMode,
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String,
    val lawyerId: String,
    val lawyerName: String
)
