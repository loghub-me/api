package me.loghub.api.constant.http

import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

object HttpCookie {
    object RefreshToken {
        const val NAME = "refresh_token"
        const val DOMAIN = "loghub.me"
        val MAX_AGE = 30.days.toJavaDuration()
    }
}