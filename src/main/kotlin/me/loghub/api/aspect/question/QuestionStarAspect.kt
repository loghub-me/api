package me.loghub.api.aspect.question

import me.loghub.api.lib.redis.key.question.QuestionTrendingScoreRedisKey
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class QuestionStarAspect(private val redisTemplate: RedisTemplate<String, String>) {
    private object TrendingScoreDelta {
        const val STAR = 3.toDouble()
    }

    private val trendingScoreKey = QuestionTrendingScoreRedisKey()
    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning("execution(* me.loghub.api.service.question.QuestionStarService.addStar(..)) && args(questionId, ..)")
    fun afterAddStar(questionId: Long) {
        updateTrendingScoreAfterAddStar(questionId)
    }

    @AfterReturning("execution(* me.loghub.api.service.question.QuestionStarService.deleteStar(..)) && args(questionId, ..)")
    fun afterDeleteStar(questionId: Long) {
        updateTrendingScoreAfterDeleteStar(questionId)
    }

    private fun updateTrendingScoreAfterAddStar(questionId: Long) =
        zSetOps.incrementScore(trendingScoreKey, questionId.toString(), TrendingScoreDelta.STAR)

    private fun updateTrendingScoreAfterDeleteStar(questionId: Long) =
        zSetOps.incrementScore(trendingScoreKey, questionId.toString(), -TrendingScoreDelta.STAR)
}