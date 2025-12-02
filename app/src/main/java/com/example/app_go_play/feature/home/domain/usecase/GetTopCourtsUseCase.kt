package com.example.app_go_play.feature.home.domain.usecase

import com.example.app_go_play.feature.court.domain.repository.CourtRepository
import javax.inject.Inject

class GetTopCourtsUseCase @Inject constructor(
    private val courtRepository: CourtRepository
) {
    // In the future, you can add logic here to determine what a "top" court is
    // (e.g., sort by rating, add specific filters).
    // For now, it will just fetch a general list of courts.

    // SỬA LỖI: Sử dụng hàm searchCourts đã có thay vì getCourts không tồn tại.
    // Gọi với các tham số null để lấy danh sách chung.
    suspend operator fun invoke(page: Int = 0, size: Int = 10) =
        courtRepository.searchCourts(
            name = null,
            courtType = null,
            minPrice = null,
            maxPrice = null,
            minRating = null, // You could set this to 4.0 for example to get actual top courts
            latitude = null,
            longitude = null,
            radiusInKm = null,
            page = page,
            size = size
        )
}
