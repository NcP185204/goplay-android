package com.example.app_go_play.feature.auth.data.repository

import com.example.app_go_play.feature.auth.data.remote.AuthApi
import com.example.app_go_play.feature.auth.data.remote.dto.ForgotPasswordRequest
import com.example.app_go_play.feature.auth.data.remote.dto.LoginRequest
import com.example.app_go_play.feature.auth.data.remote.dto.RegisterRequest
import com.example.app_go_play.feature.auth.data.remote.dto.SocialLoginRequest
import com.example.app_go_play.feature.auth.domain.model.Role
import com.example.app_go_play.feature.auth.domain.model.User
import com.example.app_go_play.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun register(fullName: String, email: String?, phoneNumber: String?, password: String, role: Role): Result<User> {
        return try {
            val registerRequest = RegisterRequest(fullName, email, phoneNumber, password, role)
            val user = authApi.register(registerRequest)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(emailOrPhone: String, password: String): Result<String> {
        return try {
            val loginRequest = LoginRequest(emailOrPhone, password)
            val response = authApi.login(loginRequest)
            Result.success(response.accessToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            val forgotPasswordRequest = ForgotPasswordRequest(email)
            authApi.forgotPassword(forgotPasswordRequest)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun socialLogin(token: String, provider: String): Result<String> {
        return try {
            val socialLoginRequest = SocialLoginRequest(token, provider)
            val response = authApi.socialLogin(socialLoginRequest)
            Result.success(response.accessToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        // For now, this function does nothing as it might clear local tokens
    }
}
