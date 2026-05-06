package com.example.app_go_play.feature.booking.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BookingRequest(
    @SerializedName("courtId")
    val courtId: Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("timeSlotIds")
    val timeSlotIds: List<Long>,
    @SerializedName("note")
    val note: String = "",
    @SerializedName("paymentMethod")
    val paymentMethod: String
)

data class PaymentResponse(
    @SerializedName("paymentUrl")
    val paymentUrl: String,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("orderId")
    val orderId: String? = null, // Chuyển sang optional để không lỗi khi parse
    @SerializedName("amount")
    val amount: Double? = null   // Chuyển sang optional
)
