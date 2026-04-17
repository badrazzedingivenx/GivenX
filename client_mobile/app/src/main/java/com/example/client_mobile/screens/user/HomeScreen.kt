package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.TopBarActions
import com.example.client_mobile.screens.shared.NotificationRepository
import com.example.client_mobile.screens.shared.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.client_mobile.R
import coil.compose.AsyncImage
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.screens.lawyer.LawyerDashboardHost


// ─── Data Model ───────────────────────────────────────────────────────────────
data class LegalCategory(
    val title: String,
    val icon: ImageVector,
    val domaine: String   // matches LawyerItem.domaine for filtering
)

// ─── Main Dashboard Host (Social-First, Universal Shell) ─────────────────────
/**
 * Universal host that wraps both Lawyers and Clients with a shared bottom nav.
 * Tab order: [Feed] → [Dashboard] → [Messages] → [Profile]
 *
 * This is the startDestination after login — Feed is the heart of the app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardHost(
    isLawyer: Boolean = TokenManager.getUserType() == "lawyer",
    // Lawyer callbacks
    onNavigateToLawyerProfile:       () -> Unit = {},
    onNavigateToNotifications:       () -> Unit = {},
    onNavigateToChat:                (String) -> Unit = {},
    onNavigateToRequests:            () -> Unit = {},
    onNavigateToPayments:            () -> Unit = {},
    onNavigateToCreator:             () -> Unit = {},
    // Client callbacks
    onNavigateToUserProfile:         () -> Unit = {},
    onNavigateToAbout:               () -> Unit = {},
    onNavigateToLawyerDetail:        (String) -> Unit = {},
    onNavigateToCategory:            (String) -> Unit = {},
    onNavigateToAppointments:        () -> Unit = {},
    onNavigateToDocuments:           () -> Unit = {},
    onNavigateToFacturation:         () -> Unit = {},
    onNavigateToDossier:             (String) -> Unit = {}
) {
    val innerNav  = rememberNavController()
    val backEntry by innerNav.currentBackStackEntryAsState()
    val current   = backEntry?.destination?.route

    val onProfile = if (isLawyer) onNavigateToLawyerProfile else onNavigateToUserProfile

    AppScaffold(
        showBackground = false,
        topBar = {}, // Empty to allow per-screen detailed headers
        bottomBar = {
            HaqqiPremiumBottomBar(
                currentRoute = current,
                onTabSelected = { tab ->
                    when (tab) {
                        is MainTab.Profile -> onProfile()
                        else -> {
                            innerNav.navigate(tab.route) {
                                popUpTo(MainTab.Reels.route) { inclusive = false }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                },
                onReelsClick = {
                    innerNav.navigate(MainTab.Reels.route) {
                        popUpTo(MainTab.Reels.route) { inclusive = false }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            navController    = innerNav,
            startDestination = MainTab.Reels.route,
            modifier         = Modifier.fillMaxSize()
        ) {
            // ── Tab 1: Social Feed (Primary) ──────────────────────────────────
            composable(MainTab.Accueil.route) {
                HaqqiSocialFeedScreen(
                    paddingValues = padding,
                    isLawyer      = isLawyer,
                    onNavigateToNotifications = onNavigateToNotifications,
                    onCreatePost  = { onNavigateToCreator() }
                )
            }

            // ── Tab 2: Messages ───────────────────────────────────────────────
            composable(MainTab.Messages.route) {
                MessagesInboxScreen(
                    isLawyer      = isLawyer,
                    paddingValues = padding,
                    onNavigateToNotifications = onNavigateToNotifications,
                    onNavigateToChat = onNavigateToChat
                )
            }

            // ── Tab 3: Reels (center FAB destination) ─────────────────────────
            composable(MainTab.Reels.route) {
                LegalReelsScreen(paddingValues = padding)
            }

            // ── Tab 4: Dossiers (role-specific dashboard) ─────────────────────
            composable(MainTab.Dossiers.route) {
                if (isLawyer) {
                    LawyerDashboardHost(
                        paddingValues             = padding,
                        onNavigateToProfile       = onNavigateToLawyerProfile,
                        onNavigateToNotifications = onNavigateToNotifications,
                        onNavigateToChat          = onNavigateToChat,
                        onNavigateToRequests      = onNavigateToRequests,
                        onNavigateToPayments      = onNavigateToPayments,
                        onNavigateToCreator       = onNavigateToCreator
                    )
                } else {
                    UserDashboardHost(
                        paddingValues             = padding,
                        onNavigateToProfile       = onNavigateToUserProfile,
                        onNavigateToAbout         = onNavigateToAbout,
                        onNavigateToLawyerDetail  = onNavigateToLawyerDetail,
                        onNavigateToCategory      = onNavigateToCategory,
                        onNavigateToNotifications = onNavigateToNotifications,
                        onNavigateToChat          = onNavigateToChat,
                        onNavigateToAppointments  = onNavigateToAppointments,
                        onNavigateToDocuments     = onNavigateToDocuments,
                        onNavigateToFacturation   = onNavigateToFacturation,
                        onNavigateToDossier       = onNavigateToDossier
                    )
                }
            }

            // ── Tab 5: Profile (navigates out of inner nav) ───────────────────
            composable(MainTab.Profile.route) {
                Box(Modifier.fillMaxSize())
            }
        }
    }
}

// ─── Feed-specific Top Bar (Warm Beige + Centered Logo) ───────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardHost(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToLawyerDetail: (String) -> Unit = {},
    onNavigateToCategory: (String) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToChat: (String) -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToDocuments: () -> Unit = {},
    onNavigateToFacturation: () -> Unit = {},
    onNavigateToDossier: (String) -> Unit = {},
    userViewModel: UserViewModel = viewModel()
) {
    // Live user initials + photo from REST profile
    val profile by userViewModel.profile.collectAsStateWithLifecycle()
    val photoUrl = profile?.effectiveAvatarUrl()?.takeIf { it.isNotBlank() }
        ?: UserSession.avatarUrl.takeIf { it.isNotBlank() }
    val initials = profile?.effectiveFullName()?.let {
        it.trim()
            .split(" ")
            .mapNotNull { w -> w.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
    }.takeIf { !it.isNullOrBlank() }

    val dashboardViewModel: UserDashboardViewModel = viewModel()
    val dashboardError by dashboardViewModel.errorMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(dashboardError) {
        if (!dashboardError.isNullOrBlank()) {
            snackbarHostState.showSnackbar(dashboardError!!)
            dashboardViewModel.clearError()
        }
    }

    BaseScreen(
        title = "ACCUEIL",
        onNotifications = onNavigateToNotifications,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        actions = {
            TopBarActions(
                unreadCount     = NotificationRepository.userNotifications.count(),
                photoUrl        = photoUrl,
                initials        = initials,
                onNotifications = onNavigateToNotifications,
                onProfile       = onNavigateToProfile
            )
        }
    ) { localPadding ->
        UserHomeTabContent(
            paddingValues = PaddingValues(
                 top = localPadding.calculateTopPadding(),
                 bottom = paddingValues.calculateBottomPadding()
            ),
            onNavigateToAbout = onNavigateToAbout,
            onNavigateToCategory = onNavigateToCategory
        )
    }
}

// ─── Home Screen ──────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    onNavigateToCases: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToLawyerDetail: (String) -> Unit = {},
    onNavigateToCategory: (String) -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToChat: (String) -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToDocuments: () -> Unit = {},
    onNavigateToFacturation: () -> Unit = {}
) {
    UserDashboardHost(
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToAbout = onNavigateToAbout,
        onNavigateToLawyerDetail = onNavigateToLawyerDetail,
        onNavigateToCategory = onNavigateToCategory,
        onNavigateToNotifications = onNavigateToNotifications,
        onNavigateToChat = onNavigateToChat,
        onNavigateToAppointments = onNavigateToAppointments,
        onNavigateToDocuments = onNavigateToDocuments,
        onNavigateToFacturation = onNavigateToFacturation
    )
}

// ─── User Home Tab Content ────────────────────────────────────────────────────

// ─── User Home Tab Content ────────────────────────────────────────────────────
@Composable
internal fun UserHomeTabContent(
    paddingValues: PaddingValues,
    onNavigateToAbout: () -> Unit = {},
    onNavigateToCategory: (String) -> Unit = {}
) {
    val categories = listOf(
        LegalCategory("Droit de la\nFamille", Icons.Default.Groups,        domaine = "Droit Civil"),
        LegalCategory("Droit des\nAffaires",  Icons.Default.BusinessCenter, domaine = "Droit des Affaires"),
        LegalCategory("Droit\nPénal",         Icons.Default.Gavel,          domaine = "Droit Pénal"),
        LegalCategory("Droit\nImmobilier",    Icons.Default.Apartment,      domaine = "Droit Immobilier"),
        LegalCategory("Droit du\nTravail",    Icons.Default.Work,           domaine = "Droit du Travail"),
        LegalCategory("Droit\nFiscal",        Icons.Default.AccountBalance,  domaine = "Droit Fiscal")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding(),
            bottom = paddingValues.calculateBottomPadding() + 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Top balancing spacer ─────────────────────────────────────
        item { Spacer(modifier = Modifier.height(12.dp)) }

        // ── Hero Section ─────────────────────────────────────────────
        item { HomeHeroSection(onAbout = onNavigateToAbout) }

        // ── Service Grid ─────────────────────────────────────────────
        item { 
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(title = "Domaines Juridiques")
                ServiceCategoryGrid(categories = categories, onCategoryClick = onNavigateToCategory)
            }
        }

        // ── Quick Stats ──────────────────────────────────────────────
        item { 
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(title = "En chiffres")
                HomeQuickStats()
            }
        }
    }
}

// ─── Messages Tab (placeholder) ───────────────────────────────────────────────
@Composable
internal fun UserMessagesTabContent(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = AppDarkGreen.copy(alpha = 0.35f),
                                modifier = Modifier.size(64.dp)
                            )
            Text(
                text = "Messages",
                fontSize = 20.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = AppDarkGreen
            )
            Text(
                text = "Bientôt disponible",
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                color = Color.Gray
            )
        }
    }
}

// ─── Hero Section ─────────────────────────────────────────────────────────────
@Composable
private fun HomeHeroSection(onAbout: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp), // Reduced from 195dp
        shape = RoundedCornerShape(24.dp),
        color = AppDarkGreen,
        border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.50f)),
        shadowElevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Decorative gold circles
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0xFFD4AF37).copy(alpha = 0.08f),
                    radius = 180.dp.toPx(), // Slightly scaled down
                    center = Offset(size.width * 0.88f, -size.height * 0.15f)
                )
                drawCircle(
                    color = Color(0xFFD4AF37).copy(alpha = 0.05f),
                    radius = 120.dp.toPx(),
                    center = Offset(0f, size.height * 1.05f)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp), // Reduced from 24.dp
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) { // Reduced from 6.dp
                    Text(
                        text = "Votre droit,\nnos experts.",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp, // Reduced from 27.sp
                        color = Color.White,
                        lineHeight = 30.sp
                    )
                    Text(
                        text = "Trouvez l'avocat qu'il vous faut en quelques secondes.",
                        fontFamily = FontFamily.Serif,
                        fontSize = 12.sp, // Reduced from 13.sp
                        color = Color.White.copy(alpha = 0.68f),
                        lineHeight = 17.sp
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppGoldColor),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Consulter",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AppDarkGreen
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = AppDarkGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    OutlinedButton(
                        onClick = onAbout,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AppGoldColor.copy(alpha = 0.60f)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "À Propos",
                            fontFamily = FontFamily.Serif,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ─── Service Category Grid ────────────────────────────────────────────────────
@Composable
private fun ServiceCategoryGrid(
    categories: List<LegalCategory>,
    onCategoryClick: (String) -> Unit = {}
) {
    // LazyVerticalGrid cannot be nested inside a verticalScroll Column — use plain Rows
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        categories.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { category ->
                    CategoryCard(
                        modifier = Modifier.weight(1f),
                        category = category,
                        onClick = { onCategoryClick(category.domaine) }
                    )
                }
                // Pad out incomplete last row
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    modifier: Modifier = Modifier,
    category: LegalCategory,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.10f)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = AppDarkGreen
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.title,
                        tint = AppGoldColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = category.title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = AppDarkGreen,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )
        }
    }
}

// ─── Quick Stats Row ──────────────────────────────────────────────────────────
@Composable
private fun HomeQuickStats() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(
            Triple(Icons.Default.VerifiedUser, "240+", "Avocats certifiés"),
            Triple(Icons.Default.Gavel, "1 200+", "Dossiers traités"),
            Triple(Icons.Default.Star, "4.8 / 5", "Satisfaction")
        ).forEach { (icon, value, label) ->
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                color = AppDarkGreen,
                border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.35f)),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AppGoldColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = value,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = label,
                        fontFamily = FontFamily.Serif,
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 11.sp
                    )
                }
            }
        }
    }
}
