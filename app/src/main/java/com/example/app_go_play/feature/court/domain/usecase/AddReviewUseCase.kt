package com.example.app_go_play.feature.court.domain.usecase

import com.example.app_go_play.feature.court.domain.repository.CourtRepository
import javax.inject.Inject

class AddReviewUseCase @Inject constructor(
    private val repository: CourtRepository
) {
    suspend operator fun invoke(courtId: Int, rating: Int, comment: String?) = repository.addReview(courtId, rating, comment)
}
