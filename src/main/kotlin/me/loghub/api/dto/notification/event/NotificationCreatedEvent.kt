package me.loghub.api.dto.notification.event

import me.loghub.api.dto.notification.NotificationDTO

data class NotificationCreatedEvent(
    val data: NotificationDTO,
    override val recipientId: Long,
) : NotificationEvent {
    override val name: String
        get() = "notification.created"
}