package kr.loghub.api.handler.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.loghub.api.constant.http.HttpContentType
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.response.MessageResponseBody
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler(private val objectMapper: ObjectMapper) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        val responseBody = MessageResponseBody(
            status = HttpStatus.FORBIDDEN,
            message = ResponseMessage.FORBIDDEN
        )

        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "${HttpContentType.APPLICATION_JSON};${HttpContentType.CHARSET_UTF_8}"
        response.writer.write(objectMapper.writeValueAsString(responseBody))
        response.writer.flush()
    }
}