package com.example.app_go_play.feature.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @SerializedName("refreshToken")
    val refreshToken: String
)
