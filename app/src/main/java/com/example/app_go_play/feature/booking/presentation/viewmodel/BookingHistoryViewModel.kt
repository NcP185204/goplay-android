package com.example.app_go_play.feature.booking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_go_play.feature.booking.domain.model.Booking
import com.example.app_go_play.feature.booking.domain.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BookingHistoryState {
    object Loading : BookingHistoryState
    data class Success(val bookings: List<Booking>) : BookingHistoryState
    data class Error(val message: String) : BookingHistoryState
}

@HiltViewModel
class BookingHistoryViewModel @Inject constructor(
    private val repository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookingHistoryState>(BookingHistoryState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { BookingHistoryState.Loading }
            repository.getMyBooking()
                .onSuccess { list ->
                    _uiState.update { BookingHistoryState.Success(list) }
                }
                .onFailure { error ->
                    _uiState.update { BookingHistoryState.Error(error.message ?: "Không thể tải lịch sử") }
                }
        }
    }
}
