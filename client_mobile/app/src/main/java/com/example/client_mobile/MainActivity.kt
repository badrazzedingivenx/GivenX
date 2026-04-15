package com.example.client_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.client_mobile.Navigation.AppNavigation
import com.example.client_mobile.network.TokenManager
import com.example.client_mobile.ui.theme.LegalAgroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialise JWT storage before any API call is made.
        // Navigation and token validation are handled by SplashScreen.
        TokenManager.init(applicationContext)

        setContent {
            LegalAgroTheme {
                AppNavigation()
            }
        }
    }
}

