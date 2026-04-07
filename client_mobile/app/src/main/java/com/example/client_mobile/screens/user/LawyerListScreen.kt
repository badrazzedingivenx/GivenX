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
import kotlinx.coroutines.delay

// ─── Screen ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerListScreen(
    domaine: String,
    onBack: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {}
) {
    // ── Pretty title: replace the stored domaine with a display-friendly label ──
    val displayTitle = domaine
        .replace("Droit Civil", "Droit de la Famille")
        .ifBlank { "Avocats" }

    // ── State ─────────────────────────────────────────────────────────────────
    var searchFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    // Chip filter — pre-select the domaine the user tapped on the home screen.
    // Falls back to "Tous" if the domaine string doesn't appear in the chip list.
    var selectedFilter by rememberSaveable {
        mutableStateOf(if (domaine in lawyerFilterDomaines) domaine else "Tous")
    }

    // 250 ms debounce — avoids filtering on every single keystroke
    var debouncedQuery by remember { mutableStateOf("") }
    LaunchedEffect(searchFieldValue.text) {
        delay(250L)
        debouncedQuery = searchFieldValue.text
    }

    val focusManager = LocalFocusManager.current

    // ── Combined filter: chip domain + search text ─────────────────────────────
    val filteredList by remember {
        derivedStateOf {
            sampleLawyers.filter { lawyer ->
                val matchesChip = selectedFilter == "Tous" || lawyer.domaine == selectedFilter
                val matchesSearch = debouncedQuery.isBlank() ||
                    lawyer.name.contains(debouncedQuery, ignoreCase = true) ||
                    lawyer.city.contains(debouncedQuery, ignoreCase = true) ||
                    lawyer.specialty.contains(debouncedQuery, ignoreCase = true)
                matchesChip && matchesSearch
            }
        }
    }

    val isSearchActive = searchFieldValue.text.isNotEmpty()

    // Total count for the subtitle — how many lawyers are in the pre-selected domain
    val domaineTotalCount = remember(selectedFilter) {
        if (selectedFilter == "Tous") sampleLawyers.size
        else sampleLawyers.count { it.domaine == selectedFilter }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = displayTitle,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${domaineTotalCount} avocat${if (domaineTotalCount > 1) "s" else ""} disponible${if (domaineTotalCount > 1) "s" else ""}",
                            fontFamily = FontFamily.Serif,
                            fontSize = 11.sp,
                            color = AppGoldColor.copy(alpha = 0.80f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppDarkGreen,
                    scrolledContainerColor = AppDarkGreen
                )
            )
        },
        containerColor = Color(0xFFF4F6F4)
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(Modifier.height(6.dp)) }

            // ── Filter chips ──────────────────────────────────────────────────
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp)
                ) {
                    items(lawyerFilterDomaines) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = {
                                selectedFilter = filter
                                // Clear search when switching domain
                                searchFieldValue = TextFieldValue("")
                                focusManager.clearFocus()
                            },
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
                                containerColor = Color.White,
                                labelColor = AppDarkGreen
                            )
                        )
                    }
                }
            }

            // ── Search bar ────────────────────────────────────────────────────
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
                        focusedBorderColor = AppDarkGreen,
                        unfocusedBorderColor = AppDarkGreen.copy(alpha = 0.18f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White.copy(alpha = 0.90f),
                        cursorColor = AppGoldColor,
                        selectionColors = TextSelectionColors(
                            handleColor = AppGoldColor,
                            backgroundColor = AppGoldColor.copy(alpha = 0.25f)
                        )
                    ),
                    singleLine = true
                )
            }

            // ── Result count indicator ─────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = when {
                            debouncedQuery.isBlank() && selectedFilter == "Tous" -> "Tous les avocats"
                            filteredList.isEmpty()   -> "Aucun résultat"
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

            // ── Lawyer cards or empty state ────────────────────────────────────
            if (filteredList.isEmpty()) {
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
                        lawyer = lawyer,
                        onClick = { onNavigateToDetail(lawyer.id) }
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
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
