package me.loghub.api.lib.redis.key

import java.time.Duration

data class RedisKey(
    val key: String,
    val ttl: Duration,
)