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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _courtsState = MutableStateFlow<CourtListState>(CourtListState.Idle)
    val courtsState = _courtsState.asStateFlow()

    private val _searchQuery = MutableStateFlow(savedStateHandle.get<String>("query") ?: "")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedSport = MutableStateFlow<String?>(null)
    val selectedSport = _selectedSport.asStateFlow()

    private val _selectedDistrict = MutableStateFlow<String?>(null)
    val selectedDistrict = _selectedDistrict.asStateFlow()

    init {
        searchCourts()
    }

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
        searchCourts()
    }

    fun onSportChanged(sport: String?) {
        _selectedSport.value = if (sport == "Tất cả") null else sport
        searchCourts()
    }

    fun onDistrictChanged(district: String?) {
        _selectedDistrict.value = if (district == "Tất cả") null else district
        searchCourts()
    }

    private fun mapDistrictToDbFormat(district: String?): String? {
        if (district == null || district == "Tất cả") return null
        return if (district.startsWith("Quận ")) {
            district.replace("Quận ", "Q")
        } else {
            district
        }
    }

    fun searchCourts(page: Int = 0, size: Int = 20) {
        viewModelScope.launch {
            _courtsState.update { CourtListState.Loading }

            val dbDistrict = mapDistrictToDbFormat(_selectedDistrict.value)
            val nameQuery = _searchQuery.value.trim().ifEmpty { null }

            // TÁCH BIỆT: nameQuery gửi vào 'name', dbDistrict gửi vào 'address'
            searchCourtsUseCase(
                name = nameQuery,
                address = dbDistrict, // Truyền vào tham số address riêng biệt
                courtType = _selectedSport.value,
                page = page,
                size = size
            ).onSuccess { pagedResult ->
                _courtsState.update { CourtListState.Success(pagedResult) }
            }.onFailure { error ->
                _courtsState.update { CourtListState.Error(error.message ?: "Đã có lỗi xảy ra") }
            }
        }
    }
}
