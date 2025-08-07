package me.loghub.api.dto.auth.token

import me.loghub.api.constant.http.HttpCookie
import org.springframework.boot.web.server.Cookie
import org.springframework.http.ResponseCookie
import java.util.*

data class TokenDTO(
    val accessToken: String,
    val refreshToken: UUID,
) {
    val authorization get() = "Bearer $accessToken"
    val cookie
        get() = ResponseCookie
            .from(HttpCookie.RefreshToken.NAME, refreshToken.toString())
            // TODO: Uncomment this when domain is set
//            .domain(HttpCookie.RefreshToken.DOMAIN)
            .httpOnly(true)
            .secure(true)
            .sameSite(Cookie.SameSite.NONE.name)
            .path("/")
            .maxAge(HttpCookie.RefreshToken.MAX_AGE)
            .build()
            .toString()
}