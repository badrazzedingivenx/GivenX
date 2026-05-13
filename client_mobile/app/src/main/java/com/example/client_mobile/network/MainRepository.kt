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
            if (response.isSuccessful) response.body()?.data?.reels ?: emptyList() else emptyList()
        } catch (_: Exception) { emptyList() }
    }

    suspend fun getLegalFeed(): List<LegalPostDto> {
        return try {
            val response = RetrofitClient.haqApi.getLegalFeed()
            // getLegalFeed reuses the /reels endpoint; map ReelDto → LegalPostDto-compatible fields
            if (response.isSuccessful)
                response.body()?.data?.reels?.map { reel ->
                    LegalPostDto(
                        lawyerName   = reel.lawyerName,
                        legalText    = reel.caption.ifBlank { reel.title },
                        likesCount   = reel.likes
                    )
                } ?: emptyList()
            else emptyList()
        } catch (_: Exception) { emptyList() }
    }

    suspend fun getStories(): List<StoryDto> {
        return try {
            val response = RetrofitClient.haqApi.getStories()
            if (response.isSuccessful) response.body()?.data?.stories ?: emptyList() else emptyList()
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
                response.body()?.data?.lawyers?.map { dto ->
                    LawyerSearchResultDto(
                        id        = dto.id        ?: "",
                        name      = dto.name      ?: "Avocat",
                        specialty = dto.specialty ?: "",
                        avatarUrl = dto.avatarUrl ?: "",
                        rating    = dto.rating    ?: 0f,
                        domaine   = dto.domaine   ?: ""
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
            if (response.isSuccessful) response.body()?.data?.lives ?: emptyList() else emptyList()
        } catch (_: Exception) { emptyList() }
    }

    suspend fun sendMessage(
        conversationId: String,
        content: String,
        senderName: String,
        isFromUser: Boolean
    ): SendMessageResponseDto? {
        // Optimistic local insert
        if (isFromUser) {
            ConversationRepository.sendUserMessage(conversationId, content, senderName)
        } else {
            ConversationRepository.sendLawyerMessage(conversationId, content, senderName)
        }

        return try {
            val response = RetrofitClient.haqApi.sendMessage(
                conversationId,
                SendMessageRequest(conversationId = conversationId, content = content)
            )
            if (response.isSuccessful) response.body()?.data else null
        } catch (_: Exception) { null }
    }

    fun getOrCreateConversation(
        lawyerId: String,
        lawyerName: String,
        clientName: String
    ): Conversation = ConversationRepository.getOrCreate(lawyerId, lawyerName, clientName)
}
