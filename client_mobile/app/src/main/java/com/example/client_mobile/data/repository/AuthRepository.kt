package com.example.client_mobile.data.repository

import android.util.Log
import com.example.client_mobile.data.model.dto.*
import com.example.client_mobile.data.remote.RetrofitClient
import com.example.client_mobile.core.utils.TokenManager
import com.google.gson.Gson

/**
 * Lead Developer Refactor: 
 * Role-based authentication using json-server relation mapping.
 */
object AuthRepository {

    suspend fun login(email: String, password: String) {
        // 1. Authenticate (Custom POST /auth/login)
        val response = RetrofitClient.authApi.login(LoginRequest(email.lowercase().trim(), password))
        val apiResponse = response.body()
        
        if (!response.isSuccessful || apiResponse?.success != true || apiResponse.data?.user == null) {
            throw Exception("Email ou mot de passe incorrect")
        }

        val authData = apiResponse.data!!
        val user = authData.user ?: throw Exception("Données utilisateur manquantes")
        val role = user.effectiveRole()
        
        // Save initial identity
        val userIdStr = user.effectiveId()
        val userIdInt = userIdStr.toDoubleOrNull()?.toInt() ?: -1

        TokenManager.saveToken(authData.token ?: "mock-jwt-token-for-$userIdStr")
        TokenManager.saveUserId(userIdInt)
        TokenManager.saveEmail(user.effectiveEmail())

        // 2. Fetch Profile (The bridge between User and Role Data)
        val profileResp = RetrofitClient.authApi.getProfileByUserId(userIdInt)
        val profile = profileResp.body()?.data?.firstOrNull() ?: throw Exception("Profil manquant")
        
        TokenManager.saveFullName(profile.fullName ?: "Utilisateur")
        TokenManager.saveAvatarUrl(profile.avatarUrl ?: "")

        // 3. Resolve Role-Specific Data (Lawyer ID or Client ID)
        if (role == "LAWYER") {
            val lawyerResp = RetrofitClient.authApi.getLawyerByProfileId(profile.id)
            val lawyer = lawyerResp.body()?.data?.firstOrNull() ?: throw Exception("Détails avocat manquants")
            
            TokenManager.saveUserType("lawyer")
            TokenManager.saveLawyerId(lawyer.effectiveId())
            
            // Create a combined UserDto for legacy UI compatibility
            val legacyUser = UserDto(
                id = user.effectiveId(),
                email = user.effectiveEmail(),
                role = "lawyer",
                fullName = profile.fullName ?: "",
                avatarUrl = profile.avatarUrl ?: "",
                phone = profile.phone ?: "",
                address = profile.address ?: "",
                specialty = lawyer.effectiveSpeciality(),
                barNumber = lawyer.effectiveBarNumber()
            )
            TokenManager.saveLawyerJson(Gson().toJson(legacyUser))
        } else {
            val clientResp = RetrofitClient.authApi.getClientByProfileId(profile.id)
            val client = clientResp.body()?.data?.firstOrNull() ?: throw Exception("Détails client manquants")
            
            TokenManager.saveUserType("user")
            TokenManager.saveClientId(client.id)
            
            val legacyUser = UserDto(
                id = user.effectiveId(),
                email = user.effectiveEmail(),
                role = "user",
                fullName = profile.fullName ?: "",
                avatarUrl = profile.avatarUrl ?: "",
                phone = profile.phone ?: "",
                address = profile.address ?: ""
            )
            TokenManager.saveUserJson(Gson().toJson(legacyUser))
        }
    }

    suspend fun autoLogin(): String? {
        if (!TokenManager.isLoggedIn()) return null
        return TokenManager.getUserType()
    }

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        phone: String = "",
        role: String = "CLIENT",
        speciality: String = ""
    ) {
        try {
            // 1. Create User
            val user = UserDto(
                id = (1000..9999).random().toString(), // Mock ID for json-server
                email = email,
                role = role.uppercase()
            )
            // In a real app with json-server, we'd POST to /users
            // RetrofitClient.authApi.createUser(user)

            // 2. Create Profile
            // (Mock POSTing to profiles table for json-server)
            // RetrofitClient.authApi.createProfile(ProfileDto(...))

            // 3. Save to Session
            TokenManager.saveToken("mock-token-" + user.effectiveId())
            TokenManager.saveUserId(user.effectiveId().toIntOrNull() ?: -1)
            TokenManager.saveEmail(user.effectiveEmail())
            TokenManager.saveUserType(user.effectiveRole())
            TokenManager.saveFullName(fullName)

            if (user.effectiveRole() == "LAWYER") {
                TokenManager.saveLawyerId(user.effectiveId().toIntOrNull() ?: -1)
            } else {
                TokenManager.saveClientId(user.effectiveId().toIntOrNull() ?: -1)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun logout() {
        TokenManager.clear()
    }

    fun isLoggedIn(): Boolean = TokenManager.isLoggedIn()
}
