package com.example.app_go_play.feature.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SocialLoginRequest(
    @SerializedName("token")
    val token: String,

    @SerializedName("provider")
    val provider: String // "FACEBOOK" or "GOOGLE"
)
