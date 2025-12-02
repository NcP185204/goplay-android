package com.example.app_go_play.feature.booking.domain.usecase

import com.example.app_go_play.feature.booking.domain.model.TimeSlot
import com.example.app_go_play.feature.booking.domain.repository.BookingRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use Case (Trường hợp sử dụng) cho việc lấy danh sách các khung giờ.
 * Nó chứa logic nghiệp vụ và đóng vai trò trung gian giữa ViewModel và Repository.
 */
class GetTimeSlotsUseCase @Inject constructor(
    private val repository: BookingRepository
) {

    /**
     * Khi được gọi, Use Case này sẽ thực thi hành động lấy Time Slots từ repository.
     * Việc sử dụng `operator fun invoke` cho phép gọi class này như một hàm.
     */
    suspend operator fun invoke(courtId: String, date: LocalDate): Result<List<TimeSlot>> {
        return try {
            val timeSlots = repository.getTimeSlots(courtId, date)
            Result.success(timeSlots)
        } catch (e: Exception) {
            // Bắt và đóng gói lỗi lại để ViewModel xử lý
            Result.failure(e)
        }
    }
}
