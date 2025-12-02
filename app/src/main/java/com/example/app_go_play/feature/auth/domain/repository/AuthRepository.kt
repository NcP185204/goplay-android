package com.example.app_go_play.feature.auth.domain.repository

import com.example.app_go_play.feature.auth.domain.model.Role
import com.example.app_go_play.feature.auth.domain.model.User

interface AuthRepository {

    suspend fun register(fullName: String, email: String?, phoneNumber: String?, password: String, role: Role): Result<User>

    suspend fun login(emailOrPhone: String, password: String): Result<String> // Returns a JWT token

    suspend fun forgotPassword(email: String): Result<Unit>

    // HÀM MỚI CHO ĐĂNG NHẬP MẠNG XÃ HỘI
    suspend fun socialLogin(token: String, provider: String): Result<String> // Returns a JWT token

    suspend fun logout()

}
