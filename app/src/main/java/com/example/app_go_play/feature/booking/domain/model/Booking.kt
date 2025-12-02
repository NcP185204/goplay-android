package com.example.app_go_play.feature.booking.domain.model

import java.util.Date

data class Booking(
    val id: String,
    val userId: String,
    val courtId: String,
    val timeSlotId: String,
    val bookingDate: Date,
    val totalPrice: Float,
    val status: String // e.g., "CONFIRMED", "CANCELLED"
)
