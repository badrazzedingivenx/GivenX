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
 * Data source: GET /api/lawyers via the Postman Mock (RetrofitClient.mockApi).
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

    init { load(initial = true) }

    /** Pull-to-refresh: shows the spinner immediately then re-fetches. */
    fun refresh() { load(initial = false) }

    private fun load(initial: Boolean) {
        if (initial) _lawyers.value = null   // show skeleton on first load
        _isError.value = false
        _isRefreshing.value = !initial        // spinner for pull-to-refresh only
        viewModelScope.launch {
            try {
                val response = RetrofitClient.mockApi.getLawyers()
                if (response.isSuccessful) {
                    _lawyers.value = response.body()?.map { it.toItem() } ?: emptyList()
                } else {
                    if (_lawyers.value == null) _lawyers.value = emptyList()
                    _isError.value = true
                }
            } catch (_: Exception) {
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
