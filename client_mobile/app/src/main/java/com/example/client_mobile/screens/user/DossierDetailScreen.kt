package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

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
import androidx.compose.runtime.Composable
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

// ─── Sample Repository ────────────────────────────────────────────────────────

object DossierRepository {

    val cases = listOf(
        DossierCase(
            id = "HAQ-2024-0312",
            caseNumber = "HAQ-2024-0312",
            category = "Droit de la Famille",
            status = "En cours",
            statusContainerColor = Color(0xFFD4AF37).copy(alpha = 0.15f),
            statusTextColor = Color(0xFFD4AF37),
            openingDate = "15 Janvier 2025",
            lawyerId = "1",
            lawyerName = "Maître Yassine El Amrani",
            lawyerSpecialty = "Droit Pénal",
            steps = listOf(
                DossierStep(
                    title = "Soumis",
                    subtitle = "Dossier reçu et enregistré",
                    date = "15 Jan",
                    isDone = true,
                    isActive = false
                ),
                DossierStep(
                    title = "Analyse",
                    subtitle = "Documents vérifiés par le cabinet",
                    date = "22 Jan",
                    isDone = true,
                    isActive = false
                ),
                DossierStep(
                    title = "Plaidoirie",
                    subtitle = "Document approuvé par l'avocat",
                    date = "28 Jan",
                    isDone = true,
                    isActive = false
                ),
                DossierStep(
                    title = "En cours",
                    subtitle = "En attente de la date d'audience",
                    date = "30 Jan",
                    isDone = false,
                    isActive = true
                ),
                DossierStep(
                    title = "Décision",
                    subtitle = "En attente du jugement",
                    date = "--",
                    isDone = false,
                    isActive = false
                ),
                DossierStep(
                    title = "Clôturé",
                    subtitle = "Dossier résolu",
                    date = "--",
                    isDone = false,
                    isActive = false
                )
            ),
            recentActions = listOf(
                DossierAction(
                    description = "Document approuvé par l'avocat",
                    author = "Maître Yassine",
                    date = "30 Jan 2025",
                    icon = Icons.Default.CheckCircle
                ),
                DossierAction(
                    description = "Date d'audience demandée au tribunal",
                    author = "Maître Yassine",
                    date = "27 Jan 2025",
                    icon = Icons.Default.Gavel
                ),
                DossierAction(
                    description = "Nouvelle pièce ajoutée: Attestation Travail",
                    author = "Vous",
                    date = "24 Jan 2025",
                    icon = Icons.Default.UploadFile
                ),
                DossierAction(
                    description = "Consultation initiale effectuée",
                    author = "Maître Yassine",
                    date = "20 Jan 2025",
                    icon = Icons.Default.Forum
                ),
                DossierAction(
                    description = "Dossier ouvert et assigné",
                    author = "Système HAQ",
                    date = "15 Jan 2025",
                    icon = Icons.Default.FolderOpen
                )
            ),
            documentIds = listOf(1L, 3L, 4L)
        )
    )

    fun getById(id: String): DossierCase? = cases.find { it.id == id }
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DossierDetailScreen(
    caseId: String,
    onBack: () -> Unit = {},
    onNavigateToChat: (String) -> Unit = {}
) {
    val dossier = DossierRepository.getById(caseId) ?: DossierRepository.cases.first()
    val associatedDocs = DocumentRepository.documents.filter { it.id in dossier.documentIds }

    DashBoardBackground {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour",
                                tint = AppDarkGreen
                            )
                        }
                    },
                    title = {
                        Text(
                            "Mon Dossier",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = AppDarkGreen
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->

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
                item {
                    SectionHeader(title = "Historique du Dossier")
                }
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
                item {
                    SectionHeader(title = "Dernières Actions")
                }
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

                // ── Associated Documents ──────────────────────────────────
                if (associatedDocs.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Pièces Jointes")
                    }
                    item {
                        DashCard {
                            associatedDocs.forEachIndexed { index, doc ->
                                DocumentVaultItem(doc = doc)
                                if (index < associatedDocs.lastIndex) {
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
                item {
                    SectionHeader(title = "Votre Avocat")
                }
                item {
                    LawyerContactCard(
                        dossier = dossier,
                        onNavigateToChat = onNavigateToChat
                    )
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
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
                            .background(Color.White.copy(alpha = 0.9f))
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
                val lawyer = sampleLawyers.find { it.id == dossier.lawyerId } ?: sampleLawyers.first()
                val conv = ConversationRepository.getOrCreate(
                    lawyerId = dossier.lawyerId,
                    lawyerName = lawyer.name,
                    lawyerSpecialty = lawyer.specialty,
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
