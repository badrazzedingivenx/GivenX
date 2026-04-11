package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

data class InvoiceDto(
    @SerializedName("id")          val id:         String  = "",
    @SerializedName("number")      val number:     String  = "",
    @SerializedName("lawyer_name") val lawyerName: String  = "",
    @SerializedName("amount")      val amount:     Float   = 0f,
    @SerializedName("status")      val status:     String  = "",
    @SerializedName("is_paid")     val isPaid:     Boolean = false
)

// JSON shape from /api/billing/me:
// { "paidAmount": 1240.5, "pendingAmount": 315.75 }
data class BillingSummaryDto(
    @SerializedName("paidAmount")    val paidAmount:    Float            = 0f,
    @SerializedName("pendingAmount") val pendingAmount: Float            = 0f,
    @SerializedName("invoices")      val invoices:      List<InvoiceDto> = emptyList()  // optional, not in mock
)
