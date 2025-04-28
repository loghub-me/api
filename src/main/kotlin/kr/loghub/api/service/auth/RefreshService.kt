package kr.loghub.api.service.auth

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.auth.token.TokenDTO
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.repository.auth.RefreshTokenRepository
import kr.loghub.api.repository.user.UserRepository
import kr.loghub.api.service.auth.token.TokenService
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import java.util.*

@Service
class RefreshService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
) {
    fun refreshToken(token: UUID): TokenDTO {
        val refreshToken = refreshTokenRepository.findByToken(token)
            ?.also { refreshTokenRepository.delete(it) }
            ?: throw BadCredentialsException(ResponseMessage.Auth.INVALID_TOKEN)
        val user = userRepository.findById(refreshToken.userId)
            .orElseThrow { EntityNotFoundException(ResponseMessage.User.NOT_FOUND) }
        return tokenService.generateToken(user)
    }
}