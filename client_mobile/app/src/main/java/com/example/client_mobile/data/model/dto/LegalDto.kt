package com.example.client_mobile.data.model.dto

import com.google.gson.annotations.SerializedName

/**
 * Profile model representing both LAWYER and CLIENT roles.
 */
data class Profile(
    @SerializedName("id")          val id: Int? = null,
    @SerializedName("email")       val email: String,
    @SerializedName("full_name")   val fullName: String,
    @SerializedName("role")        val role: String, // 'LAWYER' or 'CLIENT'
    @SerializedName("speciality")  val speciality: String? = null,
    @SerializedName("bar_number")  val barNumber: String? = null,
    @SerializedName("avatar_url")  val avatarUrl: String? = null,
    @SerializedName("phone")       val phone: String? = null
)

/**
 * Consultation model representing an appointment.
 */
data class Consultation(
    @SerializedName("id")          val id: Int? = null,
    @SerializedName("client_id")   val clientId: Int,
    @SerializedName("lawyer_id")   val lawyerId: Int,
    @SerializedName("status")      val status: String, // 'pending', 'accepted', 'rejected'
    @SerializedName("subject")     val subject: String,
    @SerializedName("date")        val date: String? = null // Optional: for db.json consistency
)

/**
 * Specialty model for legal categories.
 */
data class Specialty(
    @SerializedName("id")          val id: Int? = null,
    @SerializedName("name")        val name: String
)
