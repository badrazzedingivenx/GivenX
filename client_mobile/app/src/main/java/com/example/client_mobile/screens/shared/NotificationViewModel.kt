package com.example.client_mobile.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.dto.NotificationDto
import kotlinx.coroutines.launch

/**
 * Fetches user notifications from /api/notifications and writes them into
 * [NotificationRepository.userNotifications] — the observable list consumed by
 * both [NotificationScreen] (list of cards) and the notification badge in HomeScreen.
 *
 * Also exposes helpers for mark-read / remove that the screen calls so the
 * mutations flow through a single ViewModel rather than calling the repository object directly.
 */
class NotificationViewModel : ViewModel() {

    init { fetch() }

    fun fetch() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.haqApi.getNotifications()
                if (response.isSuccessful && response.body()?.success == true) {
                    val items = response.body()?.data?.map { it.toAppNotification() } ?: return@launch
                    NotificationRepository.userNotifications.clear()
                    NotificationRepository.userNotifications.addAll(items)
                }
            } catch (_: Exception) {
                // Keep existing state on network failure
            }
        }
    }

    fun markRead(id: String)   = NotificationRepository.markReadUser(id)
    fun remove(id: String)     = NotificationRepository.removeUser(id)
    fun markAllRead()          = NotificationRepository.markAllReadUser()

    // ── Mapping ───────────────────────────────────────────────────────────────

    private fun NotificationDto.toAppNotification() = AppNotification(
        id      = id,
        title   = title,
        message = description,
        type    = inferType(title, description),
        isRead  = isRead,
        time    = formatTime(time)
    )

    private fun inferType(title: String, body: String): NotificationType = when {
        title.contains("rendez-vous", ignoreCase = true)                         -> NotificationType.APPOINTMENT
        title.contains("message",     ignoreCase = true)                         -> NotificationType.MESSAGE
        title.contains("document",    ignoreCase = true)                         -> NotificationType.CASE_UPDATE
        title.contains("dossier",     ignoreCase = true)
                || body.contains("dossier", ignoreCase = true)                   -> NotificationType.CASE_UPDATE
        else                                                                     -> NotificationType.CASE_UPDATE
    }

    /** Trims ISO-8601 timestamp to a readable date string. */
    private fun formatTime(isoTime: String): String =
        if (isoTime.length >= 10) isoTime.substring(0, 10) else isoTime
}
