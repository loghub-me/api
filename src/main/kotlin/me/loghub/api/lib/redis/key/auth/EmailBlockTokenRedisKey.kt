package me.loghub.api.lib.redis.key.auth

import me.loghub.api.lib.redis.key.RedisKey
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

object EmailBlockTokenRedisKey : RedisKey {
    val TTL = 1.days.toJavaDuration()
    operator fun invoke(token: UUID) = "email_block_token:$token"
}