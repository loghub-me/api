package me.loghub.api.aspect.logging.question

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.question.QuestionAnswer
import me.loghub.api.entity.user.User
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class QuestionAnswerLoggingAspect {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionAnswerService.postAnswer(..)) && args(questionId, .., writer)",
        returning = "answer"
    )
    fun afterPostAnswer(questionId: Long, writer: User, answer: QuestionAnswer) =
        logger.info { "[QuestionAnswer] posted: { questionId=${questionId}, answerId=${answer.id}, writerId=${writer.id}, title=\"${answer.title}\" }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionAnswerService.editAnswer(..)) && args(questionId, .., writer)",
        returning = "answer"
    )
    fun afterEditAnswer(questionId: Long, writer: User, answer: QuestionAnswer) =
        logger.info { "[QuestionAnswer] edited: { questionId=${questionId}, answerId=${answer.id}, writerId=${writer.id}, title=\"${answer.title}\" }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionAnswerService.deleteAnswer(..)) && args(questionId, answerId, writer)",
    )
    fun afterDeleteAnswer(questionId: Long, answerId: Long, writer: User) =
        logger.info { "[QuestionAnswer] deleted: { questionId=${questionId}, answerId=${answerId}, writerId=${writer.id} }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.question.QuestionAnswerService.acceptAnswer(..)) && args(questionId, answerId, writer)",
    )
    fun acceptAnswer(questionId: Long, answerId: Long, writer: User) =
        logger.info { "[QuestionAnswer] accepted: { questionId=${questionId}, answerId=${answerId}, writerId=${writer.id} }" }
}