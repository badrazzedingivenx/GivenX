package com.example.client_mobile.screens.user

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.dto.CreateDocumentRequest
import com.example.client_mobile.network.dto.DocumentApiDto
import com.example.client_mobile.network.dto.RenameDocumentRequest
import com.example.client_mobile.screens.shared.DocumentRepository
import com.example.client_mobile.screens.shared.VaultDocument
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DocumentViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetch()
    }

    fun fetch() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.mockApi.getDocuments()
                if (response.isSuccessful) {
                    val dtos = response.body() ?: emptyList()
                    DocumentRepository.documents.clear()
                    DocumentRepository.documents.addAll(dtos.map { it.toVaultDocument() })
                }
            } catch (_: Exception) {
                // keep existing list unchanged on network error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun add(name: String) {
        viewModelScope.launch {
            val ext = name.substringAfterLast('.', "").lowercase()
            try {
                val response = RetrofitClient.haqApi.createDocument(CreateDocumentRequest(name = name.trim(), type = ext))
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let { DocumentRepository.documents.add(0, it.toVaultDocument()) }
                } else {
                    DocumentRepository.add(name)
                }
            } catch (_: Exception) {
                DocumentRepository.add(name)
            }
        }
    }

    fun rename(id: Long, newName: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.haqApi.renameDocument(id.toString(), RenameDocumentRequest(newName.trim()))
                if (response.isSuccessful && response.body()?.success == true) {
                    DocumentRepository.rename(id, newName)
                } else {
                    DocumentRepository.rename(id, newName)
                }
            } catch (_: Exception) {
                DocumentRepository.rename(id, newName)
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            try {
                RetrofitClient.haqApi.deleteDocument(id.toString())
            } catch (_: Exception) {
                // ignore — remove locally regardless
            } finally {
                DocumentRepository.delete(id)
            }
        }
    }

    private fun DocumentApiDto.toVaultDocument(): VaultDocument {
        // Derive icon from file extension in the title (e.g. "Contract.pdf" → PDF icon)
        val ext = title.substringAfterLast('.', "").lowercase()
        val icon = when (ext) {
            "jpg", "jpeg", "png" -> Icons.Default.Image
            "pdf"                -> Icons.Default.PictureAsPdf
            else                 -> Icons.AutoMirrored.Filled.InsertDriveFile
        }
        return VaultDocument(
            id        = id.toLongOrNull() ?: id.hashCode().toLong(),
            name      = title,       // "title" from mock JSON
            addedDate = uploadDate,  // "uploadDate" from mock JSON
            icon      = icon
        )
    }
}
