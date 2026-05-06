package com.example.app_go_play.feature.notification.domain.model

import java.util.Date

data class Notification(
    val id: Long,
    val title: String,
    val content: String,
    val type: String,
    val relatedId: String?,
    val isRead: Boolean,
    val createdAt: Date
)
