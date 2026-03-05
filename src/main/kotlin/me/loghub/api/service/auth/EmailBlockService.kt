package me.loghub.api.service.auth

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.auth.email.EmailBlockDTO
import me.loghub.api.exception.auth.token.BadEmailBlockTokenException
import me.loghub.api.lib.redis.key.auth.BlockedEmailRedisKey
import me.loghub.api.lib.redis.key.auth.EmailBlockTokenRedisKey
import me.loghub.api.util.sha256
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class EmailBlockService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    fun generateBlockToken(email: String): UUID {
        val token = UUID.randomUUID()
        val redisKey = EmailBlockTokenRedisKey(token)
        redisTemplate.opsForValue().set(redisKey, sha256(email), EmailBlockTokenRedisKey.TTL)
        return token
    }

    fun blockEmail(requestBody: EmailBlockDTO) {
        val tokenKey = EmailBlockTokenRedisKey(requestBody.token)
        val hashedEmail = redisTemplate.opsForValue().getAndDelete(tokenKey)
            ?: throw BadEmailBlockTokenException(ResponseMessage.Auth.INVALID_TOKEN)
        val deniedKey = BlockedEmailRedisKey(hashedEmail)
        redisTemplate.opsForValue().set(deniedKey, true.toString(), BlockedEmailRedisKey.TTL)
    }

    fun isDeniedEmail(email: String) =
        redisTemplate.hasKey(
            BlockedEmailRedisKey(sha256(email))
        ) == true
}