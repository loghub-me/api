package me.loghub.api.lib.redis

import me.loghub.api.dto.notification.NotificationDTO
import org.springframework.data.redis.serializer.RedisSerializer

class NotificationRedisSerializer : RedisSerializer<NotificationDTO> {
    override fun serialize(value: NotificationDTO?): ByteArray? =
        value?.let { "${it.href},${it.message}:${it.timestamp}".toByteArray(Charsets.UTF_8) }

    override fun deserialize(bytes: ByteArray?): NotificationDTO? {
        if (bytes == null) return null
        val data = String(bytes).split(",")
        return NotificationDTO(
            href = data[0],
            message = data[1],
            timestamp = data[2].toLong(),
        )
    }
}