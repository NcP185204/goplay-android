package com.example.app_go_play.feature.notification.data.remote

import com.example.app_go_play.feature.notification.data.remote.dto.NotificationPageResponse
import com.example.app_go_play.feature.notification.data.remote.dto.NotificationResponse
import retrofit2.http.*

interface NotificationApi {

    @GET("api/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): NotificationPageResponse

    @GET("api/notifications/unread-count")
    suspend fun getUnreadCount(): Int

    @PUT("api/notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Long)

    @PUT("api/notifications/read-all")
    suspend fun markAllAsRead()
}
