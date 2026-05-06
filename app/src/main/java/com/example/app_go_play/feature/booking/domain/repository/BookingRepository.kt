package com.example.app_go_play.feature.booking.domain.repository

import com.example.app_go_play.feature.booking.domain.model.Booking
import com.example.app_go_play.feature.booking.domain.model.PaymentInfo

interface BookingRepository {
    suspend fun createBooking(
        courtId: Int, 
        date: String, 
        timeSlotIds: List<Long>,
        paymentMethod: String,
        note: String
    ): Result<Booking>

    suspend fun createPayment(bookingId: Int): Result<PaymentInfo>

    suspend fun getUpcomingBooking(): Result<Booking?>
    
    // Thêm hàm lấy lịch sử
    suspend fun getMyBooking(): Result<List<Booking>>
}
