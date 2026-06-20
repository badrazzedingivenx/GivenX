package com.example.client_mobile.screens.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Fetches reels exclusively from GET /api/reels on the local Node.js server
 * (http://10.0.2.2:3001/api/reels) and maps ReelDto → LegalReel UI model.
 *
 * Field mapping:
 *   dto.lawyerName → reel.lawyerName  (author text — already contains full title from API)
 *   dto.title      → reel.specialty   (domain/category badge)
 *   dto.caption    → reel.title       (main description text)
 *   dto.videoUrl   → resolved absolute URL consumed by ExoPlayer
 *
 * Video URL resolution:
 *   json-server serves api_server/public/ at the ROOT of http://10.0.2.2:3001/
 *   So vid1.mp4 → http://10.0.2.2:3001/vid1.mp4  (NOT /public/vid1.mp4)
 *   If db.json already stores an absolute http:// URL it is used as-is.
 */
class ReelViewModel : ViewModel() {

    /** null = loading; empty list = loaded but none available */
    private val _reels = MutableStateFlow<List<LegalReel>?>(null)
    val reels: StateFlow<List<LegalReel>?> = _reels

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    /** Maps UI integer IDs back to original API string IDs for like toggling. */
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
                // ── Network fetch: GET http://10.0.2.2:3001/api/reels ────────────
                val dtos = MainRepository.getReels()
                Log.d("ReelVM", "Fetched ${dtos.size} reels from API")
                reelApiIds.clear()

                // json-server mounts api_server/public/ at the server root (/),
                // so the media base is http://10.0.2.2:3001/ — NOT /public/
                val mediaRoot = com.example.client_mobile.network.RetrofitClient.BASE_URL
                    .removeSuffix("api/")   // "http://10.0.2.2:3001/"

                _reels.value = dtos.map { dto ->
                    val uiId = dto.id.hashCode()
                    reelApiIds[uiId] = dto.id

                    // Absolute URL (db.json stores http://10.0.2.2:3001/vid1.mp4) → use as-is
                    // Relative path (e.g. "vid1.mp4" or "/vid1.mp4")             → prepend mediaRoot
                    val finalVideoUrl = when {
                        dto.videoUrl.startsWith("http") -> dto.videoUrl
                        dto.videoUrl.isNotBlank()        -> mediaRoot + dto.videoUrl.trimStart('/')
                        else                             -> ""
                    }

                    Log.d("ReelVM", "  [${dto.id}] videoUrl='${dto.videoUrl}' → resolved='$finalVideoUrl'")

                    LegalReel(
                        id         = uiId,
                        lawyerName = dto.lawyerName,  // API already includes full name e.g. "Maître Youssef El Alami"
                        specialty  = dto.title,        // e.g. "Droits du Salarié"  → displayed as badge
                        title      = dto.caption,      // e.g. "⚖️ Top 5 droits…"   → displayed as description
                        likes      = dto.likes,
                        comments   = (dto.likes * 0.07).toInt(),
                        shares     = (dto.likes * 0.035).toInt(),
                        views      = if (dto.views > 0) "${dto.views}" else "",
                        videoUrl   = finalVideoUrl
                    )
                }
                if (dtos.isEmpty() && !isRefresh) _isError.value = true
            } catch (e: Exception) {
                Log.e("ReelVM", "Fetch failed: ${e.message}", e)
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
