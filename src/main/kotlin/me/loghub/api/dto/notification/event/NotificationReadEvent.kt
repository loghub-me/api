package me.loghub.api.dto.notification.event

data class NotificationReadEvent(
    val notificationId: Long,
    override val recipientId: Long,
) : NotificationEvent {
    override val name: String
        get() = "notification.read"
}
