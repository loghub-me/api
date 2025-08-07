package me.loghub.api.handler.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.loghub.api.constant.http.HttpContentType
import me.loghub.api.constant.http.HttpCookie
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.service.auth.token.RefreshTokenService
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Component

@Component
class CustomLogoutHandler(
    private val objectMapper: ObjectMapper,
    private val refreshTokenService: RefreshTokenService,
) : LogoutHandler {
    override fun logout(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?,
    ) {
        if (request == null || response == null) {
            return
        }

        for (cookie in request.cookies) {
            if (cookie.name == HttpCookie.RefreshToken.NAME) {
                refreshTokenService.revokeToken(cookie.value)
                cookie.apply {
                    value = ""
                    maxAge = 0
                    path = "/"
                }
                response.addCookie(cookie)
                break
            }
        }
        SecurityContextHolder.clearContext()

        val responseBody = MessageResponseBody(
            message = ResponseMessage.Logout.SUCCESS,
            status = HttpStatus.OK,
        )

        response.status = HttpServletResponse.SC_OK
        response.contentType = "${HttpContentType.APPLICATION_JSON};${HttpContentType.CHARSET_UTF_8}"
        response.writer.write(objectMapper.writeValueAsString(responseBody))
        response.writer.flush()
    }
}