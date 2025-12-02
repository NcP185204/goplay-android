package com.example.app_go_play.feature.court.domain.model

/**
 * Represents the clean, business-logic-friendly model of a Court.
 * This is the object used by the Domain and Presentation layers.
 */
data class Court(
    val id: Int,
    val name: String,
    val address: String,
    val courtType: String,       // e.g., "FOOTBALL", "TENNIS"
    val rating: Double?,
    val pricePerHour: Double,
    val imageUrls: List<String>,
    val ownerId: Long
)
