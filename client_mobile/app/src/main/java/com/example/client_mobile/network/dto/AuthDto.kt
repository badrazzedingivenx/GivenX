package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

// ─── JSON-SERVER AUTH MODELS ─────────────────────────────────────────────────

data class LoginRequest(
    val email: String,
    val password: String
)

data class UserDto(
    @SerializedName("id")       val id:       String? = "",
    @SerializedName("email")    val email:    String? = "",
    @SerializedName("role")     val role:     String? = "", // "LAWYER" or "CLIENT"
    
    // Flattened fields for UI compatibility (populated from profiles table)
    @SerializedName("fullName")   val fullName:    String? = "",
    @SerializedName("avatarUrl")  val avatarUrl:   String? = "",
    @SerializedName("phone")      val phone:       String? = "",
    @SerializedName("address")    val address:     String? = "",
    @SerializedName("specialty")  val specialty:   String? = "",
    @SerializedName("barNumber")  val barNumber:   String? = "",
    
    // Legacy fields for backward compatibility with older UI code
    @SerializedName("firstName")  val firstName:   String? = "",
    @SerializedName("lastName")   val lastName:    String? = "",
    @SerializedName("photoUrl")   val photoUrl:    String? = ""
) {
    fun effectiveFullName(): String = (fullName ?: "").ifBlank { "${firstName ?: ""} ${lastName ?: ""}".trim().ifBlank { email ?: "" } }
    fun effectiveAvatarUrl(): String = (avatarUrl ?: "").ifBlank { photoUrl ?: "" }
    fun effectiveBarNumber(): String = barNumber ?: ""
    fun effectiveId(): String = id ?: ""
    fun effectiveEmail(): String = email ?: ""
    fun effectiveRole(): String = role ?: ""
}

data class ProfileDto(
    @SerializedName("id")        val id:        Int,
    @SerializedName("userId")    val userId:    Int,
    @SerializedName("fullName")  val fullName:  String?,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("phone")     val phone:     String?,
    @SerializedName("role")      val role:      String?,
    @SerializedName("address")   val address:   String? = ""
)

/** Professional details for LAWYER role */
data class LawyerDataDto(
    @SerializedName("id")          val id:         Int?,
    @SerializedName("profileId")   val profileId:  Int?,
    @SerializedName("speciality")  val speciality: String?,
    @SerializedName("bar_number")  val barNumber:  String?,
    @SerializedName("bio")         val bio:        String?,
    @SerializedName("profile")     val profile:    ProfileDto? = null
) {
    fun effectiveId(): Int = id ?: -1
    fun effectiveSpeciality(): String = speciality ?: ""
    fun effectiveBarNumber(): String = barNumber ?: ""
}

/** Specific details for CLIENT role */
data class ClientDataDto(
    @SerializedName("id")           val id:          Int,
    @SerializedName("profileId")    val profileId:   Int,
    @SerializedName("company_name") val companyName: String?
)

// ─── AUTH REQUESTS ────────────────────────────────────────────────────────────

data class SignupRequest(
    @SerializedName("full_name")  val fullName:   String,
    @SerializedName("email")      val email:      String,
    @SerializedName("password")   val password:   String,
    @SerializedName("phone")      val phone:      String = "",
    @SerializedName("role")       val role:       String = "CLIENT",
    @SerializedName("speciality") val speciality: String = ""
)

data class RegisterRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name")  val lastName:  String,
    @SerializedName("email")      val email:     String,
    @SerializedName("password")   val password:  String,
    @SerializedName("phone")      val phone:     String = "",
    @SerializedName("role")       val role:      String = "CLIENT"
)

data class RegisterLawyerRequest(
    @SerializedName("full_name")        val fullName:        String,
    @SerializedName("email")            val email:           String,
    @SerializedName("password")         val password:        String,
    @SerializedName("phone")            val phone:           String = "",
    @SerializedName("address")          val address:         String = "",
    @SerializedName("speciality")       val speciality:      String = "",
    @SerializedName("bar_association")  val barAssociation:  String = "",
    @SerializedName("bar_number")       val barNumber:       String = "",
    @SerializedName("years_experience") val yearsExperience: Int    = 0,
    @SerializedName("bio")              val bio:             String = ""
)

data class UpdateProfileRequest(
    @SerializedName("first_name") val firstName: String = "",
    @SerializedName("last_name")  val lastName:  String = "",
    @SerializedName("phone")      val phone:     String = "",
    @SerializedName("address")    val address:   String = ""
)

data class UpdateLawyerProfileRequest(
    @SerializedName("full_name")        val fullName:        String = "",
    @SerializedName("phone")            val phone:           String = "",
    @SerializedName("address")          val address:         String = "",
    @SerializedName("bio")              val bio:             String = "",
    @SerializedName("speciality")       val speciality:      String = "",
    @SerializedName("years_experience") val yearsExperience: Int    = 0,
    @SerializedName("is_available")     val isAvailable:     Boolean = true
)

// ─── LEGACY RESPONSE WRAPPER ──────────────────────────────────────────────────

data class AuthResponse(
    val success: Boolean = true,
    val token: String? = "json-server-mock-token",
    val user: UserDto? = null,
    val userId: Int? = null,
    val role: String? = null
) {
    fun effectiveToken(): String? = token
    fun effectiveUser(): UserDto? = user ?: userId?.let { UserDto(id = it.toString()) }
}
