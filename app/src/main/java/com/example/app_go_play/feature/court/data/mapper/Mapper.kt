package com.example.app_go_play.feature.court.data.mapper

import com.example.app_go_play.feature.court.data.remote.dto.*
import com.example.app_go_play.feature.court.domain.model.Court
import com.example.app_go_play.feature.court.domain.model.Review
import com.example.app_go_play.feature.court.domain.model.TimeSlot
import com.example.app_go_play.util.PagedResult
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        courtType = this.courtType, // SỬA LỖI: sportType -> courtType
        rating = this.rating,
        pricePerHour = this.pricePerHour,
        imageUrls = this.imageUrls ?: emptyList(),
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
        endTime = LocalDateTime.parse(this.endTime, formatter), // SỬA LỖI: Bỏ tham số 'b'
        price = this.price,
        isAvailable = this.isAvailable
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
