package me.loghub.api.service.auth

import jakarta.transaction.Transactional
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.constant.redis.RedisKey
import me.loghub.api.dto.auth.JoinConfirmDTO
import me.loghub.api.dto.auth.JoinRequestDTO
import me.loghub.api.dto.auth.token.JoinTokenDTO
import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.task.avatar.AvatarGenerateRequest
import me.loghub.api.dto.task.mail.JoinMailSendRequest
import me.loghub.api.entity.user.User
import me.loghub.api.proxy.TaskAPIProxy
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.token.TokenService
import me.loghub.api.util.OTPBuilder
import me.loghub.api.util.checkAlreadyExists
import me.loghub.api.worker.MailSendWorker
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service

@Service
class JoinService(
    private val redisTemplate: RedisTemplate<String, JoinTokenDTO>,
    private val userRepository: UserRepository,
    private val mailSendWorker: MailSendWorker,
    private val tokenService: TokenService,
    private val taskAPIProxy: TaskAPIProxy,
) {
    @Transactional
    fun requestJoin(requestBody: JoinRequestDTO) {
        checkJoinable(requestBody.email, requestBody.username)

        val otp = issueOTP(requestBody)
        val mail = JoinMailSendRequest(to = requestBody.email, otp = otp)
        mailSendWorker.addToQueue(mail)
    }

    @Transactional
    fun confirmJoin(requestBody: JoinConfirmDTO): TokenDTO {
        val otp = redisTemplate.opsForValue().get("${RedisKey.JOIN_OTP.prefix}:${requestBody.email}")
            ?: throw BadCredentialsException(ResponseMessage.Auth.INVALID_OTP)

        when {
            requestBody.otp != otp.otp -> throw BadCredentialsException(ResponseMessage.Auth.INVALID_OTP)
            else -> redisTemplate.delete("${RedisKey.JOIN_OTP.prefix}:${requestBody.email}")
        }

        val joinedUser = userRepository.save(otp.toUserEntity())
        taskAPIProxy.generateAvatar(AvatarGenerateRequest(joinedUser.id!!))
        return tokenService.generateToken(joinedUser)
    }

    private fun checkJoinable(email: String, username: String) {
        checkAlreadyExists(
            User::email.name,
            userRepository.existsByEmail(email),
        ) { ResponseMessage.User.EMAIL_ALREADY_EXISTS }
        checkAlreadyExists(
            User::username.name,
            userRepository.existsByUsername(username),
        ) { ResponseMessage.User.USERNAME_ALREADY_EXISTS }
    }

    private fun issueOTP(requestBody: JoinRequestDTO): String {
        val otp = OTPBuilder.generateOTP()
        val key = "${RedisKey.JOIN_OTP.prefix}:${requestBody.email}"
        val dto = JoinTokenDTO(otp, requestBody.email, requestBody.username, requestBody.nickname)
        redisTemplate.opsForValue().set(key, dto, RedisKey.JOIN_OTP.ttl)
        return otp
    }
}