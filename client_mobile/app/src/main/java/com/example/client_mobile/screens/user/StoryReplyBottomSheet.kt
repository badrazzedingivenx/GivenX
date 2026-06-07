package com.example.client_mobile.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client_mobile.network.MainRepository
import com.example.client_mobile.screens.shared.AppDarkGreen
import com.example.client_mobile.screens.shared.AppGoldColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryReplyBottomSheet(
    storyId: String,
    lawyerId: String,
    lawyerName: String,
    onDismiss: () -> Unit,
    onNavigateToChat: (conversationId: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var message by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFF7F1EA),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Répondre à la story",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = AppDarkGreen
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it; errorMsg = null },
                placeholder = { Text("Votre message…", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppGoldColor,
                    unfocusedBorderColor = Color(0xFFCCBFA5),
                    focusedTextColor = AppDarkGreen,
                    unfocusedTextColor = AppDarkGreen
                ),
                enabled = !isSending
            )

            if (errorMsg != null) {
                Text(
                    text = errorMsg!!,
                    color = Color(0xFFD32F2F),
                    fontSize = 13.sp
                )
            }

            Button(
                onClick = {
                    if (message.isBlank()) return@Button
                    isSending = true
                    scope.launch {
                        val conversationId = MainRepository.replyToStory(
                            storyId   = storyId,
                            message   = message.trim(),
                            lawyerId  = lawyerId,
                            lawyerName = lawyerName
                        )
                        isSending = false
                        if (conversationId != null) {
                            onDismiss()
                            onNavigateToChat(conversationId)
                        } else {
                            errorMsg = "Échec de l'envoi. Réessayez."
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppDarkGreen,
                    contentColor   = AppGoldColor
                ),
                enabled = !isSending && message.isNotBlank()
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color    = AppGoldColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text       = "Envoyer",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp
                    )
                }
            }

            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.ime))
        }
    }
}
