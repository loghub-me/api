package me.loghub.api.handler.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.loghub.api.service.auth.CustomOAuth2UserService
import me.loghub.api.service.auth.LoginService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CustomAuthenticationSuccessHandler(
    private val oAuth2UserService: CustomOAuth2UserService,
    private val loginService: LoginService,
    @Value("\${client.host}") private val clientHost: String
) : AuthenticationSuccessHandler {

    @Transactional
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val user = oAuth2UserService.findOrCreateUser(authentication)
        val otp = loginService.issueOTP(user.email)

        response.sendRedirect("$clientHost/login/confirm?email=${user.email}&otp=$otp")
    }
}