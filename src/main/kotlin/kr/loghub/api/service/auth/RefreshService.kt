package kr.loghub.api.service.auth

import jakarta.transaction.Transactional
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.auth.token.TokenDTO
import kr.loghub.api.repository.user.UserRepository
import kr.loghub.api.service.auth.token.TokenService
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.BadCredentialsException
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

        val userId = redisTemplate.opsForValue().get("refresh_token:$token")
            ?.also { redisTemplate.expire("refresh_token:$token", 10.seconds.toJavaDuration()) }  // Grace period
            ?: throw BadCredentialsException(ResponseMessage.Auth.INVALID_TOKEN)
        val user = userRepository.findById(userId.toLong())
            .orElseThrow { BadCredentialsException(ResponseMessage.Auth.INVALID_TOKEN) }
        return tokenService.generateToken(user)
    }
}