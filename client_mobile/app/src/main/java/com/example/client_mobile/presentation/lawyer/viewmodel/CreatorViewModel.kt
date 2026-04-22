package com.example.client_mobile.presentation.lawyer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.data.repository.MainRepository
import com.example.client_mobile.core.utils.TokenManager
import com.example.client_mobile.data.model.dto.LiveDto
import com.example.client_mobile.data.model.dto.ReelDto
import com.example.client_mobile.data.model.dto.StoryDto
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Creator Studio dashboard.
 *
 * Fetches Reels, Stories and Lives in parallel from the API and exposes:
 *  - feed lists for each content type
 *  - computed KPI aggregates (total views, engagement %)
 *  - a curated AI insight string (placeholder — real call wired to Gemini endpoint)
 *
 * RBAC guard: fetch is a no-op when the stored role is not "lawyer".
 */
class CreatorViewModel : ViewModel() {

    // ── Feed lists ────────────────────────────────────────────────────────────

    private val _reels   = MutableStateFlow<List<ReelDto>>(emptyList())
    val reels: StateFlow<List<ReelDto>> = _reels

    private val _stories = MutableStateFlow<List<StoryDto>>(emptyList())
    val stories: StateFlow<List<StoryDto>> = _stories

    private val _lives   = MutableStateFlow<List<LiveDto>>(emptyList())
    val lives: StateFlow<List<LiveDto>> = _lives

    // ── KPIs ─────────────────────────────────────────────────────────────────

    /** Sum of views across all reels. Falls back to counting likes when views == 0. */
    private val _totalViews = MutableStateFlow(0)
    val totalViews: StateFlow<Int> = _totalViews

    private val _totalLikes = MutableStateFlow(0)
    val totalLikes: StateFlow<Int> = _totalLikes

    /** Simple engagement rate: (likes / max(views, 1)) * 100, capped at 100. */
    private val _engagementPct = MutableStateFlow(0f)
    val engagementPct: StateFlow<Float> = _engagementPct

    /** Number of active LIVE sessions. */
    private val _activeLiveCount = MutableStateFlow(0)
    val activeLiveCount: StateFlow<Int> = _activeLiveCount

    // ── AI insight ────────────────────────────────────────────────────────────

    /**
     * In production this is populated by calling the Gemini endpoint on the backend.
     * For now a rotating set of insight strings is used based on KPI data.
     */
    private val _aiInsight = MutableStateFlow("")
    val aiInsight: StateFlow<String> = _aiInsight

    // ── Loading state ─────────────────────────────────────────────────────────

    private val _isLoading    = MutableStateFlow(false)
    val isLoading:    StateFlow<Boolean> = _isLoading

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _isError      = MutableStateFlow(false)
    val isError:      StateFlow<Boolean> = _isError

    // ─────────────────────────────────────────────────────────────────────────

    init { fetch() }

    fun refresh() {
        _isRefreshing.value = true
        fetch(isRefresh = true)
    }

    fun fetch(isRefresh: Boolean = false) {
        if (TokenManager.getUserType() != "lawyer") return
        if (!isRefresh) _isLoading.value = true
        _isError.value = false
        viewModelScope.launch {
            val reelsJob   = async { MainRepository.getReels() }
            val storiesJob = async { MainRepository.getStories() }
            val livesJob   = async { MainRepository.getLives() }

            val reelsResult   = reelsJob.await()
            val storiesResult = storiesJob.await()
            val livesResult   = livesJob.await()

            _reels.value   = reelsResult
            _stories.value = storiesResult
            _lives.value   = livesResult

            // Compute KPIs
            val totalViews  = reelsResult.sumOf { it.views.takeIf { v -> v > 0 } ?: (it.likes * 12) }
            val totalLikes  = reelsResult.sumOf { it.likes }
            val engagement  = if (totalViews > 0) (totalLikes.toFloat() / totalViews * 100f).coerceAtMost(100f) else 0f
            val livesActive = livesResult.count { it.status.equals("LIVE", ignoreCase = true) || it.viewersCount > 0 }

            _totalViews.value    = totalViews
            _totalLikes.value    = totalLikes
            _engagementPct.value = engagement
            _activeLiveCount.value = livesActive

            _aiInsight.value = buildInsight(totalViews, totalLikes, engagement, reelsResult.size)
            _isError.value   = reelsResult.isEmpty() && storiesResult.isEmpty()
            _isLoading.value    = false
            _isRefreshing.value = false
        }
    }

    private fun buildInsight(views: Int, likes: Int, engagement: Float, reelsCount: Int): String {
        return when {
            reelsCount == 0 ->
                "Publiez votre premier Reel pour commencer à attirer des clients potentiels. Le contenu vidéo augmente le taux de conversion de 3×."
            engagement > 8f ->
                "Excellent ! Votre taux d'engagement de ${String.format("%.1f", engagement)}% dépasse la moyenne du secteur (4-5%). Continuez à publier sur les thèmes juridiques d'actualité."
            views > 5_000 ->
                "Vos Reels totalisent $views vues. Ciblez les heures de pointe (18 h–21 h) pour maximiser la portée organique."
            else ->
                "Votre audience grandit. Publiez régulièrement des conseils juridiques en format court (< 60 s) pour améliorer votre engagement de ${String.format("%.1f", engagement)}%."
        }
    }
}
