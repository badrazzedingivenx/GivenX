package com.example.client_mobile.presentation.user.screens

import com.example.client_mobile.presentation.common.screens.*
import com.example.client_mobile.presentation.common.viewmodel.*
import com.example.client_mobile.presentation.common.components.*
import com.example.client_mobile.presentation.common.repositories.UserSession

import com.example.client_mobile.presentation.common.screens.*
import com.example.client_mobile.presentation.common.viewmodel.*
import com.example.client_mobile.presentation.auth.screens.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.client_mobile.data.repository.DossierApiRepository
import com.example.client_mobile.core.utils.DossierData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ─── Data Models ──────────────────────────────────────────────────────────────

data class DossierStep(
    val title: String,
    val subtitle: String,
    val date: String,
    val isDone: Boolean,
    val isActive: Boolean
)

data class DossierAction(
    val description: String,
    val author: String,
    val date: String,
    val icon: ImageVector
)

data class DossierCase(
    val id: String,
    val caseNumber: String,
    val category: String,
    val status: String,
    val statusContainerColor: Color,
    val statusTextColor: Color,
    val openingDate: String,
    val lawyerId: String,
    val lawyerName: String,
    val lawyerSpecialty: String,
    val steps: List<DossierStep>,
    val recentActions: List<DossierAction>,
    val documentIds: List<Long>
)

// ─── Detail ViewModel ────────────────────────────────────────────────────────

sealed interface DossierDetailState {
    object Loading  : DossierDetailState
    object NotFound : DossierDetailState
    data class Success(val dossierCase: DossierCase) : DossierDetailState
}

class DossierDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow<DossierDetailState>(DossierDetailState.Loading)
    val state: StateFlow<DossierDetailState> = _state

    fun fetchById(dossierDocId: String) {
        _state.value = DossierDetailState.Loading
        viewModelScope.launch {
            try {
                val data = DossierApiRepository.getDossierById(dossierDocId)
                _state.value = if (data != null)
                    DossierDetailState.Success(data.toCase())
                else
                    DossierDetailState.NotFound
            } catch (e: Exception) {
                _state.value = DossierDetailState.NotFound
            }
        }
    }
}

// ─── Firestore → UI Converter ─────────────────────────────────────────────────

private fun statusColors(status: String): Pair<Color, Color> = when (status.trim()) {
    "En cours"   -> Color(0xFFD4AF37).copy(alpha = 0.15f) to Color(0xFFD4AF37)
    "En attente" -> Color(0xFF2196F3).copy(alpha = 0.15f) to Color(0xFF2196F3)
    "Clôturé"   -> Color(0xFF4CAF50).copy(alpha = 0.15f) to Color(0xFF4CAF50)
    "Résolu"    -> Color(0xFF4CAF50).copy(alpha = 0.15f) to Color(0xFF4CAF50)
    else         -> Color(0xFF9E9E9E).copy(alpha = 0.15f) to Color(0xFF9E9E9E)
}

private fun DossierData.toCase(): DossierCase {
    val p = progress
    val (bgColor, textColor) = statusColors(status)
    val steps = listOf(
        DossierStep("Soumis",     "Dossier reçu et enregistré",         openingDate.ifBlank { "--" }, p > 0,   p in 1..15),
        DossierStep("Analyse",    "Documents vérifiés par le cabinet",  "--",                         p > 25,  p in 16..40),
        DossierStep("Plaidoirie", "Document approuvé par l'avocat",     "--",                         p > 50,  p in 41..65),
        DossierStep("En cours",   "En attente de la date d'audience",   "--",                         p > 75,  p in 66..85),
        DossierStep("Décision",   "En attente du jugement",             "--",                         p > 90,  p in 86..95),
        DossierStep("Clôturé",    "Dossier résolu",                     "--",                         p >= 100, p == 100)
    )
    return DossierCase(
        id                   = id,
        caseNumber           = caseNumber.ifBlank { id },
        category             = category.ifBlank { "—" },
        status               = status.ifBlank { "—" },
        statusContainerColor = bgColor,
        statusTextColor      = textColor,
        openingDate          = openingDate.ifBlank { "—" },
        lawyerId             = lawyerId,
        lawyerName           = lawyerName.ifBlank { "—" },
        lawyerSpecialty      = lawyerSpecialty.ifBlank { "—" },
        steps                = steps,
        recentActions        = emptyList(),
        documentIds          = emptyList()
    )
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun DossierDetailScreen(
    caseId: String,
    onBack: () -> Unit = {},
    onNavigateToChat: (String) -> Unit = {}
) {
    val vm: DossierDetailViewModel = viewModel(key = caseId)
    LaunchedEffect(caseId) { vm.fetchById(caseId) }
    val screenState by vm.state.collectAsStateWithLifecycle()

    BaseScreen(
        title = "Mon Dossier",
        onBack = onBack
    ) { paddingValues ->
        when (val s = screenState) {
                is DossierDetailState.Loading ->
                    Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppDarkGreen, strokeWidth = 2.5.dp)
                    }

                is DossierDetailState.NotFound ->
                    Box(
                        Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.FolderOff,
                                contentDescription = null,
                                tint = AppDarkGreen.copy(alpha = 0.30f),
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                "Dossier introuvable",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = AppDarkGreen
                            )
                            Text(
                                "Ce dossier n'existe pas ou vous n'y avez pas accès.",
                                fontFamily = FontFamily.Serif,
                                fontSize = 13.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                            Button(
                                onClick = onBack,
                                colors = ButtonDefaults.buttonColors(containerColor = AppDarkGreen),
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("Retour", fontFamily = FontFamily.Serif, color = Color.White) }
                        }
                    }

                is DossierDetailState.Success -> {
                    val dossier = s.dossierCase
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(4.dp)) }

                        // ── Case Information Card ─────────────────────────────────
                        item {
                            DashCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Affaire N°",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Serif,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = dossier.caseNumber,
                                            fontSize = 17.sp,
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            color = AppGoldColor
                                        )
                                    }
                                    StatusChip(
                                        label = dossier.status,
                                        containerColor = dossier.statusContainerColor,
                                        textColor = dossier.statusTextColor
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.07f))
                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    CaseInfoTile(
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.Default.Balance,
                                        label = "Catégorie",
                                        value = dossier.category
                                    )
                                    CaseInfoTile(
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.Default.CalendarToday,
                                        label = "Ouvert le",
                                        value = dossier.openingDate
                                    )
                                }
                            }
                        }

                        // ── Detailed Timeline ─────────────────────────────────────
                        item { SectionHeader(title = "Historique du Dossier") }
                        item {
                            DashCard {
                                dossier.steps.forEachIndexed { index, step ->
                                    DossierTimelineRow(
                                        step = step,
                                        isLast = index == dossier.steps.lastIndex
                                    )
                                }
                            }
                        }

                        // ── Recent Actions ────────────────────────────────────────
                        if (dossier.recentActions.isNotEmpty()) {
                            item { SectionHeader(title = "Dernières Actions") }
                            item {
                                DashCard {
                                    dossier.recentActions.forEachIndexed { index, action ->
                                        DossierActionRow(action = action)
                                        if (index < dossier.recentActions.lastIndex) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(vertical = 10.dp),
                                                color = AppDarkGreen.copy(alpha = 0.07f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ── Lawyer Contact ────────────────────────────────────────
                        if (dossier.lawyerName.isNotBlank() && dossier.lawyerName != "—") {
                            item { SectionHeader(title = "Votre Avocat") }
                            item {
                                LawyerContactCard(
                                    dossier = dossier,
                                    onNavigateToChat = onNavigateToChat
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(20.dp)) }
                    }
                }
            }
    }
}

// ─── Timeline Row ─────────────────────────────────────────────────────────────

@Composable
private fun DossierTimelineRow(step: DossierStep, isLast: Boolean) {
    val dotColor = when {
        step.isDone   -> Color(0xFF34A853)
        step.isActive -> AppGoldColor
        else          -> Color(0xFFDDDDDD)
    }
    val textAlpha = when {
        step.isDone || step.isActive -> 1f
        else                         -> 0.40f
    }

    Row(modifier = Modifier.fillMaxWidth()) {

        // Left column: dot + connector line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(dotColor),
                contentAlignment = Alignment.Center
            ) {
                when {
                    step.isDone   -> Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    step.isActive -> Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    else -> Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.7f))
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(44.dp)
                        .background(
                            if (step.isDone) Color(0xFF34A853).copy(alpha = 0.35f)
                            else Color(0xFFDDDDDD)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Right column: text content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = step.title,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = if (step.isActive) FontWeight.Bold else FontWeight.Medium,
                    color = AppDarkGreen.copy(alpha = textAlpha)
                )
                Text(
                    text = step.date,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Serif,
                    color = if (step.isDone) Color(0xFF34A853) else Color.Gray.copy(alpha = textAlpha)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = step.subtitle,
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif,
                color = Color.Gray.copy(alpha = textAlpha)
            )
        }
    }
}

// ─── Action Row ───────────────────────────────────────────────────────────────

@Composable
private fun DossierActionRow(action: DossierAction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = RoundedCornerShape(13.dp),
            color = AppDarkGreen.copy(alpha = 0.07f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = AppGoldColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = action.description,
                fontSize = 13.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                color = AppDarkGreen
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = action.author,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Serif,
                    color = AppGoldColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "·",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = action.date,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.Gray
                )
            }
        }
    }
}

// ─── Lawyer Contact Card ──────────────────────────────────────────────────────

@Composable
private fun LawyerContactCard(
    dossier: DossierCase,
    onNavigateToChat: (String) -> Unit
) {
    DarkDashCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = AppGoldColor.copy(alpha = 0.18f),
                border = BorderStroke(1.5.dp, AppGoldColor)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = AppGoldColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dossier.lawyerName,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = dossier.lawyerSpecialty,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Serif,
                    color = AppGoldColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = AppGoldColor.copy(alpha = 0.20f))
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val conv = ConversationRepository.getOrCreate(
                    lawyerId   = dossier.lawyerId.ifBlank { dossier.id },
                    lawyerName = dossier.lawyerName,
                    clientName = UserSession.name
                )
                onNavigateToChat(conv.id)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppGoldColor.copy(alpha = 0.18f)),
            border = BorderStroke(1.dp, AppGoldColor.copy(alpha = 0.60f))
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
                tint = AppGoldColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "Envoyer un Message",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = AppGoldColor,
                fontSize = 15.sp
            )
        }
    }
}

// ─── Case Info Tile ───────────────────────────────────────────────────────────

@Composable
private fun CaseInfoTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = AppDarkGreen.copy(alpha = 0.05f),
        border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.10f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppGoldColor,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.Gray
                )
            }
            Text(
                text = value,
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = AppDarkGreen
            )
        }
    }
}
