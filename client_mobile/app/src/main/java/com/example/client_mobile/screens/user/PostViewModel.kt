package com.example.client_mobile.screens.user

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.MainRepository
import com.example.client_mobile.network.dto.PostDto
import com.example.client_mobile.screens.shared.PostItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    // ── Feed state ─────────────────────────────────────────────────────────────
    /** null = initial loading; emptyList = loaded with no results */
    private val _posts = MutableStateFlow<List<PostItem>?>(null)
    val posts: StateFlow<List<PostItem>?> = _posts

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    // ── Create-post state ──────────────────────────────────────────────────────
    private val _createState = MutableStateFlow<CreatePostState>(CreatePostState.Idle)
    val createState: StateFlow<CreatePostState> = _createState

    init { loadPosts() }

    // ── Public API ─────────────────────────────────────────────────────────────

    fun refresh() {
        _isRefreshing.value = true
        loadPosts(isRefresh = true)
    }

    fun loadPosts(isRefresh: Boolean = false) {
        if (!isRefresh) _posts.value = null
        _isError.value = false
        viewModelScope.launch {
            try {
                val fetched = MainRepository.getPosts()
                _posts.value = fetched.map { it.toItem() }
            } catch (e: Exception) {
                Log.e("PostViewModel", "loadPosts failed: ${e.message}", e)
                if (_posts.value == null) _posts.value = emptyList()
                _isError.value = true
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    /** Optimistic like toggle; fires the remote call in the background. */
    fun toggleLike(postId: String) {
        val current = _posts.value ?: return
        _posts.value = current.map { post ->
            if (post.id == postId) {
                val nowLiked = !post.isLiked
                post.copy(
                    isLiked    = nowLiked,
                    likesCount = if (nowLiked) post.likesCount + 1 else maxOf(0, post.likesCount - 1)
                )
            } else post
        }
        viewModelScope.launch { MainRepository.likePost(postId) }
    }

    fun createPost(
        context:  Context,
        content:  String,
        mediaUri: Uri?,
        hashtags: String,
        mentions: List<Long>
    ) {
        if (content.isBlank() && mediaUri == null) return
        _createState.value = CreatePostState.Loading
        viewModelScope.launch {
            try {
                val ok = MainRepository.createPost(
                    context  = context,
                    content  = content,
                    mediaUri = mediaUri,
                    hashtags = hashtags.takeIf { it.isNotBlank() },
                    mentions = mentions.takeIf { it.isNotEmpty() }
                )
                _createState.value = if (ok) CreatePostState.Success
                                     else CreatePostState.Error("La publication a échoué")
                if (ok) loadPosts(isRefresh = true)
            } catch (e: Exception) {
                _createState.value = CreatePostState.Error(e.message ?: "Erreur réseau")
            }
        }
    }

    fun resetCreateState() {
        _createState.value = CreatePostState.Idle
    }

    // ── Private mapper ─────────────────────────────────────────────────────────

    private fun PostDto.toItem() = PostItem(
        id            = id,
        authorName    = authorName,
        authorAvatar  = authorAvatarUrl.takeIf { it.isNotBlank() },
        content       = content,
        mediaUrl      = mediaUrl?.takeIf { it.isNotBlank() },
        hashtags      = hashtagList,
        likesCount    = likesCount,
        commentsCount = commentsCount,
        isLiked       = isLiked,
        createdAt     = createdAt
    )
}

// ─── Create-post state machine ─────────────────────────────────────────────────
sealed class CreatePostState {
    object Idle    : CreatePostState()
    object Loading : CreatePostState()
    object Success : CreatePostState()
    data class Error(val message: String) : CreatePostState()
}
