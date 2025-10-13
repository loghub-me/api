package me.loghub.api.dto.auth.join

import me.loghub.api.entity.user.User

data class OAuth2JoinInfoDTO(
    val token: String,
    val email: String,
    val provider: User.Provider,
)