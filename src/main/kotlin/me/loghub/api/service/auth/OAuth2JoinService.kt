package me.loghub.api.service.auth

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.auth.SessionDTO
import me.loghub.api.dto.auth.join.OAuth2JoinConfirmDTO
import me.loghub.api.dto.auth.join.OAuth2JoinInfoDTO
import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.task.avatar.AvatarGenerateRequest
import me.loghub.api.entity.user.User
import me.loghub.api.exception.auth.BadOTPException
import me.loghub.api.lib.redis.key.auth.OAuth2JoinTokenRedisKey
import me.loghub.api.proxy.TaskAPIProxy
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.token.TokenService
import org.springframework.data.redis.core.RedisTemplate
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
    fun issueToken(email: String, provider: User.Provider): String {
        val info = OAuth2JoinInfoDTO(
            token = UUID.randomUUID().toString(),
            email = email,
            provider = provider,
        )
        val redisKey = OAuth2JoinTokenRedisKey(info.email)
        redisTemplate.opsForValue().set(redisKey, info, OAuth2JoinTokenRedisKey.TTL)
        return info.token
    }

    @Transactional
    fun confirmJoin(requestBody: OAuth2JoinConfirmDTO): Pair<TokenDTO, SessionDTO> {
        val redisKey = OAuth2JoinTokenRedisKey(requestBody.email)
        val info = redisTemplate.opsForValue().get(redisKey)
            ?: throw BadOTPException(ResponseMessage.Auth.INVALID_TOKEN)

        when {
            requestBody.token != info.token -> throw BadOTPException(ResponseMessage.Auth.INVALID_TOKEN)
            else -> redisTemplate.delete(redisKey)
        }

        val joinedUser = userRepository.save(requestBody.toUserEntity(info.provider))
        taskAPIProxy.generateAvatar(AvatarGenerateRequest(joinedUser.id!!))
        return Pair(tokenService.generateToken(joinedUser), SessionDTO(joinedUser))
    }
}