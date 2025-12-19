package me.loghub.api.lib.redis.serializer

import me.loghub.api.dto.auth.join.JoinInfoDTO
import org.springframework.data.redis.serializer.RedisSerializer

class JoinInfoRedisSerializer : RedisSerializer<JoinInfoDTO> {
    override fun serialize(value: JoinInfoDTO?): ByteArray {
        requireNotNull(value)
        val (otp, email, username, nickname) = value
        return "$otp|$email|$username|$nickname".toByteArray(Charsets.UTF_8)
    }

    override fun deserialize(bytes: ByteArray?): JoinInfoDTO? {
        if (bytes == null) return null
        val data = String(bytes).split("|")
        return JoinInfoDTO(
            otp = data[0],
            email = data[1],
            username = data[2],
            nickname = data[3],
        )
    }
}