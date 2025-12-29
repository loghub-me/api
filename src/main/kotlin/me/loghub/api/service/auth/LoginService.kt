package me.loghub.api.service.auth

import jakarta.transaction.Transactional
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.auth.SessionDTO
import me.loghub.api.dto.auth.login.LoginConfirmDTO
import me.loghub.api.dto.auth.login.LoginRequestDTO
import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.task.mail.LoginMailSendRequest
import me.loghub.api.entity.user.User
import me.loghub.api.exception.auth.BadOTPException
import me.loghub.api.exception.entity.EntityNotFoundFieldException
import me.loghub.api.lib.redis.key.RedisKeys
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.token.TokenService
import me.loghub.api.service.common.MailService
import me.loghub.api.util.OTPBuilder
import me.loghub.api.util.checkExists
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val userRepository: UserRepository,
    private val tokenService: TokenService,
    private val mailService: MailService,
) {
    @Transactional
    fun requestLogin(requestBody: LoginRequestDTO) {
        checkExists(
            User::email.name,
            userRepository.existsByEmail(requestBody.email)
        ) { ResponseMessage.User.NOT_FOUND }

        val otp = issueOTP(requestBody.email)
        val mail = LoginMailSendRequest(to = requestBody.email, otp = otp)
        mailService.sendMailAsync(mail)
    }

    @Transactional
    fun confirmLogin(requestBody: LoginConfirmDTO): Pair<TokenDTO, SessionDTO> {
        val redisKey = RedisKeys.LOGIN_OTP(requestBody.email)
        val otp = redisTemplate.opsForValue().get(redisKey.key)
            ?: throw BadOTPException(ResponseMessage.Auth.INVALID_OTP)

        when {
            requestBody.otp != otp -> throw BadOTPException(ResponseMessage.Auth.INVALID_OTP)
            else -> redisTemplate.delete(redisKey.key)
        }

        val user = userRepository.findByEmail(requestBody.email)
            ?: throw EntityNotFoundFieldException(User::email.name, ResponseMessage.User.NOT_FOUND)
        return Pair(tokenService.generateToken(user), SessionDTO(user))
    }

    fun issueOTP(email: String): String {
        val otp = OTPBuilder.generateOTP()
        val redisKey = RedisKeys.LOGIN_OTP(email)
        redisTemplate.opsForValue().set(redisKey.key, otp, redisKey.ttl)
        return otp
    }
}