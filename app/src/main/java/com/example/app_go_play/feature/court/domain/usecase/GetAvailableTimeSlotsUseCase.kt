package com.example.app_go_play.feature.court.domain.usecase

import com.example.app_go_play.feature.court.domain.repository.CourtRepository
import java.time.LocalDate
import javax.inject.Inject

class GetAvailableTimeSlotsUseCase @Inject constructor(
    private val repository: CourtRepository
) {
    suspend operator fun invoke(courtId: Int, date: LocalDate) = repository.getAvailableTimeSlots(courtId, date)
}
