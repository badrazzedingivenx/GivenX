package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.screens.shared.UserSession
import com.example.client_mobile.services.UserProfile
import com.example.client_mobile.services.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    /** null = loading, blank string = not logged-in/no name */
    private val _firstName = MutableStateFlow<String?>(null)
    val firstName: StateFlow<String?> = _firstName

    /** true while a save is in progress */
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            try {
                val p = UserService.getUserProfile()
                _profile.value   = p
                _firstName.value = p?.firstName?.takeIf { it.isNotBlank() } ?: ""
                // Sync live Firestore data into UserSession for backward-compatible code paths
                p?.let {
                    val fullName = "${it.firstName} ${it.lastName}".trim()
                    if (fullName.isNotBlank()) UserSession.name = fullName
                    if (it.email.isNotBlank())   UserSession.email   = it.email
                    if (it.phone.isNotBlank())   UserSession.phone   = it.phone
                    if (it.address.isNotBlank()) UserSession.address = it.address
                }
            } catch (e: Exception) {
                _firstName.value = ""
            }
        }
    }

    /**
     * Persists the edited profile to Firestore then refreshes local state.
     */
    fun saveProfile(firstName: String, lastName: String, phone: String, address: String) {
        _isSaving.value = true
        viewModelScope.launch {
            try {
                UserService.updateUserProfile(firstName, lastName, phone, address)
                fetchProfile()
            } catch (e: Exception) {
                // Swallow – UI can show a retry if needed
            } finally {
                _isSaving.value = false
            }
        }
    }
}

