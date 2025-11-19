package me.loghub.api.service.notification

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.constant.redis.RedisKeys
import me.loghub.api.dto.notification.NotificationDTO
import me.loghub.api.entity.user.User
import me.loghub.api.util.checkExists
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class NotificationService(private val redisTemplate: RedisTemplate<String, NotificationDTO>) {
    private companion object {
        private const val PAGE_SIZE = 20L
    }

    fun getNotifications(user: User): List<NotificationDTO> {
        val redisKey = RedisKeys.NOTIFICATIONS(user.id!!)
        val notifications = redisTemplate.opsForZSet().reverseRange(redisKey.key, 0, PAGE_SIZE - 1)
        return notifications?.toList() ?: emptyList()
    }

    fun countNotifications(user: User): Long {
        val redisKey = RedisKeys.NOTIFICATIONS(user.id!!)
        return redisTemplate.opsForZSet().zCard(redisKey.key) ?: 0L
    }

    fun addNotification(userId: Long, notification: NotificationDTO) {
        val redisKey = RedisKeys.NOTIFICATIONS(userId)
        redisTemplate.opsForZSet().add(redisKey.key, notification, notification.timestamp.toDouble())
    }

    fun deleteNotification(user: User, timestamp: Long) {
        val redisKey = RedisKeys.NOTIFICATIONS(user.id!!)
        val removedCount =
            redisTemplate.opsForZSet().removeRangeByScore(redisKey.key, timestamp.toDouble(), timestamp.toDouble())

        checkExists(removedCount == null || removedCount > 0) { ResponseMessage.Notification.NOT_FOUND }
    }
}