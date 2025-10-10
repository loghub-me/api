package me.loghub.api.aspect.logging.question

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.question.Question
import me.loghub.api.entity.user.User
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class QuestionLoggingAspect {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionService.postQuestion(..)) && args(.., writer)",
        returning = "question"
    )
    fun afterPostQuestion(writer: User, question: Question) =
        logger.info { "[Question] posted: { questionId=${question.id}, writerId=${writer.id}, title=\"${question.title}\" }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionService.editQuestion(..)) && args(.., writer)",
        returning = "question"
    )
    fun afterEditQuestion(writer: User, question: Question) =
        logger.info { "[Question] edited: { questionId=${question.id}, writerId=${writer.id}, title=\"${question.title}\" }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionService.deleteQuestion(..)) && args(questionId, writer)"
    )
    fun afterDeleteQuestion(questionId: Long, writer: User) =
        logger.info { "[Question] deleted: { questionId=${questionId}, writerId=${writer.id} }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionService.closeQuestion(..)) && args(questionId, writer)"
    )
    fun afterCloseQuestion(questionId: Long, writer: User) =
        logger.info { "[Question] closed: { questionId=${questionId}, writerId=${writer.id} }" }
}