package com.example.client_mobile.Screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client_mobile.R
import kotlinx.coroutines.delay
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
    onNavigateToCategory: (String) -> Unit = {}
) {
    val innerNavController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_app),
                            contentDescription = "Logo",
                            modifier = Modifier.size(330.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        BadgedBox(badge = { Badge { Text("2") } }) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = AppDarkGreen
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
                        onNavigateToLawyerDetail = onNavigateToLawyerDetail,
                        onNavigateToCategory = onNavigateToCategory
                    )
                }
                composable(UserTab.Cases.route) {
                    UserCasesTabContent(paddingValues = paddingValues)
                }
                composable(UserTab.Messages.route) {
                    UserMessagesTabContent(paddingValues = paddingValues)
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
    onNavigateToCategory: (String) -> Unit = {}
) {
    UserDashboardHost(
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToAbout = onNavigateToAbout,
        onNavigateToLawyerDetail = onNavigateToLawyerDetail,
        onNavigateToCategory = onNavigateToCategory
    )
}

// ─── User Home Tab Content ────────────────────────────────────────────────────

// ─── User Home Tab Content ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UserHomeTabContent(
    paddingValues: PaddingValues,
    onNavigateToAbout: () -> Unit = {},
    onNavigateToLawyerDetail: (String) -> Unit = {},
    onNavigateToCategory: (String) -> Unit = {}
) {
    // ── Search state (survives rotation via rememberSaveable) ─────────────
    var searchFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var selectedFilter by rememberSaveable { mutableStateOf("Tous") }

    // ── Debounce: delay filtering 250 ms after the user stops typing ──────
    var debouncedQuery by remember { mutableStateOf("") }
    LaunchedEffect(searchFieldValue.text) {
        delay(250L)
        debouncedQuery = searchFieldValue.text
    }

    val focusManager = LocalFocusManager.current

    val categories = listOf(
        LegalCategory("Droit de la\nFamille", Icons.Default.Groups,       domaine = "Droit Civil"),
        LegalCategory("Droit des\nAffaires",   Icons.Default.BusinessCenter, domaine = "Droit des Affaires"),
        LegalCategory("Droit\nPénal",          Icons.Default.Gavel,         domaine = "Droit Pénal"),
        LegalCategory("Droit\nImmobilier",     Icons.Default.Apartment,     domaine = "Droit Immobilier"),
        LegalCategory("Droit du\nTravail",     Icons.Default.Work,          domaine = "Droit du Travail"),
        LegalCategory("Droit\nFiscal",         Icons.Default.AccountBalance, domaine = "Droit Fiscal")
    )

    // ── derivedStateOf: recomputes only when debouncedQuery / selectedFilter
    //    actually change — avoids unnecessary recompositions on every keystroke
    val filteredLawyers by remember {
        derivedStateOf {
            sampleLawyers.filter { lawyer ->
                val matchesFilter = selectedFilter == "Tous" || lawyer.domaine == selectedFilter
                val matchesSearch = debouncedQuery.isBlank() ||
                    lawyer.name.contains(debouncedQuery, ignoreCase = true) ||
                    lawyer.city.contains(debouncedQuery, ignoreCase = true) ||
                    lawyer.specialty.contains(debouncedQuery, ignoreCase = true)
                matchesFilter && matchesSearch
            }
        }
    }

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

        // ── Search Bar ───────────────────────────────────────────────
        item {
            val isSearchActive = searchFieldValue.text.isNotEmpty()
            OutlinedTextField(
                value = searchFieldValue,
                onValueChange = { searchFieldValue = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Nom, ville ou spécialisation…",
                        fontFamily = FontFamily.Serif,
                        color = AppDarkGreen.copy(alpha = 0.40f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = if (isSearchActive) AppDarkGreen else AppDarkGreen.copy(alpha = 0.50f),
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (isSearchActive) {
                        IconButton(onClick = {
                            searchFieldValue = TextFieldValue("")
                            focusManager.clearFocus()
                        }) {
                            Surface(
                                shape = CircleShape,
                                color = AppDarkGreen.copy(alpha = 0.10f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Effacer la recherche",
                                    tint = AppDarkGreen,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(16.dp)
                                )
                            }
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filtrer",
                            tint = AppDarkGreen.copy(alpha = 0.40f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { focusManager.clearFocus() }
                ),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppDarkGreen,
                    unfocusedBorderColor = AppDarkGreen.copy(alpha = 0.20f),
                    focusedContainerColor = Color.White.copy(alpha = 0.96f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.88f),
                    cursorColor = AppGoldColor,
                    selectionColors = TextSelectionColors(
                        handleColor = AppGoldColor,
                        backgroundColor = AppGoldColor.copy(alpha = 0.25f)
                    )
                ),
                singleLine = true
            )
        }

        // ── Service Grid ─────────────────────────────────────────────
        item { SectionHeader(title = "Domaines Juridiques") }
        item { ServiceCategoryGrid(categories = categories, onCategoryClick = onNavigateToCategory) }

        // ── Quick Stats ──────────────────────────────────────────────
        item { SectionHeader(title = "En chiffres") }
        item { HomeQuickStats() }

        // ── Lawyer Discovery ─────────────────────────────────────────
        item {
            SectionHeader(
                title = "Avocats Disponibles",
                actionLabel = when {
                    debouncedQuery.isBlank() && selectedFilter == "Tous" ->
                        "${sampleLawyers.size} avocats"
                    filteredLawyers.isEmpty() -> "Aucun résultat"
                    else -> "${filteredLawyers.size} résultat${if (filteredLawyers.size > 1) "s" else ""}"
                }
            )
        }

        // Filter chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                items(lawyerFilterDomaines) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                filter,
                                fontFamily = FontFamily.Serif,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        },
                        leadingIcon = if (selectedFilter == filter) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AppDarkGreen,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = AppGoldColor,
                            containerColor = Color.White.copy(alpha = 0.90f),
                            labelColor = AppDarkGreen
                        )
                    )
                }
            }
        }

        // Lawyer cards
        if (filteredLawyers.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White.copy(alpha = 0.72f),
                    border = BorderStroke(1.dp, AppDarkGreen.copy(alpha = 0.07f))
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 40.dp, horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(AppDarkGreen.copy(alpha = 0.07f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = AppDarkGreen.copy(alpha = 0.35f),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Text(
                            "Aucun avocat trouvé",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = AppDarkGreen
                        )
                        Text(
                            "Essayez un autre nom, une ville\nou un domaine différent.",
                            fontFamily = FontFamily.Serif,
                            fontSize = 13.sp,
                            color = AppDarkGreen.copy(alpha = 0.50f),
                            textAlign = TextAlign.Center
                        )
                        if (searchFieldValue.text.isNotEmpty() || selectedFilter != "Tous") {
                            TextButton(
                                onClick = {
                                    searchFieldValue = TextFieldValue("")
                                    selectedFilter = "Tous"
                                    focusManager.clearFocus()
                                }
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = AppGoldColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "Réinitialiser les filtres",
                                    fontFamily = FontFamily.Serif,
                                    color = AppGoldColor,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        } else {
            items(filteredLawyers) { lawyer ->
                LawyerCard(
                    lawyer = lawyer,
                    onClick = { onNavigateToLawyerDetail(lawyer.id) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

// ─── Lawyer Card ─────────────────────────────────────────────────────────────
@Composable
private fun LawyerCard(lawyer: LawyerItem, onClick: () -> Unit) {
    val initials = lawyer.name
        .removePrefix("Maître ")
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = 0.92f),
        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.10f)),
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AppDarkGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    initials,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AppGoldColor
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        lawyer.name,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = AppDarkGreen,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (lawyer.isVerified) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF34A853), modifier = Modifier.size(14.dp))
                    }
                }
                Text(
                    lawyer.specialty,
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    color = AppGoldColor,
                    maxLines = 1
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = AppDarkGreen.copy(alpha = 0.40f), modifier = Modifier.size(11.dp))
                        Text(lawyer.city, fontFamily = FontFamily.Serif, fontSize = 11.sp, color = AppDarkGreen.copy(alpha = 0.50f), maxLines = 1)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("★", fontSize = 11.sp, color = AppGoldColor)
                        Text(
                            "${lawyer.rating}",
                            fontFamily = FontFamily.Serif,
                            fontSize = 11.sp,
                            color = AppDarkGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AppDarkGreen,
                border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.45f))
            ) {
                Text(
                    "Voir",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = AppGoldColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
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
                imageVector = Icons.Default.Chat,
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
