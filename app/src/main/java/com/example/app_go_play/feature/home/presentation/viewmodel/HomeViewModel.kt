package com.example.app_go_play.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_go_play.feature.court.domain.model.Court
import com.example.app_go_play.feature.court.domain.usecase.SearchCourtsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Data class to hold the UI state for the home screen
data class HomeState(
    val isLoading: Boolean = false,
    val topCourts: List<Court> = emptyList(),
    val nearestCourts: List<Court> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchCourtsUseCase: SearchCourtsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        loadCourts()
    }

    private fun loadCourts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Launch both API calls in parallel
                val topCourtsDeferred = async { searchCourtsUseCase(minRating = 4.0, size = 5) }
                val nearestCourtsDeferred = async { searchCourtsUseCase(latitude = 10.7769, longitude = 106.6954,  radiusInKm = 5.0, size = 5) } // Example: Near Ho Chi Minh City Center

                // Wait for both to complete
                val topCourtsResult = topCourtsDeferred.await()
                val nearestCourtsResult = nearestCourtsDeferred.await()

                // Check for failures and update state
                val errorMessages = mutableListOf<String>()
                topCourtsResult.onFailure { errorMessages.add("Top courts: ${it.message}") }
                nearestCourtsResult.onFailure { errorMessages.add("Nearest courts: ${it.message}") }

                if (errorMessages.isNotEmpty()) {
                    _uiState.update { it.copy(isLoading = false, error = errorMessages.joinToString("\n")) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            topCourts = topCourtsResult.getOrNull()?.content ?: emptyList(),
                            nearestCourts = nearestCourtsResult.getOrNull()?.content ?: emptyList(),
                            error = null
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "An unexpected error occurred: ${e.message}") }
            }
        }
    }
}
