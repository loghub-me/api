package kr.loghub.api.filter

import com.auth0.jwt.exceptions.TokenExpiredException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.loghub.api.constant.CustomHttpHeader
import kr.loghub.api.constant.HttpContentType
import kr.loghub.api.constant.ResponseMessage
import kr.loghub.api.dto.response.MessageResponseBody
import kr.loghub.api.service.auth.AccessTokenService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class TokenValidationFilter(
    private val accessTokenService: AccessTokenService,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {
    companion object {
        const val TOKEN_PREFIX = "Bearer "
        const val CODE_EXPIRED_TOKEN = "100"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val accessToken = extractAccessToken(request)

            val authentication = accessTokenService.generateAuthentication(accessToken)
            SecurityContextHolder.getContext().authentication = authentication
        } catch (e: TokenExpiredException) {
            val responseBody = MessageResponseBody(
                status = HttpStatus.UNAUTHORIZED,
                message = e.message ?: ResponseMessage.EXPIRED_TOKEN
            )

            response.setHeader(CustomHttpHeader.X_ERROR_CODE, CODE_EXPIRED_TOKEN)
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "${HttpContentType.APPLICATION_JSON};${HttpContentType.CHARSET_UTF_8}"
            response.writer.write(objectMapper.writeValueAsString(responseBody))
            response.writer.flush()
            SecurityContextHolder.clearContext()
        } catch (_: Exception) {
            SecurityContextHolder.clearContext()
        } finally {
            filterChain.doFilter(request, response)
        }
    }

    fun extractAccessToken(request: HttpServletRequest) =
        request.getHeader(HttpHeaders.AUTHORIZATION)
            ?.takeIf { it.startsWith(TOKEN_PREFIX) }
            ?.substringAfter(TOKEN_PREFIX)
            ?: throw BadCredentialsException(ResponseMessage.INVALID_TOKEN)
}
