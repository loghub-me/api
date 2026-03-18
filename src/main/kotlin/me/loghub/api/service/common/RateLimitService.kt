package me.loghub.api.service.common

import me.loghub.api.lib.redis.key.common.RateLimitRedisKey
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class RateLimitService(private val redisTemplate: RedisTemplate<String, String>) {
    private companion object {
        val TRY_CONSUME_SCRIPT = DefaultRedisScript<Long>().apply {
            setScriptText(
                """
                local key = KEYS[1]
                local now = tonumber(ARGV[1])
                local window = tonumber(ARGV[2])
                local limit = tonumber(ARGV[3])
                local member = ARGV[4]

                redis.call('ZREMRANGEBYSCORE', key, '-inf', now - window)
                local count = redis.call('ZCARD', key)

                if count >= limit then
                    redis.call('PEXPIRE', key, window)
                    return 0
                end

                redis.call('ZADD', key, now, member)
                redis.call('PEXPIRE', key, window)
                return 1
                """.trimIndent()
            )
            resultType = Long::class.java
        }
    }

    fun tryConsume(
        userId: Long,
        className: String,
        methodName: String,
        limit: Int,
        window: Long,
        unit: ChronoUnit
    ): Boolean {
        val nowMillis = Instant.now().toEpochMilli()
        val windowMillis = unit.duration.multipliedBy(window).toMillis()
        val redisKey = RateLimitRedisKey(userId, className, methodName)
        val member = "$nowMillis:${UUID.randomUUID()}"

        return redisTemplate.execute(
            TRY_CONSUME_SCRIPT,
            listOf(redisKey),
            nowMillis.toString(),
            windowMillis.toString(),
            limit.toString(),
            member,
        ) == 1L
    }
}
