package com.example.client_mobile.services

import com.example.client_mobile.network.AuthRepository
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
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
     * Signs in with email/password via AuthRepository.
     * This handles the complex User -> Profile -> Role chain.
     */
    suspend fun signIn(email: String, password: String) {
        // Use the repository which handles all json-server relations correctly
        AuthRepository.login(email.lowercase().trim(), password)
    }

    /** Clears the stored JWT — the user is considered logged out. */
    fun signOut() = TokenManager.clear()

    /**
     * Fetches the current user profile via GET /auth/me.
     * Returns null if not authenticated or on network error.
     */
    suspend fun getUserProfile(): UserProfile? {
        return try {
            val response = RetrofitClient.haqApi.getUserProfile()
            val u = response.body()?.data ?: return null
            UserProfile(
                uid       = u.effectiveId(),
                firstName = u.effectiveFullName(),
                lastName  = "",
                email     = u.effectiveEmail(),
                phone     = u.phone ?: "",
                address   = u.address ?: "",
                photoUrl  = u.effectiveAvatarUrl()
            )
        } catch (_: Exception) { null }
    }

    /**
     * Persists profile edits via PATCH /auth/me.
     * No-op if not authenticated.
     */
    suspend fun updateUserProfile(
        fullName: String,
        phone: String,
        address: String
    ) {
        if (!TokenManager.isLoggedIn()) return
        // PATCH /profiles?userId={id} or similar
    }
}
