package me.loghub.api.constant.redis

import me.loghub.api.lib.redis.RedisKeySpec
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

object RedisKeys {
    val JOIN_OTP = RedisKeySpec("join_otp:%s", 3.minutes.toJavaDuration())
    val LOGIN_OTP = RedisKeySpec("login_otp:%s", 3.minutes.toJavaDuration())
    val OAUTH2_JOIN_TOKEN = RedisKeySpec("oauth2_join_token:%s", 10.minutes.toJavaDuration())
    val REFRESH_TOKEN = RedisKeySpec("refresh_token:%s", 30.days.toJavaDuration())
    val MARKDOWN = RedisKeySpec("markdown:%s", 1.days.toJavaDuration())

    object User {
        val NOTIFICATIONS = RedisKeySpec("users:%s:notifications", 1.days.toJavaDuration())
    }

    object Article {
        val TRENDING_SCORE = RedisKeySpec("articles:trending_score", 6.hours.toJavaDuration())
    }

    object Series {
        val TRENDING_SCORE = RedisKeySpec("series:trending_score", 6.hours.toJavaDuration())
    }

    object Question {
        val TRENDING_SCORE = RedisKeySpec("questions:trending_score", 6.hours.toJavaDuration())

        object Answer {
            val GENERATING = RedisKeySpec("questions:%s:answer:generating", 5.minutes.toJavaDuration())
            val GENERATE_COOLDOWN = RedisKeySpec("questions:%s:answer:generate_cooldown", 10.minutes.toJavaDuration())
        }
    }
}