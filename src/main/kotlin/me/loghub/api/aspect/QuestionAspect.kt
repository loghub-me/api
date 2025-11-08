package me.loghub.api.aspect

import me.loghub.api.constant.redis.RedisKeys
import me.loghub.api.entity.question.Question
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserActivity
import me.loghub.api.repository.user.UserActivityRepository
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class QuestionAspect(
    private val userActivityRepository: UserActivityRepository,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private object TrendingScoreDelta {
        const val ANSWER = 2.toDouble()
        const val STAR = 3.toDouble()
    }

    private val trendingScoreKey = RedisKeys.Question.TRENDING_SCORE()
    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionService.postQuestion(..)) && args(.., writer)",
        returning = "question"
    )
    fun afterPostQuestion(writer: User, question: Question) {
        val activity = UserActivity(
            action = UserActivity.Action.POST_QUESTION,
            user = writer,
            question = question
        )
        userActivityRepository.save(activity)
    }

    @AfterReturning("execution(* me.loghub.api.service.question.QuestionAnswerService.postAnswer(..)) && args(questionId, ..)")
    fun afterPostAnswer(questionId: Long) =
        zSetOps.incrementScore(trendingScoreKey.key, questionId.toString(), TrendingScoreDelta.ANSWER)

    @AfterReturning("execution(* me.loghub.api.service.question.QuestionAnswerService.deleteAnswer(..)) && args(questionId, ..)")
    fun afterDeleteAnswer(questionId: Long) =
        zSetOps.incrementScore(trendingScoreKey.key, questionId.toString(), -TrendingScoreDelta.ANSWER)

    @AfterReturning("execution(* me.loghub.api.service.question.QuestionStarService.addStar(..)) && args(questionId, ..)")
    fun afterAddStar(questionId: Long) =
        zSetOps.incrementScore(trendingScoreKey.key, questionId.toString(), TrendingScoreDelta.STAR)

    @AfterReturning("execution(* me.loghub.api.service.question.QuestionStarService.deleteStar(..)) && args(questionId, ..)")
    fun afterDeleteStar(questionId: Long) =
        zSetOps.incrementScore(trendingScoreKey.key, questionId.toString(), -TrendingScoreDelta.STAR)
}