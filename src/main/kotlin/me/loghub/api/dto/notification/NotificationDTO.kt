package me.loghub.api.dto.notification

import me.loghub.api.dto.user.UserDTO
import me.loghub.api.entity.notification.Notification

data class NotificationDTO(
    val id: Long,
    val href: String,
    val title: String,
    val message: String,
    val read: Boolean,
    val type: Notification.Type = Notification.Type.INFO,
    val createdAt: String,
    val actor: UserDTO,
)
