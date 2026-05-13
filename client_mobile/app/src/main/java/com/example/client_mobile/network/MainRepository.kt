package com.example.client_mobile.network

import android.content.Context
import android.net.Uri
import android.util.Log
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
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

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

    suspend fun toggleLike(reelId: String): LikeResponseDto? {        return try {
            val response = RetrofitClient.haqApi.likeReel(reelId)
            if (response.isSuccessful) response.body()?.data else null
        } catch (_: Exception) { null }
    }

    // ── Content Upload ────────────────────────────────────────────────────────

    /**
     * Upload an image/video as a Story to POST /stories.
     * Returns true on HTTP 2xx success.
     */
    suspend fun uploadStory(context: Context, uri: Uri): Boolean {
        return try {
            val part = buildFilePart(context, uri, "media") ?: return false
            val response = RetrofitClient.haqApi.uploadStory(part)
            if (!response.isSuccessful) {
                Log.e("ContentUpload", "uploadStory HTTP ${response.code()} — ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ContentUpload", "uploadStory threw: ${e.message}", e)
            false
        }
    }

    /**
     * Upload a File directly as a Story (used by CameraX capture).
     */
    suspend fun uploadStory(file: File): Boolean {
        return try {
            val part = buildFilePart(file, "media") ?: return false
            val response = RetrofitClient.haqApi.uploadStory(part)
            if (!response.isSuccessful) {
                Log.e("ContentUpload", "uploadStory HTTP ${response.code()} — ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ContentUpload", "uploadStory threw: ${e.message}", e)
            false
        }
    }

    /**
     * Upload an image/video as a Reel to POST /reels.
     * Returns true on HTTP 2xx success.
     */
    suspend fun uploadReel(context: Context, uri: Uri, title: String): Boolean {
        return try {
            val videoPart  = buildFilePart(context, uri, "video") ?: return false
            val titleBody  = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = RetrofitClient.haqApi.uploadReel(videoPart, titleBody)
            if (!response.isSuccessful) {
                Log.e("ContentUpload", "uploadReel HTTP ${response.code()} — ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ContentUpload", "uploadReel threw: ${e.message}", e)
            false
        }
    }

    /**
     * Upload a File directly as a Reel (used by CameraX video capture).
     */
    suspend fun uploadReel(file: File, title: String): Boolean {
        return try {
            val videoPart = buildFilePart(file, "video") ?: return false
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = RetrofitClient.haqApi.uploadReel(videoPart, titleBody)
            if (!response.isSuccessful) {
                Log.e("ContentUpload", "uploadReel HTTP ${response.code()} — ${response.errorBody()?.string()}")
            }
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ContentUpload", "uploadReel threw: ${e.message}", e)
            false
        }
    }

    /** Reads a content URI and wraps it in a [MultipartBody.Part]. Returns null if the URI can't be read. */
    private fun buildFilePart(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
        return try {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
            val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
            val fileName = uri.lastPathSegment ?: "upload"
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, fileName, requestBody)
        } catch (e: Exception) {
            Log.e("ContentUpload", "buildFilePart failed: ${e.message}", e)
            null
        }
    }

    /**
     * Copies a content [Uri] into a temp cache file. Returns the file or null on failure.
     * The caller should NOT rely on the file persisting beyond the current app session.
     */
    fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
            val ext = if (mimeType.startsWith("video/")) ".mp4" else ".jpg"
            val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}$ext")
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            } ?: run {
                Log.e("ContentUpload", "uriToFile: openInputStream returned null for $uri")
                return null
            }
            if (file.length() == 0L) {
                Log.e("ContentUpload", "uriToFile: copied 0 bytes from $uri")
                file.delete()
                return null
            }
            file
        } catch (e: Exception) {
            Log.e("ContentUpload", "uriToFile failed: ${e.message}", e)
            null
        }
    }

    /** Wraps a local [File] directly into a [MultipartBody.Part] (no ContentResolver needed). */
    private fun buildFilePart(file: File, partName: String): MultipartBody.Part? {
        return try {
            val bytes = file.readBytes()
            val mimeType = when {
                file.extension.lowercase() in listOf("jpg", "jpeg") -> "image/jpeg"
                file.extension.lowercase() in listOf("png")         -> "image/png"
                file.extension.lowercase() in listOf("mp4")         -> "video/mp4"
                file.extension.lowercase() in listOf("3gp")         -> "video/3gp"
                else                                                -> "application/octet-stream"
            }
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, file.name, requestBody)
        } catch (e: Exception) {
            Log.e("ContentUpload", "buildFilePart(file) failed: ${e.message}", e)
            null
        }
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
