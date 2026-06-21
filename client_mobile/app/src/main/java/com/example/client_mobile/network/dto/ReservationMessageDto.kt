package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

data class ReservationMessageDto(
    @SerializedName("id")            val id:            String = "",
    @SerializedName("senderId")      val senderId:      String = "",
    @SerializedName("receiverId")    val receiverId:    String = "",
    @SerializedName("reservationId") val reservationId: String = "",
    @SerializedName("content")       val content:       String = "",
    @SerializedName("timestamp")     val timestamp:     String = ""
)

data class SendReservationMessageRequest(
    @SerializedName("senderId")      val senderId:      String,
    @SerializedName("receiverId")    val receiverId:    String,
    @SerializedName("reservationId") val reservationId: String,
    @SerializedName("content")       val content:       String,
    @SerializedName("timestamp")     val timestamp:     String
)
