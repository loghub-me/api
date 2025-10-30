package me.loghub.api.service.question

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.constant.redis.RedisKey
import me.loghub.api.dto.question.answer.PostQuestionAnswerDTO
import me.loghub.api.dto.question.answer.QuestionAnswerDTO
import me.loghub.api.dto.question.answer.QuestionAnswerForEditDTO
import me.loghub.api.dto.question.answer.RequestGenerateAnswerDTO
import me.loghub.api.dto.task.answer.AnswerGenerateRequest
import me.loghub.api.entity.question.Question
import me.loghub.api.entity.question.QuestionAnswer
import me.loghub.api.entity.user.User
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.mapper.question.QuestionAnswerMapper
import me.loghub.api.repository.question.QuestionAnswerRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.service.common.CacheService
import me.loghub.api.util.checkConflict
import me.loghub.api.util.checkCooldown
import me.loghub.api.util.checkField
import me.loghub.api.util.checkPermission
import me.loghub.api.worker.AnswerGenerateWorker
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
        val renderedMarkdowns = cacheService.findOrGenerateMarkdownCache(answerMarkdowns)

        return answers.mapIndexed { i, answer -> QuestionAnswerMapper.map(answer, renderedMarkdowns[i]) }
    }

    @Transactional(readOnly = true)
    fun getAnswerForEdit(questionId: Long, answerId: Long, writer: User): QuestionAnswerForEditDTO {
        val answer = questionAnswerRepository.findWithWriterByIdAndQuestionId(answerId, questionId)
            ?: throw EntityNotFoundException(ResponseMessage.Question.Answer.NOT_FOUND)

        checkPermission(answer.writer == writer) { ResponseMessage.Question.Answer.PERMISSION_DENIED }

        return QuestionAnswerMapper.mapForEdit(answer)
    }

    @Transactional
    fun postAnswer(questionId: Long, requestBody: PostQuestionAnswerDTO, writer: User): QuestionAnswer {
        val question = questionRepository.findWithWriterById(questionId)
            ?: throw EntityNotFoundException(ResponseMessage.Question.NOT_FOUND)

        checkConflict(question.writer == writer) { ResponseMessage.Question.Answer.CANNOT_POST_SELF }

        val answer = requestBody.toEntity(question, writer)
        questionRepository.incrementAnswerCount(question.id!!)
        return questionAnswerRepository.save(answer)
    }

    @Transactional
    fun editAnswer(questionId: Long, answerId: Long, requestBody: PostQuestionAnswerDTO, writer: User): QuestionAnswer {
        val answer = findUpdatableAnswer(questionId, answerId)
        checkPermission(answer.writer == writer) { ResponseMessage.Question.PERMISSION_DENIED }
        answer.update(requestBody)
        return answer
    }

    @Transactional
    fun deleteAnswer(questionId: Long, answerId: Long, writer: User) {
        val answer = findUpdatableAnswer(questionId, answerId)
        checkPermission(answer.writer == writer) { ResponseMessage.Question.PERMISSION_DENIED }
        questionRepository.decrementAnswerCount(answer.question.id!!)
        questionAnswerRepository.delete(answer)
    }

    @Transactional
    fun acceptAnswer(questionId: Long, answerId: Long, writer: User): QuestionAnswer {
        val answer = findUpdatableAnswer(questionId, answerId)
        checkPermission(answer.question.writer == writer) { ResponseMessage.Question.PERMISSION_DENIED }
        answer.accept()
        answer.question.solved()
        return answer
    }

    fun checkGeneratingAnswer(questionId: Long) =
        redisTemplate.hasKey("${RedisKey.Question.Answer.GENERATING.prefix}:${questionId}")

    @Transactional
    fun requestGenerateAnswer(questionId: Long, requestBody: RequestGenerateAnswerDTO, writer: User) {
        val question = questionRepository.findWithWriterById(questionId)
            ?: throw EntityNotFoundException(ResponseMessage.Question.NOT_FOUND)

        checkPermission(question.writer == writer) { ResponseMessage.Question.PERMISSION_DENIED }
        checkPermission(question.status === Question.Status.OPEN) { ResponseMessage.Question.STATUS_MUST_BE_OPEN }
        checkCooldown(redisTemplate.hasKey("${RedisKey.Question.Answer.GENERATE_COOLDOWN.prefix}:${question.id}")) {
            ResponseMessage.Question.Answer.COOLDOWN_NOT_ELAPSED
        }

        val request = AnswerGenerateRequest(
            question.id!!, question.title, question.content,
            requestBody.chatModel, requestBody.instruction
        )
        answerGenerateWorker.addToQueue(request)
    }

    private fun findUpdatableAnswer(questionId: Long, answerId: Long): QuestionAnswer {
        val answer = questionAnswerRepository.findWithWriterByIdAndQuestionId(answerId, questionId)
            ?: throw EntityNotFoundException(ResponseMessage.Question.Answer.NOT_FOUND)

        checkField(
            Question::status.name,
            answer.question.status == Question.Status.OPEN
        ) { ResponseMessage.Question.STATUS_MUST_BE_OPEN }
        checkField(
            QuestionAnswer::accepted.name,
            !answer.accepted,
        ) { ResponseMessage.Question.Answer.ALREADY_ACCEPTED }

        return answer
    }
}