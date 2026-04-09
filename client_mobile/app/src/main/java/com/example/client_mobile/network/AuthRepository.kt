package com.example.client_mobile.network

import com.example.client_mobile.network.dto.LoginRequest
import com.example.client_mobile.network.dto.SignupRequest

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

        // Persist all session fields to EncryptedSharedPreferences
        TokenManager.saveToken(body.token)
        TokenManager.saveEmail(email.trim())
        TokenManager.saveUserType(effectiveRole)
        body.user?.id?.takeIf { it.isNotBlank() }?.let { TokenManager.saveUserId(it) }

        // Persist display name and avatar so the UI can render them immediately on cold start
        val fullName = body.user?.effectiveFullName()?.takeIf { it.isNotBlank() } ?: ""
        if (fullName.isNotBlank()) TokenManager.saveFullName(fullName)
        val avatarUrl = body.user?.effectiveAvatarUrl()?.takeIf { it.isNotBlank() } ?: ""
        if (avatarUrl.isNotBlank()) TokenManager.saveAvatarUrl(avatarUrl)
    }

    /**
     * Clears all stored credentials. The user is considered logged out
     * and must authenticate again before reaching the dashboard.
     */
    fun logout() = TokenManager.clear()

    /**
     * Posts a new account to POST /api/signup.
     * On success, persists the JWT, role, name, and email — same as login.
     * The caller's role is the authoritative fallback when the server omits it.
     *
     * @param role "user" | "lawyer"
     */
    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        phone: String = "",
        role: String = "user",
        speciality: String = ""
    ) {
        val response = RetrofitClient.authApi.signup(
            SignupRequest(fullName.trim(), email.trim(), password, phone.trim(), role, speciality.trim())
        )

        if (!response.isSuccessful || response.body() == null) {
            throw Exception("Inscription échouée. Veuillez réessayer.")
        }

        val body = response.body()!!
        if (body.token.isBlank()) throw Exception("Token manquant dans la réponse du serveur")

        val effectiveRole = body.user?.role?.takeIf { it.isNotBlank() } ?: role

        TokenManager.saveToken(body.token)
        TokenManager.saveEmail(email.trim())
        TokenManager.saveUserType(effectiveRole)
        body.user?.id?.takeIf { it.isNotBlank() }?.let { TokenManager.saveUserId(it) }

        val savedName = body.user?.effectiveFullName()?.takeIf { it.isNotBlank() } ?: fullName.trim()
        if (savedName.isNotBlank()) TokenManager.saveFullName(savedName)
    }

    /** Returns true if a non-blank token is currently stored. */
    fun isLoggedIn(): Boolean = TokenManager.isLoggedIn()
}
