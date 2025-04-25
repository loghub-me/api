package kr.loghub.api.constant.http

object HttpCookie {
    object RefreshToken {
        const val NAME = "refresh_token"
        const val DOMAIN = "loghub.kr"
        const val MAX_AGE = 60 * 60 * 24 * 30L // 30 days
    }
}