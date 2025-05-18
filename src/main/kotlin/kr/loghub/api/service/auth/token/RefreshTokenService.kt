package kr.loghub.api.service.auth.token

import kr.loghub.api.entity.user.User
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class RefreshTokenService(private val refreshTokenTemplate: RedisTemplate<String, Long>) {
    companion object {
        const val REFRESH_TOKEN_EXPIRE_TIME = 30L
    }

    fun generateToken(user: User): UUID {
        val token = UUID.randomUUID()
        refreshTokenTemplate.opsForValue().set(
            token.toString(),
            user.id ?: TODO(),
            REFRESH_TOKEN_EXPIRE_TIME,
            TimeUnit.DAYS
        )
        return token
    }
}