package me.loghub.api.lib.redis

import java.time.Duration

data class RedisKeySpec(
    val template: String,
    val ttl: Duration,
) {
    operator fun invoke(vararg parameters: Any) = RedisKey(
        key = template.format(*parameters),
        ttl = ttl,
    )
}