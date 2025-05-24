package kr.loghub.api.service.question.answer

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.question.answer.PostAnswerDTO
import kr.loghub.api.entity.question.Answer
import kr.loghub.api.entity.question.Question
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.repository.quesiton.QuestionRepository
import kr.loghub.api.repository.quesiton.answer.AnswerRepository
import kr.loghub.api.util.checkField
import kr.loghub.api.util.checkPermission
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AnswerService(
    private val answerRepository: AnswerRepository,
    private val questionRepository: QuestionRepository,
) {
    @Transactional
    fun postAnswer(questionId: Long, requestBody: PostAnswerDTO, writer: User): Answer {
        val question = questionRepository.findWithWriterById(questionId)
            ?.also { checkPermission(it.writer == writer) { ResponseMessage.Answer.CANNOT_POST_SELF } }
            ?: throw EntityNotFoundException(ResponseMessage.Question.NOT_FOUND)
        val answer = requestBody.toEntity(question, writer)
        return answerRepository.save(answer)
    }

    @Transactional
    fun acceptAnswer(questionId: Long, answerId: Long, writer: User): Answer {
        val question = questionRepository.findWithWriterById(questionId)
            ?: throw EntityNotFoundException(ResponseMessage.Question.NOT_FOUND)

        checkPermission(question.writer == writer) { ResponseMessage.Question.PERMISSION_DENIED }
        checkField("status", question.status == Question.Status.OPEN) { ResponseMessage.Question.ALREADY_CLOSED }

        val answer = answerRepository.findWithWriterByIdAndQuestionId(answerId, questionId)
            ?: throw EntityNotFoundException(ResponseMessage.Answer.NOT_FOUND)
        answer.accept()
        return answer
    }

    @Transactional
    fun editAnswer(questionId: Long, answerId: Long, requestBody: PostAnswerDTO, writer: User): Answer {
        val answer = answerRepository.findWithWriterByIdAndQuestionId(answerId, questionId)
            ?.also { checkPermission(it.writer == writer) { ResponseMessage.Question.PERMISSION_DENIED } }
            ?: throw EntityNotFoundException(ResponseMessage.Answer.NOT_FOUND)

        answer.update(requestBody)
        return answer
    }

    @Transactional
    fun removeAnswer(questionId: Long, answerId: Long, writer: User) {
        val answer = answerRepository.findWithWriterByIdAndQuestionId(answerId, questionId)
            ?.also { checkPermission(it.writer == writer) { ResponseMessage.Question.PERMISSION_DENIED } }
            ?: throw EntityNotFoundException(ResponseMessage.Answer.NOT_FOUND)

        answerRepository.delete(answer)
    }
}