package com.example.client_mobile.core.utils

import com.example.client_mobile.core.utils.TokenManager
import com.example.client_mobile.presentation.common.repositories.UserSession
import com.example.client_mobile.data.repository.ConversationRepository
import com.example.client_mobile.presentation.common.repositories.LawyerSession
import com.example.client_mobile.presentation.common.repositories.MessageRepository
import com.example.client_mobile.presentation.common.repositories.DocumentRepository
import com.example.client_mobile.presentation.common.repositories.CreatorRepository
import com.example.client_mobile.presentation.common.screens.NotificationRepository

object UserService {

    /** Clears the stored JWT — the user is considered logged out. */
    fun signOut() {
        TokenManager.clear()
        UserSession.clear()
        LawyerSession.clear()
        ConversationRepository.clear()
        MessageRepository.clear()
        DocumentRepository.clear()
        CreatorRepository.clear()
        NotificationRepository.clear()
    }
}
