package me.loghub.api.dto.notification

import java.time.LocalDateTime
import java.time.ZoneId

data class NotificationDTO(
    val href: String,
    val title: String,
    val message: String,
    val timestamp: Long = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
    val type: NotificationType = NotificationType.INFO,
)