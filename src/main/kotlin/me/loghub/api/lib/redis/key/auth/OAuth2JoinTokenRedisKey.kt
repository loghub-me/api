package me.loghub.api.lib.redis.key.auth

import me.loghub.api.lib.redis.key.RedisKey
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

object OAuth2JoinTokenRedisKey : RedisKey {
    val TTL = 10.minutes.toJavaDuration()
    operator fun invoke(email: String) = "oauth2_join_token:$email"
}