package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

/** Which dimension the quick-filter chips operate on. */
private enum class FilterMode { SPECIALTY, CITY }

// ─── Screen ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerListScreen(
    domaine: String,
    onBack: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    lawyerListViewModel: LawyerListViewModel = viewModel()
) {
    // ── Pretty title ──────────────────────────────────────────────────────────
    val displayTitle = domaine
        .replace("Droit Civil", "Droit de la Famille")
        .ifBlank { "Avocats" }

    // ── API state ─────────────────────────────────────────────────────────────
    val lawyersState   by lawyerListViewModel.lawyers.collectAsStateWithLifecycle()
    val isRefreshing   by lawyerListViewModel.isRefreshing.collectAsStateWithLifecycle()
    val isError        by lawyerListViewModel.isError.collectAsStateWithLifecycle()
    val allLawyers     = lawyersState ?: emptyList()

    // ── Search bar ────────────────────────────────────────────────────────────
    var searchFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var debouncedQuery by remember { mutableStateOf("") }
    LaunchedEffect(searchFieldValue.text) {
        delay(250L)
        debouncedQuery = searchFieldValue.text
    }
    val focusManager = LocalFocusManager.current

    // ── Filter mode toggle (Specialty / City) ─────────────────────────────────
    var filterMode by rememberSaveable { mutableStateOf(FilterMode.SPECIALTY) }

    // Dynamic chip values derived from the loaded data
    val specialtyChips = remember(allLawyers) {
        listOf("Tous") + allLawyers
            .map { it.specialty.ifBlank { it.domaine } }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }
    val cityChips = remember(allLawyers) {
        listOf("Toutes villes") + allLawyers
            .map { it.city }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    // ── Selected filter chip ──────────────────────────────────────────────────
    var selectedFilter by rememberSaveable { mutableStateOf("Tous") }

    // Reset chip when mode changes so the selection stays valid
    LaunchedEffect(filterMode) {
        selectedFilter = if (filterMode == FilterMode.SPECIALTY) "Tous" else "Toutes villes"
    }

    // ── Combined filter logic ─────────────────────────────────────────────────
    val filteredList by remember(allLawyers, filterMode, selectedFilter, debouncedQuery) {
        derivedStateOf {
            allLawyers.filter { lawyer ->
                val matchesChip = when (filterMode) {
                    FilterMode.SPECIALTY ->
                        selectedFilter == "Tous" ||
                        lawyer.specialty.equals(selectedFilter, ignoreCase = true) ||
                        lawyer.domaine.equals(selectedFilter, ignoreCase = true)
                    FilterMode.CITY ->
                        selectedFilter == "Toutes villes" ||
                        lawyer.city.equals(selectedFilter, ignoreCase = true)
                }
                val matchesSearch = debouncedQuery.isBlank() ||
                    lawyer.name.contains(debouncedQuery, ignoreCase = true) ||
                    lawyer.city.contains(debouncedQuery, ignoreCase = true) ||
                    lawyer.specialty.contains(debouncedQuery, ignoreCase = true)
                matchesChip && matchesSearch
            }
        }
    }

    val isSearchActive = searchFieldValue.text.isNotEmpty()

    val domaineTotalCount = remember(selectedFilter, filterMode, allLawyers) {
        when {
            filterMode == FilterMode.SPECIALTY && selectedFilter == "Tous" -> allLawyers.size
            filterMode == FilterMode.CITY && selectedFilter == "Toutes villes" -> allLawyers.size
            else -> filteredList.size
        }
    }

    AppScaffold(
        topBar = {
            StandardTopBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = displayTitle.uppercase(),
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "${domaineTotalCount} AVOCAT${if (domaineTotalCount > 1) "S" else ""} DISPONIBLE${if (domaineTotalCount > 1) "S" else ""}",
                            fontFamily = FontFamily.Serif,
                            fontSize = 10.sp,
                            color = AppGoldColor.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                    }
                },
                onBack = onBack
            )
        }
    ) { paddingValues ->

        // ── No-connection state ───────────────────────────────────────────────
        if (isError && lawyersState?.isEmpty() == true) {
            NoConnectionScreen(onRetry = { lawyerListViewModel.refresh() })
        } else {
            // ── Pull-to-refresh + scrollable content ──────────────────────────────
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh    = { lawyerListViewModel.refresh() },
                modifier     = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                item { Spacer(Modifier.height(6.dp)) }

                // ── Filter mode toggle (Spécialité / Ville) ───────────────────
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filterMode == FilterMode.SPECIALTY,
                            onClick  = { filterMode = FilterMode.SPECIALTY },
                            label    = {
                                Text(
                                    "Spécialité",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 12.sp
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AppDarkGreen,
                                selectedLabelColor     = Color.White,
                                selectedLeadingIconColor = AppGoldColor,
                                containerColor  = Color.White,
                                labelColor      = AppDarkGreen
                            )
                        )
                        FilterChip(
                            selected = filterMode == FilterMode.CITY,
                            onClick  = { filterMode = FilterMode.CITY },
                            label    = {
                                Text(
                                    "Ville",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 12.sp
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.LocationCity, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AppDarkGreen,
                                selectedLabelColor     = Color.White,
                                selectedLeadingIconColor = AppGoldColor,
                                containerColor  = Color.White,
                                labelColor      = AppDarkGreen
                            )
                        )
                    }
                }

                // ── Dynamic filter chips (values derived from API data) ────────
                item {
                    val chips = if (filterMode == FilterMode.SPECIALTY) specialtyChips else cityChips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 0.dp)
                    ) {
                        items(chips) { chip ->
                            FilterChip(
                                selected = selectedFilter == chip,
                                onClick  = {
                                    selectedFilter = chip
                                    searchFieldValue = TextFieldValue("")
                                    focusManager.clearFocus()
                                },
                                label    = {
                                    Text(
                                        chip,
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                },
                                leadingIcon = if (selectedFilter == chip) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor   = AppGoldColor.copy(alpha = 0.90f),
                                    selectedLabelColor       = AppDarkGreen,
                                    selectedLeadingIconColor = AppDarkGreen,
                                    containerColor  = Color.White,
                                    labelColor      = AppDarkGreen
                                )
                            )
                        }
                    }
                }

                // ── Search bar ────────────────────────────────────────────────
                item {
                    OutlinedTextField(
                        value = searchFieldValue,
                        onValueChange = { searchFieldValue = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Rechercher par nom ou ville…",
                                fontFamily = FontFamily.Serif,
                                fontSize = 13.sp,
                                color = AppDarkGreen.copy(alpha = 0.40f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = if (isSearchActive) AppDarkGreen else AppDarkGreen.copy(alpha = 0.45f),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (isSearchActive) {
                                IconButton(onClick = {
                                    searchFieldValue = TextFieldValue("")
                                    focusManager.clearFocus()
                                }) {
                                    Surface(shape = CircleShape, color = AppDarkGreen.copy(alpha = 0.10f)) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Effacer",
                                            tint = AppDarkGreen,
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .size(16.dp)
                                        )
                                    }
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = AppDarkGreen,
                            unfocusedBorderColor = AppDarkGreen.copy(alpha = 0.18f),
                            focusedContainerColor   = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = AppGoldColor,
                            selectionColors = TextSelectionColors(
                                handleColor     = AppGoldColor,
                                backgroundColor = AppGoldColor.copy(alpha = 0.25f)
                            )
                        ),
                        singleLine = true
                    )
                }

                // ── Result count indicator ────────────────────────────────────
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = when {
                                debouncedQuery.isBlank() &&
                                    (selectedFilter == "Tous" || selectedFilter == "Toutes villes")
                                    -> "Tous les avocats"
                                filteredList.isEmpty() -> "Aucun résultat"
                                else -> "${filteredList.size} résultat${if (filteredList.size > 1) "s" else ""}"
                            },
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = AppDarkGreen
                        )
                        if (isSearchActive) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AppGoldColor.copy(alpha = 0.12f),
                                border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.35f))
                            ) {
                                Text(
                                    "\"${searchFieldValue.text}\"",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 11.sp,
                                    color = AppDarkGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // ── Lawyer cards, loading skeleton, or empty state ────────────
                if (lawyersState == null) {
                    items(4) { LawyerCardSkeleton() }
                } else if (filteredList.isEmpty()) {
                    item {
                        LawyerListEmptyState(
                            isFiltered = isSearchActive,
                            onReset = {
                                searchFieldValue = TextFieldValue("")
                                focusManager.clearFocus()
                            }
                        )
                    }
                } else {
                    items(filteredList, key = { it.id }) { lawyer ->
                        LawyerListCard(
                            lawyer  = lawyer,
                            onClick = { onNavigateToDetail(lawyer.id) }
                        )
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}
}

// ─── Lawyer Card ─────────────────────────────────────────────────────────────
@Composable
private fun LawyerListCard(lawyer: LawyerItem, onClick: () -> Unit) {
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
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.08f)),
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar circle
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(AppDarkGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    initials,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = AppGoldColor
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Name + verified badge
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
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (lawyer.isVerified) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Vérifié",
                            tint = Color(0xFF34A853),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Specialty in gold
                Text(
                    lawyer.specialty,
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    color = AppGoldColor,
                    maxLines = 1
                )

                // City + rating + experience
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = AppDarkGreen.copy(alpha = 0.40f),
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            lawyer.city,
                            fontFamily = FontFamily.Serif,
                            fontSize = 11.sp,
                            color = AppDarkGreen.copy(alpha = 0.55f),
                            maxLines = 1
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text("★", fontSize = 11.sp, color = AppGoldColor)
                        Text(
                            "${lawyer.rating}",
                            fontFamily = FontFamily.Serif,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppDarkGreen
                        )
                        Text(
                            "(${lawyer.reviewCount})",
                            fontFamily = FontFamily.Serif,
                            fontSize = 10.sp,
                            color = AppDarkGreen.copy(alpha = 0.40f)
                        )
                    }
                }
            }

            // "Voir" button
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AppDarkGreen,
                border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.40f))
            ) {
                Text(
                    "Voir",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = AppGoldColor,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                )
            }
        }
    }
}

// ─── Empty State ─────────────────────────────────────────────────────────────
@Composable
private fun LawyerListEmptyState(
    isFiltered: Boolean,
    onReset: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.07f)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 44.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(AppDarkGreen.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = null,
                    tint = AppDarkGreen.copy(alpha = 0.30f),
                    modifier = Modifier.size(38.dp)
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
                if (isFiltered)
                    "Aucun résultat pour cette recherche.\nEssayez un autre nom ou ville."
                else
                    "Aucun avocat disponible\ndans ce domaine pour l'instant.",
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                color = AppDarkGreen.copy(alpha = 0.50f),
                textAlign = TextAlign.Center
            )
            if (isFiltered) {
                TextButton(onClick = onReset) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        tint = AppGoldColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Effacer la recherche",
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

// ─── Loading Skeleton ─────────────────────────────────────────────────────────
@Composable
private fun LawyerCardSkeleton() {
    val shimmer = androidx.compose.animation.core.rememberInfiniteTransition(label = "lawyerSkeleton")
    val alpha by shimmer.animateFloat(
        initialValue  = 0.15f,
        targetValue   = 0.35f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation   = androidx.compose.animation.core.tween(900),
            repeatMode  = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "skeletonAlpha"
    )
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.06f)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(AppDarkGreen.copy(alpha = alpha))
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    Modifier
                        .width(160.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(AppDarkGreen.copy(alpha = alpha))
                )
                Box(
                    Modifier
                        .width(110.dp)
                        .height(11.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(AppGoldColor.copy(alpha = alpha))
                )
                Box(
                    Modifier
                        .width(80.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(AppDarkGreen.copy(alpha = alpha * 0.6f))
                )
            }
        }
    }
}
