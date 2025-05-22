package kr.loghub.api.constant.http

import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

object HttpCookie {
    object RefreshToken {
        const val NAME = "refresh_token"
        const val DOMAIN = "loghub.kr"
        val MAX_AGE = 30.days.toJavaDuration()
    }
}