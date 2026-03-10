package me.loghub.api.dto.notification.event

data class NotificationConnectedEvent(
    val connectedAt: Long = System.currentTimeMillis(),
    override val recipientId: Long,
) : NotificationEvent {
    override val name: String
        get() = "notification.connected"
}
