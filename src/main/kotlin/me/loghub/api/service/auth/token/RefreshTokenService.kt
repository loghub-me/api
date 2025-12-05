package me.loghub.api.service.auth.token

import me.loghub.api.entity.user.User
import me.loghub.api.lib.redis.key.RedisKeys
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class RefreshTokenService(private val redisTemplate: RedisTemplate<String, String>) {
    fun generateToken(user: User): String {
        val token = "${user.id}:${UUID.randomUUID()}"
        val redisKey = RedisKeys.REFRESH_TOKEN(token)
        redisTemplate.opsForValue().set(redisKey.key, user.id.toString(), redisKey.ttl)
        return token
    }

    fun revokeToken(token: String) {
        val redisKey = RedisKeys.REFRESH_TOKEN(token)
        redisTemplate.delete(redisKey.key)
    }
}