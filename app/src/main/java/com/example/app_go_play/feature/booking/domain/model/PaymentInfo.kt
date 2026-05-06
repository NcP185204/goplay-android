package com.example.app_go_play.feature.booking.domain.model

data class PaymentInfo(
    val paymentUrl: String?,
    val orderId: String? = null, // Cho phép null để khớp với DTO
    val amount: Double? = null,   // Cho phép null để khớp với DTO
    val message: String? = null
)
