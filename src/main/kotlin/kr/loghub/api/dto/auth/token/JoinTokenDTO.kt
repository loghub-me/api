package kr.loghub.api.dto.auth.token

import kr.loghub.api.entity.user.User
import kr.loghub.api.entity.user.UserPrivacy
import kr.loghub.api.entity.user.UserProfile

data class JoinTokenDTO(
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
    )
}