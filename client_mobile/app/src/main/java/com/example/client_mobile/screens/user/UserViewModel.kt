package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.network.dto.UpdateProfileRequest
import com.example.client_mobile.network.dto.UserDto
import com.example.client_mobile.screens.shared.UserSession
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Single source of truth for the authenticated user's profile.
 *
 * Uses [UserDto] directly — the same model returned by GET /api/auth/me
 * and PATCH /api/auth/me — eliminating the separate UserProfile adapter layer.
 */
class UserViewModel : ViewModel() {

    private val _profile = MutableStateFlow<UserDto?>(null)
    val profile: StateFlow<UserDto?> = _profile

    /** true while GET /api/auth/me is in flight (for pull-to-refresh). */
    private val _isFetching = MutableStateFlow(false)
    val isFetching: StateFlow<Boolean> = _isFetching

    /** true after the initial fetch fails with a network error. */
    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    /** true while a PATCH /auth/me call is in flight */
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    /** Fires true once after a successful profile update; reset via [clearUpdateSuccess]. */
    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess

    /** Non-null when an error occurs; reset via [clearError]. */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun clearUpdateSuccess() { _updateSuccess.value = false }
    fun clearError()         { _errorMessage.value = null; _isError.value = false }

    init { fetchProfile() }

    /** Pull-to-refresh trigger on the profile screen. */
    fun refresh() { fetchProfile() }

    /** Fetches the user profile, seeding from cache first then refreshing in background. */
    fun fetchProfile() {
        if (!TokenManager.isLoggedIn()) return
        _isError.value = false

        // ── Step 1: render immediately from cache (no spinner, no wait) ──────
        if (_profile.value == null) {
            val cached = TokenManager.getUserJson()
            if (cached != null) {
                try {
                    val dto = Gson().fromJson(cached, UserDto::class.java)
                    _profile.value = dto
                    syncSession(dto)
                } catch (_: Exception) { /* malformed cache — ignore */ }
            }
        }

        // ── Step 2: silently refresh from network ─────────────────────────────
        _isFetching.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.haqApi.getUserProfile()
                if (response.isSuccessful && response.body()?.success == true) {
                    val dto = response.body()?.data ?: return@launch
                    _profile.value = dto
                    syncSession(dto)
                    TokenManager.saveUserJson(Gson().toJson(dto))
                } else {
                    if (_profile.value == null) _isError.value = true
                }
            } catch (_: Exception) {
                if (_profile.value == null) _isError.value = true
            } finally {
                _isFetching.value = false
            }
        }
    }

    /**
     * Sends PATCH /api/auth/me and, on success, updates [profile] directly
     * from the server response — no second fetch required.
     */
    fun saveProfile(fullName: String, phone: String, address: String) {
        if (!TokenManager.isLoggedIn()) return
        _isSaving.value = true
        viewModelScope.launch {
            try {
                // For json-server, update the profile table
                val userId = TokenManager.getUserIdInt()
                val profileResp = RetrofitClient.authApi.getProfileByUserId(userId)
                val currentProfile = profileResp.body()?.firstOrNull()
                
                if (currentProfile != null) {
                    val updates = mutableMapOf(
                        "fullName" to fullName.trim(),
                        "phone" to phone.trim(),
                        "address" to address.trim()
                    )
                    val updateResp = RetrofitClient.authApi.patchProfile(currentProfile.id, updates)
                    
                    if (updateResp.isSuccessful) {
                        val updatedProfile = updateResp.body()
                        _profile.value = (_profile.value ?: UserDto()).copy(
                            fullName = updatedProfile?.fullName ?: fullName.trim(),
                            phone = updatedProfile?.phone ?: phone.trim(),
                            address = updatedProfile?.address ?: address.trim()
                        )
                        _updateSuccess.value = true
                    } else {
                        _errorMessage.value = "Erreur lors de la mise à jour."
                    }
                }
            } catch (_: Exception) {
                _errorMessage.value = "Erreur réseau. Réessayez."
            } finally {
                _isSaving.value = false
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun syncSession(dto: UserDto) {
        val fullName = dto.effectiveFullName()
        if (fullName.isNotBlank())   UserSession.name     = fullName
        
        val email = dto.effectiveEmail()
        if (email.isNotBlank())      UserSession.email    = email
        
        val phone = dto.phone ?: ""
        if (phone.isNotBlank())      UserSession.phone    = phone
        
        val address = dto.address ?: ""
        if (address.isNotBlank())     UserSession.address = address

        val avatar = dto.effectiveAvatarUrl()
        if (avatar.isNotBlank())     UserSession.avatarUrl = avatar
    }
}

