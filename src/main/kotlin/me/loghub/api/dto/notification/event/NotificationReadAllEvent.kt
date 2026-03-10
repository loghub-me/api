package me.loghub.api.dto.notification.event

data class NotificationReadAllEvent(
    val readCount: Int,
    override val recipientId: Long,
) : NotificationEvent {
    override val name: String
        get() = "notification.read_all"
}
