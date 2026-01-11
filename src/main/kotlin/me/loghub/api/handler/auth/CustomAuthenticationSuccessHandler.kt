package me.loghub.api.handler.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.loghub.api.config.ClientConfig
import me.loghub.api.entity.user.User
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.LoginService
import me.loghub.api.service.auth.OAuth2JoinService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationSuccessHandler(
    private val userRepository: UserRepository,
    private val oAuth2JoinService: OAuth2JoinService,
    private val loginService: LoginService,
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val email = authentication.name
        val principal = authentication.principal as DefaultOAuth2User
        val provider = User.Provider.valueOf(principal.attributes["provider"] as String)

        val existingUser = userRepository.findByEmail(email)

        // Join new user
        if (existingUser == null) {
            val token = oAuth2JoinService.issueToken(email, provider)
            response.sendRedirect("${ClientConfig.HOST}/join/confirm/social?email=${email}&token=${token}")
            return
        }

        // Login existing user
        if (existingUser.provider == provider) {
            val otp = loginService.issueOTP(email)
            response.sendRedirect("${ClientConfig.HOST}/login/confirm?email=${email}&otp=${otp}")
            return
        }

        // Social provider mismatch
        response.sendRedirect("${ClientConfig.HOST}/login/error?error=social_provider_mismatch")
    }
}