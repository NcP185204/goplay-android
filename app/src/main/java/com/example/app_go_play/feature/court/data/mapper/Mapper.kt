package com.example.app_go_play.feature.court.data.mapper

import com.example.app_go_play.feature.court.data.remote.dto.*
import com.example.app_go_play.feature.court.domain.model.Court
import com.example.app_go_play.feature.court.domain.model.Review
import com.example.app_go_play.feature.court.domain.model.TimeSlot
import com.example.app_go_play.util.PagedResult
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Base URL to construct absolute image paths
private const val BASE_URL = "http://10.0.2.2:8080"

private fun toAbsoluteUrl(path: String?): String? {
    // Return null if path is null, otherwise prepend BASE_URL if it's a relative path
    return path?.let {
        if (it.startsWith("http")) it else BASE_URL + it
    }
}

// --- Mapper for Paged Data ---
fun <T, R> PagedResponseDto<T>.toPagedResult(mapper: (T) -> R): PagedResult<R> {
    return PagedResult(
        content = this.content.map(mapper),
        pageNumber = this.pageNo,
        pageSize = this.pageSize,
        totalElements = this.totalElements,
        totalPages = this.totalPages,
        isLast = this.last
    )
}

// --- Mapper for Court ---
fun CourtDto.toCourt(): Court {
    return Court(
        id = this.id,
        name = this.name,
        address = this.address,
        courtType = this.courtType,
        averageRating = this.averageRating,
        pricePerHour = this.pricePerHour,
        thumbnailUrl = toAbsoluteUrl(this.thumbnailUrl),
        imageUrls = this.imageUrls?.mapNotNull { toAbsoluteUrl(it) } ?: emptyList(),
        ownerId = this.ownerId
    )
}

// --- Mapper from Domain Model to Request DTO ---
fun Court.toCreateCourtRequestDto(): CreateCourtRequestDto {
    return CreateCourtRequestDto(
        name = this.name,
        address = this.address,
        courtType = this.courtType,
        pricePerHour = this.pricePerHour
    )
}

// --- Mapper for TimeSlot ---
fun TimeSlotDto.toTimeSlot(): TimeSlot {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    return TimeSlot(
        id = this.id,
        startTime = LocalDateTime.parse(this.startTime, formatter),
        endTime = LocalDateTime.parse(this.endTime, formatter),
        price = this.price,
        available = this.available
    )
}

// --- Mapper for Review ---
fun ReviewResponseDto.toReview(): Review {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    return Review(
        id = this.id,
        reviewerName = this.reviewerName,
        rating = this.rating,
        comment = this.comment,
        createdAt = LocalDateTime.parse(this.createdAt, formatter)
    )
}
