package me.loghub.api.service.auth

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.constant.oauth2.OAuth2Attribute
import me.loghub.api.dto.auth.join.OAuth2JoinConfirmDTO
import me.loghub.api.dto.auth.join.OAuth2JoinInfoDTO
import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.task.avatar.AvatarGenerateRequest
import me.loghub.api.entity.user.User
import me.loghub.api.exception.auth.BadOTPException
import me.loghub.api.lib.redis.key.RedisKeys
import me.loghub.api.proxy.TaskAPIProxy
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.token.TokenService
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class OAuth2JoinService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val redisTemplate: RedisTemplate<String, OAuth2JoinInfoDTO>,
    private val taskAPIProxy: TaskAPIProxy,
) {
    @Transactional(readOnly = true)
    fun existsByEmail(email: String) = userRepository.existsByEmail(email)

    fun issueToken(user: DefaultOAuth2User): String {
        val info = OAuth2JoinInfoDTO(
            token = UUID.randomUUID().toString(),
            email = user.attributes[OAuth2Attribute.EMAIL].toString(),
            provider = User.Provider.valueOf(user.attributes[OAuth2Attribute.PROVIDER].toString())
        )
        val redisKey = RedisKeys.OAUTH2_JOIN_TOKEN(info.email)
        redisTemplate.opsForValue().set(redisKey.key, info, redisKey.ttl)
        return info.token
    }

    @Transactional
    fun confirmJoin(requestBody: OAuth2JoinConfirmDTO): TokenDTO {
        val redisKey = RedisKeys.OAUTH2_JOIN_TOKEN(requestBody.email)
        val info = redisTemplate.opsForValue().get(redisKey.key)
            ?: throw BadOTPException(ResponseMessage.Auth.INVALID_TOKEN)

        when {
            requestBody.token != info.token -> throw BadOTPException(ResponseMessage.Auth.INVALID_TOKEN)
            else -> redisTemplate.delete(redisKey.key)
        }

        val joinedUser = userRepository.save(requestBody.toUserEntity(info.provider))
        taskAPIProxy.generateAvatar(AvatarGenerateRequest(joinedUser.id!!))
        return tokenService.generateToken(joinedUser)
    }
}