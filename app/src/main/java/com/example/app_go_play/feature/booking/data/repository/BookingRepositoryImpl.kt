package com.example.app_go_play.feature.booking.data.repository

import com.example.app_go_play.feature.booking.data.remote.BookingApi
import com.example.app_go_play.feature.booking.data.remote.dto.BookingRequest
import com.example.app_go_play.feature.booking.data.remote.dto.toBooking
import com.example.app_go_play.feature.booking.domain.model.Booking
import com.example.app_go_play.feature.booking.domain.model.PaymentInfo
import com.example.app_go_play.feature.booking.domain.repository.BookingRepository
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val api: BookingApi
) : BookingRepository {

    override suspend fun createBooking(
        courtId: Int,
        date: String,
        timeSlotIds: List<Long>,
        paymentMethod: String,
        note: String
    ): Result<Booking> {
        return try {
            val request = BookingRequest(
                courtId = courtId,
                date = date,
                timeSlotIds = timeSlotIds,
                note = note,
                paymentMethod = paymentMethod
            )
            val response = api.createBooking(request)
            Result.success(response.toBooking())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createPayment(bookingId: Int): Result<PaymentInfo> {
        return try {
            val response = api.createPayment(bookingId)
            Result.success(
                PaymentInfo(
                    paymentUrl = response.paymentUrl,
                    orderId = response.orderId,
                    amount = response.amount
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUpcomingBooking(): Result<Booking?> {
        return try {
            val response = api.getUpcomingBooking()
            Result.success(response?.toBooking())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyBooking(): Result<List<Booking>> {
        return try {
            val responses = api.getMyBooking()
            // Chuyển đổi danh sách các BookingResponse (Data) sang Booking (Domain)
            val bookings = responses.map { it.toBooking() }
            Result.success(bookings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
