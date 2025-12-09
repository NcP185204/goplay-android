package com.example.app_go_play.feature.auth.domain.usecase

import com.example.app_go_play.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        // Simply call the logout function from the repository.
        // The repository is responsible for handling API calls and exceptions.
        repository.logout()
    }
}
