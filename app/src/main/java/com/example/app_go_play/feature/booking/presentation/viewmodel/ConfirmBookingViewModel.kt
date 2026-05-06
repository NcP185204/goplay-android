package com.example.app_go_play.feature.booking.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_go_play.feature.booking.domain.model.Booking
import com.example.app_go_play.feature.booking.domain.repository.BookingRepository
import com.example.app_go_play.feature.booking.domain.usecase.CreateBookingUseCase
import com.example.app_go_play.feature.court.domain.model.TimeSlot
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConfirmBookingState(
    val selectedSlots: List<TimeSlot> = emptyList(),
    val courtName: String = "",
    val courtAddress: String = "",
    val totalPrice: Double = 0.0,
    val isBooking: Boolean = false,
    val paymentMethod: String = "CASH", // Mặc định là CASH
    val bookingResult: Result<Booking>? = null,
    val paymentUrl: String? = null
)

@HiltViewModel
class ConfirmBookingViewModel @Inject constructor(
    private val createBookingUseCase: CreateBookingUseCase,
    private val repository: BookingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ConfirmBookingState())
    val state = _state.asStateFlow()

    private val courtId: Int = savedStateHandle.get<Int>("courtId") ?: 0
    private val bookingDate: String = savedStateHandle.get<String>("date") ?: ""

    init {
        val slotsJson = savedStateHandle.get<String>("selectedSlots") ?: ""
        val courtName = savedStateHandle.get<String>("courtName") ?: ""
        val courtAddress = savedStateHandle.get<String>("courtAddress") ?: ""

        if (slotsJson.isNotEmpty()) {
            val type = object : TypeToken<List<TimeSlot>>() {}.type
            val slots = Gson().fromJson<List<TimeSlot>>(slotsJson, type) ?: emptyList()
            val total = slots.sumOf { it.price }
            _state.update { it.copy(selectedSlots = slots, courtName = courtName, courtAddress = courtAddress, totalPrice = total) }
        }
    }

    fun onPaymentMethodChanged(method: String) {
        _state.update { it.copy(paymentMethod = method.uppercase()) }
    }

    fun onConfirmBookingClicked(note: String?) {
        viewModelScope.launch {
            val currentState = _state.value
            val selectedIds = currentState.selectedSlots.map { it.id.toLong() }
            
            _state.update { it.copy(isBooking = true) }
            
            val result = createBookingUseCase(
                courtId = courtId,
                date = bookingDate,
                timeSlotIds = selectedIds,
                paymentMethod = currentState.paymentMethod,
                note = note ?: ""
            )
            
            result.onSuccess { booking ->
                if (currentState.paymentMethod == "MOMO") {
                    val bookingIdInt = booking.id.toIntOrNull() ?: 0
                    repository.createPayment(bookingIdInt).onSuccess { paymentInfo ->
                        _state.update { it.copy(
                            isBooking = false, 
                            paymentUrl = paymentInfo.paymentUrl, 
                            bookingResult = result
                        ) }
                    }.onFailure { error ->
                        _state.update { it.copy(isBooking = false, bookingResult = Result.failure(error)) }
                    }
                } else {
                    _state.update { it.copy(isBooking = false, bookingResult = result) }
                }
            }.onFailure { error ->
                _state.update { it.copy(isBooking = false, bookingResult = Result.failure(error)) }
            }
        }
    }

    // Hàm bị thiếu gây ra lỗi Unresolved reference
    fun clearBookingResult() {
        _state.update { it.copy(bookingResult = null, paymentUrl = null) }
    }
}
