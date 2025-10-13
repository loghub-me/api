package me.loghub.api.lib.redis

import me.loghub.api.dto.auth.join.OAuth2JoinInfoDTO
import me.loghub.api.entity.user.User
import org.springframework.data.redis.serializer.RedisSerializer

class OAuth2JoinInfoRedisSerializer : RedisSerializer<OAuth2JoinInfoDTO> {
    override fun serialize(value: OAuth2JoinInfoDTO?): ByteArray? =
        value?.let { "${it.token},${it.email},${it.provider.name}".toByteArray(Charsets.UTF_8) }

    override fun deserialize(bytes: ByteArray?): OAuth2JoinInfoDTO? {
        if (bytes == null) return null
        val data = String(bytes).split(",")
        return OAuth2JoinInfoDTO(
            token = data[0],
            email = data[1],
            provider = User.Provider.valueOf(data[2]),
        )
    }
}