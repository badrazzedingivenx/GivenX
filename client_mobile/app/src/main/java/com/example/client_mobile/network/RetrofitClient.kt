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

    /** Postman Mock Server URL — your mock from app.postman.com */
    private const val POSTMAN_MOCK_URL = "https://be265b06-28af-4cd8-bb16-fd55835c84ca.mock.pstmn.io/api/"

    /** Local dev server (Android Emulator → host localhost:3000) */
    private const val LOCAL_BASE_URL = "http://10.0.2.2:3000/api/"

    /**
     * Toggle: set true to hit the Postman Mock, false for local dev server.
     * NOTE: do NOT commit API keys in production — use BuildConfig fields instead.
     */
    private const val USE_MOCK_SERVER = true

    /**
     * Postman API key — required for private mock servers.
     * Get it from https://app.postman.com/settings/me/api-keys
     */
    private const val POSTMAN_API_KEY = "tarik"

    /** Active base URL, selected by the USE_MOCK_SERVER flag. */
    val BASE_URL: String = if (USE_MOCK_SERVER) POSTMAN_MOCK_URL else LOCAL_BASE_URL

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

    // ── Postman Mock API-key interceptor ──────────────────────────────────────
    // Sends the x-api-key header when USE_MOCK_SERVER is true and the key is set.

    private val postmanKeyInterceptor = Interceptor { chain ->
        val request = if (USE_MOCK_SERVER && POSTMAN_API_KEY.isNotBlank()) {
            chain.request().newBuilder()
                .addHeader("x-api-key", POSTMAN_API_KEY)
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
            .addInterceptor(postmanKeyInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // ── Retrofit instance ─────────────────────────────────────────────────────

    /**
     * Custom Gson that coerces any JSON primitive (number, boolean) to String when
     * the target Kotlin field is String. This handles the Postman mock returning
     * `"id": 1` (Int) for fields declared as `val id: String`.
     */
    private val gson = GsonBuilder()
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
