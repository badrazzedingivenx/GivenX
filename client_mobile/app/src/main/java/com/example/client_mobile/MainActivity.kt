package com.example.client_mobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.client_mobile.Navigation.AppNavigation
import com.example.client_mobile.network.RetrofitClient
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

        testApiConnection()

        setContent {
            AppNavigation()
        }
    }

    /**
     * Fires a lightweight GET /lawyers?limit=1 probe on startup and logs
     * the result under the tag "GivenX-API" in Logcat.
     * Remove (or gate behind BuildConfig.DEBUG) once connectivity is confirmed.
     */
    private fun testApiConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.haqApi.getLawyers(limit = 1)
                if (response.isSuccessful) {
                    Log.d("GivenX-API", "API connection SUCCESS ✅  base=${RetrofitClient.BASE_URL}")
                } else {
                    Log.w("GivenX-API", "API responded HTTP ${response.code()} — check server")
                }
            } catch (e: Exception) {
                Log.e("GivenX-API", "API connection FAILURE ❌  — ${e.message}")
            }
        }
    }
}

