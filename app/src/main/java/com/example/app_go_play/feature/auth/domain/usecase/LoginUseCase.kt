package com.example.app_go_play.feature.auth.domain.usecase

import com.example.app_go_play.feature.auth.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(emailOrPhone: String, password: String): Result<String> {
        return authRepository.login(emailOrPhone, password)
    }

}
