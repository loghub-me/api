package me.loghub.api.aspect.question

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.question.QuestionAnswer
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserActivity
import me.loghub.api.repository.user.UserActivityRepository
import me.loghub.api.repository.user.saveActivityIgnoreConflict
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class QuestionAnswerAspect(
    private val userActivityRepository: UserActivityRepository,
) {
    private companion object {
        val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionAnswerService.postAnswer(..))",
        returning = "postedAnswer"
    )
    fun afterPostAnswer(postedAnswer: QuestionAnswer) {
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
        logAfterDeleteAnswer(questionId, answerId, writer)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionAnswerService.acceptAnswer(..))",
        returning = "acceptedAnswer"
    )
    fun afterAcceptAnswer(acceptedAnswer: QuestionAnswer) {
        logAfterAcceptAnswer(acceptedAnswer)
    }

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
