package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

data class ReservationDto(
    @SerializedName("id")             val id:             String = "",
    @SerializedName("lawyerId")       val lawyerId:       String = "",
    @SerializedName("lawyerName")     val lawyerName:     String = "",
    @SerializedName("clientId")       val clientId:       String = "",
    @SerializedName("fullName")       val fullName:       String = "",
    @SerializedName("contact")        val contact:        String = "",
    @SerializedName("domain")         val domain:         String = "",
    @SerializedName("description")    val description:    String = "",
    @SerializedName("mode")           val mode:           String = "",
    @SerializedName("price")          val price:          Int    = 0,
    @SerializedName("status")         val status:         String = "pending",
    @SerializedName("paymentStatus")  val paymentStatus:  String = "unpaid",
    @SerializedName("createdAt")      val createdAt:      String = ""
)

data class CreateReservationRequest(
    @SerializedName("lawyerId")      val lawyerId:      String,
    @SerializedName("lawyerName")    val lawyerName:    String,
    @SerializedName("clientId")      val clientId:      String,
    @SerializedName("fullName")      val fullName:      String,
    @SerializedName("contact")       val contact:       String,
    @SerializedName("domain")        val domain:        String,
    @SerializedName("description")   val description:   String,
    @SerializedName("mode")          val mode:          String,
    @SerializedName("price")         val price:         Int,
    @SerializedName("paymentStatus") val paymentStatus: String = "unpaid"
)

data class UpdateReservationStatusRequest(
    @SerializedName("status") val status: String
)

data class UpdatePaymentStatusRequest(
    @SerializedName("paymentStatus") val paymentStatus: String
)
