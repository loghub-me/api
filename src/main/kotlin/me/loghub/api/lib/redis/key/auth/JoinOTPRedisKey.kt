package me.loghub.api.lib.redis.key.auth

import me.loghub.api.lib.redis.key.RedisKey
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

object JoinOTPRedisKey : RedisKey {
    val TTL = 3.minutes.toJavaDuration()
    operator fun invoke(email: String) = "join_otp:$email"
}