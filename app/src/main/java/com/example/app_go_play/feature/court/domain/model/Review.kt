package com.example.app_go_play.feature.court.domain.model

import java.time.LocalDateTime

data class Review(
    val id: Long,
    val reviewerName: String,
    val rating: Int,
    val comment: String?,
    val createdAt: LocalDateTime
)
