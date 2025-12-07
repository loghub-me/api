package me.loghub.api.dto.auth.join

import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserGitHub
import me.loghub.api.entity.user.UserPrivacy
import me.loghub.api.entity.user.UserProfile

data class JoinInfoDTO(
    val otp: String,
    val email: String,
    val username: String,
    val nickname: String,
) {
    fun toUserEntity() = User(
        email = email,
        username = username,
        profile = UserProfile(nickname = nickname),
        privacy = UserPrivacy(),
        github = UserGitHub(),
    )
}