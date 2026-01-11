package me.loghub.api.dto.auth.oauth2

import me.loghub.api.entity.user.User

data class OAuth2UserAttributes(
    val email: String,
    val provider: User.Provider,
) {
    fun toMap() = mapOf(
        OAuth2UserAttributes::email.name to email,
        OAuth2UserAttributes::provider.name to provider.name,
    )
}
