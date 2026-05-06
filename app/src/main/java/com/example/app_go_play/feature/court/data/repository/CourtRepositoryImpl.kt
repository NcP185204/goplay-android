package com.example.app_go_play.feature.court.data.repository

import com.example.app_go_play.feature.court.data.remote.CourtApi
import com.example.app_go_play.feature.court.domain.model.Court
import com.example.app_go_play.feature.court.domain.model.Review
import com.example.app_go_play.feature.court.domain.model.TimeSlot
import com.example.app_go_play.feature.court.domain.repository.CourtRepository
import com.example.app_go_play.util.PagedResult
import com.example.app_go_play.feature.court.data.remote.dto.* // Import tất cả mappers
import java.time.LocalDate
import javax.inject.Inject

class CourtRepositoryImpl @Inject constructor(
    private val api: CourtApi
) : CourtRepository {

    override suspend fun createCourt(courtData: Court): Result<Court> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCourt(courtId: Int, courtData: Court): Result<Court> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCourt(courtId: Int): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun generateInitialTimeSlots(courtId: Int): Result<List<TimeSlot>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchCourts(
        name: String?,
        address: String?,
        courtType: String?,
        minPrice: Double?,
        maxPrice: Double?,
        minRating: Double?,
        latitude: Double?,
        longitude: Double?,
        radiusInKm: Double?,
        page: Int?,
        size: Int?
    ): Result<PagedResult<Court>> {
        return try {
            val response = api.searchCourts(
                name = name,
                address = address,
                courtType = courtType,
                minPrice = minPrice,
                maxPrice = maxPrice,
                minRating = minRating,
                latitude = latitude,
                longitude = longitude,
                radiusInKm = radiusInKm,
                page = page,
                size = size
            )
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCourtDetails(courtId: Int): Result<Court> {
        return try {
            val response = api.getCourtDetails(courtId)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAvailableTimeSlots(courtId: Int, date: LocalDate): Result<List<TimeSlot>> {
        return try {
            val response = api.getAvailableTimeSlots(courtId, date.toString())
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addReview(courtId: Int, rating: Int, comment: String?): Result<Review> {
        return try {
            val request = CreateReviewRequestDto(rating = rating, comment = comment)
            val response = api.addReview(courtId, request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReviews(courtId: Int, page: Int?, size: Int?): Result<PagedResult<Review>> {
        return try {
            val response = api.getReviews(courtId, page, size)
            Result.success(response.toReviewDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
