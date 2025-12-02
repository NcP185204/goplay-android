package com.example.app_go_play.feature.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") // SỬA LỖI: Gửi đi với key là "email" để khớp với backend
    val emailOrPhone: String,
    val password: String
)
