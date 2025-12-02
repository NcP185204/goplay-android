package com.example.app_go_play.feature.booking.data.repository

import com.example.app_go_play.feature.booking.domain.model.TimeSlot
import com.example.app_go_play.feature.booking.domain.repository.BookingRepository
import kotlinx.coroutines.delay
import java.time.LocalDate
import javax.inject.Inject

/**
 * Triển khai (Implementation) của BookingRepository.
 * Tầng này chịu trách nhiệm lấy dữ liệu từ các nguồn (API, Database,...).
 * Hiện tại, nó chỉ trả về dữ liệu giả.
 */
class BookingRepositoryImpl @Inject constructor() : BookingRepository {

    override suspend fun getTimeSlots(courtId: String, date: LocalDate): List<TimeSlot> {
        // Mô phỏng việc gọi API, bạn có thể thêm delay
        delay(500)

        // TODO: Thay thế bằng lời gọi Retrofit/Room để lấy dữ liệu thật
        return listOf(
            TimeSlot("08:00", "10:00", true),
            TimeSlot("10:00", "12:00", false),
            TimeSlot("12:00", "14:00", true),
            TimeSlot("14:00", "16:00", true),
            TimeSlot("16:00", "18:00", true),
            TimeSlot("18:00", "20:00", false),
            TimeSlot("20:00", "22:00", true)
        ).shuffled()
    }
}
