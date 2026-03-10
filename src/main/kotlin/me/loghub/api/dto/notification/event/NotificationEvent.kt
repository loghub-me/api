package me.loghub.api.dto.notification.event

interface NotificationEvent {
    val name: String
    val recipientId: Long
}