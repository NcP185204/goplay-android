package com.example.app_go_play.feature.booking.data.remote

import com.example.app_go_play.feature.booking.data.remote.dto.BookingRequest
import com.example.app_go_play.feature.booking.data.remote.dto.BookingResponse
import com.example.app_go_play.feature.booking.data.remote.dto.PaymentResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BookingApi {

    @POST("api/bookings")
    suspend fun createBooking(@Body request: BookingRequest): BookingResponse

    @POST("api/bookings/{id}/create-payment")
    suspend fun createPayment(@Path("id") id: Int): PaymentResponse

    @GET("api/bookings/my-history")
    suspend fun getMyBooking(): List<BookingResponse>


    @GET("api/bookings/upcoming")
    suspend fun getUpcomingBooking(): BookingResponse?
}
