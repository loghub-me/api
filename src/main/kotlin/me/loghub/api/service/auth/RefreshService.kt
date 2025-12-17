package me.loghub.api.service.auth

import jakarta.transaction.Transactional
import me.loghub.api.config.RefreshTokenConfig
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.exception.auth.BadRefreshTokenException
import me.loghub.api.lib.redis.key.RedisKeys
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.token.TokenService
import me.loghub.api.util.checkField
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Service
class RefreshService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
) {
    @Transactional
    fun refreshToken(token: String?): TokenDTO {
        checkField(RefreshTokenConfig.NAME, token != null) { ResponseMessage.Auth.INVALID_TOKEN }

        val redisKey = RedisKeys.REFRESH_TOKEN(token)
        val userId = redisTemplate.opsForValue().get(redisKey.key)
            ?.also { redisTemplate.expire(redisKey.key, 10.seconds.toJavaDuration()) }  // Grace period
            ?: throw BadRefreshTokenException(ResponseMessage.Auth.INVALID_TOKEN)
        val user = userRepository.findById(userId.toLong())
            .orElseThrow { BadRefreshTokenException(ResponseMessage.Auth.INVALID_TOKEN) }
        return tokenService.generateToken(user)
    }
}