package com.example.client_mobile.network

import com.example.client_mobile.network.dto.LawyerSearchResultDto
import com.example.client_mobile.network.dto.LikeResponseDto
import com.example.client_mobile.network.dto.LiveDto
import com.example.client_mobile.network.dto.ReelDto
import com.example.client_mobile.network.dto.SendMessageRequest
import com.example.client_mobile.network.dto.SendMessageResponseDto
import com.example.client_mobile.screens.shared.Conversation
import com.example.client_mobile.screens.shared.ConversationRepository

/**
 * Unified repository that merges Reels, Search (lawyers), and Messaging
 * logic so ViewModels stay thin and have a single data source.
 *
 * All methods are suspend funs — call inside a coroutine scope.
 */
object MainRepository {

    // ── Reels ─────────────────────────────────────────────────────────────────

    /** Fetches the reel feed from GET /api/reels. */
    suspend fun getReels(): List<ReelDto> {
        val response = RetrofitClient.mockApi.getReels()
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }

    /**
     * Toggles a like on [reelId] via POST /api/reels/{id}/like.
     * Returns the updated [LikeResponseDto] or null on failure.
     */
    suspend fun toggleLike(reelId: String): LikeResponseDto? {
        return try {
            val response = RetrofitClient.mockApi.likeReel(reelId)
            if (response.isSuccessful) response.body() else null
        } catch (_: Exception) { null }
    }

    // ── Search ────────────────────────────────────────────────────────────────

    /**
     * Searches lawyers by name/specialty via GET /api/lawyers?search={query}.
     * Returns mapped [LawyerSearchResultDto] list (max 20 results).
     */
    suspend fun searchLawyers(query: String): List<LawyerSearchResultDto> {
        return try {
            val response = RetrofitClient.mockApi.getLawyers(limit = 20)
            if (response.isSuccessful) {
                // Filter client-side while the mock server doesn't support ?search=
                val q = query.lowercase().trim()
                (response.body() ?: emptyList())
                    .filter {
                        q.isBlank()
                            || it.name.lowercase().contains(q)
                            || it.specialty.lowercase().contains(q)
                            || it.domaine.lowercase().contains(q)
                    }
                    .map { dto ->
                        LawyerSearchResultDto(
                            id        = dto.id,
                            name      = dto.name,
                            specialty = dto.specialty,
                            avatarUrl = dto.avatarUrl,
                            rating    = dto.rating,
                            domaine   = dto.domaine
                        )
                    }
            } else emptyList()
        } catch (_: Exception) { emptyList() }
    }

    // ── Messaging ─────────────────────────────────────────────────────────────

    /**
     * Fetches the active live sessions from GET /api/lives.
     * Used by [LiveViewModel] so the polling logic has a clear data-source.
     */
    suspend fun getLives(): List<LiveDto> {
        return try {
            val response = RetrofitClient.mockApi.getLives()
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (_: Exception) { emptyList() }
    }

    /**
     * Sends a message via POST /api/messages/send and inserts it into
     * [ConversationRepository] immediately so the UI updates optimistically.
     *
     * @return the server response, or null when the call fails.
     */
    suspend fun sendMessage(
        conversationId: String,
        content: String,
        senderName: String,
        isFromUser: Boolean
    ): SendMessageResponseDto? {
        // 1. Optimistic local insert
        if (isFromUser) {
            ConversationRepository.sendUserMessage(conversationId, content, senderName)
        } else {
            ConversationRepository.sendLawyerMessage(conversationId, content, senderName)
        }

        // 2. Fire the API call in the background
        return try {
            val response = RetrofitClient.mockApi.sendMessage(
                conversationId,
                SendMessageRequest(conversationId = conversationId, content = content)
            )
            if (response.isSuccessful) response.body() else null
        } catch (_: Exception) { null }
    }

    /**
     * Ensures a conversation exists locally and returns it.
     * Called by screens that navigate into a chat from a lawyer profile.
     */
    fun getOrCreateConversation(
        lawyerId: String,
        lawyerName: String,
        clientName: String
    ): Conversation = ConversationRepository.getOrCreate(lawyerId, lawyerName, clientName)
}
