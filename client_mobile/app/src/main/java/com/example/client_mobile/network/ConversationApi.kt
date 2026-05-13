package com.example.client_mobile.network

import com.example.client_mobile.network.dto.HaqConversationDto
import com.example.client_mobile.network.dto.HaqCreateConversationRequest
import com.example.client_mobile.network.dto.HaqMessageDto
import com.example.client_mobile.network.dto.HaqSendMessageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit interface for private conversation / messaging endpoints.
 *
 * GET  /conversations                               — list the user's conversations
 * POST /conversations                               — open a new conversation with a lawyer
 * GET  /conversations/{id}/messages                 — paginated message history
 * POST /conversations/{id}/messages                 — send a message
 *
 * Auth header injected automatically by [AuthInterceptor].
 * All paths relative to [NetworkModule.BASE_URL].
 */
interface ConversationApi {

    /**
     * GET /conversations
     *
     * Lists all conversations for the authenticated user.
     * Each item contains metadata + the last message snippet.
     *
     * Response 200: data = List<HaqConversationDto>
     * Errors: 401 UNAUTHORIZED
     */
    @GET("conversations")
    suspend fun getConversations(): Response<ApiResponse<List<HaqConversationDto>>>

    /**
     * POST /conversations
     *
     * Client-only. Opens a new private conversation with a lawyer.
     *
     * Response 201: data = HaqConversationDto (the newly created conversation)
     * Errors: 401 UNAUTHORIZED, 422 UNPROCESSABLE_ENTITY
     */
    @POST("conversations")
    suspend fun createConversation(
        @Body request: HaqCreateConversationRequest
    ): Response<ApiResponse<HaqConversationDto>>

    /**
     * GET /conversations/{id}/messages
     *
     * Returns the message history for a specific conversation.
     * Messages are ordered chronologically (oldest first).
     *
     * Response 200: data = List<HaqMessageDto>
     * Errors: 401 UNAUTHORIZED, 403 FORBIDDEN, 404 NOT_FOUND
     */
    @GET("conversations/{id}/messages")
    suspend fun getMessages(
        @Path("id") id: Int
    ): Response<ApiResponse<List<HaqMessageDto>>>

    /**
     * POST /conversations/{id}/messages
     *
     * Sends a message in an existing conversation.
     *
     * Response 201: data = HaqMessageDto (the sent message)
     * Errors: 401 UNAUTHORIZED, 403 FORBIDDEN, 404 NOT_FOUND, 422 UNPROCESSABLE_ENTITY
     */
    @POST("conversations/{id}/messages")
    suspend fun sendMessage(
        @Path("id") id: Int,
        @Body request: HaqSendMessageRequest
    ): Response<ApiResponse<HaqMessageDto>>
}
