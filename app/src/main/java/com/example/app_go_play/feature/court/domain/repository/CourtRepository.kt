package com.example.app_go_play.feature.court.domain.repository

import com.example.app_go_play.feature.court.domain.model.Court
import com.example.app_go_play.feature.court.domain.model.Review
import com.example.app_go_play.feature.court.domain.model.TimeSlot
import com.example.app_go_play.util.PagedResult
import java.time.LocalDate

interface CourtRepository {

    // --- Chức năng cho Chủ sân (Manager/Admin) ---

    suspend fun createCourt(courtData: Court): Result<Court>

    suspend fun updateCourt(courtId: Int, courtData: Court): Result<Court>

    suspend fun deleteCourt(courtId: Int): Result<Unit>

    suspend fun generateInitialTimeSlots(courtId: Int): Result<List<TimeSlot>>

    // --- Chức năng chung & cho Người chơi (Player) ---

    suspend fun searchCourts(
        name: String?,
        courtType: String?,
        minPrice: Double?,
        maxPrice: Double?,
        minRating: Double?,
        latitude: Double?,
        longitude: Double?,
        radiusInKm: Double?,
        page: Int?,
        size: Int?
    ): Result<PagedResult<Court>>

    suspend fun getCourtDetails(courtId: Int): Result<Court>

    suspend fun getAvailableTimeSlots(courtId: Int, date: LocalDate): Result<List<TimeSlot>>

    suspend fun addReview(courtId: Int, rating: Int, comment: String?): Result<Review>

    suspend fun getReviews(courtId: Int, page: Int?, size: Int?): Result<PagedResult<Review>>
}
