package kr.loghub.api.service.auth

import jakarta.transaction.Transactional
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.auth.LoginConfirmDTO
import kr.loghub.api.dto.auth.LoginRequestDTO
import kr.loghub.api.dto.auth.token.TokenDTO
import kr.loghub.api.dto.mail.LoginOTPMailDTO
import kr.loghub.api.exception.entity.EntityExistsFieldException
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.repository.auth.LoginOTPRepository
import kr.loghub.api.repository.user.UserRepository
import kr.loghub.api.service.auth.token.TokenService
import kr.loghub.api.worker.MailSendWorker
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service

@Service
class LoginService(
    private val loginOTPRepository: LoginOTPRepository,
    private val userRepository: UserRepository,
    private val mailSendWorker: MailSendWorker,
    private val tokenService: TokenService,
) {
    @Transactional
    fun requestLogin(requestBody: LoginRequestDTO) {
        validateLoginable(requestBody.email)?.let { throw it }

        val loginOTP = loginOTPRepository.save(requestBody.toEntity()).otp
        val mailDTO = LoginOTPMailDTO(to = requestBody.email, token = loginOTP)
        mailSendWorker.addToQueue(mailDTO)
    }

    @Transactional
    fun confirmLogin(requestBody: LoginConfirmDTO): TokenDTO {
        val loginOTP = loginOTPRepository.findByOtp(requestBody.otp)
        when {
            loginOTP == null -> throw BadCredentialsException(ResponseMessage.INVALID_OTP)
            loginOTP.email != requestBody.email -> throw BadCredentialsException(ResponseMessage.INVALID_OTP)
            else -> loginOTPRepository.delete(loginOTP)
        }

        val user = userRepository.findByEmail(requestBody.email)
            ?: throw EntityNotFoundException("email", ResponseMessage.USER_NOT_FOUND)
        return tokenService.generateToken(user)
    }

    private fun validateLoginable(email: String) = when {
        !userRepository.existsByEmail(email) ->
            EntityExistsFieldException("email", ResponseMessage.USER_EMAIL_ALREADY_EXISTS)

        else -> null
    }
}