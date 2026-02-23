package me.loghub.api.service.question

import me.loghub.api.dto.common.UpdateDraftDTO
import me.loghub.api.exception.auth.PermissionDeniedException
import me.loghub.api.lib.redis.key.question.QuestionDraftRedisKey
import me.loghub.api.repository.question.QuestionRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations

class QuestionDraftServiceTest {
    private lateinit var questionRepository: QuestionRepository
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var valueOperations: ValueOperations<String, String>

    private lateinit var questionDraftService: QuestionDraftService

    @BeforeEach
    fun setUp() {
        questionRepository = mock()
        redisTemplate = mock()
        valueOperations = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        questionDraftService = QuestionDraftService(questionRepository, redisTemplate)
    }

    @Nested
    inner class UpdateQuestionDraftTest {
        @Test
        fun `should update draft when writer has permission`() {
            val questionId = 1L
            val writer = QuestionFixtures.writer()
            val requestBody = UpdateDraftDTO(content = "draft content")
            whenever(questionRepository.existsByIdAndWriter(questionId, writer)).thenReturn(true)

            questionDraftService.updateQuestionDraft(questionId, requestBody, writer)

            verify(questionRepository).existsByIdAndWriter(questionId, writer)
            verify(valueOperations).set(
                QuestionDraftRedisKey(questionId),
                requestBody.content,
                QuestionDraftRedisKey.TTL
            )
        }

        @Test
        fun `should throw PermissionDeniedException when writer has no permission on update`() {
            val questionId = 1L
            val writer = QuestionFixtures.writer()
            val requestBody = UpdateDraftDTO(content = "draft content")
            whenever(questionRepository.existsByIdAndWriter(questionId, writer)).thenReturn(false)

            assertThrows<PermissionDeniedException> {
                questionDraftService.updateQuestionDraft(questionId, requestBody, writer)
            }

            verify(redisTemplate, never()).opsForValue()
            verifyNoInteractions(valueOperations)
        }
    }

    @Nested
    inner class DeleteQuestionDraftTest {
        @Test
        fun `should delete draft when writer has permission`() {
            val questionId = 1L
            val writer = QuestionFixtures.writer()
            whenever(questionRepository.existsByIdAndWriter(questionId, writer)).thenReturn(true)

            questionDraftService.deleteQuestionDraft(questionId, writer)

            verify(redisTemplate).delete(QuestionDraftRedisKey(questionId))
        }

        @Test
        fun `should throw PermissionDeniedException when writer has no permission on delete`() {
            val questionId = 1L
            val writer = QuestionFixtures.writer()
            whenever(questionRepository.existsByIdAndWriter(questionId, writer)).thenReturn(false)

            assertThrows<PermissionDeniedException> {
                questionDraftService.deleteQuestionDraft(questionId, writer)
            }

            verify(redisTemplate, never()).delete(any<String>())
        }
    }
}
