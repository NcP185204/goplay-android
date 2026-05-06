package com.example.app_go_play.feature.booking.domain.usecase

import com.example.app_go_play.feature.booking.domain.model.Booking
import com.example.app_go_play.feature.booking.domain.repository.BookingRepository
import javax.inject.Inject

class CreateBookingUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    /**
     * @param courtId: ID sân (Int)
     * @param date: Ngày (yyyy-MM-dd)
     * @param timeSlotIds: List<Long> các slot đã chọn
     * @param paymentMethod: Phương thức thanh toán (MOMO, CASH, ...)
     * @param note: Ghi chú từ người dùng
     */
    suspend operator fun invoke(
        courtId: Int,
        date: String,
        timeSlotIds: List<Long>,
        paymentMethod: String,
        note: String
    ): Result<Booking> {
        if (timeSlotIds.isEmpty()) {
            return Result.failure(IllegalArgumentException("Vui lòng chọn ít nhất một khung giờ."))
        }
        
        // Truyền đầy đủ các tham số mới xuống Repository
        return repository.createBooking(
            courtId = courtId,
            date = date,
            timeSlotIds = timeSlotIds,
            paymentMethod = paymentMethod,
            note = note
        )
    }
}
