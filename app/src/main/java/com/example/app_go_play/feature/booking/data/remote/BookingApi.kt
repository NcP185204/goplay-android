package com.example.app_go_play.feature.booking.data.remote

import com.example.app_go_play.feature.booking.data.remote.dto.BookCourtRequest
import com.example.app_go_play.feature.booking.data.remote.dto.BookingDto
import retrofit2.http.Body
import retrofit2.http.POST

interface BookingApi {

    @POST("bookings")
    suspend fun bookCourt(@Body bookCourtRequest: BookCourtRequest): BookingDto

}
