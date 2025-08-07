package me.loghub.api.service.auth.token

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.entity.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TokenService(
    private val accessTokenService: AccessTokenService,
    private val refreshTokenService: RefreshTokenService,
) {
    @Transactional
    fun generateToken(user: User) = TokenDTO(
        accessToken = accessTokenService.generateToken(user),
        refreshToken = refreshTokenService.generateToken(user),
    )
}