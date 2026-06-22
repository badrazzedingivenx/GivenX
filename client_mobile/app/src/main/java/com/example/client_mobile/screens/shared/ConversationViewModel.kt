package com.example.client_mobile.screens.shared

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import com.example.client_mobile.screens.shared.ConsultationRepository
import com.example.client_mobile.screens.shared.UserSession

class ConversationViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun clearError() {
        _errorMessage.value = null
        _isError.value = false
    }

    init {
        fetch()
    }

    /** Pull-to-refresh trigger. */
    fun refresh() {
        if (_isLoading.value || _isRefreshing.value) return
        _isRefreshing.value = true
        fetch(isRefresh = true)
    }

    fun fetch(isRefresh: Boolean = false) {
        if (!TokenManager.isLoggedIn()) return
        if (!isRefresh) _isLoading.value = true
        _isError.value = false
        val isLawyer = TokenManager.getUserType() == "lawyer"
        viewModelScope.launch {
            try {
                // Fetch valid lawyer IDs from Consultations, Messages, and Profiles concurrently
                val messagesDeferred = async { 
                    if (isLawyer) RetrofitClient.haqApi.getMessages(lawyerId = TokenManager.getUserIdInt().toString())
                    else RetrofitClient.haqApi.getMessages(clientId = TokenManager.getUserIdInt().toString()) 
                }
                val profilesDeferred = async { RetrofitClient.haqApi.getProfiles() }
                val consultationsDeferred = if (!isLawyer) {
                    async { RetrofitClient.haqApi.getConsultations(clientId = TokenManager.getUserIdInt()) }
                } else null

                val response = messagesDeferred.await()
                val profilesResponse = try { profilesDeferred.await() } catch (e: Exception) { null }
                val profiles = profilesResponse?.body()?.data ?: emptyList()

                var validLawyerIds = setOf<String>()
                if (!isLawyer) {
                    try {
                        val consResponse = consultationsDeferred?.await()
                        if (consResponse?.isSuccessful == true) {
                            validLawyerIds = consResponse.body()?.data?.map { it.lawyerId.toString() }?.toSet() ?: emptySet()
                        }
                    } catch (e: Exception) {
                        // Keep empty set on failure to enforce strict zero-trust
                    }
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    val rawDtos = response.body()?.data ?: emptyList()
                    
                    // Filter DTOs based on strictly loaded business rules (must have paid active Consultation)
                    val filteredDtos = if (!isLawyer) {
                        rawDtos.filter { dto ->
                            val lId = dto.lawyer?.id
                            lId != null && validLawyerIds.contains(lId)
                        }
                    } else {
                        rawDtos
                    }

                    val mappedConversations = filteredDtos.map { dto ->
                        val targetId = if (isLawyer) dto.client?.id?.toIntOrNull() else dto.lawyer?.id?.toIntOrNull()
                        val matchingProfile = profiles.find { it.id == targetId }
                        
                        val resolvedAvatar = if (dto.displayAvatar(isLawyer).isNotBlank()) {
                            dto.displayAvatar(isLawyer)
                        } else {
                            matchingProfile?.avatarUrl ?: ""
                        }

                        val resolvedName = if (dto.displayName(isLawyer).isNotBlank() && dto.displayName(isLawyer).lowercase() != "avocat") {
                            dto.displayName(isLawyer)
                        } else {
                            matchingProfile?.fullName ?: "Avocat"
                        }

                        Conversation(
                            id             = dto.id,
                            otherPartyName = resolvedName,
                            lastMessage    = dto.displayLastMessage(),
                            timestamp      = dto.displayTimestamp(),
                            unreadCount    = dto.displayUnreadCount(isLawyer),
                            avatarUrl      = resolvedAvatar,
                            lawyerId       = dto.lawyer?.id ?: "",
                            clientId       = dto.client?.id ?: ""
                        )
                    }
                    
                    // Group by lawyerId to eliminate duplicates (keeping the most recent)
                    val distinctConversations = mappedConversations
                        .groupBy { it.lawyerId }
                        .map { (_, convs) -> convs.maxByOrNull { it.timestamp } ?: convs.first() }

                    ConversationRepository.replaceFromApi(distinctConversations)
                    
                    // Force inject all locally saved ACTIVE consultations into the inbox
                    if (!isLawyer) {
                        val clientId = TokenManager.getUserIdInt().toString()
                        ConsultationRepository.getActiveConsultations(clientId).forEach { cons ->
                            ConversationRepository.getOrCreate(
                                lawyerId = cons.lawyerId,
                                lawyerName = cons.lawyerName,
                                clientName = UserSession.name,
                                avatarUrl  = cons.avatarUrl
                            )
                        }
                    }
                }
            } catch (_: Exception) {
                // Genuine network failure (no internet, timeout, etc.)
                _isError.value = ConversationRepository.conversations.isEmpty()
                _errorMessage.value = "Erreur réseau. Vérifiez votre connexion."
            } finally {
                _isLoading.value = false
                _isRefreshing.value = false
            }
        }
    }

    /**
     * Delete a conversation optimistically from the UI and execute the backend DELETE request.
     */
    fun deleteConversation(conversationId: String) {
        // 1. Optimistic UI update
        ConversationRepository.removeConversation(conversationId)
        
        // 2. Network DELETE request
        viewModelScope.launch {
            try {
                val response = RetrofitClient.haqApi.deleteConversation(conversationId)
                if (!response.isSuccessful) {
                    // Revert UI if needed, but for json-server we assume success or ignore failure
                    Log.e("ConversationViewModel", "Failed to delete conversation on server: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ConversationViewModel", "Error deleting conversation", e)
            }
        }
    }
}
