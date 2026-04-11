package com.example.client_mobile.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit instance.
 *
 * Replace BASE_URL with your production server URL.
 * The AuthInterceptor automatically attaches the stored JWT to every request.
 */
object RetrofitClient {

    // ── Server configuration ──────────────────────────────────────────────────

    /** Mockable.io public mock — no API key required */
    private const val MOCK_URL = "http://demo3674879.mockable.io/"

    /** Local dev server (Android Emulator → host localhost:3000) */
    private const val LOCAL_BASE_URL = "http://10.0.2.2:3000/"

    /**
     * Toggle: set true to hit the Mockable.io mock, false for local Express server.
     *
     * Local server test accounts (POST http://localhost:3000/api/seed to create):
     *   CLIENT  →  tarik@example.com   / 123456  → dashboard dyal l-muwakil
     *   LAWYER  →  yassine@example.com / 123456  → dashboard dyal l-mu7ami
     *
     * To run the local server:
     *   cd api_server && npm install && node index.js
     * Then set USE_MOCK_SERVER = false and run the app on an emulator.
     */
    private const val USE_MOCK_SERVER = true

    /** Active base URL — Mockable.io mock or local Express. */
    val BASE_URL: String = if (USE_MOCK_SERVER) MOCK_URL else LOCAL_BASE_URL

    /**
     * Exposed so other layers (Repository, etc.) can adapt behaviour:
     *   true  → hitting Mockable.io  — not all endpoints may be configured
     *   false → hitting local/prod Express server
     */
    val isMockMode: Boolean = USE_MOCK_SERVER

    init {
        Log.d("GivenX-Config", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        Log.d("GivenX-Config", "  API mode  : ${if (USE_MOCK_SERVER) "MOCK (Mockable.io)" else "REAL (Express)"}")
        Log.d("GivenX-Config", "  BASE_URL  : $BASE_URL")
        if (!USE_MOCK_SERVER && BASE_URL.contains("localhost")) {
            Log.e("GivenX-Config", "  ⚠ BASE_URL contains 'localhost' — Android Emulator cannot reach")
            Log.e("GivenX-Config", "    the host machine via 'localhost'. Use 10.0.2.2 instead.")
        }
        Log.d("GivenX-Config", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }

    // ── Auth interceptor – adds: Authorization: Bearer <token> ───────────────

    private val authInterceptor = Interceptor { chain ->
        val token = TokenManager.getToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        chain.proceed(request)
    }

    // ── OkHttp client ─────────────────────────────────────────────────────────

    private val okHttpClient: OkHttpClient by lazy {
        // Routes all OkHttp logs through android.util.Log so they appear in
        // Logcat under the tag "GivenX-API". Filter with: tag:GivenX-API
        val logging = HttpLoggingInterceptor { message ->
            Log.d("GivenX-API", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY   // full request + response
            // redactHeader("Authorization")            // uncomment to hide the JWT in logs
        }
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    // ── Retrofit instance ─────────────────────────────────────────────────────

    /**
     * Custom Gson that coerces any JSON primitive (number, boolean) to String when
     * the target Kotlin field is String. This handles the Postman mock returning
     * `"id": 1` (Int) for fields declared as `val id: String`.
     */
    private val gson = GsonBuilder()
        .setLenient()                        // tolerate non-strict JSON from mock server
        .registerTypeAdapter(
            String::class.java,
            object : JsonDeserializer<String> {
                override fun deserialize(
                    json: JsonElement,
                    typeOfT: Type,
                    context: JsonDeserializationContext
                ): String = when {
                    json.isJsonNull      -> ""
                    json.isJsonPrimitive -> json.asJsonPrimitive.asString  // Int/Long/Bool → "1"
                    else                 -> json.toString()
                }
            }
        )
        .create()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // ── Service factory ───────────────────────────────────────────────────────

    fun <T> create(service: Class<T>): T = retrofit.create(service)

    // ── Pre-built instances (lazily created) ──────────────────────────────────

    val lawyerApi: LawyerApiService by lazy { create(LawyerApiService::class.java) }
    val authApi:   AuthApiService   by lazy { create(AuthApiService::class.java)   }
    val haqApi:    HaqApiService    by lazy { create(HaqApiService::class.java)    }
    /** Raw-type service for Postman mock endpoints (no ApiResponse wrapper). */
    val mockApi:   MockApiService   by lazy { create(MockApiService::class.java)   }
}
