package me.loghub.api.lib.redis

import me.loghub.api.dto.notification.NotificationDTO
import me.loghub.api.dto.notification.NotificationType
import org.springframework.data.redis.serializer.RedisSerializer

class NotificationRedisSerializer : RedisSerializer<NotificationDTO> {
    override fun serialize(value: NotificationDTO?): ByteArray? =
        value?.let { "${it.href}|${it.title}|${it.message}|${it.timestamp}|${it.type}".toByteArray(Charsets.UTF_8) }

    override fun deserialize(bytes: ByteArray?): NotificationDTO? {
        if (bytes == null) return null
        val data = String(bytes).split("|")
        return NotificationDTO(
            href = data[0],
            title = data[1],
            message = data[2],
            timestamp = data[3].toLong(),
            type = NotificationType.valueOf(data[4]),
        )
    }
}