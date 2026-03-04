package me.loghub.api.lib.redis.key.auth

import me.loghub.api.lib.redis.key.RedisKey
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

object BlockedEmailRedisKey : RedisKey {
    val TTL = 3.days.toJavaDuration()
    operator fun invoke(email: String) = "blocked_email:$email"
}