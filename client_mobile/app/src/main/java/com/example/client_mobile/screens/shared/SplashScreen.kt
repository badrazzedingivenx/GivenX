package com.example.client_mobile.screens.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client_mobile.R
import com.example.client_mobile.network.AuthRepository
import com.example.client_mobile.network.TokenManager

/**
 * Set to true during development to always land on the Login screen,
 * regardless of any stored token. Flip to false to re-enable auto-login.
 */
private const val FORCE_LOGIN = false

/**
 * Always the first screen rendered by AppNavigation.
 *
 * Flow:
 *  1. No stored token  → Check if onboarding seen. If not -> Onboarding. If yes -> Login.
 *  2. Stored token     → call AuthRepository.autoLogin() to validate it with the server.
 *       • Server confirms role → navigate to the correct Dashboard.
 *       • Server rejects (401) or network error → token is cleared, navigate to Login.
 *
 * This ensures navigation to the Dashboard ONLY happens after a live API response,
 * never from a hardcoded bypass.
 */
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToUserHome: () -> Unit,
    onNavigateToLawyerHome: () -> Unit
) {
    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(AppDarkGreen),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter            = painterResource(id = R.drawable.logo_app),
                contentDescription = "Logo",
                modifier           = Modifier.size(200.dp),
                contentScale       = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text       = "HAQ",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize   = 13.sp,
                color      = AppGoldColor.copy(alpha = 0.70f),
                letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified
            )
        }

        CircularProgressIndicator(
            modifier    = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp),
            color       = AppGoldColor,
            strokeWidth = 3.dp
        )
    }

    LaunchedEffect(Unit) {
        if (FORCE_LOGIN) {
            TokenManager.clear()
            onNavigateToLogin()
            return@LaunchedEffect
        }

        if (!TokenManager.isLoggedIn()) {
            if (!TokenManager.hasSeenOnboarding()) {
                onNavigateToOnboarding()
            } else {
                onNavigateToLogin()
            }
            return@LaunchedEffect
        }

        val confirmedRole = AuthRepository.autoLogin()
        when (confirmedRole) {
            "lawyer" -> onNavigateToLawyerHome()
            "user"   -> onNavigateToUserHome()
            else     -> onNavigateToLogin()
        }
    }
}
