package com.example.app_go_play.feature.booking.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_go_play.feature.booking.domain.model.SelectableDate
import com.example.app_go_play.feature.booking.domain.model.TimeSlot
import com.example.app_go_play.feature.booking.domain.usecase.GetTimeSlotsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SelectTimeSlotViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getTimeSlotsUseCase: GetTimeSlotsUseCase // 1. Tiêm UseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelectTimeSlotState())
    val uiState: StateFlow<SelectTimeSlotState> = _uiState.asStateFlow()

    private val PRICE_PER_SLOT = 200000

    init {
        val courtId = savedStateHandle.get<String>("courtId")
        Log.d("SelectTimeSlotVM", "Received courtId: $courtId")

        if (courtId == null) {
            _uiState.update { it.copy(error = "Court ID not found.") }
        } else {
            _uiState.update { it.copy(courtId = courtId) }
            loadInitialData()
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val dates = List(14) { i ->
                val date = LocalDate.now().plusDays(i.toLong())
                SelectableDate(
                    date = date,
                    dayOfWeek = date.format(DateTimeFormatter.ofPattern("E")),
                    dayOfMonth = date.dayOfMonth.toString()
                )
            }
            _uiState.update {
                it.copy(isLoading = false, dates = dates)
            }
        }
    }

    fun onDateSelected(date: LocalDate) {
        val currentState = _uiState.value
        if (currentState.selectedDate == date) return

        _uiState.update { it.copy(isLoading = true, selectedDate = date, selectedTimeSlots = emptyList(), totalPrice = 0) }

        viewModelScope.launch {
            val courtId = currentState.courtId
            if (courtId == null) {
                _uiState.update { it.copy(isLoading = false, error = "Court ID is missing.") }
                return@launch
            }

            // 2. Gọi UseCase để lấy dữ liệu
            getTimeSlotsUseCase(courtId, date)
                .onSuccess { timeSlots ->
                    // 3. Xử lý kết quả thành công
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            timeSlots = timeSlots
                        )
                    }
                }
                .onFailure { throwable ->
                    // 3. Xử lý kết quả thất bại
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "An unexpected error occurred."
                        )
                    }
                }
        }
    }

    fun onTimeSlotToggled(slot: TimeSlot) {
        if (!slot.isAvailable) return

        val currentSelected = _uiState.value.selectedTimeSlots.toMutableList()
        if (currentSelected.contains(slot)) {
            currentSelected.remove(slot)
        } else {
            currentSelected.add(slot)
        }

        val newPrice = currentSelected.size * PRICE_PER_SLOT

        _uiState.update { it.copy(
            selectedTimeSlots = currentSelected,
            totalPrice = newPrice
        ) }
    }
}
