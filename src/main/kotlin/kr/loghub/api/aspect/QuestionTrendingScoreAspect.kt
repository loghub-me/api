package kr.loghub.api.aspect

import kr.loghub.api.constant.redis.RedisKey
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class QuestionTrendingScoreAspect(private val redisTemplate: RedisTemplate<String, String>) {
    private object TrendingScoreDelta {
        const val ANSWER = 2.toDouble()
        const val STAR = 3.toDouble()
    }

    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning("execution(* kr.loghub.api.service.question.QuestionAnswerService.postAnswer(..)) && args(questionId, ..)")
    fun updateTrendingScoreAfterPostAnswer(questionId: Long) =
        zSetOps.incrementScore(RedisKey.Question.TRENDING_SCORE, questionId.toString(), TrendingScoreDelta.ANSWER)

    @AfterReturning("execution(* kr.loghub.api.service.question.QuestionAnswerService.removeAnswer(..)) && args(questionId, ..)")
    fun updateTrendingScoreAfterRemoveAnswer(questionId: Long) =
        zSetOps.incrementScore(RedisKey.Question.TRENDING_SCORE, questionId.toString(), -TrendingScoreDelta.ANSWER)

    @AfterReturning("execution(* kr.loghub.api.service.question.QuestionStarService.addStar(..)) && args(questionId, ..)")
    fun updateTrendingScoreAfterAddQuestionStar(questionId: Long) =
        zSetOps.incrementScore(RedisKey.Question.TRENDING_SCORE, questionId.toString(), TrendingScoreDelta.STAR)

    @AfterReturning("execution(* kr.loghub.api.service.question.QuestionStarService.removeStar(..)) && args(questionId, ..)")
    fun updateTrendingScoreAfterRemoveQuestionStar(questionId: Long) =
        zSetOps.incrementScore(RedisKey.Question.TRENDING_SCORE, questionId.toString(), -TrendingScoreDelta.STAR)
}