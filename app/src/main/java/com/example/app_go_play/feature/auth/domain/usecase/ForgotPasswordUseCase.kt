package com.example.app_go_play.feature.auth.domain.usecase

import com.example.app_go_play.feature.auth.domain.repository.AuthRepository

class ForgotPasswordUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String): Result<Unit> {
        return authRepository.forgotPassword(email)
    }

}
