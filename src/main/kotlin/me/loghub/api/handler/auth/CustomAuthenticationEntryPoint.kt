package me.loghub.api.handler.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.loghub.api.constant.http.HttpContentType
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.MessageResponseBody
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val responseBody = MessageResponseBody(
            message = ResponseMessage.Auth.UNAUTHORIZED,
            status = HttpStatus.UNAUTHORIZED,
        )

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "${HttpContentType.APPLICATION_JSON};${HttpContentType.CHARSET_UTF_8}"
        response.writer.write(objectMapper.writeValueAsString(responseBody))
        response.writer.flush()
    }
}