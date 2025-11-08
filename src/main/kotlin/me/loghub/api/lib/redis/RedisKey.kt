package me.loghub.api.lib.redis

import java.time.Duration

data class RedisKey(
    val key: String,
    val ttl: Duration,
)