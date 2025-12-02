package com.example.app_go_play.feature.court.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_go_play.feature.court.domain.model.Court
import com.example.app_go_play.feature.court.domain.usecase.SearchCourtsUseCase
import com.example.app_go_play.util.PagedResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CourtListState {
    object Idle : CourtListState
    object Loading : CourtListState
    data class Success(val courts: PagedResult<Court>) : CourtListState
    data class Error(val message: String) : CourtListState
}

@HiltViewModel
class CourtListViewModel @Inject constructor(
    private val searchCourtsUseCase: SearchCourtsUseCase,
    savedStateHandle: SavedStateHandle // Inject SavedStateHandle to get nav args
) : ViewModel() {

    private val _courtsState = MutableStateFlow<CourtListState>(CourtListState.Idle)
    val courtsState = _courtsState.asStateFlow()

    // Read the initial query from navigation arguments. This is a key change.
    val initialQuery: String? = savedStateHandle.get<String>("query")

    init {
        // If there's an initial query from another screen, perform a search right away.
        if (!initialQuery.isNullOrBlank()) {
            searchCourts(name = initialQuery)
        }
    }

    fun searchCourts(
        name: String?,
        page: Int = 0,
        size: Int = 10
    ) {
        if (name.isNullOrBlank()) {
            _courtsState.value = CourtListState.Idle
            return
        }

        viewModelScope.launch {
            _courtsState.update { CourtListState.Loading }

            searchCourtsUseCase(name = name, page = page, size = size)
                .onSuccess { pagedResult ->
                    _courtsState.update { CourtListState.Success(pagedResult) }
                }
                .onFailure { error ->
                    _courtsState.update { CourtListState.Error(error.message ?: "An unknown error occurred") }
                }
        }
    }
}
