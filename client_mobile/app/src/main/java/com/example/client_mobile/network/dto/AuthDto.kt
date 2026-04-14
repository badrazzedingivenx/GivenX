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
    @SerializedName("phone")      val phone:     String = "",
    @SerializedName("role")       val role:      String = "user"
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
 * One entry inside the "profiles" map returned by the multi-profile mock:
 *   { "token": "...", "role": "CLIENT", "user": { ... } }
 */
data class ProfileEntry(
    @SerializedName("token") val token: String  = "",
    @SerializedName("role")  val role:  String  = "",
    @SerializedName("user")  val user:  UserDto? = null
)

/**
 * Flexible auth response that handles three shapes:
 *
 *   1. Multi-profile mock (Mockable.io testing):
 *      { "profiles": { "tarik@example.com": { "token": "...", "role": "CLIENT", "user": {...} }, ... } }
 *
 *   2. Real API envelope:
 *      { "success": true, "data": { "profile": {...}, "token": "..." } }
 *
 *   3. Legacy flat mock:
 *      { "token": "abc123", "role": "user", "user": {...} }
 */
data class AuthResponse(
    // Multi-profile mock map — keyed by email address
    @SerializedName("profiles")   val profiles:     Map<String, ProfileEntry>? = null,
    // Real API envelope
    @SerializedName("success")    val success:      Boolean       = false,
    @SerializedName("data")       val data:         AuthLoginData? = null,
    // Legacy mock flat fields
    @SerializedName("token")      val legacyToken:  String?       = null,
    @SerializedName("expires_in") val expiresIn:    Long          = 3600L,
    @SerializedName("role")       val role:         String        = "",
    @SerializedName("user")       val legacyUser:   UserDto?      = null,
    // New fields directly in response if any
    @SerializedName("userId")     val userId:       String?       = null,
    @SerializedName("message")    val message:      String?       = null
) {
    /**
     * Looks up [email] in the profiles map and returns the matching entry,
     * or null if this response does not use the multi-profile format.
     */
    fun profileFor(email: String): ProfileEntry? =
        profiles?.get(email.lowercase().trim())

    /** JWT — tries nested envelope first, then flat field. */
    fun effectiveToken(): String? = data?.token?.takeIf { it.isNotBlank() } ?: legacyToken?.takeIf { it.isNotBlank() }
    /** User profile — tries nested envelope first, then flat field. */
    fun effectiveUser():  UserDto? = data?.profile ?: legacyUser
}

// JSON shape from /api/auth/me (Postman mock + real server):
// { "id": 1, "firstName": "Tarik", "lastName": "Haq", "email": "tarik@example.com" }
// Note: id may be an Int in mock — the custom Gson in RetrofitClient handles Int→String coercion.
data class UserDto(
    @SerializedName("id")          val id:          String = "",
    @SerializedName("firstName")   val firstName:   String = "",
    @SerializedName("lastName")    val lastName:    String = "",
    /** Real API uses full_name; mock may use name or firstName+lastName. */
    @SerializedName("full_name")   val fullName:    String = "",
    @SerializedName("name")        val name:        String = "",
    @SerializedName("email")       val email:       String = "",
    @SerializedName("phone")       val phone:       String = "",
    @SerializedName("address")     val address:     String = "",
    @SerializedName("city")        val city:        String = "",
    /** Mock server may use avatar, photoUrl, or avatar_url. */
    @SerializedName("avatar")      val avatar:      String = "",
    @SerializedName("photoUrl")    val photoUrl:    String = "",
    @SerializedName("avatar_url")  val avatarUrl:   String = "",
    /** Server-confirmed role: "user" | "lawyer". Drives RBAC routing. */
    @SerializedName("role")        val role:        String = "",
    // ── Lawyer-only fields (populated when role == "lawyer") ─────────────────
    @SerializedName("bar_number")  val barNumber:   String = "",
    @SerializedName("barNumber")   val barNumberCamel: String = "",
    @SerializedName(value = "specialty", alternate = ["speciality"]) val specialty: String = ""
) {
    /** Effective bar number (supports both snake_case and camelCase). */
    fun effectiveBarNumber(): String = barNumber.ifBlank { barNumberCamel }
    /** Effective display name (prefers full_name, then name, then first+last). */
    fun effectiveFullName(): String = fullName.ifBlank { name.ifBlank { "$firstName $lastName".trim() } }
    /** Effective avatar URL (prefers snake_case real API, then camelCase, then plain avatar). */
    fun effectiveAvatarUrl(): String = avatarUrl.ifBlank { photoUrl.ifBlank { avatar } }
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

/** Body for POST /api/auth/register-user */
data class RegisterUserRequest(
    @SerializedName("full_name")  val fullName:   String,
    @SerializedName("email")      val email:      String,
    @SerializedName("password")   val password:   String,
    @SerializedName("phone")      val phone:      String = "",
    @SerializedName("address")    val address:    String = ""
)

/** Body for POST /api/auth/register-lawyer */
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

/** Body for PUT /api/lawyers/me */
data class UpdateLawyerProfileRequest(
    @SerializedName("full_name")        val fullName:        String = "",
    @SerializedName("phone")            val phone:           String = "",
    @SerializedName("address")          val address:         String = "",
    @SerializedName("bio")              val bio:             String = "",
    @SerializedName("speciality")       val speciality:      String = "",
    @SerializedName("years_experience") val yearsExperience: Int    = 0,
    @SerializedName("is_available")     val isAvailable:     Boolean = true
)

/** Nested data body inside the real API's { "success": true, "data": {...} } login envelope. */
data class AuthLoginData(
    @SerializedName("profile")       val profile:      UserDto? = null,
    @SerializedName("token")         val token:        String?  = null,
    @SerializedName("refresh_token") val refreshToken: String?  = null,
    @SerializedName("userId")        val userId:       String?  = null,
    @SerializedName("message")       val message:      String?  = null
)
