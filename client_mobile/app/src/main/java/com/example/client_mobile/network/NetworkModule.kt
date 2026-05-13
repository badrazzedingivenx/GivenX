package com.example.client_mobile.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Central factory for all HAQ production API services.
 *
 * This mirrors a Hilt / Koin NetworkModule but is implemented as a plain
 * Kotlin singleton to match the existing project architecture (no DI framework).
 *
 * Base URL : https://lavender-spoonbill-389199.hostingersite.com/api/v1/
 *
 * All service instances are lazily created and re-used for the process lifetime.
 *
 * Usage:
 *   val result = safeApiCall { NetworkModule.authApi.login(req) }
 */
object NetworkModule {

    // ── Configuration ──────────────────────────────────────────────────────────

    /**
     * Production base URL — includes /api/v1/ so all Retrofit paths are
     * relative (e.g. "auth/login", not "/api/v1/auth/login").
     * The trailing slash is required by Retrofit.
     */
    const val BASE_URL = "https://lavender-spoonbill-389199.hostingersite.com/api/v1/"

    // ── Gson ───────────────────────────────────────────────────────────────────

    private val gson by lazy {
        GsonBuilder()
            .setLenient()
            .registerTypeAdapter(
                String::class.java,
                object : JsonDeserializer<String> {
                    override fun deserialize(
                        json: JsonElement,
                        typeOfT: Type,
                        context: JsonDeserializationContext
                    ): String = when {
                        json.isJsonNull      -> ""
                        json.isJsonPrimitive -> json.asJsonPrimitive.asString
                        else                 -> json.toString()
                    }
                }
            )
            .create()
    }

    // ── OkHttpClient ──────────────────────────────────────────────────────────

    // TODO: REMOVE IN PRODUCTION - Unsafe SSL Bypass
    // Trusts all certificates to work around CertPathValidatorException on the
    // emulator while the Hostinger SSL chain is not yet trusted by Android.
    private val trustAllCerts: Array<TrustManager> = arrayOf(
        object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    )

    private val sslContext: SSLContext = SSLContext.getInstance("SSL").apply {
        init(null, trustAllCerts, SecureRandom())
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor { message ->
            Log.d("HaqNetwork", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(logging)
            // TODO: REMOVE IN PRODUCTION - Unsafe SSL Bypass
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // ── Retrofit ──────────────────────────────────────────────────────────────

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // ── Service instances ─────────────────────────────────────────────────────

    /** POST /auth/login, /auth/register, /auth/logout, /auth/refresh, GET /auth/me */
    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }

    /** PUT /profile — update profile for any authenticated role */
    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }

    /** GET /lawyers, GET /lawyers/{id} — public lawyer discovery */
    val lawyerApi: LawyerApi by lazy { retrofit.create(LawyerApi::class.java) }

    /** CRUD /live-sessions + comments */
    val liveSessionApi: LiveSessionApi by lazy { retrofit.create(LiveSessionApi::class.java) }

    /** GET/POST/DELETE /consultations */
    val consultationApi: ConsultationApi by lazy { retrofit.create(ConsultationApi::class.java) }

    /** GET/POST /conversations + messages */
    val conversationApi: ConversationApi by lazy { retrofit.create(ConversationApi::class.java) }

    /** GET/POST /payments */
    val paymentApi: PaymentApi by lazy { retrofit.create(PaymentApi::class.java) }
}

