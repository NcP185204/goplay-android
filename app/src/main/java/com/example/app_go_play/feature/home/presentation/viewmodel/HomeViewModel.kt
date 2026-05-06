package com.example.app_go_play.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_go_play.feature.court.domain.model.Court
import com.example.app_go_play.feature.court.domain.usecase.SearchCourtsUseCase
import com.example.app_go_play.feature.booking.domain.repository.BookingRepository
import com.example.app_go_play.feature.booking.domain.model.Booking
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = "Phúc",
    val userAvatar: String? = null,
    val upcomingBooking: Booking? = null, // Dữ liệu thật từ Backend
    val topCourts: List<Court> = emptyList(),
    val nearestCourts: List<Court> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchCourtsUseCase: SearchCourtsUseCase,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Gọi song song các API
                val upcomingDeferred = async { bookingRepository.getUpcomingBooking() }
                val topCourtsDeferred = async { searchCourtsUseCase(minRating = 4.0, size = 10) }
                val nearestCourtsDeferred = async { searchCourtsUseCase(address = "Quận 10", size = 10) }

                val upcomingResult = upcomingDeferred.await()
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        upcomingBooking = upcomingResult.getOrNull(), // Sẽ là null nếu 204 No Content
                        topCourts = topCourtsDeferred.await().getOrNull()?.content ?: emptyList(),
                        nearestCourts = nearestCourtsDeferred.await().getOrNull()?.content ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
