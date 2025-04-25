package kr.loghub.api.service.auth

import jakarta.transaction.Transactional
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.auth.JoinConfirmDTO
import kr.loghub.api.dto.auth.JoinRequestDTO
import kr.loghub.api.dto.auth.token.TokenDTO
import kr.loghub.api.dto.mail.JoinOTPMailDTO
import kr.loghub.api.exception.entity.EntityExistsFieldException
import kr.loghub.api.repository.auth.JoinOTPRepository
import kr.loghub.api.repository.user.UserRepository
import kr.loghub.api.service.auth.token.TokenService
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
        validateJoinable(requestBody.email, requestBody.username)?.let { throw it }

        val joinOTP = joinOTPRepository.save(requestBody.toEntity()).otp
        val mailDTO = JoinOTPMailDTO(to = requestBody.email, token = joinOTP)
        mailSendWorker.addToQueue(mailDTO)
    }

    @Transactional
    fun confirmJoin(requestBody: JoinConfirmDTO): TokenDTO {
        val joinOTP = joinOTPRepository.findByOtp(requestBody.otp)
        when {
            joinOTP == null -> throw BadCredentialsException(ResponseMessage.INVALID_OTP)
            joinOTP.email != requestBody.email -> throw BadCredentialsException(ResponseMessage.INVALID_OTP)
            else -> joinOTPRepository.delete(joinOTP)
        }

        validateJoinable(joinOTP.email, joinOTP.username)?.let { throw it }
        val joinedUser = userRepository.save(joinOTP.toUserEntity())
        return tokenService.generateToken(joinedUser)
    }

    private fun validateJoinable(email: String, username: String) = when {
        userRepository.existsByEmail(email) ->
            EntityExistsFieldException("email", ResponseMessage.USER_EMAIL_ALREADY_EXISTS)

        userRepository.existsByUsername(username) ->
            EntityExistsFieldException("username", ResponseMessage.USER_EMAIL_ALREADY_EXISTS)

        else -> null
    }
}