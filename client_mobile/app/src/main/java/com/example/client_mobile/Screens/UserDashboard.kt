package com.example.client_mobile.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

data class DocumentItem(
    val name: String,
    val uploadDate: String
)

data class AppointmentItem(
    val lawyerName: String,
    val speciality: String,
    val date: String,
    val time: String
)

// ─── User Dashboard Screen ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboard(
    userName: String = "Karim Bennani",
    onNavigateToProfile: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var selectedTab by remember { mutableIntStateOf(0) }

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

    val documents = listOf(
        DocumentItem("Contrat de Bail.pdf", "Ajouté le 20 Fév"),
        DocumentItem("Pièce d'Identité.jpg", "Ajouté le 15 Jan"),
        DocumentItem("Attestation Travail.pdf", "Ajouté le 10 Jan")
    )

    val paidAmount = 2400f
    val pendingAmount = 800f
    val total = paidAmount + pendingAmount

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
            UserBottomBar(selectedTab = selectedTab) { index ->
                selectedTab = index
                if (index == 3) onNavigateToProfile()
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        DashBoardBackground {
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
                    Text(
                        text = "Bonjour, ${userName.split(" ").first()} 👋",
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

                // ── Quick Actions ──────────────────────────────────────────────
                DashCard {
                    SectionHeader(title = "Actions rapides")
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        QuickActionButton(Icons.Default.Event, "Consulter") {}
                        QuickActionButton(Icons.Default.Chat, "Messagerie") {}
                        QuickActionButton(Icons.Default.CloudUpload, "Documents") {}
                        QuickActionButton(Icons.Default.MonetizationOn, "Facturation") {}
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
                SectionHeader(title = "Coffre-fort Numérique", actionLabel = "Ajouter") {}
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
    }
}

// ─── Case Status Timeline ─────────────────────────────────────────────────────
@Composable
fun CaseStatusTimeline(steps: List<CaseStep>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        steps.forEachIndexed { index, step ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val dotColor = when {
                    step.isDone -> Color(0xFF34A853)
                    step.isActive -> AppGoldColor
                    else -> Color(0xFFD9D9D9)
                }
                val lineColor = if (step.isDone) Color(0xFF34A853) else Color(0xFFDDDDDD)
                val dotSize = if (step.isActive) 22.dp else 18.dp

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (index > 0) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (steps[index - 1].isDone) Color(0xFF34A853) else Color(0xFFDDDDDD))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .clip(CircleShape)
                            .background(dotColor),
                        contentAlignment = Alignment.Center
                    ) {
                        if (step.isDone) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                    if (index < steps.lastIndex) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(lineColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = step.title,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Serif,
                    color = if (step.isActive) AppDarkGreen else AppDarkGreen.copy(alpha = 0.45f),
                    fontWeight = if (step.isActive) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = step.date,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Serif,
                    color = if (step.isDone) Color(0xFF34A853) else Color.Gray,
                    textAlign = TextAlign.Center
                )
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
fun DocumentVaultItem(doc: DocumentItem) {
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
                    imageVector = if (doc.name.endsWith(".pdf")) Icons.Default.Description
                    else Icons.Default.Image,
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
                doc.uploadDate,
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
