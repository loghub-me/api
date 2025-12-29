package me.loghub.api.dto.auth.token

import me.loghub.api.config.ClientConfig
import org.springframework.boot.web.server.Cookie.SameSite
import org.springframework.http.ResponseCookie
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

class RefreshToken(val value: String) {
    object Cookie {
        const val NAME = "refresh_token"
        const val PATH = "/"
        val SAME_SITE = SameSite.NONE.name
        val MAX_AGE = 14.days.toJavaDuration()
    }

    val cookie
        get() = ResponseCookie
            .from(Cookie.NAME, value)
            .domain(ClientConfig.DOMAIN)
            .httpOnly(true)
            .secure(true)
            .path(Cookie.PATH)
            .sameSite(Cookie.SAME_SITE)
            .maxAge(Cookie.MAX_AGE)
            .build()
            .toString()
}