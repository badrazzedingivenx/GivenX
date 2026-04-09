package com.example.client_mobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.client_mobile.Navigation.AppNavigation
import com.example.client_mobile.network.AuthRepository
import com.example.client_mobile.network.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialise JWT storage before any API call is made
        TokenManager.init(applicationContext)

        // Auto-login: if a token is stored, silently re-validate it and refresh
        // UserSession / LawyerSession with the latest server data.
        if (TokenManager.isLoggedIn()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val confirmedRole = AuthRepository.autoLogin()
                    if (confirmedRole != null) {
                        Log.d("GivenX-Auth", "Auto-login OK — role=$confirmedRole")
                    } else {
                        Log.w("GivenX-Auth", "Auto-login cleared token (token expired or invalid)")
                    }
                } catch (e: Exception) {
                    Log.e("GivenX-Auth", "Auto-login error: ${e.message}")
                }
            }
        }

        setContent {
            AppNavigation()
        }
    }
}

