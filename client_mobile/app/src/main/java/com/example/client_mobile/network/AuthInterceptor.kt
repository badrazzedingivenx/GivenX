package com.example.client_mobile.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that injects  Authorization: Bearer <token>  into every
 * outgoing request, reading the current JWT from [TokenManager].
 *
 * No-op when the user is unauthenticated (token is null) so that public
 * endpoints (login, register, forgot-password) work without a token.
 */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = TokenManager.getToken()
        if (token == null) {
            Log.w("AuthInterceptor", "No token — sending unauthenticated request to ${chain.request().url}")
        } else {
            Log.d("AuthInterceptor", "Attaching Bearer token to ${chain.request().url}")
        }
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        val response = chain.proceed(request)
        if (response.code == 401) {
            Log.e("AuthInterceptor", "401 Unauthorized on ${chain.request().url} — token may be expired or invalid")
        }
        return response
    }
}
