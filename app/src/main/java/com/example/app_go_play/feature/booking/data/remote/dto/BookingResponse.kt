package com.example.app_go_play.feature.booking.data.remote.dto

import com.example.app_go_play.feature.booking.domain.model.Booking
import com.example.app_go_play.feature.booking.domain.model.BookingStatus
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BookingResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("courtId")
    val courtId: String? = null,
    @SerializedName("courtName")
    val courtName: String,
    @SerializedName("courtAddress")
    val courtAddress: String,
    @SerializedName("totalPrice")
    val totalPrice: Double,
    @SerializedName("status")
    val status: BookingStatus,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("timeSlotDetails")
    val timeSlotDetails: List<String>,
    @SerializedName("latitude")
    val latitude: Double? = null,
    @SerializedName("longitude")
    val longitude: Double? = null
)

fun BookingResponse.toBooking(): Booking {
    // Backend trả về: "2026-01-14T10:30:00"
    // Thử các định dạng phổ biến để đảm bảo luôn lấy được ngày đúng
    val dateFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    )

    var parsedDate: Date? = null
    for (format in dateFormats) {
        try {
            parsedDate = format.parse(createdAt)
            if (parsedDate != null) break
        } catch (e: Exception) {
            continue
        }
    }

    return Booking(
        id = id.toString(),
        userId = userId ?: "",
        courtId = courtId ?: "",
        courtName = courtName,
        courtAddress = courtAddress,
        timeSlotId = "",
        bookingDate = parsedDate ?: Date(), // Chỉ dùng Date() hiện tại nếu parse thất bại hoàn toàn
        totalPrice = totalPrice.toFloat(),
        status = status.name,
        timeSlotDetails = timeSlotDetails,
        latitude = latitude,
        longitude = longitude
    )
}
