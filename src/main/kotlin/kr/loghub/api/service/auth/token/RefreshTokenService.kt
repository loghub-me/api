package kr.loghub.api.service.auth.token

import kr.loghub.api.constant.redis.RedisKey
import kr.loghub.api.entity.user.User
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class RefreshTokenService(private val redisTemplate: RedisTemplate<String, String>) {
    fun generateToken(user: User): UUID {
        val token = UUID.randomUUID()
        val key = "${RedisKey.REFRESH_TOKEN.prefix}:${token}"
        redisTemplate.opsForValue().set(key, user.id.toString(), RedisKey.REFRESH_TOKEN.ttl)
        return token
    }

    fun revokeToken(token: String) {
        val key = "${RedisKey.REFRESH_TOKEN.prefix}:${token}"
        redisTemplate.delete(key)
    }
}