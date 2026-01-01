package me.loghub.api.lib.redis.key.auth

import me.loghub.api.lib.redis.key.RedisKey
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

object RefreshTokenRedisKey : RedisKey {
    val TTL = 14.days.toJavaDuration()
    operator fun invoke(token: String) = "refresh_token:$token"
}