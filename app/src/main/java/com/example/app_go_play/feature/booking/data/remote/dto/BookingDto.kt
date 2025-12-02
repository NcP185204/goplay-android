package com.example.app_go_play.feature.booking.data.remote.dto

import com.example.app_go_play.feature.booking.domain.model.Booking
import java.util.Date

data class BookingDto(
    val id: String,
    val userId: String,
    val courtId: String,
    val timeSlotId: String,
    val bookingDate: Long,
    val totalPrice: Float,
    val status: String
)

fun BookingDto.toBooking(): Booking {
    return Booking(
        id = id,
        userId = userId,
        courtId = courtId,
        timeSlotId = timeSlotId,
        bookingDate = Date(bookingDate),
        totalPrice = totalPrice,
        status = status
    )
}

data class BookCourtRequest(
    val timeSlotId: String
)
