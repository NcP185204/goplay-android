package com.example.app_go_play.feature.booking.domain.model

import java.util.Date

data class Booking(
    val id: String,
    val userId: String,
    val courtId: String,
    val courtName: String,       // Tên sân bóng
    val courtAddress: String,    // Địa chỉ sân bóng
    val timeSlotId: String,
    val bookingDate: Date,
    val totalPrice: Float,
    val status: String,          // Ví dụ: "PENDING", "CONFIRMED"
    val timeSlotDetails: List<String> = emptyList(), // Chi tiết giờ chơi
    val latitude: Double? = null,
    val longitude: Double? = null
)
