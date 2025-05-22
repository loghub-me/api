package kr.loghub.api.service.auth.token

import kr.loghub.api.entity.user.User
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

@Service
class RefreshTokenService(private val redisTemplate: RedisTemplate<String, String>) {
    companion object {
        val REFRESH_TOKEN_EXPIRE_TIME = 30.days.toJavaDuration()
    }

    fun generateToken(user: User): UUID {
        val token = UUID.randomUUID()
        redisTemplate.opsForValue().set(
            "refresh_token:${token}",
            user.id.toString(),
            REFRESH_TOKEN_EXPIRE_TIME,
        )
        return token
    }

    fun revokeToken(token: String) =
        redisTemplate.delete("refresh_token:${token}")
}