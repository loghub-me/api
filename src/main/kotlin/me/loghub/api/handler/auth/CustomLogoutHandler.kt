package me.loghub.api.handler.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.loghub.api.constant.http.HttpContentType
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.auth.token.RefreshToken
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.service.auth.token.RefreshTokenService
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class CustomLogoutHandler(
    private val jsonMapper: JsonMapper,
    private val refreshTokenService: RefreshTokenService,
) : LogoutHandler {
    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?,
    ) {
        for (cookie in request.cookies) {
            if (cookie.name == RefreshToken.Cookie.NAME) {
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
        response.writer.write(jsonMapper.writeValueAsString(responseBody))
        response.writer.flush()
    }
}
