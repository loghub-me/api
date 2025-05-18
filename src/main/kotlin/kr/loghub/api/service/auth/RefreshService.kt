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
import java.util.concurrent.TimeUnit

@Service
class RefreshService(
    private val refreshTokenTemplate: RedisTemplate<String, Long>,
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
) {
    @Transactional
    fun refreshToken(token: UUID): TokenDTO {
        val userId = refreshTokenTemplate.opsForValue().get(token.toString())
            ?.also { refreshTokenTemplate.expire(token.toString(), 30, TimeUnit.SECONDS) }  // Grace period
            ?: throw BadCredentialsException(ResponseMessage.Auth.INVALID_TOKEN)
        val user = userRepository.findById(userId)
            .orElseThrow { BadCredentialsException(ResponseMessage.Auth.INVALID_TOKEN) }
        return tokenService.generateToken(user)
    }
}