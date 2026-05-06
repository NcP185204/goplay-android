package com.example.app_go_play.feature.notification.data.remote.dto

import com.example.app_go_play.feature.notification.domain.model.Notification
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class NotificationResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("type") val type: String,
    @SerializedName("relatedId") val relatedId: Long?, // Backend trả về kiểu số (0)
    @SerializedName("read") val isRead: Boolean,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("user") val user: NotificationUserResponse? // Thông tin user kèm theo
)

data class NotificationUserResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("phoneNumber") val phoneNumber: String?
)

data class NotificationPageResponse(
    @SerializedName("content") val content: List<NotificationResponse>,
    @SerializedName("totalElements") val totalElements: Long,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("number") val number: Int
)

fun NotificationResponse.toDomain(): Notification {
    // Parse định dạng: 2026-03-30T02:09:58.171Z
    val formats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    )
    
    var date: Date? = null
    for (format in formats) {
        try {
            date = format.parse(createdAt)
            if (date != null) break
        } catch (e: Exception) {
            continue
        }
    }

    return Notification(
        id = id,
        title = title,
        content = content,
        type = type,
        // Chuyển từ Long? sang String? để dùng làm bookingId trong App
        relatedId = relatedId?.toString(),
        isRead = isRead,
        createdAt = date ?: Date()
    )
}
