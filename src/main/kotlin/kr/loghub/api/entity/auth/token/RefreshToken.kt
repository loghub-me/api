package kr.loghub.api.entity.auth.token

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.util.*

@RedisHash(value = "refresh_token", timeToLive = 60 * 3)
data class RefreshToken(
    @Id
    val token: UUID,

    val userId: Long,
)