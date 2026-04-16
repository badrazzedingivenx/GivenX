package com.example.client_mobile.screens.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    val lawyerNotifications = mutableStateListOf<AppNotification>()

    fun markAllReadUser()   { userNotifications.replaceAll { it.copy(isRead = true) } }
    fun markAllReadLawyer() { lawyerNotifications.replaceAll { it.copy(isRead = true) } }
    fun markReadUser(id: String)   { val i = userNotifications.indexOfFirst { it.id == id };   if (i >= 0) userNotifications[i] = userNotifications[i].copy(isRead = true) }
    fun markReadLawyer(id: String) { val i = lawyerNotifications.indexOfFirst { it.id == id }; if (i >= 0) lawyerNotifications[i] = lawyerNotifications[i].copy(isRead = true) }
    fun removeUser(id: String)     { userNotifications.removeAll { it.id == id } }
    fun removeLawyer(id: String)   { lawyerNotifications.removeAll { it.id == id } }

    fun clear() {
        userNotifications.clear()
        lawyerNotifications.clear()
    }

    fun refresh() {
        // NotificationViewModel's fetch() writes directly to these lists.
        // We can expose a global trigger here if needed, or simply 
        // call the VM's fetch from the screens' Lifecycle.
    }
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

    AppScaffold(
        topBar = {
            StandardTopBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "NOTIFICATIONS",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = FontFamily.Serif,
                                fontSize = 16.sp,
                                letterSpacing = 2.sp
                            )
                        )
                        if (unreadCount > 0) {
                            Text(
                                "$unreadCount nouvelle${if (unreadCount > 1) "s" else ""}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AppGoldColor,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                },
                onBack = onBack,
                actions = {
                    if (unreadCount > 0) {
                        IconButton(
                            onClick = { notifViewModel.markAllRead(isLawyer) }
                        ) {
                            Icon(
                                Icons.Default.DoneAll,
                                contentDescription = "Tout lire",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            )
        }
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
                        onDismiss = { notifViewModel.remove(notif.id, isLawyer) }
                    ) {
                        NotificationCard(
                            notification = notif,
                            onRead = { notifViewModel.markRead(notif.id, isLawyer) }
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
    val isRead = notification.isRead
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!isRead) onRead() },
        shape = RoundedCornerShape(16.dp),
        color = if (isRead) Color.White.copy(alpha = 0.6f) else Color.White,
        border = BorderStroke(
            width = if (isRead) 1.dp else 1.5.dp,
            color = if (isRead) AppDarkGreen.copy(alpha = 0.05f) else AppGoldColor.copy(alpha = 0.3f)
        ),
        shadowElevation = if (isRead) 0.dp else 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Icon tile with soft background ────────────────────────────────
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isRead) AppDarkGreen.copy(alpha = 0.05f)
                        else AppDarkGreen.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notificationIcon(notification.type),
                    contentDescription = null,
                    tint = if (isRead) AppDarkGreen.copy(alpha = 0.4f) else AppDarkGreen,
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
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (isRead) FontWeight.SemiBold else FontWeight.ExtraBold,
                            color = AppDarkGreen,
                            fontSize = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        notification.time,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AppDarkGreen.copy(alpha = 0.4f),
                            fontSize = 10.sp
                        )
                    )
                }

                Text(
                    notification.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AppDarkGreen.copy(alpha = if (isRead) 0.5f else 0.8f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = AppGoldColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            notificationTypeLabel(notification.type).uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppGoldColor,
                                fontSize = 9.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                    if (!isRead) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AppGoldColor)
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
