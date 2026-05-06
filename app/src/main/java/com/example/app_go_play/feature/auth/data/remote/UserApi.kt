package com.example.app_go_play.feature.auth.data.remote

import retrofit2.http.PUT
import retrofit2.http.Query

interface UserApi {

    @PUT("api/users/me/fcm-token")
    suspend fun updateFcmToken(
        @Query("token") token: String
    )
}
