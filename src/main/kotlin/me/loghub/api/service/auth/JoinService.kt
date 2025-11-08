package me.loghub.api.service.auth

import jakarta.transaction.Transactional
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.constant.redis.RedisKeys
import me.loghub.api.dto.auth.join.JoinConfirmDTO
import me.loghub.api.dto.auth.join.JoinInfoDTO
import me.loghub.api.dto.auth.join.JoinRequestDTO
import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.task.avatar.AvatarGenerateRequest
import me.loghub.api.dto.task.mail.JoinMailSendRequest
import me.loghub.api.entity.user.User
import me.loghub.api.exception.auth.BadOTPException
import me.loghub.api.proxy.TaskAPIProxy
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.token.TokenService
import me.loghub.api.util.OTPBuilder
import me.loghub.api.util.checkConflict
import me.loghub.api.worker.MailSendWorker
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class JoinService(
    private val redisTemplate: RedisTemplate<String, JoinInfoDTO>,
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
        val redisKey = RedisKeys.JOIN_OTP(requestBody.email)
        val info = redisTemplate.opsForValue().get(redisKey.key)
            ?: throw BadOTPException(ResponseMessage.Auth.INVALID_OTP)

        when {
            requestBody.otp != info.otp -> throw BadOTPException(ResponseMessage.Auth.INVALID_OTP)
            else -> redisTemplate.delete(redisKey.key)
        }

        val joinedUser = userRepository.save(info.toUserEntity())
        taskAPIProxy.generateAvatar(AvatarGenerateRequest(joinedUser.id!!))
        return tokenService.generateToken(joinedUser)
    }

    private fun checkJoinable(email: String, username: String) {
        checkConflict(
            User::email.name,
            userRepository.existsByEmail(email),
        ) { ResponseMessage.User.EMAIL_ALREADY_EXISTS }
        checkConflict(
            User::username.name,
            userRepository.existsByUsername(username),
        ) { ResponseMessage.User.USERNAME_ALREADY_EXISTS }
    }

    private fun issueOTP(requestBody: JoinRequestDTO): String {
        val otp = OTPBuilder.generateOTP()
        val info = JoinInfoDTO(otp, requestBody.email, requestBody.username, requestBody.nickname)
        val redisKey = RedisKeys.JOIN_OTP(requestBody.email)
        redisTemplate.opsForValue().set(redisKey.key, info, redisKey.ttl)
        return otp
    }
}