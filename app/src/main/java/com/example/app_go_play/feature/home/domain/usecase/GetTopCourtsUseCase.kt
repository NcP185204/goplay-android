package com.example.app_go_play.feature.home.domain.usecase

import com.example.app_go_play.feature.court.domain.repository.CourtRepository
import javax.inject.Inject

class GetTopCourtsUseCase @Inject constructor(
    private val courtRepository: CourtRepository
) {
    /**
     * Lấy danh sách các sân hàng đầu.
     * Hiện tại lọc theo rating >= 4.0 và không giới hạn địa điểm.
     */
    suspend operator fun invoke(page: Int = 0, size: Int = 10) =
        courtRepository.searchCourts(
            name = null,
            address = null, // THÊM THAM SỐ NÀY ĐỂ KHÁC PHỤC LỖI
            courtType = null,
            minPrice = null,
            maxPrice = null,
            minRating = 4.0, 
            latitude = null,
            longitude = null,
            radiusInKm = null,
            page = page,
            size = size
        )
}
