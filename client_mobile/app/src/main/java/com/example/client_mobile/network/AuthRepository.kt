package com.example.client_mobile.network

import android.util.Log
import com.example.client_mobile.network.dto.LoginRequest
import com.example.client_mobile.network.dto.RegisterLawyerRequest
import com.example.client_mobile.network.dto.RegisterRequest
import com.example.client_mobile.network.dto.SignupRequest
import com.example.client_mobile.screens.shared.LawyerSession
import com.example.client_mobile.screens.shared.UserSession
import com.google.gson.Gson

/**
 * Single source of truth for authentication.
 * All login / logout / auto-login logic lives here; ViewModels call this object.
 */
object AuthRepository {

    /**
     * Posts credentials to POST /api/login.
     * Supports both the real API envelope { "success", "data": { "profile", "token" } }
     * and the legacy mock flat format { "token", "user" }.
     * Throws an [Exception] with a user-readable message on failure.
     */
    suspend fun login(email: String, password: String, userType: String) {
        val request = LoginRequest(email.lowercase().trim(), password)
        val loginBody = Gson().toJson(request)
        Log.d("API_DEBUG", "Body: $loginBody")

        val response = try {
            RetrofitClient.authApi.login(request)
        } catch (e: java.io.IOException) {
            Log.e("API_DEBUG", "Network error: ${e.message}", e)
            println("API Network Error: ${e.message}")
            throw Exception("Erreur de connexion au serveur")
        }

        Log.d("API_DEBUG", "Response Code: ${response.code()}")
        println("API Response Code: ${response.code()}")

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            Log.e("API_DEBUG", "Error Body: $errorBody")
            println("API Error Body: $errorBody")
            throw when (response.code()) {
                401  -> Exception("Email ou mot de passe incorrect")
                403  -> Exception("Compte suspendu ou en attente de vérification")
                404  -> Exception("Serveur introuvable (vérifiez l'URL de base)")
                500  -> Exception("Erreur interne du serveur. Réessayez plus tard.")
                else -> Exception("Erreur ${response.code()} : $errorBody")
            }
        }

        if (response.body() == null) {
            Log.e("API_DEBUG", "HTTP 200 but body is null — mapping error (check AuthResponse fields)")
            println("API Response Code: 200 — body is null (mapping error)")
            throw Exception("Réponse du serveur invalide (corps vide)")
        }

        val body = response.body()!!

        // ── Shape 1: multi-profile mock — look up by the entered email ─────────
        val entry = body.profileFor(email)
        val token: String
        val user: com.example.client_mobile.network.dto.UserDto?
        val serverRole: String?

        if (entry != null) {
            // Found a matching profile in the "profiles" map
            token = entry.token.takeIf { it.isNotBlank() }
                ?: throw Exception("Token manquant pour ce compte")
            user  = entry.user
            serverRole = entry.role.takeIf { it.isNotBlank() } ?: user?.role
        } else {
            // ── Shape 2 / 3: real API envelope or legacy flat mock ────────────
            token = body.effectiveToken() ?: ""
            user = body.effectiveUser()
            // Priority: user.role → top-level body.role
            serverRole = user?.role?.takeIf { it.isNotBlank() } ?: body.role.takeIf { it.isNotBlank() }
        }

        // Trust serverRole if available, otherwise fallback to UI-provided userType
        val normalizedRole = serverRole?.lowercase()?.trim() ?: userType.lowercase().trim()
        val storedRole = when (normalizedRole) {
            "lawyer", "avocat" -> "lawyer"
            "user", "client"   -> "user"
            else               -> "user" // Default to client if unknown
        }

        TokenManager.saveToken(token)
        TokenManager.saveEmail(email.trim())
        TokenManager.saveUserType(storedRole)
        user?.id?.takeIf { it.isNotBlank() }?.let { TokenManager.saveUserId(it) }

        val fullName  = user?.effectiveFullName()?.takeIf { it.isNotBlank() } ?: ""
        if (fullName.isNotBlank())  TokenManager.saveFullName(fullName)
        val avatarUrl = user?.effectiveAvatarUrl()?.takeIf { it.isNotBlank() } ?: ""
        if (avatarUrl.isNotBlank()) TokenManager.saveAvatarUrl(avatarUrl)
        val city = user?.city?.takeIf { it.isNotBlank() } ?: ""
        if (city.isNotBlank())      TokenManager.saveCity(city)

        // Cache the full user object so Profile screens can render without a network call
        if (user != null) {
            val userJson = Gson().toJson(user)
            if (storedRole == "lawyer") {
                // Convert UserDto → LawyerProfileDto fields for the lawyer cache
                val lawyerDto = com.example.client_mobile.network.dto.LawyerProfileDto(
                    id              = user.id,
                    fullName        = user.effectiveFullName(),
                    email           = user.email,
                    phone           = user.phone,
                    address         = user.address,
                    avatarUrl       = user.effectiveAvatarUrl(),
                    speciality      = user.specialty,
                    barNumber       = user.effectiveBarNumber(),
                    role            = "lawyer"
                )
                TokenManager.saveLawyerJson(Gson().toJson(lawyerDto))
            } else {
                TokenManager.saveUserJson(userJson)
            }
        }
    }

    /**
     * If a stored token exists, fetches the user's profile from the appropriate
     * /me endpoint and refreshes UserSession / LawyerSession.
     * If the token is invalid (401), clears it so the user is redirected to login.
     * Returns the confirmed role ("user" | "lawyer") or null on any failure.
     */
    suspend fun autoLogin(): String? {
        if (!TokenManager.isLoggedIn()) return null
        return try {
            val role = TokenManager.getUserType()
            if (role == "lawyer") {
                val resp = RetrofitClient.authApi.getLawyerMe()
                if (resp.code() == 401) { TokenManager.clear(); return null }
                val dto = resp.body() ?: return role
                LawyerSession.fullName = dto.fullName.ifBlank { LawyerSession.fullName }
                LawyerSession.title    = dto.speciality.ifBlank { LawyerSession.title }
                LawyerSession.email    = dto.email.ifBlank { LawyerSession.email }
                LawyerSession.phone    = dto.phone.ifBlank { LawyerSession.phone }
                LawyerSession.address  = dto.address.ifBlank { LawyerSession.address }
                LawyerSession.bio      = dto.bio.ifBlank { LawyerSession.bio }
                TokenManager.saveFullName(dto.fullName)
                TokenManager.saveAvatarUrl(dto.avatarUrl)
                "lawyer"
            } else {
                val resp = RetrofitClient.authApi.getMe()
                if (resp.code() == 401) { TokenManager.clear(); return null }
                val dto = resp.body() ?: return role
                val name = dto.effectiveFullName().ifBlank { TokenManager.getFullName() }
                if (name.isNotBlank())            UserSession.name      = name
                if (dto.email.isNotBlank())        UserSession.email    = dto.email
                if (dto.phone.isNotBlank())        UserSession.phone    = dto.phone
                if (dto.address.isNotBlank())      UserSession.address  = dto.address
                val avatar = dto.effectiveAvatarUrl()
                if (avatar.isNotBlank())           UserSession.avatarUrl = avatar
                TokenManager.saveFullName(name)
                TokenManager.saveAvatarUrl(avatar)
                "user"
            }
        } catch (_: Exception) {
            // Network error — keep existing session data, don't invalidate token
            TokenManager.getUserType()
        }
    }

    /** Clears all stored credentials. The user must authenticate again. */
    fun logout() = TokenManager.clear()

    /**
     * Registers a new account using the generic /api/register endpoint.
     */
    suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        role: String
    ) {
        val request = RegisterRequest(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            email = email.trim().lowercase(),
            password = password.trim(),
            role = role.uppercase() // Mock expects uppercase CLIENT or LAWYER
        )
        
        val jsonBody = Gson().toJson(request)
        Log.d("RegisterBody", jsonBody)

        val response = RetrofitClient.authApi.register(request)

        if (!response.isSuccessful || response.body() == null) {
            val errorBody = response.errorBody()?.string()
            Log.e("RegisterError", "Error: $errorBody")
            throw Exception("Inscription échouée. Veuillez réessayer.")
        }

        val body = response.body()!!
        val token = body.effectiveToken() ?: ""
        
        // Only throw if your app logic strictly requires a token immediately after registration
        // if (token.isBlank()) throw Exception("Token manquant dans la réponse")

        val user = body.effectiveUser()
        val effectiveRole = user?.role?.uppercase() ?: role.uppercase()
        val storedRole = if (effectiveRole == "LAWYER") "lawyer" else "user"

        TokenManager.saveToken(token)
        TokenManager.saveEmail(email.trim().lowercase())
        TokenManager.saveUserType(storedRole)
        user?.id?.let { TokenManager.saveUserId(it) }
        
        val fullName = user?.effectiveFullName() ?: "$firstName $lastName".trim()
        TokenManager.saveFullName(fullName)
    }

    /**
     * Registers a new account (Legacy/Specific).
     * Routes to /auth/register-user or /auth/register-lawyer based on [role].
     */
    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        phone: String = "",
        role: String = "user",
        speciality: String = ""
    ) {
        val response = if (role == "lawyer") {
            RetrofitClient.authApi.signupLawyer(
                RegisterLawyerRequest(
                    fullName   = fullName.trim(),
                    email      = email.trim(),
                    password   = password,
                    phone      = phone.trim(),
                    speciality = speciality.trim()
                )
            )
        } else {
            RetrofitClient.authApi.signup(
                SignupRequest(
                    fullName   = fullName.trim(),
                    email      = email.trim(),
                    password   = password,
                    phone      = phone.trim(),
                    role       = role
                )
            )
        }

        if (!response.isSuccessful || response.body() == null) {
            throw Exception("Inscription échouée. Veuillez réessayer.")
        }

        val body  = response.body()!!
        val token = body.effectiveToken() ?: ""
        
        // if (token.isBlank()) throw Exception("Token manquant dans la réponse du serveur")

        val user         = body.effectiveUser()
        val effectiveRole = user?.role?.takeIf { it.isNotBlank() } ?: role

        TokenManager.saveToken(token)
        TokenManager.saveEmail(email.trim())
        TokenManager.saveUserType(effectiveRole)
        user?.id?.takeIf { it.isNotBlank() }?.let { TokenManager.saveUserId(it) }

        val savedName = user?.effectiveFullName()?.takeIf { it.isNotBlank() } ?: fullName.trim()
        if (savedName.isNotBlank()) TokenManager.saveFullName(savedName)
        val avatarUrl = user?.effectiveAvatarUrl()?.takeIf { it.isNotBlank() } ?: ""
        if (avatarUrl.isNotBlank()) TokenManager.saveAvatarUrl(avatarUrl)
    }

    /** Returns true if a non-blank token is currently stored. */
    fun isLoggedIn(): Boolean = TokenManager.isLoggedIn()
}

