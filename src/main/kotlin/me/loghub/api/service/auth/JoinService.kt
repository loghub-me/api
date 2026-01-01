package me.loghub.api.service.auth

import jakarta.transaction.Transactional
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.auth.SessionDTO
import me.loghub.api.dto.auth.join.JoinConfirmDTO
import me.loghub.api.dto.auth.join.JoinInfoDTO
import me.loghub.api.dto.auth.join.JoinRequestDTO
import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.task.avatar.AvatarGenerateRequest
import me.loghub.api.dto.task.mail.JoinMailSendRequest
import me.loghub.api.exception.auth.BadOTPException
import me.loghub.api.lib.redis.key.auth.JoinOTPRedisKey
import me.loghub.api.lib.validation.isReservedUsername
import me.loghub.api.proxy.TaskAPIProxy
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.token.TokenService
import me.loghub.api.service.common.MailService
import me.loghub.api.util.OTPBuilder
import me.loghub.api.util.checkConflict
import me.loghub.api.util.checkField
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class JoinService(
    private val redisTemplate: RedisTemplate<String, JoinInfoDTO>,
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val mailService: MailService,
    private val taskAPIProxy: TaskAPIProxy,
) {
    @Transactional
    fun requestJoin(requestBody: JoinRequestDTO) {
        checkJoinable(requestBody)

        val otp = issueOTP(requestBody)
        val mail = JoinMailSendRequest(to = requestBody.email, otp = otp)
        mailService.sendMailAsync(mail)
    }

    @Transactional
    fun confirmJoin(requestBody: JoinConfirmDTO): Pair<TokenDTO, SessionDTO> {
        val redisKey = JoinOTPRedisKey(requestBody.email)
        val info = redisTemplate.opsForValue().get(redisKey)
            ?: throw BadOTPException(ResponseMessage.Auth.INVALID_OTP)

        when {
            requestBody.otp != info.otp -> throw BadOTPException(ResponseMessage.Auth.INVALID_OTP)
            else -> redisTemplate.delete(redisKey)
        }

        val joinedUser = userRepository.save(info.toUserEntity())
        taskAPIProxy.generateAvatar(AvatarGenerateRequest(joinedUser.id!!))
        return Pair(tokenService.generateToken(joinedUser), SessionDTO(joinedUser))
    }

    private fun checkJoinable(requestBody: JoinRequestDTO) {
        val (email, username) = requestBody
        checkField(
            JoinRequestDTO::username.name,
            !username.isReservedUsername(),
        ) { ResponseMessage.User.USERNAME_NOT_ALLOWED }
        checkConflict(
            JoinRequestDTO::email.name,
            userRepository.existsByEmail(email),
        ) { ResponseMessage.User.EMAIL_ALREADY_EXISTS }
        checkConflict(
            JoinRequestDTO::username.name,
            userRepository.existsByUsernameIgnoreCase(username),
        ) { ResponseMessage.User.USERNAME_ALREADY_EXISTS }
    }

    private fun issueOTP(requestBody: JoinRequestDTO): String {
        val otp = OTPBuilder.generateOTP()
        val info = JoinInfoDTO(otp, requestBody.email, requestBody.username, requestBody.nickname)
        val redisKey = JoinOTPRedisKey(requestBody.email)
        redisTemplate.opsForValue().set(redisKey, info, JoinOTPRedisKey.TTL)
        return otp
    }
}