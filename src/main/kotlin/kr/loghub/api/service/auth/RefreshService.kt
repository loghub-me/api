package kr.loghub.api.service.auth

import jakarta.transaction.Transactional
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.auth.token.TokenDTO
import kr.loghub.api.repository.auth.RefreshTokenRepository
import kr.loghub.api.service.auth.token.TokenService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class RefreshService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val tokenService: TokenService,
) {
    @Transactional
    fun refreshToken(token: UUID): TokenDTO {
        val refreshToken = refreshTokenRepository.findByToken(token)
            ?.also {
                val expiredAt = LocalDateTime.now().plusSeconds(30)  // Grace period
                refreshTokenRepository.updateExpiredAtByToken(expiredAt, token)
            }
            ?: throw BadCredentialsException(ResponseMessage.Auth.INVALID_TOKEN)
        return tokenService.generateToken(refreshToken.user)
    }
}