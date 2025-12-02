package com.example.app_go_play.feature.court.data.repository

import com.example.app_go_play.feature.court.data.mapper.toCourt
import com.example.app_go_play.feature.court.data.mapper.toCreateCourtRequestDto
import com.example.app_go_play.feature.court.data.mapper.toPagedResult
import com.example.app_go_play.feature.court.data.mapper.toReview
import com.example.app_go_play.feature.court.data.mapper.toTimeSlot
import com.example.app_go_play.feature.court.data.remote.CourtApi
import com.example.app_go_play.feature.court.data.remote.dto.CreateReviewRequestDto
import com.example.app_go_play.feature.court.domain.model.Court
import com.example.app_go_play.feature.court.domain.model.Review
import com.example.app_go_play.feature.court.domain.model.TimeSlot
import com.example.app_go_play.feature.court.domain.repository.CourtRepository
import com.example.app_go_play.util.PagedResult
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CourtRepositoryImpl @Inject constructor(
    private val courtApi: CourtApi
) : CourtRepository {

    override suspend fun createCourt(courtData: Court): Result<Court> = runApiCall {
        courtApi.createCourt(courtData.toCreateCourtRequestDto()).toCourt()
    }

    override suspend fun updateCourt(courtId: Int, courtData: Court): Result<Court> = runApiCall {
        courtApi.updateCourt(courtId, courtData.toCreateCourtRequestDto()).toCourt()
    }

    override suspend fun deleteCourt(courtId: Int): Result<Unit> = runApiCall {
        courtApi.deleteCourt(courtId)
    }

    override suspend fun generateInitialTimeSlots(courtId: Int): Result<List<TimeSlot>> = runApiCall {
        courtApi.generateInitialTimeSlots(courtId).map { it.toTimeSlot() }
    }

    override suspend fun searchCourts(
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
    ): Result<PagedResult<Court>> = runApiCall {
        courtApi.searchCourts(
            name, courtType, minPrice, maxPrice, minRating, latitude, longitude, radiusInKm, page, size
        ).toPagedResult { it.toCourt() }
    }

    override suspend fun getCourtDetails(courtId: Int): Result<Court> = runApiCall {
        courtApi.getCourtDetails(courtId).toCourt()
    }

    override suspend fun getAvailableTimeSlots(courtId: Int, date: LocalDate): Result<List<TimeSlot>> = runApiCall {
        // SỬA LỖI API LEVEL: Sử dụng date.toString() thay vì date.format(...)
        // Nó tạo ra cùng một chuỗi "yyyy-MM-dd" nhưng tương thích với các API level cũ hơn.
        val dateString = date.toString()
        courtApi.getAvailableTimeSlots(courtId, dateString).map { it.toTimeSlot() }
    }

    override suspend fun addReview(courtId: Int, rating: Int, comment: String?): Result<Review> = runApiCall {
        val request = CreateReviewRequestDto(rating, comment)
        courtApi.addReview(courtId, request).toReview()
    }

    override suspend fun getReviews(courtId: Int, page: Int?, size: Int?): Result<PagedResult<Review>> = runApiCall {
        courtApi.getReviews(courtId, page, size).toPagedResult { it.toReview() }
    }

    // Helper function to reduce boilerplate try-catch blocks
    private suspend fun <T> runApiCall(apiCall: suspend () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: Exception) {
            // You can add more specific error handling here if needed
            Result.failure(e)
        }
    }
}
