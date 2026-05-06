package com.example.app_go_play.feature.booking.data.remote.dto

import com.example.app_go_play.feature.booking.domain.model.Booking
import java.util.Date

data class BookingDto(
    val id: String,
    val userId: String,
    val courtId: String,
    val courtName: String,       // Thêm thông tin sân từ Backend
    val courtAddress: String,    // Thêm thông tin địa chỉ từ Backend
    val timeSlotId: String,
    val bookingDate: Long,
    val totalPrice: Float,
    val status: String,
    val timeSlotDetails: List<String> = emptyList(), // Thêm chi tiết giờ chơi
    val latitude: Double? = null,
    val longitude: Double? = null
)

fun BookingDto.toBooking(): Booking {
    return Booking(
        id = id,
        userId = userId,
        courtId = courtId,
        courtName = courtName,       // Ánh xạ sang Domain
        courtAddress = courtAddress, // Ánh xạ sang Domain
        timeSlotId = timeSlotId,
        bookingDate = Date(bookingDate),
        totalPrice = totalPrice,
        status = status,
        timeSlotDetails = timeSlotDetails,
        latitude = latitude,
        longitude = longitude
    )
}

data class BookCourtRequest(
    val timeSlotId: String
)
