package com.example.client_mobile.screens.shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable full-screen "no connection" state shown when an API call fails
 * due to a network error or when the backend is unreachable.
 *
 * [onRetry] is called when the user taps the Retry button.
 */
@Composable
fun NoConnectionScreen(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Aucune connexion",
    subtitle: String = "Impossible de contacter le serveur.\nVérifiez votre connexion Internet et réessayez."
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            // ── Icon ─────────────────────────────────────────────────────────
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = AppDarkGreen.copy(alpha = 0.07f),
                modifier = Modifier.size(96.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = AppDarkGreen.copy(alpha = 0.35f),
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            // ── Title ─────────────────────────────────────────────────────────
            Text(
                text = title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = AppDarkGreen,
                textAlign = TextAlign.Center
            )

            // ── Subtitle ──────────────────────────────────────────────────────
            Text(
                text = subtitle,
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                color = AppDarkGreen.copy(alpha = 0.55f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            // ── Retry button ──────────────────────────────────────────────────
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppDarkGreen),
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = AppGoldColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Réessayer",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = AppGoldColor
                )
            }
        }
    }
}
