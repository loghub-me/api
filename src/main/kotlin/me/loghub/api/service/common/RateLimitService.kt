package me.loghub.api.service.common

import me.loghub.api.lib.redis.key.common.RateLimitRedisKey
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class RateLimitService(private val redisTemplate: RedisTemplate<String, String>) {
    fun tryConsume(
        userId: Long,
        className: String,
        methodName: String,
        limit: Int,
        window: Long,
        unit: ChronoUnit
    ): Boolean {
        val now = Instant.now()
        val duration = unit.duration.multipliedBy(window)
        val windowStartMillis = now.minus(duration).toEpochMilli()

        val redisKey = RateLimitRedisKey(userId, className, methodName)

        val zSetOps = redisTemplate.opsForZSet()
        zSetOps.removeRangeByScore(redisKey, 0.0, windowStartMillis.toDouble())

        val currentCount = zSetOps.zCard(redisKey) ?: 0L
        if (currentCount >= limit) {
            return false
        }

        val nowMillis = now.toEpochMilli()
        zSetOps.add(redisKey, nowMillis.toString(), nowMillis.toDouble())

        val expireSeconds = duration.seconds
        if (expireSeconds > 0) {
            val currentTTL = redisTemplate.getExpire(redisKey)
            if (currentTTL == -1L || currentTTL == -2L) {  // No TTL set or key does not exist
                redisTemplate.expire(redisKey, Duration.ofSeconds(expireSeconds))
            }
        }

        return true
    }
}