package com.example.client_mobile.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            // Stories — always try live API first
            fetchStories()
            // Posts — try API, fall back to mock if missing
            fetchPosts()
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchStories() {
        try {
            val response = RetrofitClient.haqApi.getStories()
            if (response.isSuccessful && !response.body()?.data.isNullOrEmpty()) {
                _stories.value = response.body()!!.data
            } else {
                _stories.value = mockStories()
            }
        } catch (_: Exception) {
            _stories.value = mockStories()
        }
    }

    private suspend fun fetchPosts() {
        try {
            val response = RetrofitClient.haqApi.getLegalFeed()
            val data = if (response.isSuccessful) response.body()?.data else null

            if (!data.isNullOrEmpty()) {
                _posts.value = data
            } else {
                // Backend endpoint not ready → use rich mock data for PFE demo
                _posts.value = mockPosts()
            }
        } catch (_: Exception) {
            // Network error → still show mock data so the UI is presentable
            _posts.value = mockPosts()
            _isError.value = false // don't show error when mock data fills the gap
        }
    }

    // ─── Mock Data Provider (PFE Demo) ───────────────────────────────────────

    private fun mockPosts(): List<LegalPostDto> = listOf(
        LegalPostDto(
            lawyerId     = "1",
            lawyerName   = "Me. Yassine Alaoui",
            avatarUrl    = "https://randomuser.me/api/portraits/men/32.jpg",
            postImageUrl = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=800",
            legalText    = "⚖️ Rappel important : Selon l'article 475 du Code de la Famille marocain, toute convention matrimoniale doit être établie par acte notarié avant la célébration du mariage. N'attendez pas qu'il soit trop tard — consultez un avocat spécialisé en droit familial pour protéger vos droits.\n\n#DroitDeLaFamille #Haqqi #ConseilJuridique",
            date         = "2026-04-16T08:00:00Z",
            likesCount   = 142,
            isLiked      = false,
            isVerified   = true,
            commentsCount = 23
        ),
        LegalPostDto(
            lawyerId     = "2",
            lawyerName   = "Me. Fatima Zahra Benali",
            avatarUrl    = "https://randomuser.me/api/portraits/women/44.jpg",
            postImageUrl = "https://images.unsplash.com/photo-1450101499163-c8848c66ca85?w=800",
            legalText    = "🏢 Droit des affaires : La création d'une SARL au Maroc nécessite un capital minimum de 10 000 DH depuis la réforme. Cependant, plusieurs aspects statutaires sont souvent négligés par les entrepreneurs. Notre cabinet accompagne les startups de A à Z.\n\n#StartupMaroc #DroitDesAffaires #SARL",
            date         = "2026-04-15T14:30:00Z",
            likesCount   = 89,
            isLiked      = false,
            isVerified   = true,
            commentsCount = 11
        ),
        LegalPostDto(
            lawyerId     = "3",
            lawyerName   = "Me. Karim Tazi",
            avatarUrl    = "https://randomuser.me/api/portraits/men/86.jpg",
            postImageUrl = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=800",
            legalText    = "📋 Saviez-vous que le délai de prescription pour les infractions pénales varie selon leur gravité ? \n• Crimes : 15 ans\n• Délits : 5 ans  \n• Contraventions : 1 an\n\nCes délais courent à partir du jour où l'infraction a été commise. Partagez pour informer !\n\n#DroitPénal #Justice #Haqqi",
            date         = "2026-04-15T09:00:00Z",
            likesCount   = 207,
            isLiked      = false,
            isVerified   = true,
            commentsCount = 45
        ),
        LegalPostDto(
            lawyerId     = "4",
            lawyerName   = "Me. Sara El Mansouri",
            avatarUrl    = "https://randomuser.me/api/portraits/women/62.jpg",
            postImageUrl = "https://images.unsplash.com/photo-1521791055366-0d553872952f?w=800",
            legalText    = "🏠 Droit immobilier : Avant de signer un compromis de vente, vérifiez TOUJOURS :\n✅ Le titre foncier (réquisition d'immatriculation)\n✅ L'absence de servitudes cachées\n✅ Le certificat de conformité\n✅ Les charges de copropriété en souffrance\n\nContactez-nous pour un audit complet de votre projet immobilier.\n\n#DroitImmobilier #Casablanca",
            date         = "2026-04-14T16:00:00Z",
            likesCount   = 334,
            isLiked      = false,
            isVerified   = true,
            commentsCount = 67
        ),
        LegalPostDto(
            lawyerId     = "5",
            lawyerName   = "Me. Hassan Idrissi",
            avatarUrl    = "https://randomuser.me/api/portraits/men/22.jpg",
            postImageUrl = "https://images.unsplash.com/photo-1591115765373-5207764f72e7?w=800",
            legalText    = "💼 Code du travail : Tout licenciement abusif ouvre droit à des dommages-intérêts. Selon l'article 41 du Code du Travail marocain, l'employeur doit respecter la procédure disciplinaire sous peine de nullité. Vos droits valent d'être défendus.\n\n#DroitDuTravail #LicenciementAbusif #Haqqi",
            date         = "2026-04-13T11:00:00Z",
            likesCount   = 156,
            isLiked      = false,
            isVerified   = true,
            commentsCount = 28
        )
    )

    private fun mockStories(): List<StoryDto> = listOf(
        StoryDto(id = "s1", lawyerName = "Me. Y. Alaoui", lawyerAvatar = "https://randomuser.me/api/portraits/men/32.jpg", isLive = true),
        StoryDto(id = "s2", lawyerName = "Me. F. Benali", lawyerAvatar = "https://randomuser.me/api/portraits/women/44.jpg", isLive = false),
        StoryDto(id = "s3", lawyerName = "Me. K. Tazi", lawyerAvatar = "https://randomuser.me/api/portraits/men/86.jpg", isLive = false),
        StoryDto(id = "s4", lawyerName = "Me. S. Mansouri", lawyerAvatar = "https://randomuser.me/api/portraits/women/62.jpg", isLive = false),
        StoryDto(id = "s5", lawyerName = "Me. H. Idrissi", lawyerAvatar = "https://randomuser.me/api/portraits/men/22.jpg", isLive = false)
    )
}
