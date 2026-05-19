package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Fetches reels from /api/reels and maps them to the [LegalReel] UI model.
 * Like interactions call POST /api/reels/{id}/like via [MainRepository].
 */
class ReelViewModel : ViewModel() {

    /** null = loading; empty list = loaded but none available */
    private val _reels = MutableStateFlow<List<LegalReel>?>(null)
    val reels: StateFlow<List<LegalReel>?> = _reels

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    /** Stores original reel IDs (String) so we can pass them to the API. */
    private val reelApiIds = mutableMapOf<Int, String>()

    init { fetch() }

    fun refresh() {
        _isRefreshing.value = true
        fetch(isRefresh = true)
    }

    fun fetch(isRefresh: Boolean = false) {
        if (!isRefresh) _reels.value = null
        _isError.value = false
        viewModelScope.launch {
            try {
                val dtos = MainRepository.getReels()
                reelApiIds.clear()
                _reels.value = dtos.map { dto ->
                    val uiId = dto.id.hashCode()
                    reelApiIds[uiId] = dto.id
                    LegalReel(
                        id         = uiId,
                        lawyerName = dto.lawyerName,
                        specialty  = "",
                        title      = dto.caption,
                        likes      = dto.likes,
                        views      = "",
                        videoUrl   = dto.videoUrl
                    )
                }
                if (dtos.isEmpty() && !isRefresh) _isError.value = true
            } catch (_: Exception) {
                if (_reels.value == null) _reels.value = emptyList()
                _isError.value = true
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    /**
     * Toggles the like on the reel at [index] in the current list.
     * Applies the change optimistically and confirms via the API response.
     */
    fun toggleLike(index: Int) {
        val current = _reels.value?.toMutableList() ?: return
        val reel = current.getOrNull(index) ?: return
        val apiId = reelApiIds[reel.id] ?: return

        // Optimistic update
        val delta = if (reel.isLiked) -1 else 1
        current[index] = reel.copy(isLiked = !reel.isLiked, likes = reel.likes + delta)
        _reels.value = current

        viewModelScope.launch {
            val response = MainRepository.toggleLike(apiId)
            if (response != null) {
                // Confirm server truth
                val confirmed = _reels.value?.toMutableList() ?: return@launch
                val idx = confirmed.indexOfFirst { it.id == reel.id }
                if (idx >= 0) {
                    confirmed[idx] = confirmed[idx].copy(
                        isLiked = response.isLiked,
                        likes   = response.likesCount
                    )
                    _reels.value = confirmed
                }
            }
            // On null (network failure) we silently keep the optimistic state
        }
    }
}
