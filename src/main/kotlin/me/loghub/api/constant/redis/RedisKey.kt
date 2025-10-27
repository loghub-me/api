package me.loghub.api.constant.redis

import java.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

object RedisKey {
    val JOIN_OTP = ExpiringRedisKey("join_otp", 3.minutes.toJavaDuration())
    val LOGIN_OTP = ExpiringRedisKey("login_otp", 3.minutes.toJavaDuration())
    val OAUTH2_JOIN_TOKEN = ExpiringRedisKey("oauth2_join_token", 10.minutes.toJavaDuration())
    val REFRESH_TOKEN = ExpiringRedisKey("refresh_token", 30.days.toJavaDuration())
    val MARKDOWN = ExpiringRedisKey("markdown", 1.days.toJavaDuration())

    object Article {
        const val TRENDING_SCORE = "articles:trending_score"
    }

    object Series {
        const val TRENDING_SCORE = "series:trending_score"
    }

    object Question {
        const val TRENDING_SCORE = "questions:trending_score"

        object Answer {
            val GENERATING = ExpiringRedisKey("questions:answer:generating", 5.minutes.toJavaDuration())
            val GENERATE_COOLDOWN = ExpiringRedisKey("questions:answer:generate_cooldown", 10.minutes.toJavaDuration())
        }
    }

    data class ExpiringRedisKey(
        val prefix: String,
        val ttl: Duration,
    )
}