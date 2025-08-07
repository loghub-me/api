package me.loghub.api.constant.http

import org.springframework.boot.web.server.Cookie
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

object HttpCookie {
    object RefreshToken {
        const val NAME = "refresh_token"
        const val DOMAIN = "loghub.me"
        const val PATH = "/"
        val SAME_SITE = Cookie.SameSite.NONE.name
        val MAX_AGE = 30.days.toJavaDuration()
    }
}