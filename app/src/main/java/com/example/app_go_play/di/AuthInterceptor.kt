package com.example.app_go_play.di

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        // Danh sách các endpoint không cần token
        val isAuthEndpoint = path.contains("/api/auth/login") ||
                             path.contains("/api/auth/register") ||
                             path.contains("/api/auth/social-login") ||
                             path.contains("/api/auth/forgot-password")

        val accessToken = tokenManager.accessToken

        // Nếu không có token, hoặc request là một trong các auth endpoint, thì không thêm header
        if (accessToken == null || isAuthEndpoint) {
            return chain.proceed(originalRequest)
        }

        // Thêm header Authorization vào các request còn lại
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(newRequest)
    }
}
