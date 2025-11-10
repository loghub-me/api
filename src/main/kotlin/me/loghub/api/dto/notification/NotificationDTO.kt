package me.loghub.api.dto.notification

data class NotificationDTO(
    val href: String,
    val message: String,
    val timestamp: Long,
)