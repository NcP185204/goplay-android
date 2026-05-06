package com.example.app_go_play.feature.court.data.remote.dto

import com.example.app_go_play.feature.court.domain.model.Court
import com.example.app_go_play.feature.court.domain.model.Review
import com.example.app_go_play.feature.court.domain.model.TimeSlot
import com.example.app_go_play.util.PagedResult
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun CourtDto.toDomain(): Court {
    return Court(
        id = id,
        name = name,
        address = address,
        courtType = courtType,
        averageRating = averageRating,
        pricePerHour = pricePerHour,
        thumbnailUrl = thumbnailUrl,
        imageUrls = imageUrls ?: emptyList(),
        ownerId = ownerId
    )
}

fun TimeSlotDto.toDomain(): TimeSlot {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    return TimeSlot(
        id = id,
        startTime = LocalDateTime.parse(startTime, formatter),
        endTime = LocalDateTime.parse(endTime, formatter),
        price = price,
        available = available
    )
}

fun ReviewResponseDto.toDomain(): Review {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    return Review(
        id = id,
        reviewerName = reviewerName,
        rating = rating,
        comment = comment,
        createdAt = LocalDateTime.parse(createdAt, formatter)
    )
}

fun <T, R> PagedResponseDto<T>.toDomain(mapper: (T) -> R): PagedResult<R> {
    return PagedResult(
        content = content.map(mapper),
        pageNumber = pageNo,
        pageSize = pageSize,
        totalElements = totalElements,
        totalPages = totalPages,
        isLast = last
    )
}

// Helper specific for Court
fun PagedResponseDto<CourtDto>.toDomain(): PagedResult<Court> {
    return toDomain { it.toDomain() }
}

// Helper specific for Review
fun PagedResponseDto<ReviewResponseDto>.toReviewDomain(): PagedResult<Review> {
    return toDomain { it.toDomain() }
}
