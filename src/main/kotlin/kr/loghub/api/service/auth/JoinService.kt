package kr.loghub.api.service.auth

import jakarta.transaction.Transactional
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.constant.redis.RedisKey
import kr.loghub.api.dto.auth.JoinConfirmDTO
import kr.loghub.api.dto.auth.JoinRequestDTO
import kr.loghub.api.dto.auth.token.JoinTokenDTO
import kr.loghub.api.dto.auth.token.TokenDTO
import kr.loghub.api.dto.task.mail.JoinOTPMailDTO
import kr.loghub.api.entity.user.User
import kr.loghub.api.repository.user.UserRepository
import kr.loghub.api.service.auth.token.TokenService
import kr.loghub.api.util.OTPBuilder
import kr.loghub.api.util.checkAlreadyExists
import kr.loghub.api.worker.MailSendWorker
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

@Service
class JoinService(
    private val redisTemplate: RedisTemplate<String, JoinTokenDTO>,
    private val userRepository: UserRepository,
    private val mailSendWorker: MailSendWorker,
    private val tokenService: TokenService,
) {
    companion object {
        private const val OTP_LENGTH = 6
        private val OTP_EXPIRE_MINUTES = 3.minutes.toJavaDuration()
    }

    @Transactional
    fun requestJoin(requestBody: JoinRequestDTO) {
        checkJoinable(requestBody.email, requestBody.username)

        val otp = issueOTP(requestBody)
        val mail = JoinOTPMailDTO(to = requestBody.email, otp = otp)
        mailSendWorker.addToQueue(mail)
    }

    @Transactional
    fun confirmJoin(requestBody: JoinConfirmDTO): TokenDTO {
        val otp = redisTemplate.opsForValue().get("${RedisKey.JOIN_OTP}:${requestBody.email}")
            ?: throw BadCredentialsException(ResponseMessage.Auth.INVALID_OTP)

        when {
            requestBody.otp != otp.otp -> throw BadCredentialsException(ResponseMessage.Auth.INVALID_OTP)
            else -> redisTemplate.delete("${RedisKey.JOIN_OTP}:${requestBody.email}")
        }

        val joinedUser = userRepository.save(otp.toUserEntity())
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
        val otp = OTPBuilder.generateOTP(OTP_LENGTH)
        val dto = JoinTokenDTO(otp, requestBody.email, requestBody.username, requestBody.nickname)
        redisTemplate.opsForValue().set("${RedisKey.JOIN_OTP}:${requestBody.email}", dto, OTP_EXPIRE_MINUTES)
        return otp
    }
}