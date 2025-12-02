package com.example.app_go_play.feature.booking.domain.repository

import com.example.app_go_play.feature.booking.domain.model.TimeSlot
import java.time.LocalDate

/**
 * Interface (Bản hợp đồng) cho tầng Data.
 * Nó định nghĩa các chức năng mà tầng Data phải cung cấp cho tầng Domain.
 */
interface BookingRepository {

    /**
     * Lấy danh sách các khung giờ cho một sân cụ thể vào một ngày cụ thể.
     * @param courtId ID của sân.
     * @param date Ngày cần lấy dữ liệu.
     * @return Một danh sách các TimeSlot.
     */
    suspend fun getTimeSlots(courtId: String, date: LocalDate): List<TimeSlot>

}
