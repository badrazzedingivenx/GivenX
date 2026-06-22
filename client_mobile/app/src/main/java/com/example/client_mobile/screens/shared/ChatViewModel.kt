package com.example.client_mobile.screens.shared

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.MainRepository
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf

/**
 * Fetches historical messages for [conversationId] from
 * GET /api/messages/{id} and seeds [ConversationRepository].
 *
 * Outgoing messages are sent via POST /api/messages/send through [MainRepository],
 * which inserts them optimistically into [ConversationRepository] before the
 * API round-trip completes.
 */
class ChatViewModel(private val savedStateHandle: androidx.lifecycle.SavedStateHandle) : ViewModel() {

    val conversationId: String = savedStateHandle.get<String>("conversationId") ?: ""

    // Persisted UI State for Header
    val headerName: StateFlow<String> = savedStateHandle.getStateFlow(
        "headerName", 
        savedStateHandle.get<String>("lawyerName") ?: ""
    )
    val headerAvatarUrl: StateFlow<String> = savedStateHandle.getStateFlow(
        "headerAvatarUrl", 
        savedStateHandle.get<String>("avatarUrl") ?: ""
    )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun clearError() { _errorMessage.value = null }
    fun clearUserMessage() { _userMessage.value = null }

    init {
        Log.d("ChatViewModel", "Initializing for conversationId: $conversationId")
        
        // 1. If Nav arguments were empty (e.g. from Inbox), fallback to Repository
        if (headerName.value.isBlank()) {
            val localConv = ConversationRepository.conversations.find { it.id == conversationId }
            if (localConv != null) {
                savedStateHandle["headerName"] = localConv.otherPartyName
                savedStateHandle["headerAvatarUrl"] = localConv.avatarUrl
            }
        }

        // 2. Seed with any existing local messages if available
        _messages.value = ConversationRepository.getMessages(conversationId).toList()
        fetchMessages()
    }

    fun fetchMessages() {
        if (!TokenManager.isLoggedIn() || conversationId.isBlank()) {
            Log.d("ChatViewModel", "Fetch skipped: loggedIn=${TokenManager.isLoggedIn()}, id=$conversationId")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("ChatViewModel", "Fetching messages for $conversationId...")
            try {
                val response = RetrofitClient.haqApi.getChatDetails(conversationId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val serverMessages = response.body()?.data?.mapIndexed { index, dto ->
                        val doc = if (dto.documentUrl != null) {
                            val ext = dto.documentName?.substringAfterLast('.', "")?.lowercase() ?: ""
                            val icon = when (ext) {
                                "jpg", "jpeg", "png" -> Icons.Default.Image
                                "pdf"                -> Icons.Default.PictureAsPdf
                                else                 -> Icons.AutoMirrored.Filled.InsertDriveFile
                            }
                            VaultDocument(
                                id = 0L,
                                name = dto.documentName ?: "Document",
                                addedDate = "",
                                icon = icon,
                                urlOrUri = dto.documentUrl,
                                mimeType = dto.documentMime
                            )
                        } else null

                        ChatMessage(
                            id         = dto.id.ifBlank { "${dto.senderId}_${dto.effectiveTime()}_$index" },
                            content    = dto.effectiveContent(),
                            senderName = dto.senderName.ifBlank { dto.senderId },
                            timestamp  = dto.effectiveTime(),
                            isFromUser = dto.isFromUser,
                            document   = doc
                        )
                    } ?: emptyList()
                    
                    // Strict Message Deduplication
                    // We prioritize serverMessages (they come first).
                    // We strictly deduplicate by ID, and then aggressively deduplicate
                    // by document name or text content to wipe out local placeholders that lost sync.
                    val combined = (serverMessages + _messages.value)
                        .distinctBy { it.id }
                        .distinctBy { msg ->
                            val docName = msg.document?.name
                            if (!docName.isNullOrBlank()) docName else msg.content
                        }
                    
                    _messages.value = combined.sortedBy { it.timestamp }
                    ConversationRepository.replaceMessagesFromApi(conversationId, combined)
                } else {
                    _errorMessage.value = "Impossible de charger les messages."
                }
            } catch (_: Exception) {
                _errorMessage.value = "Erreur réseau. Vérifiez votre connexion."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Sends [text] via POST /api/messages/send through [MainRepository].
     */
    fun send(text: String, senderName: String, isFromUser: Boolean) {
        if (text.isBlank() || conversationId.isBlank()) return
        val tempId = java.util.UUID.randomUUID().toString()
        val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        
        // 1. Immediate Optimistic UI Update
        val optimisticMsg = ChatMessage(
            id         = tempId,
            content    = text,
            senderName = senderName.ifBlank { "Moi" },
            timestamp  = time,
            isFromUser = isFromUser
        )
        _messages.value = _messages.value + optimisticMsg

        viewModelScope.launch {
            val response = MainRepository.sendMessage(
                conversationId = conversationId,
                content        = text,
                senderName     = senderName,
                isFromUser     = isFromUser,
                tempId         = tempId
            )
            
            // 2. Safely update the placeholder with the real API response
            if (response != null) {
                _messages.value = _messages.value.map { msg ->
                    if (msg.id == tempId) {
                        msg.copy(
                            id = response.id.ifBlank { "${response.senderId}_${response.time}_new" },
                            timestamp = response.time
                        )
                    } else msg
                }
                ConversationRepository.replaceMessagesFromApi(conversationId, _messages.value)
            }
        }
    }

    /**
     * DEBUG: Instantly clears all messages for this conversation from the local UI,
     * then best-effort deletes them from the json-server.
     */
    fun clearChat() {
        val idsToDelete = _messages.value.map { it.id }
        // 1. Instant UI update
        _messages.value = emptyList()
        ConversationRepository.getMessages(conversationId).clear()
        
        // 2. Best-effort server delete (json-server supports DELETE /messages/:id)
        viewModelScope.launch(Dispatchers.IO) {
            idsToDelete.forEach { id ->
                try { RetrofitClient.haqApi.deleteMessage(id) }
                catch (_: Exception) { /* ignore — UI is already clear */ }
            }
        }
    }

    private suspend fun copyUriToInternalStorage(context: android.content.Context, uriString: String, extension: String): String {
        val uri = android.net.Uri.parse(uriString)
        if (uri.scheme != "content") return uriString // Already a file path or URL
        
        val fileName = "doc_${java.util.UUID.randomUUID()}.$extension"
        val file = java.io.File(context.filesDir, fileName)
        
        context.contentResolver.openInputStream(uri)?.use { input ->
            java.io.FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        } ?: throw Exception("Impossible d'ouvrir le fichier")
        
        return file.absolutePath
    }

    fun sendDocumentMessage(document: VaultDocument?, senderName: String?, isFromUser: Boolean, context: android.content.Context) {
        if (conversationId.isBlank() || document == null) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val safeName    = document.name.takeIf { it.isNotBlank() } ?: "Document"
                val safeMime    = document.mimeType ?: "*/*"
                val extension   = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(safeMime) ?: "bin"
                
                // 1. Copy to internal storage securely to prevent SecurityException
                val safeUri = copyUriToInternalStorage(context, document.urlOrUri, extension)

                val messageText = ""
                val tempId      = java.util.UUID.randomUUID().toString()

                val safeDocument = document.copy(
                    name     = safeName,
                    urlOrUri = safeUri,
                    mimeType = safeMime
                )
                val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                
                val optimisticMsg = ChatMessage(
                    id         = tempId,
                    content    = messageText,
                    senderName = senderName?.ifBlank { "Moi" } ?: "Moi",
                    timestamp  = time,
                    isFromUser = isFromUser,
                    document   = safeDocument
                )
                
                // Immediate Optimistic UI Update
                _messages.value = _messages.value + optimisticMsg

                try {
                    val response = MainRepository.sendMessage(
                        conversationId = conversationId,
                        content        = messageText,
                        senderName     = senderName?.ifBlank { "Moi" } ?: "Moi",
                        isFromUser     = isFromUser,
                        tempId         = tempId,
                        document       = safeDocument
                    )
                    
                    if (response != null) {
                        _messages.value = _messages.value.map { msg ->
                            if (msg.id == tempId) {
                                msg.copy(
                                    id = response.id.ifBlank { "${response.senderId}_${response.time}_new" },
                                    timestamp = response.time
                                )
                            } else msg
                        }
                        ConversationRepository.replaceMessagesFromApi(conversationId, _messages.value)
                    }
                } catch (e: Exception) {
                    Log.w("ChatViewModel", "Server send failed (doc stays local): ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("ChatError", "Crash avoided", e)
                _errorMessage.value = "Impossible d'envoyer le document"
            }
        }
    }

}
