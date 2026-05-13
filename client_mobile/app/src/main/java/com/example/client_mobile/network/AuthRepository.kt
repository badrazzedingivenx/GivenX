package com.example.client_mobile.network

import android.util.Log
import com.example.client_mobile.network.dto.*
import com.google.gson.Gson

/**
 * Authentication repository wired to the production HAQ API.
 *
 * All auth requests go through [NetworkModule.authApi] which points to:
 *   https://lavender-spoonbill-389199.hostingersite.com/api/v1/
 */
object AuthRepository {

    suspend fun login(email: String, password: String) {
        val response = NetworkModule.authApi.login(
            HaqLoginRequest(email.lowercase().trim(), password)
        )

        Log.d("AuthRepository", "HTTP ${response.code()} — isSuccessful=${response.isSuccessful}")

        val apiResponse = response.body()
        Log.d("AuthRepository", "body.success=${apiResponse?.success} body.message=${apiResponse?.message}")
        Log.d("AuthRepository", "body.data=${apiResponse?.data}")

        if (!response.isSuccessful) {
            val errBody = response.errorBody()?.string()
            Log.e("AuthRepository", "Error body: $errBody")
            val msg = apiResponse?.message?.ifBlank { null }
                ?: errBody?.take(200)
                ?: "Email ou mot de passe incorrect (${response.code()})"
            throw Exception(msg)
        }

        // Some servers return success=false even on 200 — be lenient and just check the data
        val authData = apiResponse?.data
        Log.d("AuthRepository", "authData=$authData")

        val token = authData?.accessToken?.ifBlank { null }
        if (token == null) {
            // Log the raw JSON for diagnosis
            Log.e("AuthRepository", "Token not found in data. Full body: $apiResponse")
            throw Exception("Token d'accès manquant — vérifiez les logs pour la réponse brute du serveur")
        }

        val user = authData.user
            ?: run {
                Log.w("AuthRepository", "user field null — proceeding with minimal session")
                null
            }

        // Persist session
        TokenManager.saveToken(token)
        TokenManager.saveUserId(user?.id ?: 0)
        TokenManager.saveEmail(user?.email ?: email)
        TokenManager.saveFullName(user?.fullName ?: "")
        TokenManager.saveAvatarUrl(user?.avatar ?: "")

        val userType = if (user?.role?.uppercase() == "LAWYER") "lawyer" else "user"
        TokenManager.saveUserType(userType)

        // Build legacy UserDto so older screens that read JSON from prefs keep working
        val legacyUser = UserDto(
            id        = (user?.id ?: 0).toString(),
            email     = user?.email ?: email,
            role      = userType,
            fullName  = user?.fullName ?: "",
            avatarUrl = user?.avatar ?: ""
        )

        if (userType == "lawyer") {
            TokenManager.saveLawyerId(user?.id ?: 0)
            TokenManager.saveLawyerJson(Gson().toJson(legacyUser))
        } else {
            TokenManager.saveClientId(user?.id ?: 0)
            TokenManager.saveUserJson(Gson().toJson(legacyUser))
        }

        Log.d("AuthRepository", "Login OK — role=$userType id=${user?.id}")
    }

    suspend fun autoLogin(): String? {
        if (!TokenManager.isLoggedIn()) return null
        return TokenManager.getUserType()
    }

    suspend fun register(
        fullName:   String,
        email:      String,
        password:   String,
        phone:      String = "",
        role:       String = "CLIENT",
        speciality: String = ""   // accepted for call-site compat; not sent to server
    ) {
        val response = NetworkModule.authApi.register(
            HaqRegisterRequest(
                fullName             = fullName,
                email                = email,
                password             = password,
                passwordConfirmation = password,
                role                 = role.uppercase(),
                phone                = phone.ifBlank { null }
            )
        )
        val apiResponse = response.body()

        if (!response.isSuccessful || apiResponse?.success != true) {
            val msg = apiResponse?.message ?: "Erreur lors de l'inscription (${response.code()})"
            throw Exception(msg)
        }

        val authData = apiResponse.data ?: throw Exception("Réponse du serveur invalide")
        if (authData.accessToken.isBlank()) throw Exception("Token d'accès manquant")

        val user = authData.user ?: throw Exception("Données utilisateur manquantes")

        TokenManager.saveToken(authData.accessToken)
        TokenManager.saveUserId(user.id)
        TokenManager.saveEmail(user.email)
        TokenManager.saveFullName(user.fullName)
        TokenManager.saveAvatarUrl(user.avatar ?: "")

        val userType = if (user.role.uppercase() == "LAWYER") "lawyer" else "user"
        TokenManager.saveUserType(userType)

        if (userType == "lawyer") {
            TokenManager.saveLawyerId(user.id)
        } else {
            TokenManager.saveClientId(user.id)
        }

        Log.d("AuthRepository", "Register OK — role=$userType id=${user.id}")
    }

    fun logout() {
        TokenManager.clear()
    }

    fun isLoggedIn(): Boolean = TokenManager.isLoggedIn()
}

