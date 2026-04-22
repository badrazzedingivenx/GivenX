package com.example.client_mobile.core.utils

data class DossierData(
    val id: String = "",
    val caseNumber: String = "",
    val category: String = "",
    val status: String = "",
    val openingDate: String = "",
    val lawyerId: String = "",
    val lawyerName: String = "",
    val lawyerSpecialty: String = "",
    val clientName: String = "",
    /** 0–100 progress value (maps to CaseStep activeIndex in UI) */
    val progress: Int = 0
)

