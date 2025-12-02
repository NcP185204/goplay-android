package com.example.app_go_play.feature.court.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_go_play.feature.court.domain.model.Court
import com.example.app_go_play.feature.court.domain.model.Review
import com.example.app_go_play.feature.court.domain.model.TimeSlot
import com.example.app_go_play.feature.court.domain.usecase.GetAvailableTimeSlotsUseCase
import com.example.app_go_play.feature.court.domain.usecase.GetCourtDetailsUseCase
import com.example.app_go_play.feature.court.domain.usecase.GetReviewsUseCase
import com.example.app_go_play.util.PagedResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

// Data class to hold the entire state for the detail screen
data class CourtDetailState(
    val court: Court? = null,
    val timeSlots: List<TimeSlot> = emptyList(),
    val reviews: PagedResult<Review>? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val isLoadingCourt: Boolean = false,
    val isLoadingSlots: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CourtDetailViewModel @Inject constructor(
    private val getCourtDetailsUseCase: GetCourtDetailsUseCase,
    private val getAvailableTimeSlotsUseCase: GetAvailableTimeSlotsUseCase,
    private val getReviewsUseCase: GetReviewsUseCase,
    savedStateHandle: SavedStateHandle // To get courtId from navigation arguments
) : ViewModel() {

    private val _state = MutableStateFlow(CourtDetailState())
    val state = _state.asStateFlow()

    private val courtId: Int = checkNotNull(savedStateHandle["courtId"])

    init {
        loadCourtDetails()
        loadReviews()
    }

    fun loadCourtDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingCourt = true) }
            getCourtDetailsUseCase(courtId)
                .onSuccess { court ->
                    _state.update { it.copy(isLoadingCourt = false, court = court) }
                    // After getting court details, load today's slots
                    loadAvailableTimeSlots(state.value.selectedDate)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoadingCourt = false, error = error.message) }
                }
        }
    }

    fun loadAvailableTimeSlots(date: LocalDate) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingSlots = true, selectedDate = date, error = null) }
            getAvailableTimeSlotsUseCase(courtId, date)
                .onSuccess { slots ->
                    _state.update { it.copy(isLoadingSlots = false, timeSlots = slots) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoadingSlots = false, error = error.message) }
                }
        }
    }

    private fun loadReviews(page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            // You might want a separate loading state for reviews if needed
            getReviewsUseCase(courtId, page, size)
                .onSuccess { reviewPage ->
                    _state.update { it.copy(reviews = reviewPage) }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
        }
    }
}
