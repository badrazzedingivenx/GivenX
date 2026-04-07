package com.example.client_mobile.screens.user

import com.example.client_mobile.screens.shared.*

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client_mobile.R

// ─── Data Models ──────────────────────────────────────────────────────────────
data class CaseStep(
    val title: String,
    val date: String,
    val isDone: Boolean,
    val isActive: Boolean
)

data class AppointmentItem(
    val lawyerName: String,
    val speciality: String,
    val date: String,
    val time: String
)

data class LegalStory(
    val id: Int,
    val lawyerName: String,
    val specialty: String,
    val hasNewStory: Boolean = true
)

private val sampleStories = listOf(
    LegalStory(1, "M. Amina C.",   "Pénal",    hasNewStory = true),
    LegalStory(2, "M. Khalid T.",  "Affaires", hasNewStory = true),
    LegalStory(3, "M. Sara B.",    "Famille",  hasNewStory = false),
    LegalStory(4, "M. Nadia M.",   "Travail",  hasNewStory = true),
    LegalStory(5, "M. Yassine R.", "Pénal",    hasNewStory = false),
)

// ─── User Dashboard Screen ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboard(
    onNavigateToProfile: () -> Unit = {}
) {
    UserCasesTabContent(
        paddingValues = PaddingValues(0.dp)
    )
}

@Composable
internal fun UserCasesTabContent(
    paddingValues: PaddingValues,
    onNavigateToConsulter: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
    onNavigateToDocuments: () -> Unit = {},
    onNavigateToFacturation: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    val caseSteps = listOf(
        CaseStep("Soumis", "15 Jan", isDone = true, isActive = false),
        CaseStep("Examen", "22 Jan", isDone = true, isActive = false),
        CaseStep("En cours", "30 Jan", isDone = false, isActive = true),
        CaseStep("Résolu", "--", isDone = false, isActive = false)
    )

    val appointment = AppointmentItem(
        lawyerName = "Maître Yassine",
        speciality = "Droit Pénal",
        date = "Mardi 7 Avril",
        time = "10:30"
    )

    val documents = DocumentRepository.documents.take(3)

    val paidAmount = 2400f
    val pendingAmount = 800f
    val total = paidAmount + pendingAmount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

                // ── Greeting ──────────────────────────────────────────────────
                Column {
                    // UserSession.name is mutableStateOf — recompose on profile update
                    val displayName = UserSession.name.split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: ""
                    val greeting = if (displayName.isNotEmpty()) "Bonjour, $displayName 👋" else "Bonjour 👋"
                    Text(
                        text = greeting,
                        fontSize = 26.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = AppDarkGreen
                    )
                    Text(
                        text = "Comment puis-je vous aider aujourd'hui ?",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        color = AppDarkGreen.copy(alpha = 0.60f)
                    )
                }

                // ── Stories ───────────────────────────────────────────────
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(sampleStories) { story ->
                        StoriesItem(story = story)
                    }
                }

                // ── Quick Actions ──────────────────────────────────────────────
                DashCard {
                    SectionHeader(title = "Actions rapides")
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Event,
                            label = "Consulter",
                            onClick = onNavigateToConsulter
                        )
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Chat,
                            label = "Messagerie",
                            onClick = onNavigateToMessages
                        )
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.CloudUpload,
                            label = "Documents",
                            onClick = onNavigateToDocuments
                        )
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.MonetizationOn,
                            label = "Facturation",
                            onClick = onNavigateToFacturation
                        )
                    }
                }

                // ── Case Status Timeline ───────────────────────────────────────
                DashCard {
                    SectionHeader(title = "État du Dossier", actionLabel = "Voir tout") {}
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Affaire N° HAQ-2024-0312",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif,
                        color = AppGoldColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    CaseStatusTimeline(steps = caseSteps)
                }

                // ── Upcoming Appointment ───────────────────────────────────────
                SectionHeader(title = "Prochain Rendez-vous")
                UpcomingAppointmentCard(appointment = appointment)

                // ── Document Vault ─────────────────────────────────────────────
                SectionHeader(title = "Coffre-fort Numérique", actionLabel = "Ajouter", onAction = onNavigateToDocuments)
                DashCard {
                    documents.forEachIndexed { index, doc ->
                        DocumentVaultItem(doc = doc)
                        if (index < documents.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = AppDarkGreen.copy(alpha = 0.07f)
                            )
                        }
                    }
                }

                // ── Billing Summary ────────────────────────────────────────────
                SectionHeader(title = "Résumé de Facturation")
                BillingSummaryCard(paid = paidAmount, pending = pendingAmount, total = total)

                Spacer(modifier = Modifier.height(20.dp))
    }
}

// ─── Case Status Timeline ─────────────────────────────────────────────────────
@Composable
fun CaseStatusTimeline(steps: List<CaseStep>) {
    val dotContainerH = 28.dp   // uniform height for every dot slot
    val halfContainer = 14.dp   // = dotContainerH / 2, used to inset the line

    Column(modifier = Modifier.fillMaxWidth()) {

        // ── Layer: line + dots on top ────────────────────────────────────────
        Box(modifier = Modifier.fillMaxWidth()) {

            // Background line — padded so it runs only between dot centers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = halfContainer)
                    .align(Alignment.Center)

            ) {
                steps.forEachIndexed { index, step ->
                    if (index < steps.lastIndex) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(
                                    if (step.isDone) Color(0xFF34A853) else Color(0xFFDDDDDD)
                                )
                        )
                    }
                }
            }

            // Foreground dots — each step gets equal weight
            Row(modifier = Modifier.fillMaxWidth()) {
                steps.forEach { step ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(dotContainerH),
                        contentAlignment = Alignment.Center
                    ) {
                        val dotColor = when {
                            step.isDone   -> Color(0xFF34A853)
                            step.isActive -> AppGoldColor
                            else          -> Color(0xFFDDDDDD)
                        }
                        val dotSize = if (step.isActive) 22.dp else 18.dp
                        Box(
                            modifier = Modifier
                                .size(dotSize)
                                .clip(CircleShape)
                                .background(dotColor),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                step.isDone -> Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                                step.isActive -> Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.85f))
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Labels — same weight(1f) grid, completely independent of line ────
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            steps.forEach { step ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = step.title,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Serif,
                        maxLines = 1,
                        softWrap = false,
                        color = when {
                            step.isActive -> AppDarkGreen
                            step.isDone   -> AppDarkGreen.copy(alpha = 0.70f)
                            else          -> AppDarkGreen.copy(alpha = 0.35f)
                        },
                        fontWeight = if (step.isActive) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = step.date,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Serif,
                        maxLines = 1,
                        softWrap = false,
                        color = if (step.isDone) Color(0xFF34A853) else Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ─── Upcoming Appointment Card ────────────────────────────────────────────────
@Composable
fun UpcomingAppointmentCard(appointment: AppointmentItem) {
    DarkDashCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.date,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Serif,
                    color = AppGoldColor.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = appointment.lawyerName,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = appointment.speciality,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Serif,
                    color = AppGoldColor
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.65f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            appointment.time,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Serif,
                            color = Color.White
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.VideoCall,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.65f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            "En ligne",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Serif,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                modifier = Modifier.size(54.dp),
                shape = CircleShape,
                color = AppGoldColor.copy(alpha = 0.18f),
                border = BorderStroke(1.5.dp, AppGoldColor)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.VideoCall,
                        contentDescription = null,
                        tint = AppGoldColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

// ─── Document Vault Item ──────────────────────────────────────────────────────
@Composable
fun DocumentVaultItem(doc: VaultDocument) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(12.dp),
            color = AppDarkGreen.copy(alpha = 0.08f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = doc.icon,
                    contentDescription = null,
                    tint = AppGoldColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                doc.name,
                fontSize = 13.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = AppDarkGreen
            )
            Text(
                "Ajouté le ${doc.addedDate}",
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                color = Color.Gray
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = AppDarkGreen.copy(alpha = 0.25f),
            modifier = Modifier.size(18.dp)
        )
    }
}

// ─── Billing Summary Card ─────────────────────────────────────────────────────
@Composable
fun BillingSummaryCard(paid: Float, pending: Float, total: Float) {
    DashCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    "Total",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.Gray
                )
                Text(
                    "${total.toInt()} MAD",
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkGreen
                )
            }
            StatusChip(
                label = "En cours",
                containerColor = AppGoldColor.copy(alpha = 0.14f),

                textColor = AppGoldColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val paidFraction = if (total > 0f) paid / total else 0f
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(AppDarkGreen.copy(alpha = 0.09f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(paidFraction)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFF34A853))
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF34A853))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Payé: ${paid.toInt()} MAD",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.Gray
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(AppGoldColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "En attente: ${pending.toInt()} MAD",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.Gray
                )
            }
        }
    }
}

// ─── Stories Item ─────────────────────────────────────────────────────────────
@Composable
fun StoriesItem(story: LegalStory) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(68.dp)
    ) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape    = CircleShape,
            border   = BorderStroke(
                width = 2.5.dp,
                color = if (story.hasNewStory) AppGoldColor else Color.Gray.copy(alpha = 0.35f)
            ),
            color = AppDarkGreen.copy(alpha = 0.07f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = if (story.hasNewStory) AppGoldColor else Color.Gray.copy(alpha = 0.55f),
                    modifier = Modifier.size(26.dp)
                )
                if (story.hasNewStory) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .background(AppGoldColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AppDarkGreen,
                            modifier = Modifier.size(8.dp)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = story.lawyerName,
            fontFamily = FontFamily.Serif,
            fontSize = 10.sp,
            fontWeight = if (story.hasNewStory) FontWeight.Bold else FontWeight.Normal,
            color = if (story.hasNewStory) AppDarkGreen else Color.Gray,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}
