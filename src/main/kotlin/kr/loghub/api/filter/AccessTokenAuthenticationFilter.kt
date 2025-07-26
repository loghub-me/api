package kr.loghub.api.filter

import com.auth0.jwt.exceptions.JWTVerificationException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.loghub.api.service.auth.token.AccessTokenService
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class AccessTokenAuthenticationFilter(private val accessTokenService: AccessTokenService) : OncePerRequestFilter() {
    private companion object {
        private const val TOKEN_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val accessToken = extractAccessToken(request)
        if (accessToken == null) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val authentication = accessTokenService.generateAuthentication(accessToken)
            SecurityContextHolder.getContext().authentication = authentication
        } catch (_: JWTVerificationException) {
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }

    private fun extractAccessToken(request: HttpServletRequest) =
        request.getHeader(HttpHeaders.AUTHORIZATION)
            ?.takeIf { it.startsWith(TOKEN_PREFIX) }
            ?.substringAfter(TOKEN_PREFIX)
}