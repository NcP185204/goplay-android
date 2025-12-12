package com.example.app_go_play.feature.court.data.remote.dto

// Generic DTO for paginated responses from the API
data class PagedResponseDto<T>(
    val content: List<T>,
    val pageNo: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)

// DTO for Court information
data class CourtDto(
    val id: Int,
    val name: String,
    val address: String,
    val courtType: String,
    val averageRating: Double?,
    val pricePerHour: Double,
    val thumbnailUrl: String?,
    val imageUrls: List<String>?,
    val ownerId: Long
)

// DTO for creating/updating a court
data class CreateCourtRequestDto(
    val name: String,
    val address: String,
    val courtType: String,
    val pricePerHour: Double
)

// DTO for TimeSlot information
data class TimeSlotDto(
    val id: Long,
    val startTime: String, // ISO-8601 format, e.g., "2024-05-21T08:00:00Z"
    val endTime: String,   // ISO-8601 format
    val price: Double,
    val available: Boolean
)

// DTO for creating a review
data class CreateReviewRequestDto(
    val rating: Int,
    val comment: String?
)

// DTO for displaying a review
data class ReviewResponseDto(
    val id: Long,
    val reviewerName: String,
    val rating: Int,
    val comment: String?,
    val createdAt: String // ISO-8601 format
)
