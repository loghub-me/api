package me.loghub.api.aspect.question

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.question.QuestionAnswer
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserActivity
import me.loghub.api.lib.redis.key.question.QuestionTrendingScoreRedisKey
import me.loghub.api.repository.user.UserActivityRepository
import me.loghub.api.repository.user.saveActivityIgnoreConflict
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class QuestionAnswerAspect(
    private val redisTemplate: RedisTemplate<String, String>,
    private val userActivityRepository: UserActivityRepository,
) {
    private object TrendingScoreDelta {
        const val ANSWER = 1.toDouble()
    }

    private companion object {
        val logger = KotlinLogging.logger { };
    }

    private val trendingScoreKey = QuestionTrendingScoreRedisKey()
    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionAnswerService.postAnswer(..))",
        returning = "postedAnswer"
    )
    fun afterPostAnswer(postedAnswer: QuestionAnswer) {
        val questionId = postedAnswer.question.id!!
        updateTrendingScoreAfterPostAnswer(questionId)
        addUserActivityAfterPostAnswer(postedAnswer)
        logAfterPostAnswer(postedAnswer)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionAnswerService.editAnswer(..))",
        returning = "editedAnswer"
    )
    fun afterEditAnswer(editedAnswer: QuestionAnswer) {
        logAfterEditAnswer(editedAnswer)
    }

    @AfterReturning("execution(* me.loghub.api.service.question.QuestionAnswerService.deleteAnswer(..)) && args(questionId, answerId, writer)")
    fun afterDeleteAnswer(questionId: Long, answerId: Long, writer: User) {
        updateTrendingScoreAfterDeleteAnswer(questionId)
        logAfterDeleteAnswer(questionId, answerId, writer)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionAnswerService.acceptAnswer(..))",
        returning = "acceptedAnswer"
    )
    fun afterAcceptAnswer(acceptedAnswer: QuestionAnswer) {
        logAfterAcceptAnswer(acceptedAnswer)
    }

    private fun updateTrendingScoreAfterPostAnswer(questionId: Long) =
        zSetOps.incrementScore(trendingScoreKey, questionId.toString(), TrendingScoreDelta.ANSWER)

    private fun updateTrendingScoreAfterDeleteAnswer(questionId: Long) =
        zSetOps.incrementScore(trendingScoreKey, questionId.toString(), -TrendingScoreDelta.ANSWER)

    private fun addUserActivityAfterPostAnswer(postedAnswer: QuestionAnswer) {
        userActivityRepository.saveActivityIgnoreConflict(
            UserActivity(
                action = UserActivity.Action.POST_QUESTION_ANSWER,
                createdAt = postedAnswer.createdAt,
                createdDate = postedAnswer.createdAt.toLocalDate(),
                user = postedAnswer.writer,
                question = postedAnswer.question,
                questionAnswer = postedAnswer
            )
        )
    }

    private fun logAfterPostAnswer(answer: QuestionAnswer) =
        logger.info { "[QuestionAnswer] posted: { questionId=${answer.question.id}, answerId=${answer.id}, writerId=${answer.writer.id}, content=\"${answer.content}\" }" }

    private fun logAfterEditAnswer(answer: QuestionAnswer) =
        logger.info { "[QuestionAnswer] edited: { questionId=${answer.question.id}, answerId=${answer.id}, writerId=${answer.writer.id}, content=\"${answer.content}\" }" }

    private fun logAfterDeleteAnswer(questionId: Long, answerId: Long, writer: User) =
        logger.info { "[QuestionAnswer] deleted: { questionId=${questionId}, answerId=${answerId}, writerId=${writer.id} }" }

    private fun logAfterAcceptAnswer(answer: QuestionAnswer) =
        logger.info { "[QuestionAnswer] accepted: { questionId=${answer.question.id}, answerId=${answer.id}, writerId=${answer.writer.id} }" }
}
