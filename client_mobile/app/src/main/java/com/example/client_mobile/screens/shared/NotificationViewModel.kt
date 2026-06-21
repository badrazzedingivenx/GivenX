package com.example.client_mobile.screens.shared

import android.util.Log
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

class NotificationViewModel : ViewModel() {

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    init {
        fetchUnreadCount()
        fetch()
        startPolling()
    }

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
            } catch (_: Exception) { }
        }
    }

    fun fetch() {
        viewModelScope.launch {
            try {
                val isLawyer = TokenManager.getUserType() == "lawyer"
                val response = RetrofitClient.haqApi.getNotifications()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val rawItems = response.body()?.data ?: emptyList()
                    Log.d("NotifVM", "Fetched ${rawItems.size} notifications, isLawyer=$isLawyer")
                    val items = rawItems.map { it.toAppNotification() }
                    
                    if (isLawyer) {
                        NotificationRepository.lawyerNotifications.clear()
                        NotificationRepository.lawyerNotifications.addAll(items)
                    } else {
                        NotificationRepository.userNotifications.clear()
                        NotificationRepository.userNotifications.addAll(items)
                    }
                    syncUnreadCount()
                } else {
                    Log.w("NotifVM", "fetch failed: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("NotifVM", "fetch threw: ${e.message}")
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

    private fun NotificationDto.toAppNotification() = AppNotification(
        id      = id,
        title   = title,
        message = description,
        type    = inferType(type, title, description),
        isRead  = isRead,
        time    = formatTime(time)
    )

    private fun inferType(serverType: String, title: String, body: String): NotificationType = when {
        serverType.contains("MESSAGE", ignoreCase = true)     -> NotificationType.MESSAGE
        serverType.contains("RESERVATION", ignoreCase = true)
            || serverType.contains("APPOINTMENT", ignoreCase = true)
            || title.contains("rendez-vous", ignoreCase = true) -> NotificationType.APPOINTMENT
        serverType.contains("PAYMENT", ignoreCase = true)     -> NotificationType.CASE_UPDATE
        serverType.contains("DOCUMENT", ignoreCase = true)    -> NotificationType.CASE_UPDATE
        title.contains("dossier", ignoreCase = true)
            || body.contains("dossier", ignoreCase = true)    -> NotificationType.CASE_UPDATE
        else                                                   -> NotificationType.CASE_UPDATE
    }

    private fun formatTime(isoTime: String): String =
        if (isoTime.length >= 10) isoTime.substring(0, 10) else isoTime
}
