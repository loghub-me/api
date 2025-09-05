package me.loghub.api.service.auth

import jakarta.transaction.Transactional
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.constant.redis.RedisKey
import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.exception.auth.BadRefreshTokenException
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.token.TokenService
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Service
class RefreshService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
) {
    @Transactional
    fun refreshToken(token: UUID?): TokenDTO {
        requireNotNull(token) { ResponseMessage.Auth.INVALID_TOKEN }

        val key = "${RedisKey.REFRESH_TOKEN.prefix}:$token"
        val userId = redisTemplate.opsForValue().get(key)
            ?.also { redisTemplate.expire(key, 10.seconds.toJavaDuration()) }  // Grace period
            ?: throw BadRefreshTokenException(ResponseMessage.Auth.INVALID_TOKEN)
        val user = userRepository.findById(userId.toLong())
            .orElseThrow { BadRefreshTokenException(ResponseMessage.Auth.INVALID_TOKEN) }
        return tokenService.generateToken(user)
    }
}