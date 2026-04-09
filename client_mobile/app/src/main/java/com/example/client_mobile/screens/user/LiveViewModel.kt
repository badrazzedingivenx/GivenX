package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Fetches live sessions from /api/lives and maps them to the [LiveSession] UI model.
 */
class LiveViewModel : ViewModel() {

    /** null = loading; empty list = loaded but none available */
    private val _lives = MutableStateFlow<List<LiveSession>?>(null)
    val lives: StateFlow<List<LiveSession>?> = _lives

    init { fetch() }

    fun fetch() {
        viewModelScope.launch {
            _lives.value = null
            try {
                val response = RetrofitClient.mockApi.getLives()
                _lives.value = if (response.isSuccessful) {
                    response.body()?.map { dto ->
                        LiveSession(
                            id         = dto.id.hashCode(),
                            lawyerName = dto.lawyerName,
                            specialty  = "",
                            topic      = dto.title,
                            viewers    = dto.viewersCount,
                            isLive     = true
                        )
                    } ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (_: Exception) {
                _lives.value = emptyList()
            }
        }
    }
}
