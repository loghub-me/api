package me.loghub.api.lib.redis.key.user

import me.loghub.api.lib.redis.key.RedisKey

object NotificationsRedisKey : RedisKey {
    operator fun invoke(userId: Long) = "notifications:$userId"
}