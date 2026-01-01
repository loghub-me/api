package me.loghub.api.lib.redis.key.question

import me.loghub.api.lib.redis.key.RedisKey
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

object QuestionAnswerDraftRedisKey : RedisKey {
    val TTL = 7.days.toJavaDuration()
    operator fun invoke(questionId: Long, answerId: Long) = "questions:$questionId:answer:$answerId:draft"
}