package me.loghub.api.lib.redis.key.question

import me.loghub.api.lib.redis.key.RedisKey
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

object QuestionAnswerGeneratingRedisKey : RedisKey {
    val TTL = 5.minutes.toJavaDuration()
    operator fun invoke(questionId: Long) = "questions:$questionId:answer:generating"
}