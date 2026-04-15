package com.example.client_mobile.screens.lawyer

import com.example.client_mobile.screens.shared.*

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client_mobile.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.client_mobile.network.dto.LawyerStatsDto
import com.example.client_mobile.services.DossierData

// ─── Data Models ──────────────────────────────────────────────────────────────
data class ScheduleItem(val clientName: String, val time: String, val type: String)
data class TaskItem(val label: String, val dueDate: String, val isDone: Boolean)

// ─── Lawyer Dashboard Host ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerDashboardHost(
    fullName: String = "",
    speciality: String = "",
    profileImageUri: Uri? = null,
    isMasculine: Boolean = true,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToChat: (String) -> Unit = {},
    onNavigateToRequests: () -> Unit = {},
    onNavigateToPayments: () -> Unit = {},
    onNavigateToCreator: () -> Unit = {},
    dashboardViewModel: LawyerDashboardViewModel = viewModel()
) {
    val innerNavController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Collect API data — profile overrides the passed-in params when available
    val lawyerProfile by dashboardViewModel.profile.collectAsStateWithLifecycle()
    val lawyerStats              by dashboardViewModel.stats.collectAsStateWithLifecycle()
    val revenueMonthly           by dashboardViewModel.revenueMonthly.collectAsStateWithLifecycle()
    val recentConsultations      by dashboardViewModel.recentConsultations.collectAsStateWithLifecycle()
    val consultationsError       by dashboardViewModel.consultationsError.collectAsStateWithLifecycle()

    val displayName      = lawyerProfile?.fullName?.takeIf { it.isNotBlank() }   ?: fullName
    val displaySpeciality = lawyerProfile?.speciality?.takeIf { it.isNotBlank() } ?: speciality

    // ── Create bottom sheet state ─────────────────────────────────────────────
    var showCreateSheet       by remember { mutableStateOf(false) }
    var activeLiveId          by remember { mutableStateOf<Long?>(null) }
    var mediaPickerType       by remember { mutableStateOf<MediaPostType?>(null) }

    val dashboardError by dashboardViewModel.errorMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(dashboardError) {
        if (!dashboardError.isNullOrBlank()) {
            snackbarHostState.showSnackbar(dashboardError!!)
            dashboardViewModel.clearError()
        }
    }

    // ── Live Studio overlay ───────────────────────────────────────────────────
    if (activeLiveId != null) {
        LawyerLiveStudioScreen(
            liveId    = activeLiveId!!,
            topic     = "Q&A en direct",
            onEndLive = { activeLiveId = null }
        )
        return
    }

    // ── Media picker overlay (Story or Reel) ──────────────────────────────────
    if (mediaPickerType != null) {
        MediaPickerFlow(
            postType    = mediaPickerType!!,
            onPublished = { mediaPickerType = null },
            onCancel    = { mediaPickerType = null },
            lawyerName  = displayName,
            specialty   = displaySpeciality
        )
        return
    }

    AppScaffold(
        topBar = {
            val onMessagesRoute = currentRoute == LawyerTab.Messages.route
            val titleText = if (onMessagesRoute) "Messages" else null

            StandardTopBar(
                title = titleText ?: "",
                showLogo = !onMessagesRoute,
                onBack = if (onMessagesRoute) { { innerNavController.popBackStack() } } else null,
                actions = {
                    val unreadCount = NotificationRepository.lawyerNotifications.count { !it.isRead }
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
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }
                    }
                    // Profile avatar
                    val hasActiveStory = CreatorRepository.stories.any { it.lawyerName == displayName }
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(if (hasActiveStory) 2.dp else 0.dp),
                            shape    = CircleShape,
                            color    = Color.White.copy(alpha = 0.2f),
                            border   = if (!hasActiveStory) BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)) else null
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (profileImageUri != null) {
                                    AsyncImage(model = profileImageUri, contentDescription = "Avatar", modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                                } else {
                                    Icon(Icons.Default.Person, contentDescription = "Profil",
                                        tint = AppGoldColor, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        if (hasActiveStory) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(1.5.dp, AppGoldColor, CircleShape)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            CreateContentFab(onClick = { showCreateSheet = true })
        },
        bottomBar = {
            LawyerNavBottomBar(currentRoute = currentRoute) { tab ->
                when (tab) {
                    is LawyerTab.Profile -> onNavigateToProfile()
                    is LawyerTab.Creator -> onNavigateToCreator()
                    else -> innerNavController.navigate(tab.route) {
                        popUpTo(LawyerTab.Home.route) { 
                            saveState = true 
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        showBackground = true
    ) { paddingValues ->
        NavHost(
            navController = innerNavController,
            startDestination = LawyerTab.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(LawyerTab.Home.route) {
                AvocatDashboardScreen(
                    paddingValues           = PaddingValues(0.dp),
                    profile                 = lawyerProfile,
                    stats                   = lawyerStats,
                    revenueMonthly          = revenueMonthly,
                    recentConsultations     = recentConsultations,
                    consultationsError      = consultationsError,
                    onNavigateToRequests    = onNavigateToRequests,
                    onNavigateToPayments    = onNavigateToPayments,
                    onNavigateToCreator     = onNavigateToCreator,
                    onRetryConsultations    = { dashboardViewModel.retryConsultations() }
                )
            }
            composable(LawyerTab.Messages.route) {
                MessagesInboxScreen(
                    isLawyer = true,
                    paddingValues = PaddingValues(0.dp),
                    onNavigateToChat = onNavigateToChat
                )
            }
            composable(LawyerTab.Clients.route) {
                LawyerClientsTabContent(paddingValues = PaddingValues(0.dp))
            }

        }
    }

    // ── Create Content Bottom Sheet ───────────────────────────────────────────
    if (showCreateSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCreateSheet = false },
            containerColor   = Color.White,
            shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            CreateContentSheet(
                onPostStory = {
                    showCreateSheet = false
                    mediaPickerType = MediaPostType.Story
                },
                onUploadReel = {
                    showCreateSheet = false
                    mediaPickerType = MediaPostType.Reel
                },
                onGoLive = {
                    showCreateSheet = false
                    activeLiveId = CreatorRepository.goLive(
                        lawyerName = displayName,
                        specialty  = displaySpeciality,
                        topic      = "Q&A en direct"
                    )
                }
            )
        }
    }
}

// ─── Payments Screen ──────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerPaymentsScreen(onBack: () -> Unit) {
    val payments = LawyerSession.payments

    AppScaffold(
        topBar = {
            StandardTopBar(
                title = "Suivi des Paiements",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }
            items(payments) { payment ->
                PaymentCard(payment)
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun PaymentCard(payment: PaymentItem) {
    DashCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(payment.clientName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AppDarkGreen)
                Text(payment.date, fontSize = 11.sp, color = Color.Gray)
                Text(payment.method, fontSize = 11.sp, color = AppGoldColor, fontWeight = FontWeight.Medium)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(payment.amount, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppDarkGreen)
                StatusChip(
                    label = payment.status,
                    containerColor = if (payment.status == "Reçu") StatusGreenBg else StatusOrangeBg,
                    textColor = if (payment.status == "Reçu") StatusGreen else StatusOrange
                )
            }
        }
    }
}

// ─── Clients Management Tab ──────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LawyerClientsTabContent(
    paddingValues: PaddingValues,
    clientsViewModel: LawyerClientsViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tous") }
    val filters = listOf("Tous", "En cours", "Clôturé", "En attente")

    val dossiers     by clientsViewModel.dossiers.collectAsStateWithLifecycle()
    val isLoading    by clientsViewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing by clientsViewModel.isRefreshing.collectAsStateWithLifecycle()
    val isError      by clientsViewModel.isError.collectAsStateWithLifecycle()

    val filtered = dossiers.filter { d ->
        (selectedFilter == "Tous" || d.status.contains(selectedFilter, ignoreCase = true)) &&
        (d.clientNameDisplay().contains(searchQuery, ignoreCase = true) ||
         d.caseNumber.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        SectionHeader(title = "Gestion des Clients")
        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Rechercher un dossier…", fontSize = 14.sp, fontFamily = FontFamily.Serif) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AppDarkGreen.copy(alpha = 0.4f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = AppDarkGreen,
                unfocusedBorderColor = AppDarkGreen.copy(alpha = 0.1f)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontFamily = FontFamily.Serif, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppDarkGreen,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White
                    ),
                    border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.1f))
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Loading bar (initial load only)
        if (isLoading) {
            LinearProgressIndicator(
                modifier   = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                color      = AppGoldColor,
                trackColor = AppDarkGreen.copy(alpha = 0.2f)
            )
        }

        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh    = { clientsViewModel.refresh() },
            modifier     = Modifier.weight(1f)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    isError && dossiers.isEmpty() -> item {
                        NoConnectionScreen(
                            onRetry  = { clientsViewModel.fetch() },
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                    filtered.isEmpty() -> item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = AppDarkGreen.copy(alpha = 0.25f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (searchQuery.isBlank() && selectedFilter == "Tous")
                                        "Aucun dossier client."
                                    else
                                        "Aucun résultat pour cette recherche.",
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    else -> items(filtered) { dossier ->
                        DossierClientCard(dossier)
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

/** Derives a display name from the dossier — uses clientName, then lawyerName, then the case number prefix. */
private fun DossierData.clientNameDisplay(): String {
    if (clientName.isNotBlank()) return clientName
    if (lawyerName.isNotBlank()) return lawyerName
    // Fallback: first segment of caseNumber is used as a short identifier
    // e.g. "HAQ-2024-0312" → "HAQ"
    return caseNumber.substringBefore("-").ifBlank { caseNumber }
}

@Composable
fun DossierClientCard(dossier: DossierData) {
    val clientName = dossier.clientNameDisplay()
    val initial    = clientName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    DashCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // ── Top row: avatar + name + status ──────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AppDarkGreen.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = initial,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp,
                        color      = AppDarkGreen
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = clientName,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        color      = AppDarkGreen
                    )
                    Text(
                        text       = dossier.caseNumber.ifBlank { dossier.id },
                        fontFamily = FontFamily.Serif,
                        fontSize   = 12.sp,
                        color      = AppDarkGreen.copy(alpha = 0.55f)
                    )
                }
                val statusResult = when {
                    dossier.status.contains("En cours",  ignoreCase = true) -> StatusBlue to StatusBlueBg
                    dossier.status.contains("Clôturé",   ignoreCase = true) -> StatusGreen to StatusGreenBg
                    dossier.status.contains("En attente",ignoreCase = true) -> StatusOrange to StatusOrangeBg
                    else -> StatusGray to StatusGrayBg
                }
                StatusChip(
                    label          = dossier.status.ifBlank { "—" },
                    containerColor = statusResult.second,
                    textColor      = statusResult.first
                )
            }

            // ── Category + progress ───────────────────────────────────────────
            if (dossier.category.isNotBlank()) {
                Text(
                    text       = dossier.category,
                    fontFamily = FontFamily.Serif,
                    fontSize   = 12.sp,
                    color      = AppDarkGreen.copy(alpha = 0.60f)
                )
            }
            if (dossier.progress > 0) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text       = "Avancement",
                            fontFamily = FontFamily.Serif,
                            fontSize   = 11.sp,
                            color      = AppDarkGreen.copy(alpha = 0.55f)
                        )
                        Text(
                            text       = "${dossier.progress}%",
                            fontFamily = FontFamily.Serif,
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color      = AppDarkGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress   = { dossier.progress / 100f },
                        modifier   = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color      = AppGoldColor,
                        trackColor = AppDarkGreen.copy(alpha = 0.12f)
                    )
                }
            }
        }
    }
}

// ─── Requests Management Screen ──────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerRequestsScreen(
    onBack: () -> Unit,
    viewModel: LawyerDemandsViewModel = viewModel()
) {
    val pending      by viewModel.pending.collectAsStateWithLifecycle()
    val isLoading    by viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val actionError  by viewModel.actionError.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionError) {
        if (!actionError.isNullOrBlank()) {
            snackbarHostState.showSnackbar(actionError!!)
            viewModel.clearActionError()
        }
    }

    AppScaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            StandardTopBar(
                title = "Nouvelles Demandes",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh    = { viewModel.refresh() },
            modifier     = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppDarkGreen)
                    }
                }
                pending.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint               = AppDarkGreen.copy(alpha = 0.35f),
                                modifier           = Modifier.size(56.dp)
                            )
                            Text(
                                "Aucune demande en attente",
                                fontFamily = FontFamily.Serif,
                                color      = AppDarkGreen.copy(alpha = 0.55f),
                                fontSize   = 15.sp
                            )
                            TextButton(onClick = { viewModel.refresh() }) {
                                Text("Actualiser", color = AppDarkGreen)
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier            = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(4.dp)) }
                        item {
                            SectionHeader(title = "À traiter (${pending.size})")
                        }
                        items(pending, key = { it.id }) { dossier ->
                            DemandCard(
                                dossier   = dossier,
                                onAccept  = { viewModel.accept(dossier.id) },
                                onDecline = { viewModel.reject(dossier.id) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun DemandCard(dossier: DossierData, onAccept: () -> Unit, onDecline: () -> Unit) {
    val clientDisplay = dossier.clientName.ifBlank { dossier.clientNameDisplay() }
    DashCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // ── Top row: client name + status ─────────────────────────────────
            Row(
                verticalAlignment      = Alignment.CenterVertically,
                horizontalArrangement  = Arrangement.SpaceBetween,
                modifier               = Modifier.fillMaxWidth()
            ) {
                Text(
                    clientDisplay,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = AppDarkGreen
                )
                StatusChip(
                    label          = dossier.status.ifBlank { "En attente" },
                    containerColor = StatusBlueBg,
                    textColor      = StatusBlue
                )
            }
            // ── Category ──────────────────────────────────────────────────────
            if (dossier.category.isNotBlank()) {
                Text(
                    dossier.category,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp,
                    color      = AppGoldColor
                )
            }
            // ── Case number + date ────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    dossier.caseNumber.ifBlank { dossier.id },
                    fontSize = 12.sp,
                    color    = AppDarkGreen.copy(alpha = 0.55f)
                )
                if (dossier.openingDate.isNotBlank()) {
                    Text(
                        dossier.openingDate,
                        fontSize = 12.sp,
                        color    = Color.Gray
                    )
                }
            }
            // ── Action buttons ────────────────────────────────────────────────
            HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.05f))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick          = onAccept,
                    modifier         = Modifier.weight(1f).height(42.dp),
                    colors           = ButtonDefaults.buttonColors(containerColor = AppDarkGreen),
                    shape            = RoundedCornerShape(12.dp),
                    contentPadding   = PaddingValues(0.dp)
                ) {
                    Text("Accepter", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = FontFamily.Serif)
                }
                OutlinedButton(
                    onClick          = onDecline,
                    modifier         = Modifier.weight(0.6f).height(42.dp),
                    border           = BorderStroke(1.dp, StatusRed.copy(alpha = 0.5f)),
                    shape            = RoundedCornerShape(12.dp),
                    contentPadding   = PaddingValues(0.dp)
                ) {
                    Text("Refuser", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = StatusRed, fontFamily = FontFamily.Serif)
                }
            }
        }
    }
}


// ─── Lawyer Home Tab Content ──────────────────────────────────────────────────
@Composable
private fun LawyerHomeTabContent(
    paddingValues: PaddingValues,
    fullName: String = "",
    speciality: String = "",
    profileImageUri: Uri? = null,
    isMasculine: Boolean = true,
    stats: LawyerStatsDto? = null,
    onNavigateToRequests: () -> Unit = {},
    onNavigateToPayments: () -> Unit = {},
    onNavigateToCreator: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val today = remember { SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRENCH).format(Date()).replaceFirstChar { it.uppercase() } }

    // KPI values — show "-" while the API call is in flight
    val clientCount    = stats?.totalClients?.toString()    ?: "-"
    val audiencesToday = stats?.audiencesToday?.toString()  ?: "-"
    val revenueMonth   = stats?.totalRevenueMonth?.let { "%.0f MAD".format(it) } ?: "-"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // ── Banner Hero ───────────────────────────────────────────────────────
        DarkDashCard {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = if (isMasculine) "Bonjour, Maître" else "Bonjour, Maîtresse", fontSize = 13.sp, fontFamily = FontFamily.Serif, color = AppGoldColor)
                    Text(text = fullName, fontSize = 22.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = speciality, fontSize = 13.sp, fontFamily = FontFamily.Serif, color = Color.White.copy(alpha = 0.60f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = today, fontSize = 11.sp, fontFamily = FontFamily.Serif, color = AppGoldColor.copy(alpha = 0.75f))
                }
                val hasActiveStory = CreatorRepository.stories.any { it.lawyerName == fullName }
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier
                            .size(60.dp)
                            .padding(if (hasActiveStory) 4.5.dp else 0.dp),
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = if (!hasActiveStory) BorderStroke(2.dp, AppGoldColor) else null
                    ) {
                        if (profileImageUri != null) {
                            AsyncImage(model = profileImageUri, contentDescription = "Avatar", modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                        } else {
                            Image(painter = painterResource(id = R.drawable.logo_user), contentDescription = "Avatar", modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                        }
                    }
                    if (hasActiveStory) {
                        Box(
                            modifier = Modifier
                                .size(66.dp)
                                .border(2.5.dp, AppGoldColor, CircleShape)
                        )
                    }
                }
            }
        }

        // ── RBAC KPI Cards: Mes Clients / Audiences du Jour / Honoraires ──────
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RbacKpiCard(
                modifier  = Modifier.weight(1f),
                icon      = Icons.Default.Groups,
                count     = clientCount,
                label     = "Mes Clients",
                isLoading = stats == null
            )
            RbacKpiCard(
                modifier  = Modifier.weight(1f),
                icon      = Icons.Default.Gavel,
                count     = audiencesToday,
                label     = "Audiences",
                isLoading = stats == null
            )
            RbacKpiCard(
                modifier  = Modifier.weight(1f).clickable { onNavigateToPayments() },
                icon      = Icons.Default.Payments,
                count     = revenueMonth,
                label     = "Honoraires",
                isLoading = stats == null
            )
        }

        // ── Creator Mode Quick Access ────────────────────────────────────────
        SectionHeader(
            title = "Studio Créateur",
            actionLabel = "Gérer",
            onAction = { onNavigateToCreator() }
        )
        DashCard {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onNavigateToCreator() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = AppGoldColor.copy(alpha = 0.12f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AppGoldColor, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Gérer mon contenu social", fontSize = 14.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = AppDarkGreen)
                    val totalContent = (CreatorRepository.stories.size + CreatorRepository.reels.size + CreatorRepository.liveSessions.size)
                    Text("$totalContent publications actives", fontSize = 12.sp, fontFamily = FontFamily.Serif, color = Color.Gray)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
            }
        }

        // ── Nouvelles Demandes quick view ─────────────────────────────────────
        SectionHeader(title = "Dernières Demandes", actionLabel = "Voir tout", onAction = onNavigateToRequests)
        DashCard {
            if (stats?.newRequests == 0) {
                Text("Aucune nouvelle demande.", fontSize = 13.sp, color = Color.Gray, fontFamily = FontFamily.Serif)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = AppDarkGreen.copy(alpha = 0.09f)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = AppDarkGreen, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${stats?.newRequests ?: "-"} demandes en attente", fontSize = 14.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = AppDarkGreen)
                        Text("Consultez vos dossiers", fontSize = 12.sp, fontFamily = FontFamily.Serif, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun NewLeadRow(lead: RequestItem) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = AppDarkGreen.copy(alpha = 0.09f)) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = lead.clientName.first().toString(), fontSize = 16.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = AppDarkGreen)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(lead.clientName, fontSize = 14.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = AppDarkGreen)
            Text(lead.topic, fontSize = 12.sp, fontFamily = FontFamily.Serif, color = Color.Gray)
        }
        Text(lead.date, fontSize = 10.sp, fontFamily = FontFamily.Serif, color = Color.Gray)
    }
}

// ─── RBAC KPI Card (Mes Clients / Audiences du Jour / Honoraires) ─────────────
@Composable
fun RbacKpiCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: String,
    label: String,
    isLoading: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = AppDarkGreen,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = AppGoldColor, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(6.dp))
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = AppGoldColor,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = count,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
            }
            Text(
                text = label,
                fontSize = 10.sp,
                fontFamily = FontFamily.Serif,
                color = Color.White.copy(alpha = 0.65f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun LawyerScheduleRow(item: ScheduleItem) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = RoundedCornerShape(10.dp), color = AppDarkGreen) {
            Text(text = item.time, fontSize = 12.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = AppGoldColor, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.clientName, fontSize = 14.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = AppDarkGreen)
            Text(item.type, fontSize = 12.sp, fontFamily = FontFamily.Serif, color = Color.Gray)
        }
        StatusChip(label = "RDV", containerColor = AppGoldColor.copy(alpha = 0.12f), textColor = AppDarkGreen)
    }
}

@Composable
fun TaskRow(task: TaskItem, onToggle: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = task.isDone, onCheckedChange = { onToggle() }, colors = CheckboxDefaults.colors(checkedColor = AppDarkGreen, checkmarkColor = AppGoldColor))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = task.label, fontSize = 13.sp, fontFamily = FontFamily.Serif, color = if (task.isDone) Color.Gray else AppDarkGreen, textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None)
            Text(text = task.dueDate, fontSize = 11.sp, fontFamily = FontFamily.Serif, color = AppGoldColor)
        }
    }
}

@Composable
fun RevenueBarChart(data: List<Float>, labels: List<String>, highlightIndex: Int) {
    val maxVal = data.max()
    Column {
        Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
            val totalBars = data.size
            val spacing = 5.dp.toPx()
            val barWidth = (size.width - spacing * (totalBars - 1)) / totalBars
            data.forEachIndexed { index, value ->
                val barHeight = (value / maxVal) * size.height
                drawRoundRect(
                    color = if (index == highlightIndex) AppGoldColor else AppDarkGreen.copy(alpha = 0.2f),
                    topLeft = Offset(index * (barWidth + spacing), size.height - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceAround) {
            labels.forEach { Text(it, fontSize = 9.sp, color = Color.Gray) }
        }
    }
}

// ─── Create Content FAB (Gold pulsing '+' button) ─────────────────────────────
@Composable
fun CreateContentFab(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "fabPulse")
    val fabScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 1.08f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fabScale"
    )

    FloatingActionButton(
        onClick           = onClick,
        containerColor    = AppGoldColor,
        contentColor      = AppDarkGreen,
        shape             = CircleShape,
        modifier          = Modifier.scale(fabScale).size(58.dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = "Créer du contenu",
            modifier = Modifier.size(28.dp))
    }
}

// ─── Create Content Bottom Sheet ──────────────────────────────────────────────
@Composable
fun CreateContentSheet(
    onPostStory:   () -> Unit,
    onUploadReel:  () -> Unit,
    onGoLive:      () -> Unit
) {
    // Pulse for Go Live row
    val infiniteTransition = rememberInfiniteTransition(label = "goLivePulse")
    val liveAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 0.35f,
        animationSpec = infiniteRepeatable(
            animation  = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "liveAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            "Créer du contenu",
            fontFamily    = FontFamily.Serif,
            fontWeight    = FontWeight.Bold,
            fontSize      = 20.sp,
            color         = AppDarkGreen,
            modifier      = Modifier.padding(bottom = 8.dp)
        )

        CreateOptionRow(
            icon     = Icons.Default.AutoStories,
            title    = "Publier une Story",
            subtitle = "Image ou conseil rapide visible 24h",
            tint     = AppDarkGreen,
            onClick  = onPostStory
        )
        HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.07f))

        CreateOptionRow(
            icon     = Icons.Default.VideoLibrary,
            title    = "Uploader un Reel",
            subtitle = "Conseil juridique en vidéo courte",
            tint     = AppDarkGreen,
            onClick  = onUploadReel
        )
        HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.07f))

        // Go Live — with pulsing red dot
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onGoLive() }
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(modifier = Modifier.size(48.dp), shape = CircleShape,
                    color = StatusRed.copy(alpha = 0.12f)) {}
                Icon(Icons.Default.LiveTv, contentDescription = null,
                    tint = StatusRed, modifier = Modifier.size(24.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .align(Alignment.TopEnd)
                        .background(StatusRed.copy(alpha = liveAlpha), CircleShape)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Aller en Live", fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold, fontSize = 15.sp, color = StatusRed)
                Text("Démarrer une session Q&A en direct",
                    fontFamily = FontFamily.Serif, fontSize = 12.sp,
                    color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = StatusRed.copy(alpha = 0.50f))
        }
    }
}

@Composable
private fun CreateOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(modifier = Modifier.size(48.dp), shape = CircleShape,
            color = AppDarkGreen.copy(alpha = 0.08f)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AppDarkGreen)
            Text(subtitle, fontFamily = FontFamily.Serif, fontSize = 12.sp, color = Color.Gray)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null,
            tint = AppDarkGreen.copy(alpha = 0.35f))
    }
}
