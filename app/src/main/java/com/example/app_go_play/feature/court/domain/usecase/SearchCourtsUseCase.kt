package com.example.app_go_play.feature.court.domain.usecase

import com.example.app_go_play.feature.court.domain.repository.CourtRepository
import javax.inject.Inject

class SearchCourtsUseCase @Inject constructor(
    private val repository: CourtRepository
) {
    suspend operator fun invoke(
        name: String? = null,
        courtType: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minRating: Double? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        radiusInKm: Double? = null,
        page: Int? = null,
        size: Int? = null
    ) = repository.searchCourts(name, courtType, minPrice, maxPrice, minRating, latitude, longitude, radiusInKm, page, size)
}
