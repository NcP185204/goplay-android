package com.example.app_go_play.feature.court.domain.model

import java.time.LocalDateTime

/**
 * Represents the clean, business-logic-friendly model of a TimeSlot.
 * This is the object used by the Domain and Presentation layers.
 */
data class TimeSlot(
    val id: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val price: Double,
    val isAvailable: Boolean
)
