package com.example.app_go_play.feature.auth.domain.usecase

import com.example.app_go_play.feature.auth.domain.model.Role
import com.example.app_go_play.feature.auth.domain.model.User
import com.example.app_go_play.feature.auth.domain.repository.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {

    // CẬP NHẬT CHỮ KÝ HÀM
    suspend operator fun invoke(fullName: String, email: String?, phoneNumber: String?, password: String, role: Role): Result<User> {
        // CẬP NHẬT LỜI GỌI REPOSITORY
        return authRepository.register(fullName, email, phoneNumber, password, role)
    }

}
