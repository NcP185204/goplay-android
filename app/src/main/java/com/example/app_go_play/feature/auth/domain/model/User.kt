package com.example.app_go_play.feature.auth.domain.model

data class User(
    val id: String,
    val email: String?,
    val phoneNumber: String?,
    val role: Role,
    val status: UserStatus? = null
)
