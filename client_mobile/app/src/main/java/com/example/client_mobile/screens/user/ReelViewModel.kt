package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Fetches reels from /api/reels and maps them to the [LegalReel] UI model.
 * Keeps local like-toggle state via mutable StateFlow list.
 */
class ReelViewModel : ViewModel() {

    /** null = loading; empty list = loaded but none available */
    private val _reels = MutableStateFlow<List<LegalReel>?>(null)
    val reels: StateFlow<List<LegalReel>?> = _reels

    init { fetch() }

    fun fetch() {
        viewModelScope.launch {
            _reels.value = null
            try {
                val response = RetrofitClient.mockApi.getReels()
                _reels.value = if (response.isSuccessful) {
                    response.body()?.map { dto ->
                        LegalReel(
                            id         = dto.id.hashCode(),
                            lawyerName = dto.lawyerName,
                            specialty  = "",
                            title      = dto.caption,
                            likes      = dto.likes,
                            views      = "",
                            isLiked    = false
                        )
                    } ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (_: Exception) {
                _reels.value = emptyList()
            }
        }
    }
}
