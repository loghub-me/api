package kr.loghub.api.service.question

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.question.answer.PostQuestionAnswerDTO
import kr.loghub.api.entity.question.Question
import kr.loghub.api.entity.question.QuestionAnswer
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.repository.question.QuestionAnswerRepository
import kr.loghub.api.repository.question.QuestionRepository
import kr.loghub.api.util.checkField
import kr.loghub.api.util.checkPermission
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionAnswerService(
    private val questionAnswerRepository: QuestionAnswerRepository,
    private val questionRepository: QuestionRepository,
) {
    @Transactional
    fun postAnswer(questionId: Long, requestBody: PostQuestionAnswerDTO, writer: User): QuestionAnswer {
        val question = questionRepository.findWithWriterById(questionId)
            ?: throw EntityNotFoundException(ResponseMessage.Question.NOT_FOUND)

        checkPermission(question.writer == writer) { ResponseMessage.Question.Answer.CANNOT_POST_SELF }

        val answer = requestBody.toEntity(question, writer)
        return questionAnswerRepository.save(answer)
    }

    @Transactional
    fun editAnswer(questionId: Long, answerId: Long, requestBody: PostQuestionAnswerDTO, writer: User): QuestionAnswer {
        val answer = findUpdatableAnswer(questionId, answerId, writer)
        answer.update(requestBody)
        return answer
    }

    @Transactional
    fun removeAnswer(questionId: Long, answerId: Long, writer: User) {
        val answer = findUpdatableAnswer(questionId, answerId, writer)
        questionAnswerRepository.delete(answer)
    }

    @Transactional
    fun acceptAnswer(questionId: Long, answerId: Long, writer: User): QuestionAnswer {
        val answer = findUpdatableAnswer(questionId, answerId, writer)
        answer.accept()
        answer.question.solved()
        return answer
    }

    private fun findUpdatableAnswer(questionId: Long, answerId: Long, writer: User): QuestionAnswer {
        val answer = questionAnswerRepository.findWithWriterByIdAndQuestionId(answerId, questionId)
            ?: throw EntityNotFoundException(ResponseMessage.Question.Answer.NOT_FOUND)
        val question = answer.question

        checkPermission(question.writer == writer) { ResponseMessage.Question.PERMISSION_DENIED }
        checkField(
            Question::status.name,
            question.status == Question.Status.OPEN
        ) { ResponseMessage.Question.STATUS_MUST_BE_OPEN }
        checkField(
            QuestionAnswer::accepted.name,
            !answer.accepted,
        ) { ResponseMessage.Question.Answer.ALREADY_ACCEPTED }

        return answer
    }
}