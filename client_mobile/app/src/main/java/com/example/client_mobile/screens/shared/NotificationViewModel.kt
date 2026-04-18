package com.example.client_mobile.screens.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client_mobile.network.RetrofitClient
import com.example.client_mobile.network.dto.NotificationDto
import com.example.client_mobile.network.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Fetches notifications from /api/notifications and writes them into
 * [NotificationRepository] — the observable list consumed by
 * both [NotificationScreen] (list of cards) and the notification badge in TopBarActions.
 *
 * Exposes [unreadCount] as a [StateFlow] for global badge consumption.
 * Automatically handles routing to userNotifications or lawyerNotifications
 * based on the logged-in [TokenManager.getUserType].
 */
class NotificationViewModel : ViewModel() {

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    init {
        fetchUnreadCount()
        fetch()
        startPolling()
    }

    /** Polls the server every 30 seconds for the latest unread count. */
    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                delay(30_000L)
                fetchUnreadCount()
            }
        }
    }

    fun fetchUnreadCount() {
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.haqApi.getUnreadCount()
                if (resp.isSuccessful && resp.body()?.success == true) {
                    _unreadCount.value = resp.body()?.data?.unreadCount ?: 0
                }
            } catch (_: Exception) { /* keep current value */ }
        }
    }

    fun fetch() {
        viewModelScope.launch {
            try {
                val isLawyer = TokenManager.getUserType() == "lawyer"
                val response = RetrofitClient.haqApi.getNotifications()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val items = response.body()?.data?.map { it.toAppNotification() } ?: return@launch
                    
                    if (isLawyer) {
                        NotificationRepository.lawyerNotifications.clear()
                        NotificationRepository.lawyerNotifications.addAll(items)
                    } else {
                        NotificationRepository.userNotifications.clear()
                        NotificationRepository.userNotifications.addAll(items)
                    }
                    syncUnreadCount()
                }
            } catch (_: Exception) {
                // Keep existing state on network failure
            }
        }
    }

    fun markRead(id: String, isLawyer: Boolean = false) {
        if (isLawyer) NotificationRepository.markReadLawyer(id)
        else NotificationRepository.markReadUser(id)
        syncUnreadCount()
    }

    fun remove(id: String, isLawyer: Boolean = false) {
        if (isLawyer) NotificationRepository.removeLawyer(id)
        else NotificationRepository.removeUser(id)
        syncUnreadCount()
    }

    fun markAllRead(isLawyer: Boolean = false) {
        if (isLawyer) NotificationRepository.markAllReadLawyer()
        else NotificationRepository.markAllReadUser()
        syncUnreadCount()
    }

    private fun syncUnreadCount() {
        val isLawyer = TokenManager.getUserType() == "lawyer"
        _unreadCount.value = if (isLawyer)
            NotificationRepository.lawyerNotifications.count { !it.isRead }
        else
            NotificationRepository.userNotifications.count { !it.isRead }
    }

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
