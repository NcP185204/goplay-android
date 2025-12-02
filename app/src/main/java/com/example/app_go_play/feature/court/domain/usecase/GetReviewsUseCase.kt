package com.example.app_go_play.feature.court.domain.usecase

import com.example.app_go_play.feature.court.domain.repository.CourtRepository
import javax.inject.Inject

class GetReviewsUseCase @Inject constructor(
    private val repository: CourtRepository
) {
    suspend operator fun invoke(courtId: Int, page: Int? = null, size: Int? = null) = repository.getReviews(courtId, page, size)
}
