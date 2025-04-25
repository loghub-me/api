package kr.loghub.api.service.auth.token

import kr.loghub.api.dto.auth.token.TokenDTO
import kr.loghub.api.entity.user.User
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val accessTokenService: AccessTokenService,
    private val refreshTokenService: RefreshTokenService,
) {
    fun generateToken(user: User) = TokenDTO(
        accessToken = accessTokenService.generateToken(user),
        refreshToken = refreshTokenService.generateToken(user),
    )
}