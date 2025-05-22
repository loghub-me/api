package kr.loghub.api.handler.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.loghub.api.constant.http.HttpContentType
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.response.MessageResponseBody
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationFailureHandler(private val objectMapper: ObjectMapper) : AuthenticationFailureHandler {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val responseBody = MessageResponseBody(
            message = ResponseMessage.Auth.AUTHENTICATION_FAILED,
            status = HttpStatus.UNAUTHORIZED,
        )

        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "${HttpContentType.APPLICATION_JSON};${HttpContentType.CHARSET_UTF_8}"
        response.writer.write(objectMapper.writeValueAsString(responseBody))
        response.writer.flush()
    }
}