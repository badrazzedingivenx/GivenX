package com.example.client_mobile.network

import com.example.client_mobile.network.dto.LawyerSearchResultDto
import com.example.client_mobile.network.dto.LegalPostDto
import com.example.client_mobile.network.dto.LikeResponseDto
import com.example.client_mobile.network.dto.LiveDto
import com.example.client_mobile.network.dto.ReelDto
import com.example.client_mobile.network.dto.SendMessageRequest
import com.example.client_mobile.network.dto.SendMessageResponseDto
import com.example.client_mobile.network.dto.StoryDto
import com.example.client_mobile.screens.shared.Conversation
import com.example.client_mobile.screens.shared.ConversationRepository

/**
 * Unified repository that merges Reels, Search (lawyers), and Messaging.
 * Standardized for the production-like Node.js/Express backend.
 */
object MainRepository {

    // ── Reels & Stories ───────────────────────────────────────────────────────

    suspend fun getReels(): List<ReelDto> {
        return try {
            val response = RetrofitClient.haqApi.getReels()
            if (response.isSuccessful) response.body()?.data ?: emptyList() else emptyList()
        } catch (_: Exception) { emptyList() }
    }

    suspend fun getLegalFeed(): List<LegalPostDto> {
        return try {
            val response = RetrofitClient.haqApi.getLegalFeed()
            if (response.isSuccessful) response.body()?.data ?: emptyList() else emptyList()
        } catch (_: Exception) { emptyList() }
    }

    suspend fun getStories(): List<StoryDto> {
        return try {
            val response = RetrofitClient.haqApi.getStories()
            if (response.isSuccessful) response.body()?.data ?: emptyList() else emptyList()
        } catch (_: Exception) { emptyList() }
    }

    suspend fun toggleLike(reelId: String): LikeResponseDto? {
        return try {
            val response = RetrofitClient.haqApi.likeReel(reelId)
            if (response.isSuccessful) response.body()?.data else null
        } catch (_: Exception) { null }
    }

    // ── Search ────────────────────────────────────────────────────────────────

    suspend fun searchLawyers(query: String): List<LawyerSearchResultDto> {
        return try {
            val response = RetrofitClient.haqApi.getLawyers(query = query, limit = 20)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.map { dto ->
                    LawyerSearchResultDto(
                        id        = dto.id,
                        name      = dto.name,
                        specialty = dto.specialty,
                        avatarUrl = dto.avatarUrl,
                        rating    = dto.rating,
                        domaine   = dto.domaine
                    )
                } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) { emptyList() }
    }

    // ── Messaging & Live ──────────────────────────────────────────────────────

    suspend fun getLives(): List<LiveDto> {
        return try {
            val response = RetrofitClient.haqApi.getLives()
            if (response.isSuccessful) response.body()?.data ?: emptyList() else emptyList()
        } catch (_: Exception) { emptyList() }
    }

    suspend fun sendMessage(
        conversationId: String,
        content: String,
        senderName: String,
        isFromUser: Boolean,
        tempId: String? = null,
        document: com.example.client_mobile.screens.shared.VaultDocument? = null
    ): SendMessageResponseDto? {
        // Optimistic local insert
        if (isFromUser) {
            ConversationRepository.sendUserMessage(conversationId, content, senderName, tempId, document)
        } else {
            ConversationRepository.sendLawyerMessage(conversationId, content, senderName, tempId, document)
        }

        return try {
            // Attempt to map local placeholder IDs to real API IDs
            var actualConversationId = conversationId
            if (actualConversationId.contains("_")) {
                val localConvo = ConversationRepository.conversations.find { it.id == conversationId }
                if (localConvo != null) {
                    val apiConvo = ConversationRepository.conversations.find { 
                        it.otherPartyName == localConvo.otherPartyName && !it.id.contains("_") 
                    }
                    if (apiConvo != null) {
                        actualConversationId = apiConvo.id
                    } else {
                        // Persist conversation to the server!
                        val newConvoId = "conv_${System.currentTimeMillis()}"
                        val clientIdStr = com.example.client_mobile.network.TokenManager.getUserIdInt().toString()
                        val lawyerIdStr = conversationId.substringAfterLast("_")
                        
                        val newConvo = com.example.client_mobile.network.dto.ConversationApiDto(
                            id = newConvoId,
                            client = com.example.client_mobile.network.dto.ConvoParticipant(id = clientIdStr, fullName = senderName),
                            lawyer = com.example.client_mobile.network.dto.ConvoParticipant(id = lawyerIdStr, fullName = localConvo.otherPartyName, avatarUrl = localConvo.avatarUrl),
                            createdAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault()).apply { timeZone = java.util.TimeZone.getTimeZone("UTC") }.format(java.util.Date())
                        )
                        
                        try {
                            val createResponse = RetrofitClient.haqApi.createConversation(newConvo)
                            if (createResponse.isSuccessful) {
                                val createdConvo = createResponse.body()?.data
                                if (createdConvo != null && createdConvo.id.isNotBlank()) {
                                    actualConversationId = createdConvo.id
                                    // IMPORTANT: Replace the local mock ID with the real server ID so the UI state matches the backend
                                    ConversationRepository.replaceId(conversationId, actualConversationId)
                                } else {
                                    actualConversationId = newConvoId
                                    ConversationRepository.replaceId(conversationId, actualConversationId)
                                }
                            } else {
                                actualConversationId = newConvoId
                                ConversationRepository.replaceId(conversationId, actualConversationId)
                            }
                        } catch (e: Exception) {
                            // Fallback to local if server fails to create
                            actualConversationId = newConvoId
                            ConversationRepository.replaceId(conversationId, actualConversationId)
                        }
                    }
                }
            }

            val request = SendMessageRequest(
                conversationId = actualConversationId,
                content = content,
                documentUrl = document?.urlOrUri,
                documentName = document?.name,
                documentMime = document?.mimeType
            )
            val response = RetrofitClient.haqApi.sendMessage(actualConversationId, request)
            if (response.isSuccessful) response.body()?.data else null
        } catch (_: Exception) { null }
    }

    fun getOrCreateConversation(
        lawyerId: String,
        lawyerName: String,
        clientName: String
    ): Conversation = ConversationRepository.getOrCreate(lawyerId, lawyerName, clientName)
}
