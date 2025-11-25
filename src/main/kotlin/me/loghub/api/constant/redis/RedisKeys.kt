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
    val REFRESH_TOKEN = RedisKeySpec("refresh_token:%s", 14.days.toJavaDuration())
    val MARKDOWN = RedisKeySpec("markdown:%s", 1.days.toJavaDuration())
    val NOTIFICATIONS = RedisKeySpec("notifications:%s", 1.days.toJavaDuration())

    object Article {
        val DRAFT = RedisKeySpec("articles:%s:draft", 7.days.toJavaDuration())
        val TRENDING_SCORE = RedisKeySpec("articles:trending_score", 6.hours.toJavaDuration())
    }

    object Series {
        val TRENDING_SCORE = RedisKeySpec("series:trending_score", 6.hours.toJavaDuration())

        object Chapter {
            val DRAFT = RedisKeySpec("series:%s:chapter:%s:draft", 7.days.toJavaDuration())
        }
    }

    object Question {
        val DRAFT = RedisKeySpec("questions:%s:draft", 7.days.toJavaDuration())
        val TRENDING_SCORE = RedisKeySpec("questions:trending_score", 6.hours.toJavaDuration())

        object Answer {
            val DRAFT = RedisKeySpec("questions:%s:answer:%s:draft", 7.days.toJavaDuration())
            val GENERATING = RedisKeySpec("questions:%s:answer:generating", 5.minutes.toJavaDuration())
            val GENERATE_COOLDOWN = RedisKeySpec("questions:%s:answer:generate_cooldown", 10.minutes.toJavaDuration())
        }
    }
}