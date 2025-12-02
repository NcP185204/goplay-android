package com.example.app_go_play.feature.auth.domain.usecase

import com.example.app_go_play.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SocialLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String, provider: String): Result<String> {
        return authRepository.socialLogin(token, provider)
    }
}
