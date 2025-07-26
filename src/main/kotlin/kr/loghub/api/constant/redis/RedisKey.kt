package kr.loghub.api.constant.redis

object RedisKey {
    const val JOIN_OTP = "join_otp"
    const val LOGIN_OTP = "login_otp"
    const val REFRESH_TOKEN = "refresh_token"
    const val MARKDOWN = "markdown"

    object Article {
        const val TRENDING_SCORE = "articles:trending_score"
    }

    object Series {
        const val TRENDING_SCORE = "series:trending_score"
    }

    object Question {
        const val TRENDING_SCORE = "questions:trending_score"

        object Answer {
            const val GENERATE_COOLDOWN = "questions:answer:generate_cooldown"
        }
    }
}