package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

// ─── Data Model ───────────────────────────────────────────────────────────────
data class LegalCategory(
    val title: String,
    val icon: ImageVector,
    val domaine: String   // matches LawyerItem.domaine for filtering
)

// ─── User Dashboard Host ──────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardHost(
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
    val innerNavController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Live user initials + photo from REST profile
    val profile by userViewModel.profile.collectAsStateWithLifecycle()
    val photoUrl = profile?.photoUrl?.takeIf { it.isNotBlank() }
        ?: profile?.let { "" }  // resolved via UserSession after fetch, see below
        ?: UserSession.avatarUrl.takeIf { it.isNotBlank() }
    val initials = profile?.let {
        "${it.firstName} ${it.lastName}".trim()
            .split(" ")
            .mapNotNull { w -> w.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
    }.takeIf { !it.isNullOrBlank() }

    Scaffold(
        topBar = {
            val onMessagesRoute = currentRoute == UserTab.Messages.route
            val onMatchingRoute = currentRoute == UserTab.Matching.route
            val showTitleBar    = onMessagesRoute || onMatchingRoute
            val titleBarText    = when {
                onMessagesRoute -> "Messages"
                onMatchingRoute -> "Matching"
                else            -> ""
            }
            CenterAlignedTopAppBar(
                navigationIcon = {
                    if (showTitleBar) {
                        IconButton(onClick = {
                            if (onMatchingRoute) {
                                innerNavController.navigate(UserTab.Home.route) {
                                    popUpTo(innerNavController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            } else {
                                innerNavController.popBackStack()
                            }
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour",
                                tint = AppDarkGreen
                            )
                        }
                    }
                },
                title = {
                    if (showTitleBar) {
                        Text(
                            titleBarText,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = AppDarkGreen
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.logo_app),
                            contentDescription = "Logo",
                            modifier = Modifier.height(126.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                },
                actions = {
                    val unreadCount = NotificationRepository.userNotifications.count { !it.isRead }
                    // ── Notification bell ──────────────────────────────────
                    IconButton(onClick = onNavigateToNotifications) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) Badge(containerColor = Color(0xFFD32F2F)) {
                                    Text(
                                        if (unreadCount > 9) "9+" else "$unreadCount",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = AppDarkGreen
                            )
                        }
                    }
                    // ── Profile avatar ─────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { onNavigateToProfile() }
                    ) {
                        // Gold-bordered avatar circle
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape    = CircleShape,
                            color    = AppDarkGreen.copy(alpha = 0.10f),
                            border   = androidx.compose.foundation.BorderStroke(2.dp, AppGoldColor)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val resolvedPhoto = profile?.photoUrl?.takeIf { it.isNotBlank() }
                                    ?: UserSession.avatarUrl.takeIf { it.isNotBlank() }
                                when {
                                    !resolvedPhoto.isNullOrBlank() -> AsyncImage(
                                        model              = resolvedPhoto,
                                        contentDescription = "Profil",
                                        modifier           = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale       = ContentScale.Crop
                                    )
                                    !initials.isNullOrBlank() -> Text(
                                        text       = initials,
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize   = 13.sp,
                                        color      = AppGoldColor
                                    )
                                    else -> Icon(
                                        Icons.Default.Person,
                                        contentDescription = "Profil",
                                        tint     = AppGoldColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        // Online green dot
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .align(Alignment.BottomEnd)
                                .background(Color.White, CircleShape)
                                .padding(2.dp)
                                .background(Color(0xFF34A853), CircleShape)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            UserNavBottomBar(currentRoute = currentRoute) { tab ->
                if (tab is UserTab.Profile) {
                    onNavigateToProfile()
                } else {
                    innerNavController.navigate(tab.route) {
                        popUpTo(innerNavController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        DashBoardBackground {
            NavHost(
                navController = innerNavController,
                startDestination = UserTab.Home.route
            ) {
                composable(UserTab.Home.route) {
                    UserHomeTabContent(
                        paddingValues = paddingValues,
                        onNavigateToAbout = onNavigateToAbout,
                        onNavigateToCategory = onNavigateToCategory
                    )
                }
                composable(UserTab.Cases.route) {
                    UserCasesTabContent(
                        paddingValues = paddingValues,
                        onNavigateToConsulter = onNavigateToAppointments,
                        onNavigateToMessages = {
                            innerNavController.navigate(UserTab.Messages.route) {
                                popUpTo(innerNavController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToDocuments = onNavigateToDocuments,
                        onNavigateToFacturation = onNavigateToFacturation,
                        onNavigateToDossier = onNavigateToDossier
                    )
                }
                composable(UserTab.Messages.route) {
                    MessagesInboxScreen(
                        isLawyer = false,
                        paddingValues = paddingValues,
                        onNavigateToChat = onNavigateToChat
                    )
                }
                composable(UserTab.Matching.route) {
                    LegalMatchingScreen(paddingValues = paddingValues)
                }
                composable(UserTab.Reels.route) {
                    LegalReelsScreen(paddingValues = paddingValues)
                }
                composable(UserTab.Live.route) {
                    LiveSessionsScreen(paddingValues = paddingValues)
                 }
            }
        }
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
            .padding(paddingValues)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // ── Hero Section ─────────────────────────────────────────────
        item { HomeHeroSection(onAbout = onNavigateToAbout) }

        // ── Service Grid ─────────────────────────────────────────────
        item { SectionHeader(title = "Domaines Juridiques") }
        item { ServiceCategoryGrid(categories = categories, onCategoryClick = onNavigateToCategory) }

        // ── Quick Stats ──────────────────────────────────────────────
        item { SectionHeader(title = "En chiffres") }
        item { HomeQuickStats() }

        item { Spacer(modifier = Modifier.height(8.dp)) }
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
            .height(195.dp),
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
                    radius = 200.dp.toPx(),
                    center = Offset(size.width * 0.88f, -size.height * 0.15f)
                )
                drawCircle(
                    color = Color(0xFFD4AF37).copy(alpha = 0.05f),
                    radius = 140.dp.toPx(),
                    center = Offset(0f, size.height * 1.05f)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Votre droit,\nnos experts.",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 27.sp,
                        color = Color.White,
                        lineHeight = 33.sp
                    )
                    Text(
                        text = "Trouvez l'avocat qu'il vous faut en quelques secondes.",
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.68f),
                        lineHeight = 19.sp
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppGoldColor),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Consulter",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AppDarkGreen
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = AppDarkGreen,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    OutlinedButton(
                        onClick = onAbout,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AppGoldColor.copy(alpha = 0.60f)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "À Propos",
                            fontFamily = FontFamily.Serif,
                            fontSize = 13.sp,
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
        color = Color.White.copy(alpha = 0.92f),
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
                        .padding(vertical = 14.dp, horizontal = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = AppGoldColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        value,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        label,
                        color = Color.White.copy(alpha = 0.62f),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}
