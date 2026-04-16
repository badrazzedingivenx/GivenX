package com.example.client_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.example.client_mobile.Navigation.AppNavigation
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.ui.theme.LegalAgroTheme
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Let the app draw behind system bars completely
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Initialise JWT storage before any API call is made.
        // Navigation and token validation are handled by SplashScreen.
        TokenManager.init(applicationContext)

        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setNavigationBarColor(
                color = Color.Transparent,
                darkIcons = true // usually best against translucent/blur or content
            )
            // Optional: also do status bar
            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = true
            )

            LegalAgroTheme {
                AppNavigation()
            }
        }
    }
}

