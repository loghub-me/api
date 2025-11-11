package me.loghub.api.aspect.question

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.constant.redis.RedisKeys
import me.loghub.api.dto.notification.NotificationDTO
import me.loghub.api.entity.question.QuestionAnswer
import me.loghub.api.entity.user.User
import me.loghub.api.service.user.NotificationService
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class QuestionAnswerAspect(
    private val redisTemplate: RedisTemplate<String, String>,
    private val notificationService: NotificationService,
) {
    private object TrendingScoreDelta {
        const val ANSWER = 1.toDouble()
    }

    private companion object {
        val logger = KotlinLogging.logger { };
    }

    private val trendingScoreKey = RedisKeys.Question.TRENDING_SCORE()
    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionAnswerService.postAnswer(..))",
        returning = "postedAnswer"
    )
    fun afterPostAnswer(postedAnswer: QuestionAnswer) {
        val questionId = postedAnswer.question.id!!
        updateTrendingScoreAfterPostAnswer(questionId)
        sendNotificationsAfterPostAnswer(postedAnswer)
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
        zSetOps.incrementScore(trendingScoreKey.key, questionId.toString(), TrendingScoreDelta.ANSWER)

    private fun updateTrendingScoreAfterDeleteAnswer(questionId: Long) =
        zSetOps.incrementScore(trendingScoreKey.key, questionId.toString(), -TrendingScoreDelta.ANSWER)

    private fun sendNotificationsAfterPostAnswer(postedAnswer: QuestionAnswer) {
        val question = postedAnswer.question

        val notification = NotificationDTO(
            href = "/questions/${question.writerUsername}/${question.slug}",
            title = question.title,
            message = "@${postedAnswer.writer.username}님이 회원님의 질문에 답변을 남겼습니다.",
        )
        notificationService.addNotification(question.writer.id!!, notification)
    }

    private fun logAfterPostAnswer(postedAnswer: QuestionAnswer) =
        logger.info { "[QuestionAnswer] posted: { questionId=${postedAnswer.question.id}, answerId=${postedAnswer.id}, writerId=${postedAnswer.writer.id}, content=\"${postedAnswer.content}\" }" }

    private fun logAfterEditAnswer(editedAnswer: QuestionAnswer) =
        logger.info { "[QuestionAnswer] edited: { questionId=${editedAnswer.question.id}, answerId=${editedAnswer.id}, writerId=${editedAnswer.writer.id}, content=\"${editedAnswer.content}\" }" }

    private fun logAfterDeleteAnswer(questionId: Long, answerId: Long, writer: User) =
        logger.info { "[QuestionAnswer] deleted: { questionId=${questionId}, answerId=${answerId}, writerId=${writer.id} }" }

    private fun logAfterAcceptAnswer(deletedAnswer: QuestionAnswer) =
        logger.info { "[QuestionAnswer] accepted: { questionId=${deletedAnswer.question.id}, answerId=${deletedAnswer.id}, writerId=${deletedAnswer.writer.id} }" }
}