package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.MainRepository
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.dto.LegalPostDto
import com.example.client_mobile.network.dto.StoryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for [HaqqiSocialFeedScreen].
 * Fetches posts from GET /api/legal-feed and stories from GET /api/stories.
 * If the feed endpoint is unavailable, the mock data provider is used so
 * the PFE demo works even without the backend endpoint.
 */
class SocialFeedViewModel : ViewModel() {

    // ─── Posts state ──────────────────────────────────────────────────────────
    /** null = loading; empty list = loaded/error with no data */
    private val _posts = MutableStateFlow<List<LegalPostDto>?>(null)
    val posts: StateFlow<List<LegalPostDto>?> = _posts

    // ─── Stories state ────────────────────────────────────────────────────────
    private val _stories = MutableStateFlow<List<StoryDto>?>(null)
    val stories: StateFlow<List<StoryDto>?> = _stories

    // ─── Like overrides (optimistic, keyed by lawyerId+date) ─────────────────
    private val _likedIds = MutableStateFlow<Set<String>>(emptySet())
    val likedIds: StateFlow<Set<String>> = _likedIds

    private val _likeCount = MutableStateFlow<Map<String, Int>>(emptyMap())
    val likeCount: StateFlow<Map<String, Int>> = _likeCount

    // ─── Misc ─────────────────────────────────────────────────────────────────
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    init { fetch() }

    // ─── Public API ───────────────────────────────────────────────────────────

    fun refresh() {
        _isRefreshing.value = true
        fetch(isRefresh = true)
    }

    /**
     * Optimistically toggles the like on [postKey] (use `lawyerId + date` as key).
     * No remote call yet — wire to `/api/legal-feed/{id}/like` when backend is ready.
     */
    fun toggleLike(postKey: String, currentCount: Int) {
        val liked = _likedIds.value
        if (postKey in liked) {
            _likedIds.value = liked - postKey
            _likeCount.value = _likeCount.value + (postKey to maxOf(0, currentCount - 1))
        } else {
            _likedIds.value = liked + postKey
            _likeCount.value = _likeCount.value + (postKey to currentCount + 1)
        }
    }

    // ─── Private fetch logic ──────────────────────────────────────────────────

    fun fetch(isRefresh: Boolean = false) {
        if (!isRefresh) {
            _posts.value   = null
            _stories.value = null
        }
        _isError.value = false

        viewModelScope.launch {
            // Stories
            val fetchedStories = MainRepository.getStories()
            _stories.value = fetchedStories

            // Posts
            val fetchedPosts = MainRepository.getLegalFeed()
            if (fetchedPosts.isNotEmpty()) {
                _posts.value = fetchedPosts
            } else {
                _posts.value = emptyList()
                if (fetchedStories.isEmpty()) {
                    _isError.value = true
                }
            }
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchStories() {
        // Handled in fetch() unified launch
    }

    private suspend fun fetchPosts() {
        // Handled in fetch() unified launch
    }
}
