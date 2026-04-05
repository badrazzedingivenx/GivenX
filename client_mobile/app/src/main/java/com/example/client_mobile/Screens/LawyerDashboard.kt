package com.example.client_mobile.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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

// ─── Data Models ──────────────────────────────────────────────────────────────
data class ScheduleItem(val clientName: String, val time: String, val type: String)
data class LeadItem(val name: String, val topic: String, val timeAgo: String)
data class TaskItem(val label: String, val dueDate: String, val isDone: Boolean)
data class ActivityItem(val clientName: String, val action: String, val timeAgo: String)

// ─── Lawyer Dashboard Host ────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerDashboardHost(
    fullName: String = "Yassine El Amrani",
    speciality: String = "Droit Pénal",
    isMasculine: Boolean = true,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToChat: (String) -> Unit = {}
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
                        isMasculine = isMasculine
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

// ─── Lawyer Messages Tab (inbox from MessageRepository) ──────────────────────
@Composable
private fun LawyerMessagesTabContent(paddingValues: PaddingValues) {
    val messages = MessageRepository.messages

    if (messages.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MarkEmailUnread,
                    contentDescription = null,
                    tint = AppDarkGreen.copy(alpha = 0.30f),
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Aucun message reçu",
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkGreen
                )
                Text(
                    text = "Les messages de vos clients apparaîtront ici.",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.Gray
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionHeader(title = "Messages Reçus (${messages.size})")
            }
            items(messages.reversed()) { msg ->
                InboxMessageCard(msg)
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun InboxMessageCard(msg: InboxMessage) {
    val initials = msg.fromName
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = if (!msg.isRead) Color.White else Color.White.copy(alpha = 0.75f),
        border = BorderStroke(
            width = if (!msg.isRead) 1.dp else 0.5.dp,
            color = if (!msg.isRead) AppGoldColor.copy(alpha = 0.50f) else AppDarkGreen.copy(alpha = 0.10f)
        ),
        shadowElevation = if (!msg.isRead) 4.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(AppDarkGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    initials,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = AppGoldColor
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        msg.fromName,
                        fontFamily = FontFamily.Serif,
                        fontWeight = if (!msg.isRead) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp,
                        color = AppDarkGreen,
                        maxLines = 1
                    )
                    Text(
                        msg.timestamp,
                        fontFamily = FontFamily.Serif,
                        fontSize = 10.sp,
                        color = AppDarkGreen.copy(alpha = 0.40f)
                    )
                }
                Text(
                    msg.content,
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    color = AppDarkGreen.copy(alpha = 0.60f),
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                if (!msg.isRead) {
                    Text(
                        "Nouveau",
                        fontFamily = FontFamily.Serif,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppGoldColor
                    )
                }
            }
        }
    }
}

@Composable
private fun LawyerClientsTabContent(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = AppDarkGreen.copy(alpha = 0.35f),
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Clients",
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

// ─── Lawyer Dashboard Screen ──────────────────────────────────────────────────
@Composable
private fun LawyerHomeTabContent(
    paddingValues: PaddingValues,
    fullName: String = "Yassine El Amrani",
    speciality: String = "Droit Pénal",
    isMasculine: Boolean = true,
) {
    val scrollState = rememberScrollState()

    val today = remember {
        SimpleDateFormat("EEEE d MMMM yyyy", Locale("fr")).format(Date())
            .replaceFirstChar { it.uppercase() }
    }

    val scheduleItems = listOf(
        ScheduleItem("Karim Bennani", "09:00", "Consultation"),
        ScheduleItem("Sara Alaoui", "11:30", "Suivi dossier"),
        ScheduleItem("Mohammed Fassi", "14:00", "Réunion")
    )

    val leads = listOf(
        LeadItem("Aya Berrada", "Droit de la Famille", "Il y a 10 min"),
        LeadItem("Omar Tazi", "Droit Immobilier", "Il y a 45 min"),
        LeadItem("Nour Chraibi", "Droit du Travail", "Il y a 2h")
    )

    val tasks = remember {
        mutableStateListOf(
            TaskItem("Préparer le dossier Bennani", "Aujourd'hui", false),
            TaskItem("Soumettre l'appel Alaoui", "Demain", false),
            TaskItem("Relire contrat Fassi & Associés", "4 Avr", false),
            TaskItem("Archiver dossier clôturé N°087", "5 Avr", true)
        )
    }

    val revenueData = listOf(18f, 25f, 32f, 22f, 40f, 35f, 48f, 30f, 42f, 38f, 55f, 60f)
    val monthLabels = listOf("J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")

    val activityFeed = listOf(
        ActivityItem("Karim Bennani", "a téléchargé un document", "Il y a 5 min"),
        ActivityItem("Sara Alaoui", "a confirmé son RDV", "Il y a 1h"),
        ActivityItem("Mohammed Fassi", "a envoyé un message", "Il y a 3h"),
        ActivityItem("Aya Berrada", "a soumis une nouvelle demande", "Hier")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // ── Greeting Banner ───────────────────────────────────────────
        DarkDashCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isMasculine) "Bonjour, Maître" else "Bonjour, Maîtresse",
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Serif,
                        color = AppGoldColor
                    )
                    Text(
                        text = fullName,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                                text = speciality,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Serif,
                                color = Color.White.copy(alpha = 0.60f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = today,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Serif,
                                color = AppGoldColor.copy(alpha = 0.75f)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(
                            modifier = Modifier.size(60.dp),
                            shape = CircleShape,
                            color = Color.Transparent,
                            border = BorderStroke(2.dp, AppGoldColor)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_user),
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // ── Key Stats Row ─────────────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CompactStatTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Folder,
                            count = "12",
                            label = "Actifs"
                        )
                        CompactStatTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.HourglassEmpty,
                            count = "5",
                            label = "En attente"
                        )
                        CompactStatTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.CheckCircle,
                            count = "47",
                            label = "Clôturés"
                        )
                    }
                }

                // ── Daily Schedule ────────────────────────────────────────────
                SectionHeader(title = "Agenda du Jour", actionLabel = "Calendrier complet") {}
                DashCard {
                    scheduleItems.forEachIndexed { index, item ->
                        LawyerScheduleRow(item = item)
                        if (index < scheduleItems.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = AppDarkGreen.copy(alpha = 0.07f)
                            )
                        }
                    }
                }

                // ── New Leads / Inquiries ─────────────────────────────────────
                SectionHeader(title = "Nouvelles Demandes", actionLabel = "Voir tout") {}
                DashCard {
                    leads.forEachIndexed { index, lead ->
                        NewLeadRow(lead = lead)
                        if (index < leads.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = AppDarkGreen.copy(alpha = 0.07f)
                            )
                        }
                    }
                }

                // ── Task Manager ──────────────────────────────────────────────
                SectionHeader(title = "Tâches Juridiques")
                DashCard {
                    tasks.forEachIndexed { index, task ->
                        TaskRow(
                            task = task,
                            onToggle = {
                                tasks[index] = tasks[index].copy(isDone = !tasks[index].isDone)
                            }
                        )
                        if (index < tasks.lastIndex) {
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                }

                // ── Revenue Analytics ─────────────────────────────────────────
                SectionHeader(title = "Revenus Mensuels")
                DashCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                "60 000 MAD",
                                fontSize = 24.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = AppDarkGreen
                            )
                            Text(
                                "Total annuel 2025",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Serif,
                                color = Color.Gray
                            )
                        }
                        StatusChip(
                            label = "+12% vs 2024",
                            containerColor = Color(0xFFE8F5E9),
                            textColor = Color(0xFF2E7D32)
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    RevenueBarChart(data = revenueData, labels = monthLabels, highlightIndex = 11)
                }

                // ── Recent Client Activity ────────────────────────────────────
                SectionHeader(title = "Activité Récente")
                DashCard {
                    activityFeed.forEachIndexed { index, activity ->
                        ActivityFeedRow(activity = activity)
                        if (index < activityFeed.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = AppDarkGreen.copy(alpha = 0.07f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
    }
}

// ─── Lawyer Schedule Row ──────────────────────────────────────────────────────
@Composable
fun LawyerScheduleRow(item: ScheduleItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = AppDarkGreen
        ) {
            Text(
                text = item.time,
                fontSize = 13.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = AppGoldColor,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.clientName,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = AppDarkGreen
            )
            Text(
                item.type,
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif,
                color = Color.Gray
            )
        }
        StatusChip(
            label = item.type,
            containerColor = AppGoldColor.copy(alpha = 0.12f),
            textColor = AppDarkGreen
        )
    }
}

// ─── New Lead Row ─────────────────────────────────────────────────────────────
@Composable
fun NewLeadRow(lead: LeadItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = AppDarkGreen.copy(alpha = 0.09f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = lead.name.first().toString(),
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkGreen
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                lead.name,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = AppDarkGreen
            )
            Text(
                lead.topic,
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif,
                color = Color.Gray
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                lead.timeAgo,
                fontSize = 10.sp,
                fontFamily = FontFamily.Serif,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(5.dp))
            Surface(
                modifier = Modifier.size(8.dp),
                shape = CircleShape,
                color = AppGoldColor
            ) {}
        }
    }
}

// ─── Task Row ─────────────────────────────────────────────────────────────────
@Composable
fun TaskRow(task: TaskItem, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (task.isDone) Color(0xFFF5F8F5) else Color.Transparent)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isDone,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = AppDarkGreen,
                checkmarkColor = AppGoldColor,
                uncheckedColor = AppDarkGreen.copy(alpha = 0.40f)
            )
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.label,
                fontSize = 13.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = if (task.isDone) FontWeight.Normal else FontWeight.Bold,
                color = if (task.isDone) Color.Gray else AppDarkGreen,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
            )
            Text(
                text = task.dueDate,
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                color = if (task.isDone) Color.LightGray else AppGoldColor
            )
        }
    }
}

// ─── Revenue Bar Chart ────────────────────────────────────────────────────────
@Composable
fun RevenueBarChart(data: List<Float>, labels: List<String>, highlightIndex: Int) {
    val maxVal = data.max()
    Column {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            val totalBars = data.size
            val spacing = 5.dp.toPx()
            val barWidth = (size.width - spacing * (totalBars - 1)) / totalBars
            val maxBarHeight = size.height - 16f

            data.forEachIndexed { index, value ->
                val barHeight = (value / maxVal) * maxBarHeight
                val left = index * (barWidth + spacing)
                val top = size.height - barHeight
                drawRoundRect(
                    color = if (index == highlightIndex) {
                        AppGoldColor
                    } else {
                        AppDarkGreen.copy(alpha = 0.22f)
                    },
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(5.dp.toPx())
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.Gray
                )
            }
        }
    }
}

// ─── Activity Feed Row ────────────────────────────────────────────────────────
@Composable
fun ActivityFeedRow(activity: ActivityItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = AppGoldColor.copy(alpha = 0.14f),
            border = BorderStroke(0.5.dp, AppGoldColor.copy(alpha = 0.35f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = activity.clientName.first().toString(),
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkGreen
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text(
                    activity.clientName,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = AppDarkGreen
                )
                Text(
                    " ${activity.action}",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.Gray
                )
            }
            Text(
                activity.timeAgo,
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                color = Color.LightGray
            )
        }
    }
}
