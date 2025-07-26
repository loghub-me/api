package kr.loghub.api.service.auth

import jakarta.transaction.Transactional
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.constant.redis.RedisKey
import kr.loghub.api.dto.auth.LoginConfirmDTO
import kr.loghub.api.dto.auth.LoginRequestDTO
import kr.loghub.api.dto.auth.token.TokenDTO
import kr.loghub.api.dto.task.mail.LoginMailSendRequest
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundFieldException
import kr.loghub.api.repository.user.UserRepository
import kr.loghub.api.service.auth.token.TokenService
import kr.loghub.api.util.OTPBuilder
import kr.loghub.api.util.checkExists
import kr.loghub.api.worker.MailSendWorker
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val userRepository: UserRepository,
    private val mailSendWorker: MailSendWorker,
    private val tokenService: TokenService,
) {
    @Transactional
    fun requestLogin(requestBody: LoginRequestDTO) {
        checkExists(
            User::email.name,
            userRepository.existsByEmail(requestBody.email)
        ) { ResponseMessage.User.NOT_FOUND }

        val otp = issueOTP(requestBody.email)
        val mail = LoginMailSendRequest(to = requestBody.email, otp = otp)
        mailSendWorker.addToQueue(mail)
    }

    @Transactional
    fun confirmLogin(requestBody: LoginConfirmDTO): TokenDTO {
        val otp = redisTemplate.opsForValue().get("${RedisKey.LOGIN_OTP.prefix}:${requestBody.email}")
            ?: throw BadCredentialsException(ResponseMessage.Auth.INVALID_OTP)

        when {
            requestBody.otp != otp -> throw BadCredentialsException(ResponseMessage.Auth.INVALID_OTP)
            else -> redisTemplate.delete("${RedisKey.LOGIN_OTP.prefix}:${requestBody.email}")
        }

        val user = userRepository.findByEmail(requestBody.email)
            ?: throw EntityNotFoundFieldException(User::email.name, ResponseMessage.User.NOT_FOUND)
        return tokenService.generateToken(user)
    }

    fun issueOTP(email: String): String {
        val otp = OTPBuilder.generateOTP()
        val key = "${RedisKey.LOGIN_OTP.prefix}:${email}"
        redisTemplate.opsForValue().set(key, otp, RedisKey.LOGIN_OTP.ttl)
        return otp
    }
}