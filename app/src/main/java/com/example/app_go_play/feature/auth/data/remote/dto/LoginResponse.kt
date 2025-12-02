package com.example.app_go_play.feature.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

// This class must match the JSON response from the Spring Boot backend
data class LoginResponse(
    // SỬA LỖI: Báo cho Gson biết tên trong JSON là "token"
    @SerializedName("token")
    val accessToken: String, // Tên biến trong code Kotlin vẫn giữ là accessToken cho nhất quán

    // Cho phép trường này có thể null để phòng trường hợp backend không trả về
    @SerializedName("tokenType")
    val tokenType: String?
)
