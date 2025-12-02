package com.example.app_go_play.feature.booking.presentation.viewmodel

import com.example.app_go_play.feature.booking.domain.model.SelectableDate
import com.example.app_go_play.feature.booking.domain.model.TimeSlot
import java.time.LocalDate

data class SelectTimeSlotState(
    val isLoading: Boolean = false,
    val courtId: String? = null,
    val courtName: String? = null,
    val dates: List<SelectableDate> = emptyList(),
    val timeSlots: List<TimeSlot> = emptyList(),
    val selectedDate: LocalDate? = null,
    val selectedTimeSlots: List<TimeSlot> = emptyList(),
    val error: String? = null,
    val totalPrice: Int = 0
)
