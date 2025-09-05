package me.loghub.api.dto.auth.token

import me.loghub.api.config.RefreshTokenConfig
import org.springframework.http.ResponseCookie
import java.util.*

data class TokenDTO(
    val accessToken: String,
    val refreshToken: UUID,
) {
    val authorization get() = "Bearer $accessToken"
    val cookie
        get() = ResponseCookie
            .from(RefreshTokenConfig.NAME, refreshToken.toString())
            .domain(RefreshTokenConfig.DOMAIN)
            .httpOnly(true)
            .secure(true)
            .path(RefreshTokenConfig.PATH)
            .sameSite(RefreshTokenConfig.SAME_SITE)
            .maxAge(RefreshTokenConfig.MAX_AGE)
            .build()
            .toString()
}