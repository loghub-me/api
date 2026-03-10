package me.loghub.api.dto.notification.event

data class NotificationPingEvent(
    override val recipientId: Long,
) : NotificationEvent {
    override val name: String
        get() = "notification.ping"
}
