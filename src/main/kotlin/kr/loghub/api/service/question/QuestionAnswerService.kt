package kr.loghub.api.service.question

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.constant.redis.RedisKey
import kr.loghub.api.dto.question.answer.PostQuestionAnswerDTO
import kr.loghub.api.dto.question.answer.QuestionAnswerDTO
import kr.loghub.api.dto.task.answer.AnswerGenerateRequest
import kr.loghub.api.entity.question.Question
import kr.loghub.api.entity.question.QuestionAnswer
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.mapper.question.QuestionAnswerMapper
import kr.loghub.api.repository.question.QuestionAnswerRepository
import kr.loghub.api.repository.question.QuestionRepository
import kr.loghub.api.service.common.CacheService
import kr.loghub.api.util.checkAlreadyExists
import kr.loghub.api.util.checkField
import kr.loghub.api.util.checkPermission
import kr.loghub.api.worker.AnswerGenerateWorker
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionAnswerService(
    private val questionAnswerRepository: QuestionAnswerRepository,
    private val questionRepository: QuestionRepository,
    private val cacheService: CacheService,
    private val answerGenerateWorker: AnswerGenerateWorker,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    @Transactional(readOnly = true)
    fun getAnswers(questionId: Long): List<QuestionAnswerDTO> {
        val answers = questionAnswerRepository.findAllWithWriterByQuestionIdOrderByCreatedAt(questionId)
        val answerMarkdowns = answers.map { it.content }
        val answerHTMLs = cacheService.findOrGenerateMarkdownCache(answerMarkdowns)

        return answers.mapIndexed { i, answer -> QuestionAnswerMapper.map(answer, answerHTMLs[i]) }
    }

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

    @Transactional
    fun requestGenerateAnswer(questionId: Long, writer: User) {
        val question = questionRepository.findWithWriterById(questionId)
            ?: throw EntityNotFoundException(ResponseMessage.Question.NOT_FOUND)

        checkPermission(question.writer == writer) { ResponseMessage.Question.PERMISSION_DENIED }
        checkPermission(question.status === Question.Status.OPEN) { ResponseMessage.Question.STATUS_MUST_BE_OPEN }
        checkAlreadyExists(redisTemplate.hasKey("${RedisKey.Question.Answer.GENERATE_COOLDOWN}:${question.id}")) {
            ResponseMessage.Question.Answer.GENERATE_COOLDOWN
        }

        val request = AnswerGenerateRequest(question.id!!, question.title, question.content)
        answerGenerateWorker.addToQueue(request)

        val redisKey = "${RedisKey.Question.Answer.GENERATE_COOLDOWN.prefix}:${question.id}"
        redisTemplate.opsForValue().set(redisKey, "true", RedisKey.Question.Answer.GENERATE_COOLDOWN.ttl)
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