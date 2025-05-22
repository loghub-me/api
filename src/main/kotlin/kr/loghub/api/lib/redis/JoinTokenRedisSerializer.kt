package kr.loghub.api.lib.redis

import kr.loghub.api.dto.auth.token.JoinTokenDTO
import org.springframework.data.redis.serializer.RedisSerializer

class JoinTokenRedisSerializer : RedisSerializer<JoinTokenDTO> {
    override fun serialize(value: JoinTokenDTO?): ByteArray? =
        value?.let { "${it.otp},${it.email},${it.username},${it.nickname}".toByteArray(Charsets.UTF_8) }

    override fun deserialize(bytes: ByteArray?): JoinTokenDTO? {
        if (bytes == null) return null
        val data = String(bytes).split(",")
        return JoinTokenDTO(
            otp = data[0],
            email = data[1],
            username = data[2],
            nickname = data[3],
        )
    }
}