package me.loghub.api.dto.auth.token

data class TokenDTO(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
)