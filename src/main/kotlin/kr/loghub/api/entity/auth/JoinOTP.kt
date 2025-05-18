package kr.loghub.api.entity.auth

import kr.loghub.api.entity.user.User
import kr.loghub.api.entity.user.UserPrivacy
import kr.loghub.api.entity.user.UserProfile
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "join_token", timeToLive = 60 * 3)
data class JoinOTP(
    @Id
    val otp: String,

    val email: String,
    val username: String,
    val nickname: String,
) {
    fun toUserEntity() = User(
        email = email,
        username = username,
        profile = UserProfile(nickname = nickname),
        privacy = UserPrivacy()
    )
}