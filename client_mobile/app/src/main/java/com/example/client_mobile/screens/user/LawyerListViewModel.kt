package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.dto.LawyerDto
import com.example.client_mobile.screens.shared.LawyerItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the lawyer discovery screen.
 * Data source: GET /api/lawyers via HaqApiService.
 *
 * Supports:
 *  - Server-side domaine pre-filter (passed from navigation argument)
 *  - Client-side search & chip filtering (handled in the composable)
 *  - Pull-to-refresh
 */
class LawyerListViewModel : ViewModel() {

    /** null = loading | emptyList = loaded with no results | non-empty = data ready */
    private val _lawyers = MutableStateFlow<List<LawyerItem>?>(null)
    val lawyers: StateFlow<List<LawyerItem>?> = _lawyers

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    /** true after a network failure so the UI can show NoConnectionScreen. */
    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    /** The domaine used for the current API request (set once from navigation). */
    private var activeDomaine: String? = null

    init { load(initial = true) }

    /**
     * Called by the screen when the [domaine] navigation argument is available.
     * Only re-fetches if the domaine actually changed.
     */
    fun setDomaine(domaine: String) {
        val trimmed = domaine.takeIf { it.isNotBlank() }
        if (trimmed == activeDomaine) return
        activeDomaine = trimmed
        load(initial = true)
    }

    /** Pull-to-refresh: shows the spinner immediately then re-fetches. */
    fun refresh() { load(initial = false) }

    private fun load(initial: Boolean) {
        if (initial) _lawyers.value = null   // show skeleton on first load
        _isError.value = false
        _isRefreshing.value = !initial       // spinner for pull-to-refresh only
        viewModelScope.launch {
            try {
                // Always fetch ALL lawyers — domain/specialty filtering is done
                // client-side in the composable so "Tous" shows every lawyer.
                val response = RetrofitClient.haqApi.getLawyers(
                    domaine = null,
                    limit   = 100
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    _lawyers.value = response.body()?.data?.map { it.toItem() } ?: emptyList()
                } else {
                    if (_lawyers.value == null) _lawyers.value = emptyList()
                    _isError.value = true
                }
            } catch (e: Exception) {
                if (_lawyers.value == null) _lawyers.value = emptyList()
                _isError.value = true
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun LawyerDto.toItem() = LawyerItem(
        id          = id,
        name        = name,
        specialty   = specialty,
        city        = location,
        rating      = rating,
        reviewCount = reviewCount,
        yearsExp    = experience,
        bio         = bio,
        isVerified  = isVerified,
        domaine     = domaine.ifBlank { specialty },
        avatarUrl   = avatarUrl
    )

    /** Returns the [LawyerItem] with the given ID, or null. */
    fun findById(id: String): LawyerItem? = _lawyers.value?.firstOrNull { it.id == id }
}
