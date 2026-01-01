package me.loghub.api.lib.redis.key.common

import me.loghub.api.lib.redis.key.RedisKey
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

object RateLimitRedisKey : RedisKey {
    val TTL = 1.days.toJavaDuration()
    operator fun invoke(userId: Long, className: String, methodName: String) =
        "rate_limit:$userId:$className.$methodName"
}