package me.loghub.api.dto.auth.join

import me.loghub.api.entity.user.*

data class JoinInfoDTO(
    val otp: String,
    val email: String,
    val username: String,
    val nickname: String,
) {
    fun toUserEntity(): User {
        val user = User(
            email = email,
            username = username,
            nickname = nickname,
            provider = User.Provider.LOCAL,
            privacy = UserPrivacy(),
            meta = UserMeta(profile = UserProfile(), github = UserGitHub(), stats = UserStats())
        )
        user.meta.apply { this.user = user }
        return user
    }
}