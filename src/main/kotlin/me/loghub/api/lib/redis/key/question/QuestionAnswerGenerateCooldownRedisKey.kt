package me.loghub.api.lib.redis.key.question

import me.loghub.api.lib.redis.key.RedisKey
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

object QuestionAnswerGenerateCooldownRedisKey : RedisKey {
    val TTL = 10.minutes.toJavaDuration()
    operator fun invoke(questionId: Long) = "questions:$questionId:answer:generate_cooldown"
}