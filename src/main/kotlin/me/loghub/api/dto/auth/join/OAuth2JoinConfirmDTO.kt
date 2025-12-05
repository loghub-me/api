package me.loghub.api.dto.auth.join

import jakarta.validation.constraints.Size
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserPrivacy
import me.loghub.api.entity.user.UserProfile
import me.loghub.api.lib.validation.EmailValidation
import me.loghub.api.lib.validation.NicknameValidation
import me.loghub.api.lib.validation.Trimmed
import me.loghub.api.lib.validation.UsernameValidation

data class OAuth2JoinConfirmDTO(
    @field:EmailValidation
    val email: String,

    @field:UsernameValidation
    val username: String,

    @field:NicknameValidation
    val nickname: String,

    @field:Size(min = 36, max = 36, message = "토큰은 36자리 문자열입니다.")
    @field:Trimmed
    val token: String,
) {
    fun toUserEntity(provider: User.Provider) = User(
        email = this.email,
        username = this.username,
        profile = UserProfile(nickname = nickname),
        privacy = UserPrivacy(),
        provider = provider,
    )
}