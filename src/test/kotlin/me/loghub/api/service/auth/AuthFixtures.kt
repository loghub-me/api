package me.loghub.api.service.auth

import me.loghub.api.constant.ai.LogHubChatModel
import me.loghub.api.dto.auth.join.JoinConfirmDTO
import me.loghub.api.dto.auth.join.JoinRequestDTO
import me.loghub.api.dto.auth.join.OAuth2JoinConfirmDTO
import me.loghub.api.dto.auth.login.LoginConfirmDTO
import me.loghub.api.dto.auth.login.LoginRequestDTO
import me.loghub.api.dto.auth.token.AccessToken
import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.entity.user.*

object AuthFixtures {
    fun user(
        id: Long = 1L,
        email: String = "test@loghub.me",
        username: String = "testuser",
        nickname: String = "testuser",
        provider: User.Provider = User.Provider.LOCAL,
        role: User.Role = User.Role.MEMBER,
    ) = User(
        id = id,
        email = email,
        username = username,
        nickname = nickname,
        provider = provider,
        role = role,
        privacy = UserPrivacy(),
        meta = UserMeta(
            profile = UserProfile(),
            github = UserGitHub(),
            stats = UserStats()
        ),
    )

    fun tokenDTO(
        accessToken: String = "access-token",
        refreshToken: String = "1:refresh-token",
    ) = TokenDTO(
        accessToken = AccessToken(accessToken),
        refreshToken = me.loghub.api.dto.auth.token.RefreshToken(refreshToken),
    )

    fun loginRequestDTO(
        email: String = "test@loghub.me",
    ) = LoginRequestDTO(email = email)

    fun loginConfirmDTO(
        email: String = "test@loghub.me",
        otp: String = "ABC123",
    ) = LoginConfirmDTO(email = email, otp = otp)

    fun joinRequestDTO(
        email: String = "join@loghub.me",
        username: String = "joinuser",
        nickname: String = "joinuser",
    ) = JoinRequestDTO(
        email = email,
        username = username,
        nickname = nickname,
    )

    fun joinConfirmDTO(
        email: String = "join@loghub.me",
        otp: String = "ABC123",
    ) = JoinConfirmDTO(
        email = email,
        otp = otp,
    )

    fun oauth2JoinConfirmDTO(
        email: String = "oauth@loghub.me",
        username: String = "oauthuser",
        nickname: String = "oauthuser",
        token: String = "123e4567-e89b-12d3-a456-426614174000",
    ) = OAuth2JoinConfirmDTO(
        email = email,
        username = username,
        nickname = nickname,
        token = token,
    )

    fun chatModel() = LogHubChatModel.GPT_5
}
