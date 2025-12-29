package me.loghub.api.dto.auth

import me.loghub.api.config.ClientConfig
import me.loghub.api.entity.user.User
import org.springframework.boot.web.server.Cookie.SameSite
import org.springframework.http.ResponseCookie
import java.nio.charset.StandardCharsets
import kotlin.io.encoding.Base64
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

data class SessionDTO(
    val id: Long,
    val email: String,
    val username: String,
    val nickname: String,
    val role: User.Role,
) {
    constructor(user: User) : this(
        id = user.id!!,
        email = user.email,
        username = user.username,
        nickname = user.profile.nickname,
        role = user.role,
    )

    object Cookie {
        const val NAME = "session"
        const val VALUE_TEMPLATE = """{"id":%d,"email":"%s","username":"%s","nickname":"%s","role":"%s"}"""
        const val PATH = "/"
        val SAME_SITE = SameSite.NONE.name
        val MAX_AGE = 14.days.toJavaDuration()
    }

    private val json
        get() = Cookie.VALUE_TEMPLATE.format(id, email, username, nickname, role.name)
    private val value
        get() = Base64.encode(json.toByteArray(StandardCharsets.UTF_8))
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