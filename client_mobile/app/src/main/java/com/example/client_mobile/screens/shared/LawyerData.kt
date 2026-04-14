package com.example.client_mobile.screens.shared

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector

// ─── Data Models ──────────────────────────────────────────────────────────────
data class LawyerItem(
    val id: String,
    val name: String,
    val specialty: String,
    val city: String,
    val rating: Float,
    val reviewCount: Int,
    val yearsExp: Int,
    val bio: String,
    val isVerified: Boolean,
    val domaine: String,
    val avatarUrl: String = "",
    val imageUri: Uri? = null
)

data class ClientItem(
    val id: String,
    val name: String,
    val lastAction: String,
    val status: String, // e.g., "Actif", "En attente", "Clôturé"
    val imageUri: Uri? = null
)

data class RequestItem(
    val id: String,
    val clientName: String,
    val topic: String,
    val date: String,
    var status: String, // e.g., "Nouveau", "Accepté", "Refusé"
    val description: String,
    val amount: String = "500 MAD"
)

data class PaymentItem(
    val id: String,
    val clientName: String,
    val amount: String,
    val date: String,
    val status: String, // "Reçu", "En attente"
    val method: String // "Carte Bancaire", "Virement"
)

data class InboxMessage(
    val id: String,
    val fromName: String,
    val content: String,
    val timestamp: String,
    val lawyerId: String,
    val isRead: Boolean = false
)

data class LegalStory(
    val id: Int,
    val lawyerName: String,
    val specialty: String,
    val hasNewStory: Boolean = true
)

// ─── User Session ─────────────────────────────────────────────────────────────
object UserSession {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
    var address by mutableStateOf("")
    /** CDN URL returned by the API (populated after login and profile fetch). */
    var avatarUrl by mutableStateOf("")
    var profileImageUri by mutableStateOf<Uri?>(null)

    fun updateProfile(newName: String, newEmail: String, newPhone: String, newAddress: String, newImageUri: Uri?) {
        name = newName
        email = newEmail
        phone = newPhone
        address = newAddress
        profileImageUri = newImageUri
    }
}

// ─── Lawyer Session / Repository ──────────────────────────────────────────────
object LawyerSession {
    var fullName by mutableStateOf("")
    var title by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
    var address by mutableStateOf("")
    var bio by mutableStateOf("")
    var avatarUrl by mutableStateOf("")
    var profileImageUri by mutableStateOf<Uri?>(null)
    val specializations = mutableStateListOf<String>()

    val clients = mutableStateListOf<ClientItem>()

    val requests = mutableStateListOf<RequestItem>()

    val payments = mutableStateListOf<PaymentItem>()

    fun acceptRequest(requestId: String) {
        val request = requests.find { it.id == requestId }
        request?.let {
            it.status = "Accepté"
            if (clients.none { c -> c.name == it.clientName }) {
                clients.add(0, ClientItem(
                    id = System.currentTimeMillis().toString(),
                    name = it.clientName,
                    lastAction = "Demande acceptée",
                    status = "Actif"
                ))
            }
        }
        val index = requests.indexOf(request)
        if (index != -1) requests[index] = requests[index].copy()
    }

    fun declineRequest(requestId: String) {
        val request = requests.find { it.id == requestId }
        request?.let { it.status = "Refusé" }
        val index = requests.indexOf(request)
        if (index != -1) requests[index] = requests[index].copy()
    }

    fun updateProfile(
        newName: String, newTitle: String, newEmail: String, newPhone: String, 
        newAddress: String, newBio: String, newSpecs: List<String>, newImageUri: Uri?
    ) {
        fullName = newName
        title = newTitle
        email = newEmail
        phone = newPhone
        address = newAddress
        bio = newBio
        profileImageUri = newImageUri
        specializations.clear()
        specializations.addAll(newSpecs)
    }
}

// ─── Shared Message Repository ────────────────────────────────────────────────
object MessageRepository {
    val messages = mutableStateListOf<InboxMessage>()
    fun sendMessage(fromName: String, content: String, lawyerId: String) {
        messages.add(InboxMessage(id = System.currentTimeMillis().toString(), fromName = fromName, content = content, timestamp = "À l'instant", lawyerId = lawyerId))
    }
}

// ─── Document Vault (Global Shared State) ───────────────────────────────────

data class VaultDocument(
    val id: Long,
    val name: String,
    val addedDate: String,
    val icon: ImageVector
)

object DocumentRepository {
    val documents = mutableStateListOf<VaultDocument>()

    fun add(name: String) {
        val ext  = name.substringAfterLast('.', "").lowercase()
        val icon = when (ext) {
            "jpg", "jpeg", "png" -> Icons.Default.Image
            "pdf"                -> Icons.Default.PictureAsPdf
            else                 -> Icons.Default.InsertDriveFile
        }
        documents.add(VaultDocument(id = System.currentTimeMillis(), name = name.trim(), addedDate = "Aujourd'hui", icon = icon))
    }

    fun rename(id: Long, newName: String) {
        val idx = documents.indexOfFirst { it.id == id }
        if (idx != -1) documents[idx] = documents[idx].copy(name = newName.trim())
    }

    fun delete(id: Long) {
        documents.removeAll { it.id == id }
    }
}

// ─── Sample Lawyers (For consistency) ─────────────────────────────────────────
val sampleLawyers = emptyList<LawyerItem>()

// ─── Creator Data Models (Lawyer → Social Content) ────────────────────────────

data class CreatorStory(
    val id: Long,
    val lawyerName: String,
    val specialty: String,
    val hasNewStory: Boolean = true
)

data class CreatorReel(
    val id: Long,
    val lawyerName: String,
    val specialty: String,
    val title: String,
    var likes: Int = 0,
    val views: Int = 0,
    var isLiked: Boolean = false,
    val isLive: Boolean = false
)

data class CreatorLiveSession(
    val id: Long,
    val lawyerName: String,
    val specialty: String,
    val topic: String,
    val viewers: Int = 1,
    val isLive: Boolean = true
)

data class LiveStudioChatMessage(val author: String, val text: String)

// ─── Creator Repository (Global shared state lawyer → user feed) ──────────────

object CreatorRepository {
    val stories        = mutableStateListOf<CreatorStory>()
    val reels          = mutableStateListOf<CreatorReel>()
    val liveSessions   = mutableStateListOf<CreatorLiveSession>()

    fun postStory(lawyerName: String, specialty: String) {
        stories.add(0, CreatorStory(
            id          = System.currentTimeMillis(),
            lawyerName  = lawyerName,
            specialty   = specialty
        ))
    }

    fun uploadReel(lawyerName: String, specialty: String, title: String) {
        reels.add(0, CreatorReel(
            id         = System.currentTimeMillis(),
            lawyerName = lawyerName,
            specialty  = specialty,
            title      = title
        ))
    }

    fun goLive(lawyerName: String, specialty: String, topic: String): Long {
        val id = System.currentTimeMillis()
        liveSessions.add(0, CreatorLiveSession(
            id         = id,
            lawyerName = lawyerName,
            specialty  = specialty,
            topic      = topic
        ))
        return id
    }

    fun endLive(id: Long) {
        val idx = liveSessions.indexOfFirst { it.id == id }
        if (idx != -1) liveSessions[idx] = liveSessions[idx].copy(isLive = false)
    }

    fun deleteReel(id: Long)  { reels.removeAll  { it.id == id } }
    fun deleteStory(id: Long) { stories.removeAll { it.id == id } }
    fun deleteLive(id: Long)  { liveSessions.removeAll { it.id == id } }
}
