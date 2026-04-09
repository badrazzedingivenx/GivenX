package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

// ─── Login / Register ─────────────────────────────────────────────────────────

data class LoginRequest(
    @SerializedName("email")    val email:    String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name")  val lastName:  String,
    @SerializedName("email")      val email:     String,
    @SerializedName("password")   val password:  String,
    @SerializedName("phone")      val phone:     String = ""
)

/** Body for POST /api/signup (Postman mock + real server). */
data class SignupRequest(
    @SerializedName("full_name")  val fullName:   String,
    @SerializedName("email")      val email:      String,
    @SerializedName("password")   val password:   String,
    @SerializedName("phone")      val phone:      String = "",
    @SerializedName("role")       val role:       String = "user",
    @SerializedName("speciality") val speciality: String = ""
)

/**
 * Successful auth response.
 * The JWT is stored via [com.example.client_mobile.network.TokenManager].
 */
data class AuthResponse(
    @SerializedName("token")      val token:     String,
    @SerializedName("expires_in") val expiresIn: Long   = 3600,
    @SerializedName("user")       val user:      UserDto? = null
)

// JSON shape from /api/auth/me (Postman mock + real server):
// { "id": 1, "firstName": "Tarik", "lastName": "Haq", "email": "tarik@example.com" }
// Note: id may be an Int in mock — the custom Gson in RetrofitClient handles Int→String coercion.
data class UserDto(
    @SerializedName("id")          val id:          String = "",
    @SerializedName("firstName")   val firstName:   String = "",
    @SerializedName("lastName")    val lastName:    String = "",
    /** Real API uses full_name; mock uses firstName+lastName. */
    @SerializedName("full_name")   val fullName:    String = "",
    @SerializedName("email")       val email:       String = "",
    @SerializedName("phone")       val phone:       String = "",
    @SerializedName("address")     val address:     String = "",
    /** Mock server returns camelCase photoUrl; real API returns avatar_url. */
    @SerializedName("photoUrl")    val photoUrl:    String = "",
    @SerializedName("avatar_url")  val avatarUrl:   String = "",
    /** Server-confirmed role: "user" | "lawyer". Drives RBAC routing. */
    @SerializedName("role")        val role:        String = ""
) {
    /** Effective display name (prefers server full_name, falls back to first+last). */
    fun effectiveFullName(): String = fullName.ifBlank { "$firstName $lastName".trim() }
    /** Effective avatar URL (prefers snake_case real API, falls back to camelCase mock). */
    fun effectiveAvatarUrl(): String = avatarUrl.ifBlank { photoUrl }
}

/** Generic API error body: { "error": "Invalid credentials" } */
data class ApiErrorResponse(
    @SerializedName("error")   val error:   String = "",
    @SerializedName("message") val message: String = ""
) {
    fun readable() = message.ifBlank { error }.ifBlank { "Erreur inconnue" }
}

/** Body for PATCH /auth/me */
data class UpdateProfileRequest(
    @SerializedName("first_name") val firstName: String = "",
    @SerializedName("last_name")  val lastName:  String = "",
    @SerializedName("phone")      val phone:     String = "",
    @SerializedName("address")    val address:   String = ""
)

/** Body for POST /consultations */
data class SaveConsultationRequest(
    @SerializedName("user_id")          val userId:         String = "",
    @SerializedName("lawyer_id")        val lawyerId:       String = "",
    @SerializedName("lawyer_name")      val lawyerName:     String = "",
    @SerializedName("lawyer_specialty") val lawyerSpecialty:String = "",
    @SerializedName("status")           val status:         String = "En attente"
)
