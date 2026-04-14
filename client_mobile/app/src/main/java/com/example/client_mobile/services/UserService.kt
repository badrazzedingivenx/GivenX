package com.example.client_mobile.services

import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.network.dto.LoginRequest
import com.example.client_mobile.network.dto.UpdateProfileRequest

data class UserProfile(
    val uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val photoUrl: String = ""
)

object UserService {

    /** Returns true if the user has a stored JWT (i.e. is logged in). */
    fun currentUser(): Boolean = TokenManager.isLoggedIn()

    /**
     * Signs in with email/password via POST /auth/login.
     * Stores JWT + userId in TokenManager on success.
     * Throws an exception on failure so callers can show an error message.
     */
    suspend fun signIn(email: String, password: String) {
        val response = RetrofitClient.authApi.login(LoginRequest(email, password))
        if (!response.isSuccessful || response.body() == null) {
            throw Exception("Identifiants incorrects")
        }
        val body = response.body()!!
        val token = body.effectiveToken() ?: ""
        TokenManager.saveToken(token)
        TokenManager.saveEmail(email)
        body.effectiveUser()?.id?.takeIf { it.isNotBlank() }?.let { TokenManager.saveUserId(it) }
    }

    /** Clears the stored JWT — the user is considered logged out. */
    fun signOut() = TokenManager.clear()

    /**
     * Fetches the current user profile via GET /auth/me.
     * Returns null if not authenticated or on network error.
     */
    suspend fun getUserProfile(): UserProfile? {
        return try {
            // Use mockApi (raw UserDto, no ApiResponse wrapper) when mock server is active.
            val response = RetrofitClient.mockApi.getMe()
            val u = response.body() ?: return null
            UserProfile(
                uid       = u.id,
                firstName = u.firstName,
                lastName  = u.lastName,
                email     = u.email,
                phone     = u.phone,
                address   = u.address,
                photoUrl  = u.photoUrl
            )
        } catch (_: Exception) { null }
    }

    /**
     * Persists profile edits via PATCH /auth/me.
     * No-op if not authenticated.
     */
    suspend fun updateUserProfile(
        firstName: String,
        lastName: String,
        phone: String,
        address: String
    ) {
        if (!TokenManager.isLoggedIn()) return
        val response = RetrofitClient.authApi.updateMe(
            UpdateProfileRequest(firstName = firstName, lastName = lastName,
                                 phone = phone, address = address)
        )
        if (!response.isSuccessful) {
            throw Exception("Erreur lors de la mise à jour du profil")
        }
    }
}
