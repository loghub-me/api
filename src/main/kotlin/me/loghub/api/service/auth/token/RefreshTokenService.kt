package me.loghub.api.service.auth.token

import me.loghub.api.dto.auth.token.RefreshToken
import me.loghub.api.entity.user.User
import me.loghub.api.lib.redis.key.auth.RefreshTokenRedisKey
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class RefreshTokenService(private val redisTemplate: RedisTemplate<String, String>) {
    fun generateToken(user: User): RefreshToken {
        val token = "${user.id}:${UUID.randomUUID()}"
        val redisKey = RefreshTokenRedisKey(token)
        redisTemplate.opsForValue().set(redisKey, user.id.toString(), RefreshTokenRedisKey.TTL)
        return RefreshToken(token)
    }

    fun revokeToken(token: String) {
        val redisKey = RefreshTokenRedisKey(token)
        redisTemplate.delete(redisKey)
    }
}