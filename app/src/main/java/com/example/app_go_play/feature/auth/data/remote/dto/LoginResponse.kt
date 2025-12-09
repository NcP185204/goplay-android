package com.example.app_go_play.feature.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

// This class must match the JSON response from the Spring Boot backend
data class LoginResponse(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String,

    @SerializedName("tokenType")
    val tokenType: String?
)
