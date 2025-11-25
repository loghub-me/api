package me.loghub.api.service.question

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.constant.redis.RedisKeys
import me.loghub.api.dto.common.UpdateDraftDTO
import me.loghub.api.entity.user.User
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.util.checkPermission
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionDraftService(
    private val questionRepository: QuestionRepository,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    @Transactional
    fun updateQuestionDraft(questionId: Long, requestBody: UpdateDraftDTO, writer: User) {
        checkPermission(
            questionRepository.existsByIdAndWriter(questionId, writer)
        ) { ResponseMessage.Question.PERMISSION_DENIED }

        val redisKey = RedisKeys.Question.DRAFT(questionId)
        redisTemplate.opsForValue().set(redisKey.key, requestBody.content, redisKey.ttl)
    }

    @Transactional
    fun deleteQuestionDraft(questionId: Long, writer: User) {
        checkPermission(
            questionRepository.existsByIdAndWriter(questionId, writer)
        ) { ResponseMessage.Question.PERMISSION_DENIED }

        val redisKey = RedisKeys.Question.DRAFT(questionId)
        redisTemplate.delete(redisKey.key)
    }
}