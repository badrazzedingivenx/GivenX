package com.example.client_mobile.data.repository

import com.example.client_mobile.data.model.dto.LegalPostDto
import com.example.client_mobile.data.model.dto.LikeResponseDto
import com.example.client_mobile.data.model.dto.LiveDto
import com.example.client_mobile.data.model.dto.ReelDto
import com.example.client_mobile.data.model.dto.SendMessageRequest
import com.example.client_mobile.data.model.dto.SendMessageResponseDto
import com.example.client_mobile.data.model.dto.StoryDto
import com.example.client_mobile.data.remote.RetrofitClient
import com.example.client_mobile.data.repository.ConversationRepository

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
}
