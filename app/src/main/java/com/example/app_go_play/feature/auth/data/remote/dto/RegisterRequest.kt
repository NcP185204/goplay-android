package com.example.app_go_play.feature.auth.data.remote.dto

import com.example.app_go_play.feature.auth.domain.model.Role
import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("email")
    val email: String?,

    @SerializedName("phoneNumber")
    val phoneNumber: String?,

    @SerializedName("password")
    val password: String,

    @SerializedName("role")
    val role: Role
)
