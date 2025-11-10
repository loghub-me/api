package me.loghub.api.service.user

import me.loghub.api.constant.redis.RedisKeys
import me.loghub.api.dto.notification.NotificationDTO
import me.loghub.api.entity.user.User
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class NotificationService(private val redisTemplate: RedisTemplate<String, NotificationDTO>) {
    fun getNotifications(user: User): List<NotificationDTO> {
        val redisKey = RedisKeys.User.NOTIFICATIONS(user.id!!)
        val notifications = redisTemplate.opsForZSet().reverseRangeWithScores(redisKey.key, 0, -1)
        return notifications?.map { it.value!! } ?: emptyList()
    }

    fun countNotifications(user: User): Long {
        val redisKey = RedisKeys.User.NOTIFICATIONS(user.id!!)
        return redisTemplate.opsForZSet().zCard(redisKey.key) ?: 0L
    }

    fun addNotification(user: User, notification: NotificationDTO) {
        val redisKey = RedisKeys.User.NOTIFICATIONS(user.id!!)
        redisTemplate.opsForZSet().add(redisKey.key, notification, notification.timestamp.toDouble())
    }
}