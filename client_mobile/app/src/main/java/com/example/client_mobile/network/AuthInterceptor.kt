package com.example.client_mobile.network

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
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
