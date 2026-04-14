package com.example.client_mobile.network.dto

import com.google.gson.annotations.SerializedName

// JSON shape from /api/documents/me:
// { "id": "doc_001", "title": "Power of Attorney", "uploadDate": "2026-03-20" }
data class DocumentApiDto(
    @SerializedName("id")         val id:         String = "",
    @SerializedName("title")      val title:      String = "",   // was "name"
    @SerializedName("uploadDate") val uploadDate: String = "",   // was "added_date"
    @SerializedName("type")       val type:       String = ""    // optional field
)

data class CreateDocumentRequest(
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String
)

data class RenameDocumentRequest(
    @SerializedName("name") val name: String
)
