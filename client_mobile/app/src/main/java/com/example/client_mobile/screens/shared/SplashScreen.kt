package com.example.client_mobile.screens.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.client_mobile.R
import com.example.client_mobile.network.AuthRepository
import com.example.client_mobile.network.TokenManager

/**
 * Set to true during development to always land on the Login screen,
 * regardless of any stored token. Flip to false to re-enable auto-login.
 */
private const val FORCE_LOGIN = true

/**
 * Always the first screen rendered by AppNavigation.
 *
 * Flow:
 *  1. No stored token  → navigate to Login immediately (no API call needed).
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
    onNavigateToUserHome: () -> Unit,
    onNavigateToLawyerHome: () -> Unit
) {
    // Show a simple branded loading screen while the check runs
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B3124)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_app),
            contentDescription = "Logo",
            modifier = Modifier.size(180.dp),
            contentScale = ContentScale.Fit
        )
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            color = Color(0xFFD4AF37),
            strokeWidth = 3.dp
        )
    }

    LaunchedEffect(Unit) {
        // FORCE_LOGIN = true: always go to Login so the API connection can be verified.
        // Set to false to re-enable the stored-token fast-path.
        if (FORCE_LOGIN) {
            TokenManager.clear()          // discard any stale token
            onNavigateToLogin()
            return@LaunchedEffect
        }

        if (!TokenManager.isLoggedIn()) {
            // No token stored — go straight to login, no API call needed
            onNavigateToLogin()
            return@LaunchedEffect
        }

        // Token exists — validate it against the server before trusting it
        val confirmedRole = AuthRepository.autoLogin()

        when (confirmedRole) {
            "lawyer" -> onNavigateToLawyerHome()
            "user"   -> onNavigateToUserHome()
            else     -> {
                // autoLogin() returned null → token was invalid, already cleared
                onNavigateToLogin()
            }
        }
    }
}
