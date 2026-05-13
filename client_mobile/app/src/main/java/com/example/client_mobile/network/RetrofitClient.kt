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
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Singleton Retrofit instance.
 *
 * This client is standardized to point exclusively to the production-like 
 * Node.js/Express backend. All legacy mock toggles have been removed.
 */
object RetrofitClient {

    /** 
     * Base URL for the production HAQ API on Hostinger.
     * Paths in HaqApiService are relative (e.g. "lawyers") and resolve against this URL.
     */
    const val BASE_URL = "https://lavender-spoonbill-389199.hostingersite.com/api/v1/"

    init {
        Log.d("GivenX-Config", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        Log.d("GivenX-Config", "  API Mode : PRODUCTION (Hostinger)")
        Log.d("GivenX-Config", "  BASE_URL : $BASE_URL")
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

    // TODO: REMOVE IN PRODUCTION - Unsafe SSL Bypass
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
            Log.d("GivenX-API", ">> $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            // TODO: REMOVE IN PRODUCTION - Unsafe SSL Bypass
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // ── Retrofit instance ─────────────────────────────────────────────────────

    private val gson = GsonBuilder()
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

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // ── Service factory ───────────────────────────────────────────────────────

    fun <T> create(service: Class<T>): T = retrofit.create(service)

    // ── Pre-built instances ──────────────────────────────────────────────────

    val authApi:   AuthApiService   by lazy { create(AuthApiService::class.java)   }
    val haqApi:    HaqApiService    by lazy { create(HaqApiService::class.java)    }
}
