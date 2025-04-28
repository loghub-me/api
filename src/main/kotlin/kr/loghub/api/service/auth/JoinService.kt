package kr.loghub.api.service.auth

import jakarta.transaction.Transactional
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.auth.JoinConfirmDTO
import kr.loghub.api.dto.auth.JoinRequestDTO
import kr.loghub.api.dto.auth.token.TokenDTO
import kr.loghub.api.dto.mail.JoinOTPMailDTO
import kr.loghub.api.entity.user.User
import kr.loghub.api.repository.auth.JoinOTPRepository
import kr.loghub.api.repository.user.UserRepository
import kr.loghub.api.service.auth.token.TokenService
import kr.loghub.api.util.checkExists
import kr.loghub.api.worker.MailSendWorker
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service

@Service
class JoinService(
    private val joinOTPRepository: JoinOTPRepository,
    private val userRepository: UserRepository,
    private val mailSendWorker: MailSendWorker,
    private val tokenService: TokenService,
) {
    @Transactional
    fun requestJoin(requestBody: JoinRequestDTO) {
        checkJoinable(requestBody.email, requestBody.username)

        val joinOTP = joinOTPRepository.save(requestBody.toEntity())
        val mailDTO = JoinOTPMailDTO(to = requestBody.email, otp = joinOTP.otp)
        mailSendWorker.addToQueue(mailDTO)
    }

    @Transactional
    fun confirmJoin(requestBody: JoinConfirmDTO): TokenDTO {
        val joinOTP = joinOTPRepository.findByOtp(requestBody.otp)
        when {
            joinOTP == null -> throw BadCredentialsException(ResponseMessage.Auth.INVALID_OTP)
            joinOTP.email != requestBody.email -> throw BadCredentialsException(ResponseMessage.Auth.INVALID_OTP)
            else -> joinOTPRepository.delete(joinOTP)
        }

        checkJoinable(joinOTP.email, joinOTP.username)
        val joinedUser = userRepository.save(joinOTP.toUserEntity())
        return tokenService.generateToken(joinedUser)
    }

    private fun checkJoinable(email: String, username: String) {
        checkExists(
            User::username.name,
            userRepository.existsByEmail(email),
        ) { ResponseMessage.User.EMAIL_ALREADY_EXISTS }
        checkExists(
            User::username.name,
            userRepository.existsByUsername(username),
        ) { ResponseMessage.User.USERNAME_ALREADY_EXISTS }
    }
}