package com.example.client_mobile.Screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

// ─── Data Models ──────────────────────────────────────────────────────────────
data class ScheduleItem(val clientName: String, val time: String, val type: String)
data class TaskItem(val label: String, val dueDate: String, val isDone: Boolean)

// ─── Lawyer Dashboard Host ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerDashboardHost(
    fullName: String = "Yassine El Amrani",
    speciality: String = "Droit Pénal",
    profileImageUri: Uri? = null,
    isMasculine: Boolean = true,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToChat: (String) -> Unit = {},
    onNavigateToRequests: () -> Unit = {},
    onNavigateToPayments: () -> Unit = {}
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
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = AppDarkGreen)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            LawyerNavBottomBar(currentRoute = currentRoute) { tab ->
                if (tab is LawyerTab.Profile) {
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
                startDestination = LawyerTab.Home.route
            ) {
                composable(LawyerTab.Home.route) {
                    LawyerHomeTabContent(
                        paddingValues = paddingValues,
                        fullName = fullName,
                        speciality = speciality,
                        profileImageUri = profileImageUri,
                        isMasculine = isMasculine,
                        onNavigateToRequests = onNavigateToRequests,
                        onNavigateToPayments = onNavigateToPayments
                    )
                }
                composable(LawyerTab.Messages.route) {
                    MessagesInboxScreen(
                        isLawyer = true,
                        paddingValues = paddingValues,
                        onNavigateToChat = onNavigateToChat
                    )
                }
                composable(LawyerTab.Clients.route) {
                    LawyerClientsTabContent(paddingValues = paddingValues)
                }
            }
        }
    }
}

// ─── Clients Management Tab ──────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LawyerClientsTabContent(paddingValues: PaddingValues) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tous") }
    val filters = listOf("Tous", "Actif", "Paiement en attente", "Clôturé")

    val filteredClients = LawyerSession.clients.filter {
        (selectedFilter == "Tous" || it.status == selectedFilter) &&
        (it.name.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        SectionHeader(title = "Gestion des Clients")
        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Rechercher un client…", fontSize = 14.sp, fontFamily = FontFamily.Serif) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AppDarkGreen.copy(alpha = 0.4f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.9f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.7f),
                focusedBorderColor = AppDarkGreen,
                unfocusedBorderColor = AppDarkGreen.copy(alpha = 0.1f)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Row
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontFamily = FontFamily.Serif, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppDarkGreen,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White.copy(alpha = 0.6f)
                    ),
                    border = BorderStroke(0.5.dp, AppDarkGreen.copy(alpha = 0.1f))
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            if (filteredClients.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("Aucun client trouvé.", color = Color.Gray, fontFamily = FontFamily.Serif)
                    }
                }
            } else {
                items(filteredClients) { client ->
                    ClientCard(client)
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun ClientCard(client: ClientItem) {
    DashCard(onClick = { /* Detail client */ }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AppDarkGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = client.name.first().toString(),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AppDarkGreen
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = client.name,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = AppDarkGreen
                )
                Text(
                    text = client.lastAction,
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            StatusChip(
                label = client.status,
                containerColor = when(client.status) {
                    "Actif" -> Color(0xFFE8F5E9)
                    "Paiement en attente" -> Color(0xFFFFF3E0)
                    else -> Color(0xFFF5F5F5)
                },
                textColor = when(client.status) {
                    "Actif" -> Color(0xFF2E7D32)
                    "Paiement en attente" -> Color(0xFFE65100)
                    else -> Color(0xFF616161)
                }
            )
        }
    }
}

// ─── Requests Management Screen ──────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerRequestsScreen(onBack: () -> Unit) {
    val requests = LawyerSession.requests

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nouvelles Demandes", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AppDarkGreen) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = AppDarkGreen)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        DashBoardBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }
                
                val newRequests = requests.filter { it.status == "Nouveau" }
                val processedRequests = requests.filter { it.status != "Nouveau" }

                if (newRequests.isNotEmpty()) {
                    item { Text("À traiter (${newRequests.size})", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = AppDarkGreen) }
                    items(newRequests) { request ->
                        RequestCard(
                            request = request,
                            onAccept = { LawyerSession.acceptRequest(request.id) },
                            onDecline = { LawyerSession.declineRequest(request.id) }
                        )
                    }
                }

                if (processedRequests.isNotEmpty()) {
                    item { Text("Historique", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = AppDarkGreen.copy(alpha = 0.5f)) }
                    items(processedRequests) { request ->
                        RequestCard(
                            request = request,
                            onAccept = {},
                            onDecline = {}
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun RequestCard(request: RequestItem, onAccept: () -> Unit, onDecline: () -> Unit) {
    DashCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(request.clientName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppDarkGreen)
                StatusChip(
                    label = request.status,
                    containerColor = when(request.status) {
                        "Nouveau" -> Color(0xFFE3F2FD)
                        "Accepté (Attente Paiement)" -> Color(0xFFFFF3E0)
                        "Payé" -> Color(0xFFE8F5E9)
                        "Refusé" -> Color(0xFFFFF1F1)
                        else -> Color(0xFFF5F5F5)
                    },
                    textColor = when(request.status) {
                        "Nouveau" -> Color(0xFF1976D2)
                        "Accepté (Attente Paiement)" -> Color(0xFFE65100)
                        "Payé" -> Color(0xFF2E7D32)
                        "Refusé" -> Color(0xFFD32F2F)
                        else -> Color(0xFF616161)
                    }
                )
            }
            Text(request.topic, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AppGoldColor)
            Text(request.description, fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp)
            Text("Honoraires proposés: ${request.amount}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AppDarkGreen)
            
            if (request.status == "Nouveau") {
                HorizontalDivider(color = AppDarkGreen.copy(alpha = 0.05f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AppDarkGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Accepter & Devis", fontSize = 11.sp, color = Color.White)
                    }
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier.weight(0.5f),
                        border = BorderStroke(1.dp, Color(0xFFD32F2F).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Refuser", fontSize = 11.sp, color = Color(0xFFD32F2F))
                    }
                }
            } else {
                Text("Date: ${request.date}", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

// ─── Lawyer Home Tab Content ──────────────────────────────────────────────────
@Composable
private fun LawyerHomeTabContent(
    paddingValues: PaddingValues,
    fullName: String = "Yassine El Amrani",
    speciality: String = "Droit Pénal",
    profileImageUri: Uri? = null,
    isMasculine: Boolean = true,
    onNavigateToRequests: () -> Unit = {},
    onNavigateToPayments: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val today = remember { SimpleDateFormat("EEEE d MMMM yyyy", Locale("fr")).format(Date()).replaceFirstChar { it.uppercase() } }

    val scheduleItems = listOf(
        ScheduleItem("Karim Bennani", "09:00", "Consultation"),
        ScheduleItem("Sara Alaoui", "11:30", "Suivi dossier"),
        ScheduleItem("Mohammed Fassi", "14:00", "Réunion")
    )

    val tasks = remember {
        mutableStateListOf(
            TaskItem("Préparer le dossier Bennani", "Aujourd'hui", false),
            TaskItem("Soumettre l'appel Alaoui", "Demain", false),
            TaskItem("Relire contrat Fassi & Associés", "4 Avr", false)
        )
    }

    val revenueData = listOf(18f, 25f, 32f, 22f, 40f, 35f, 48f, 30f, 42f, 38f, 55f, 60f)
    val monthLabels = listOf("J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Banner Hero
        DarkDashCard {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = if (isMasculine) "Bonjour, Maître" else "Bonjour, Maîtresse", fontSize = 13.sp, fontFamily = FontFamily.Serif, color = AppGoldColor)
                    Text(text = fullName, fontSize = 22.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = speciality, fontSize = 13.sp, fontFamily = FontFamily.Serif, color = Color.White.copy(alpha = 0.60f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = today, fontSize = 11.sp, fontFamily = FontFamily.Serif, color = AppGoldColor.copy(alpha = 0.75f))
                }
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(2.dp, AppGoldColor)
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(model = profileImageUri, contentDescription = "Avatar", modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                    } else {
                        Image(painter = painterResource(id = R.drawable.logo_user), contentDescription = "Avatar", modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CompactStatTile(modifier = Modifier.weight(1f), icon = Icons.Default.Groups, count = LawyerSession.clients.size.toString(), label = "Clients")
                CompactStatTile(modifier = Modifier.weight(1f).clickable { onNavigateToRequests() }, icon = Icons.Default.Description, count = LawyerSession.requests.count { it.status == "Nouveau" }.toString(), label = "Demandes")
                CompactStatTile(modifier = Modifier.weight(1f), icon = Icons.Default.CheckCircle, count = "47", label = "Clôturés")
            }
        }

        // Agenda Section
        SectionHeader(title = "Agenda du Jour", actionLabel = "Calendrier") {}
        DashCard {
            scheduleItems.forEachIndexed { index, item ->
                LawyerScheduleRow(item = item)
                if (index < scheduleItems.lastIndex) HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AppDarkGreen.copy(alpha = 0.07f))
            }
        }

        // Nouvelles Demandes (Management Quick view)
        SectionHeader(title = "Dernières Demandes", actionLabel = "Voir tout", onAction = onNavigateToRequests)
        DashCard {
            val pending = LawyerSession.requests.filter { it.status == "Nouveau" }.take(2)
            if (pending.isEmpty()) {
                Text("Aucune nouvelle demande.", fontSize = 13.sp, color = Color.Gray, fontFamily = FontFamily.Serif)
            } else {
                pending.forEachIndexed { index, lead ->
                    NewLeadRow(lead = lead)
                    if (index < pending.lastIndex) HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AppDarkGreen.copy(alpha = 0.07f))
                }
            }
        }

        // Revenue Section
        SectionHeader(title = "Activité Financière", actionLabel = "Détails", onAction = onNavigateToPayments)
        DashCard {
            val total = LawyerSession.payments.filter { it.status == "Reçu" }.sumOf { it.amount.replace(" MAD", "").toInt() }
            Text(text = "$total MAD", fontSize = 24.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = AppDarkGreen)
            Text(text = "Total encaissé ce mois", fontSize = 12.sp, fontFamily = FontFamily.Serif, color = Color.Gray)
            Spacer(modifier = Modifier.height(18.dp))
            RevenueBarChart(data = revenueData, labels = monthLabels, highlightIndex = 11)
        }

        // Tasks Section
        SectionHeader(title = "Tâches Juridiques")
        DashCard {
            tasks.forEachIndexed { index, task ->
                TaskRow(task = task, onToggle = { tasks[index] = tasks[index].copy(isDone = !tasks[index].isDone) })
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
