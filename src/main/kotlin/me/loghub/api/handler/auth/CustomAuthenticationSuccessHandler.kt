package me.loghub.api.handler.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.loghub.api.config.ClientConfig
import me.loghub.api.service.auth.LoginService
import me.loghub.api.service.auth.OAuth2JoinService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CustomAuthenticationSuccessHandler(
    private val oauth2JoinService: OAuth2JoinService,
    private val loginService: LoginService,
) : AuthenticationSuccessHandler {

    @Transactional
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val email = authentication.name
        if (oauth2JoinService.existsByEmail(email)) {
            val otp = loginService.issueOTP(email)
            response.sendRedirect("${ClientConfig.HOST}/login/confirm?email=${email}&otp=${otp}")
            return
        }

        val oauth2User = authentication.principal as DefaultOAuth2User
        val token = oauth2JoinService.issueToken(oauth2User)
        response.sendRedirect("${ClientConfig.HOST}/join/confirm/social?email=${email}&token=${token}")
    }
}