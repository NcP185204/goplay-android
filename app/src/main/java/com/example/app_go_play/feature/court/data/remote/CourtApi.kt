package com.example.app_go_play.feature.court.data.remote

import com.example.app_go_play.feature.court.data.remote.dto.*
import retrofit2.http.*

interface CourtApi {

    // --- Dành cho Chủ sân (Manager/Admin) ---

    @POST("api/courts")
    suspend fun createCourt(@Body request: CreateCourtRequestDto): CourtDto

    @PUT("api/courts/{courtId}")
    suspend fun updateCourt(
        @Path("courtId") courtId: Int,
        @Body request: CreateCourtRequestDto
    ): CourtDto

    @DELETE("api/courts/{courtId}")
    suspend fun deleteCourt(@Path("courtId") courtId: Int)

    @POST("api/courts/{courtId}/generate-slots")
    suspend fun generateInitialTimeSlots(@Path("courtId") courtId: Int): List<TimeSlotDto>

    // --- Dành cho Người chơi (Player) & Chung ---

    @GET("api/courts/search")
    suspend fun searchCourts(
        @Query("name") name: String?,
        @Query("courtType") courtType: String?,
        @Query("minPrice") minPrice: Double?,
        @Query("maxPrice") maxPrice: Double?,
        @Query("minRating") minRating: Double?,
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Query("radiusInKm") radiusInKm: Double?,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): PagedResponseDto<CourtDto>

    @GET("api/courts/{courtId}")
    suspend fun getCourtDetails(@Path("courtId") courtId: Int): CourtDto

    @GET("api/courts/{courtId}/available-slots")
    suspend fun getAvailableTimeSlots(
        @Path("courtId") courtId: Int,
        @Query("date") date: String // Gửi ngày theo định dạng "yyyy-MM-dd"
    ): List<TimeSlotDto>

    @POST("api/courts/{courtId}/reviews")
    suspend fun addReview(
        @Path("courtId") courtId: Int,
        @Body request: CreateReviewRequestDto
    ): ReviewResponseDto

    @GET("api/courts/{courtId}/reviews")
    suspend fun getReviews(
        @Path("courtId") courtId: Int,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): PagedResponseDto<ReviewResponseDto>
}
