package me.loghub.api.service.question

import me.loghub.api.dto.common.UpdateDraftDTO
import me.loghub.api.exception.auth.PermissionDeniedException
import me.loghub.api.lib.redis.key.question.QuestionAnswerDraftRedisKey
import me.loghub.api.repository.question.QuestionAnswerRepository
import me.loghub.api.repository.question.QuestionRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations

class QuestionAnswerDraftServiceTest {
    private lateinit var questionRepository: QuestionRepository
    private lateinit var questionAnswerRepository: QuestionAnswerRepository
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var valueOperations: ValueOperations<String, String>

    private lateinit var questionAnswerDraftService: QuestionAnswerDraftService

    @BeforeEach
    fun setUp() {
        questionRepository = mock()
        questionAnswerRepository = mock()
        redisTemplate = mock()
        valueOperations = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        questionAnswerDraftService = QuestionAnswerDraftService(
            questionRepository,
            questionAnswerRepository,
            redisTemplate
        )
    }

    @Nested
    inner class UpdateAnswerDraftTest {
        @Test
        fun `should update answer draft when writer has permission`() {
            val questionId = 1L
            val answerId = 2L
            val writer = QuestionFixtures.writer()
            val question = QuestionFixtures.question(id = questionId, writer = writer)
            val requestBody = UpdateDraftDTO(content = "draft content")
            whenever(questionRepository.getReferenceById(questionId)).thenReturn(question)
            whenever(questionAnswerRepository.existsByQuestionAndIdAndWriter(question, answerId, writer)).thenReturn(true)

            questionAnswerDraftService.updateAnswerDraft(questionId, answerId, requestBody, writer)

            verify(questionAnswerRepository).existsByQuestionAndIdAndWriter(question, answerId, writer)
            verify(valueOperations).set(
                QuestionAnswerDraftRedisKey(questionId, answerId),
                requestBody.content,
                QuestionAnswerDraftRedisKey.TTL
            )
        }

        @Test
        fun `should throw PermissionDeniedException when writer has no permission on update`() {
            val questionId = 1L
            val answerId = 2L
            val writer = QuestionFixtures.writer()
            val question = QuestionFixtures.question(id = questionId, writer = writer)
            val requestBody = UpdateDraftDTO(content = "draft content")
            whenever(questionRepository.getReferenceById(questionId)).thenReturn(question)
            whenever(questionAnswerRepository.existsByQuestionAndIdAndWriter(question, answerId, writer)).thenReturn(false)

            assertThrows<PermissionDeniedException> {
                questionAnswerDraftService.updateAnswerDraft(questionId, answerId, requestBody, writer)
            }

            verify(redisTemplate, never()).opsForValue()
            verifyNoInteractions(valueOperations)
        }
    }

    @Nested
    inner class DeleteAnswerDraftTest {
        @Test
        fun `should delete answer draft when writer has permission`() {
            val questionId = 1L
            val answerId = 2L
            val writer = QuestionFixtures.writer()
            val question = QuestionFixtures.question(id = questionId, writer = writer)
            whenever(questionRepository.getReferenceById(questionId)).thenReturn(question)
            whenever(questionAnswerRepository.existsByQuestionAndIdAndWriter(question, answerId, writer)).thenReturn(true)

            questionAnswerDraftService.deleteAnswerDraft(questionId, answerId, writer)

            verify(redisTemplate).delete(QuestionAnswerDraftRedisKey(questionId, answerId))
        }

        @Test
        fun `should throw PermissionDeniedException when writer has no permission on delete`() {
            val questionId = 1L
            val answerId = 2L
            val writer = QuestionFixtures.writer()
            val question = QuestionFixtures.question(id = questionId, writer = writer)
            whenever(questionRepository.getReferenceById(questionId)).thenReturn(question)
            whenever(questionAnswerRepository.existsByQuestionAndIdAndWriter(question, answerId, writer)).thenReturn(false)

            assertThrows<PermissionDeniedException> {
                questionAnswerDraftService.deleteAnswerDraft(questionId, answerId, writer)
            }

            verify(redisTemplate, never()).delete(any<String>())
        }
    }
}
