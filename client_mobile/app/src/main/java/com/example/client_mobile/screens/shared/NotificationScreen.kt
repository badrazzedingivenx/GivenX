package com.example.client_mobile.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Reply
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// ─── Data Model ───────────────────────────────────────────────────────────────

enum class NotificationType {
    MESSAGE, APPOINTMENT, CASE_UPDATE, DEADLINE, RESPONSE
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val time: String
)

// ─── Notification Repository (singleton, observable) ─────────────────────────

object NotificationRepository {
    val userNotifications = mutableStateListOf<AppNotification>()

    val lawyerNotifications = mutableStateListOf(
        AppNotification("l1", "Nouveau message client", "Ahmed Zian vous a envoyé un message concernant son dossier.", NotificationType.MESSAGE, isRead = false, time = "Il y a 3 min"),
        AppNotification("l2", "Nouvelle demande de consultation", "Fatima Ait Omar souhaite prendre rendez-vous ce vendredi.", NotificationType.APPOINTMENT, isRead = false, time = "Il y a 20 min"),
        AppNotification("l3", "Échéance proche", "Le dépôt des conclusions pour le dossier n°892 est dans 48h.", NotificationType.DEADLINE, isRead = false, time = "Il y a 1h"),
        AppNotification("l4", "Dossier mis à jour", "Le tribunal a publié une nouvelle décision pour l'affaire Benali.", NotificationType.CASE_UPDATE, isRead = true, time = "Hier"),
        AppNotification("l5", "Nouveau message client", "Mohamed El Fassi a posé une question sur son contrat.", NotificationType.MESSAGE, isRead = true, time = "Il y a 3 j"),
    )

    fun markAllReadUser()   { userNotifications.replaceAll { it.copy(isRead = true) } }
    fun markAllReadLawyer() { lawyerNotifications.replaceAll { it.copy(isRead = true) } }
    fun markReadUser(id: String)   { val i = userNotifications.indexOfFirst { it.id == id };   if (i >= 0) userNotifications[i] = userNotifications[i].copy(isRead = true) }
    fun markReadLawyer(id: String) { val i = lawyerNotifications.indexOfFirst { it.id == id }; if (i >= 0) lawyerNotifications[i] = lawyerNotifications[i].copy(isRead = true) }
    fun removeUser(id: String)     { userNotifications.removeAll { it.id == id } }
    fun removeLawyer(id: String)   { lawyerNotifications.removeAll { it.id == id } }
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    isLawyer: Boolean = false,
    onBack: () -> Unit = {},
    notifViewModel: NotificationViewModel = viewModel()
) {
    val notifications = if (isLawyer)
        NotificationRepository.lawyerNotifications
    else
        NotificationRepository.userNotifications

    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Notifications",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                        if (unreadCount > 0) {
                            Text(
                                "$unreadCount non lue${if (unreadCount > 1) "s" else ""}",
                                fontFamily = FontFamily.Serif,
                                fontSize = 11.sp,
                                color = AppGoldColor.copy(alpha = 0.85f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (unreadCount > 0) {
                        TextButton(
                            onClick = {
                                if (isLawyer) NotificationRepository.markAllReadLawyer()
                                else notifViewModel.markAllRead()
                            }
                        ) {
                            Text(
                                "Tout lire",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = AppGoldColor
                            )
                        }
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

        if (notifications.isEmpty()) {
            // ── Empty state ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
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
                            Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = AppDarkGreen.copy(alpha = 0.30f),
                            modifier = Modifier.size(38.dp)
                        )
                    }
                    Text(
                        "Aucune notification",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = AppDarkGreen
                    )
                    Text(
                        "Vous êtes à jour !",
                        fontFamily = FontFamily.Serif,
                        fontSize = 13.sp,
                        color = AppDarkGreen.copy(alpha = 0.45f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { Spacer(Modifier.height(6.dp)) }

                items(notifications, key = { it.id }) { notif ->
                    SwipeToDeleteNotification(
                        onDismiss = {
                            if (isLawyer) NotificationRepository.removeLawyer(notif.id)
                            else notifViewModel.remove(notif.id)
                        }
                    ) {
                        NotificationCard(
                            notification = notif,
                            onRead = {
                                if (isLawyer) NotificationRepository.markReadLawyer(notif.id)
                                else notifViewModel.markRead(notif.id)
                            }
                        )
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

// ─── Notification Card ────────────────────────────────────────────────────────

@Composable
private fun NotificationCard(
    notification: AppNotification,
    onRead: () -> Unit
) {
    val bgColor = if (notification.isRead) Color.White else Color(0xFFEAF4EE)
    val borderColor = if (notification.isRead)
        AppDarkGreen.copy(alpha = 0.07f)
    else
        AppDarkGreen.copy(alpha = 0.22f)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (notification.isRead) 1.dp else 4.dp
        ),
        colors = CardDefaults.elevatedCardColors(containerColor = bgColor),
        onClick = { if (!notification.isRead) onRead() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Icon tile ─────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        if (notification.isRead) AppDarkGreen.copy(alpha = 0.07f)
                        else AppDarkGreen
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notificationIcon(notification.type),
                    contentDescription = null,
                    tint = if (notification.isRead) AppDarkGreen.copy(alpha = 0.50f) else AppGoldColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            // ── Text content ──────────────────────────────────────────────────
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        notification.title,
                        fontFamily = FontFamily.Serif,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AppDarkGreen,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        notification.time,
                        fontFamily = FontFamily.Serif,
                        fontSize = 10.sp,
                        color = AppDarkGreen.copy(alpha = 0.40f)
                    )
                }

                Text(
                    notification.message,
                    fontFamily = FontFamily.Serif,
                    fontSize = 12.sp,
                    color = AppDarkGreen.copy(alpha = if (notification.isRead) 0.55f else 0.75f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // ── Type chip + unread dot ─────────────────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AppGoldColor.copy(alpha = if (notification.isRead) 0.08f else 0.15f)
                    ) {
                        Text(
                            notificationTypeLabel(notification.type),
                            fontFamily = FontFamily.Serif,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppDarkGreen.copy(alpha = 0.65f),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF34A853))
                        )
                        Text(
                            "Non lu",
                            fontFamily = FontFamily.Serif,
                            fontSize = 10.sp,
                            color = Color(0xFF34A853),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun notificationIcon(type: NotificationType): ImageVector = when (type) {
    NotificationType.MESSAGE      -> Icons.AutoMirrored.Filled.Message
    NotificationType.APPOINTMENT  -> Icons.Default.CalendarMonth
    NotificationType.CASE_UPDATE  -> Icons.Default.Folder
    NotificationType.DEADLINE     -> Icons.Default.AccessTime
    NotificationType.RESPONSE     -> Icons.AutoMirrored.Filled.Reply
}

private fun notificationTypeLabel(type: NotificationType): String = when (type) {
    NotificationType.MESSAGE      -> "Message"
    NotificationType.APPOINTMENT  -> "Rendez-vous"
    NotificationType.CASE_UPDATE  -> "Dossier"
    NotificationType.DEADLINE     -> "Échéance"
    NotificationType.RESPONSE     -> "Réponse"
}

// ─── Swipe-to-delete wrapper ──────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteNotification(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                true
            } else false
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.40f }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,   // only left-swipe to delete
        enableDismissFromEndToStart = true,
        backgroundContent = {
            // Red delete background revealed when swiping left
            val fraction = dismissState.progress
            val targetReached = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
            val bgAlpha = if (targetReached) 1f else (fraction * 2.5f).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFD32F2F).copy(alpha = bgAlpha)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Column(
                    modifier = Modifier.padding(end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Supprimer",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        "Supprimer",
                        fontFamily = FontFamily.Serif,
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        content = { content() }
    )
}
