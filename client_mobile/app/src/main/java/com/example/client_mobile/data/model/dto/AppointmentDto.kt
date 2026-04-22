package com.example.client_mobile.data.model.dto

import com.google.gson.annotations.SerializedName

// JSON shape from /api/appointments/me:
// { "id": "appt_001", "lawyerName": "...", "date": "2026-04-12", "time": "09:30" }
data class AppointmentDto(
    @SerializedName("id")         val id:         String = "",
    @SerializedName("lawyerName") val lawyerName: String = "",
    @SerializedName("specialty")  val specialty:  String = "",  // optional field
    @SerializedName("date")       val date:       String = "",
    @SerializedName("time")       val time:       String = "",
    @SerializedName("status")     val status:     String = ""   // optional field
)
