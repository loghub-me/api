package me.loghub.api.aspect.question

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.question.Question
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserActivity
import me.loghub.api.repository.user.UserActivityRepository
import me.loghub.api.repository.user.saveActivityIgnoreConflict
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class QuestionAspect(
    private val userActivityRepository: UserActivityRepository,
) {
    private companion object {
        val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionService.postQuestion(..))",
        returning = "postedQuestion"
    )
    fun afterPostQuestion(postedQuestion: Question) {
        addUserActivityAfterPostQuestion(postedQuestion)
        logAfterPostQuestion(postedQuestion)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionService.editQuestion(..))",
        returning = "editedQuestion"
    )
    fun afterEditQuestion(editedQuestion: Question) {
        logAfterEditQuestion(editedQuestion)
    }

    @AfterReturning("execution(* me.loghub.api.service.question.QuestionService.deleteQuestion(..)) && args(questionId, writer))")
    fun afterDeleteQuestion(questionId: Long, writer: User) {
        logAfterDeleteQuestion(questionId, writer)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionService.closeQuestion(..))",
        returning = "closedQuestion"
    )
    fun afterCloseQuestion(closedQuestion: Question) {
        logAfterCloseQuestion(closedQuestion)
    }

    private fun addUserActivityAfterPostQuestion(postedQuestion: Question) {
        userActivityRepository.saveActivityIgnoreConflict(
            UserActivity(
                action = UserActivity.Action.POST_QUESTION,
                createdAt = postedQuestion.createdAt,
                createdDate = postedQuestion.createdAt.toLocalDate(),
                user = postedQuestion.writer,
                question = postedQuestion
            )
        )
    }

    private fun logAfterPostQuestion(question: Question) =
        logger.info { "[Question] posted: { questionId=${question.id}, writerId=${question.writer.id}, title=\"${question.title}\" }" }

    private fun logAfterEditQuestion(question: Question) =
        logger.info { "[Question] edited: { questionId=${question.id}, writerId=${question.writer.id}, title=\"${question.title}\" }" }

    private fun logAfterDeleteQuestion(questionId: Long, writer: User) =
        logger.info { "[Question] deleted: { questionId=${questionId}, writerId=${writer.id} }" }

    private fun logAfterCloseQuestion(question: Question) =
        logger.info { "[Question] closed: { questionId=${question.id}, writerId=${question.writer.id} }" }
}