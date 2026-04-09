package com.example.client_mobile.network

import com.example.client_mobile.network.dto.LoginRequest

/**
 * Single source of truth for authentication.
 * All login / logout logic lives here; ViewModels call this object.
 */
object AuthRepository {

    /**
     * Posts credentials to POST /api/auth/login.
     * On success, stores the JWT token, userId, email, and userType in
     * [TokenManager] (EncryptedSharedPreferences).
     * Throws an [Exception] with a user-readable message on failure.
     *
     * @param userType "user" | "lawyer" — stored so the correct home screen
     *                 can be restored on next cold start.
     */
    suspend fun login(email: String, password: String, userType: String) {
        val response = RetrofitClient.authApi.login(LoginRequest(email.trim(), password))

        if (!response.isSuccessful || response.body() == null) {
            throw Exception("Identifiants incorrects")
        }

        val body = response.body()!!
        if (body.token.isBlank()) {
            throw Exception("Token manquant dans la réponse du serveur")
        }

        // Role is authoritative from the server; fall back to the UI-selected type
        // if the mock/server doesn't return it yet.
        val effectiveRole = body.user?.role?.takeIf { it.isNotBlank() } ?: userType

        TokenManager.saveToken(body.token)
        TokenManager.saveEmail(email.trim())
        TokenManager.saveUserType(effectiveRole)
        body.user?.id?.takeIf { it.isNotBlank() }?.let { TokenManager.saveUserId(it) }
    }

    /**
     * Clears all stored credentials. The user is considered logged out
     * and must authenticate again before reaching the dashboard.
     */
    fun logout() = TokenManager.clear()

    /** Returns true if a non-blank token is currently stored. */
    fun isLoggedIn(): Boolean = TokenManager.isLoggedIn()
}
