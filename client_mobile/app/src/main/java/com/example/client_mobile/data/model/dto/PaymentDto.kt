package com.example.client_mobile.data.model.dto

import com.google.gson.annotations.SerializedName

/**
 * Data model for payments as stored in db.json
 */
data class PaymentDto(
    @SerializedName("id")       val id:       String = "",
    @SerializedName("clientId") val clientId: Int    = 0,
    @SerializedName("lawyerId") val lawyerId: Int?   = null,
    @SerializedName("date")     val date:     String = "",
    @SerializedName("amount")   val amount:   String = "",
    @SerializedName("status")   val status:   String = "", // Completed, Pending, Failed
    @SerializedName("subject")  val subject:  String = "",
    @SerializedName("method")   val method:   String = ""
)

/**
 * Summary of payments for the UI
 */
data class PaymentSummary(
    val totalPaid: String,
    val pendingAmount: String
)
