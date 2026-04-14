package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.MainRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Fetches live sessions from GET /api/lives via [MainRepository].
 *
 * A background polling job refreshes the list every [POLL_INTERVAL_MS] so the
 * viewer count and newly-started sessions stay up to date while the screen is open.
 * Polling stops automatically when the ViewModel is cleared (screen leaves composition).
 */
class LiveViewModel : ViewModel() {

    /** null = loading; empty list = loaded but none available */
    private val _lives = MutableStateFlow<List<LiveSession>?>(null)
    val lives: StateFlow<List<LiveSession>?> = _lives

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private var pollJob: Job? = null

    init { startPolling() }

    /** Starts (or restarts) the polling loop. */
    fun startPolling() {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            while (true) {
                fetchOnce()
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    /** Manual pull-to-refresh. */
    fun fetch() {
        _isRefreshing.value = true
        viewModelScope.launch {
            fetchOnce()
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchOnce() {
        val dtos = MainRepository.getLives()
        _lives.value = dtos.map { dto ->
            LiveSession(
                id         = dto.id.hashCode(),
                lawyerName = dto.lawyerName,
                specialty  = "",
                topic      = dto.title,
                viewers    = dto.viewersCount,
                isLive     = true
            )
        }.ifEmpty {
            _lives.value?.takeIf { it.isNotEmpty() } ?: emptyList()
        }
    }

    override fun onCleared() {
        pollJob?.cancel()
        super.onCleared()
    }

    companion object {
        /** 30 seconds between polls while the screen is visible. */
        private const val POLL_INTERVAL_MS = 30_000L
    }
}
