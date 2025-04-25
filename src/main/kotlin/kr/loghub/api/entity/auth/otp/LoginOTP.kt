package kr.loghub.api.entity.auth.otp

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(value = "login_token", timeToLive = 60 * 3)
data class LoginOTP(
    @Id
    val otp: String,

    val email: String,
)