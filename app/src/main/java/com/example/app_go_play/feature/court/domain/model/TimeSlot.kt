package com.example.app_go_play.feature.court.domain.model

import java.time.LocalDateTime

data class TimeSlot(
    val id: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val price: Double,
    val available: Boolean // Using "is" prefix for Boolean properties is a Kotlin convention
)
