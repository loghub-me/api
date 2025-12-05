package me.loghub.api.service.question

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.common.UpdateDraftDTO
import me.loghub.api.entity.user.User
import me.loghub.api.lib.redis.key.RedisKeys
import me.loghub.api.repository.question.QuestionAnswerRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.util.checkPermission
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionAnswerDraftService(
    private val questionRepository: QuestionRepository,
    private val questionAnswerRepository: QuestionAnswerRepository,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    @Transactional
    fun updateAnswerDraft(questionId: Long, answerId: Long, requestBody: UpdateDraftDTO, writer: User) {
        val question = questionRepository.getReferenceById(questionId)
        checkPermission(
            questionAnswerRepository.existsByQuestionAndIdAndWriter(question, answerId, writer)
        ) { ResponseMessage.Question.PERMISSION_DENIED }

        val redisKey = RedisKeys.Question.Answer.DRAFT(questionId, answerId)
        redisTemplate.opsForValue().set(redisKey.key, requestBody.content, redisKey.ttl)
    }

    @Transactional
    fun deleteAnswerDraft(questionId: Long, answerId: Long, writer: User) {
        val question = questionRepository.getReferenceById(questionId)
        checkPermission(
            questionAnswerRepository.existsByQuestionAndIdAndWriter(question, answerId, writer)
        ) { ResponseMessage.Question.PERMISSION_DENIED }

        val redisKey = RedisKeys.Question.Answer.DRAFT(questionId, answerId)
        redisTemplate.delete(redisKey.key)
    }
}