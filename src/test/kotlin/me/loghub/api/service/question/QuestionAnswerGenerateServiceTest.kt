package me.loghub.api.service.question

import me.loghub.api.constant.ai.Bot
import me.loghub.api.dto.task.answer.AnswerGenerateRequest
import me.loghub.api.dto.task.answer.AnswerGenerateResponse
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.lib.redis.key.question.QuestionAnswerGenerateCooldownRedisKey
import me.loghub.api.lib.redis.key.question.QuestionAnswerGeneratingRedisKey
import me.loghub.api.proxy.TaskAPIProxy
import me.loghub.api.repository.question.QuestionAnswerRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.user.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuestionAnswerGenerateServiceTest {
    private lateinit var questionRepository: QuestionRepository
    private lateinit var questionAnswerRepository: QuestionAnswerRepository
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var valueOperations: ValueOperations<String, String>
    private lateinit var userRepository: UserRepository
    private lateinit var taskAPIProxy: TaskAPIProxy

    private lateinit var questionAnswerGenerateService: QuestionAnswerGenerateService

    @BeforeEach
    fun setUp() {
        questionRepository = mock()
        questionAnswerRepository = mock()
        redisTemplate = mock()
        valueOperations = mock()
        userRepository = mock()
        taskAPIProxy = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        questionAnswerGenerateService = QuestionAnswerGenerateService(
            questionRepository,
            questionAnswerRepository,
            redisTemplate,
            userRepository,
            taskAPIProxy
        )
    }

    @Nested
    inner class CheckStatusTest {
        @Test
        fun `should return generating status from redis`() {
            whenever(redisTemplate.hasKey(QuestionAnswerGeneratingRedisKey(1L))).thenReturn(true)

            val result = questionAnswerGenerateService.checkGeneratingAnswer(1L)

            assertTrue(result)
            verify(redisTemplate).hasKey(QuestionAnswerGeneratingRedisKey(1L))
        }

        @Test
        fun `should return cooldown status from redis`() {
            whenever(redisTemplate.hasKey(QuestionAnswerGenerateCooldownRedisKey(1L))).thenReturn(true)

            val result = questionAnswerGenerateService.checkGenerateCooldown(1L)

            assertTrue(result)
            verify(redisTemplate).hasKey(QuestionAnswerGenerateCooldownRedisKey(1L))
        }
    }

    @Nested
    inner class GenerateAnswerAsyncTest {
        @Test
        fun `should save generated answer when response is not rejected`() {
            val req = AnswerGenerateRequest(
                questionId = 1L,
                questionTitle = "question title",
                questionContent = "question content",
                chatModel = me.loghub.api.constant.ai.LogHubChatModel.GPT_5,
                userInstruction = "answer kindly"
            )
            val bot = QuestionFixtures.writer(id = 99L, username = Bot.USERNAME)
            val question = QuestionFixtures.question(id = 1L)
            val res = AnswerGenerateResponse(
                title = "generated title",
                content = "generated content",
                rejectionReason = AnswerGenerateResponse.RejectionReason.NONE
            )
            whenever(userRepository.findByUsername(Bot.USERNAME)).thenReturn(bot)
            doReturn(Optional.of(question)).whenever(questionRepository).findById(1L)
            whenever(taskAPIProxy.generateAnswer(req)).thenReturn(res)
            whenever(questionAnswerRepository.save(any<me.loghub.api.entity.question.QuestionAnswer>())).thenAnswer { invocation ->
                invocation.arguments.first() as me.loghub.api.entity.question.QuestionAnswer
            }

            questionAnswerGenerateService.generateAnswerAsync(req)

            verify(valueOperations).set(
                QuestionAnswerGeneratingRedisKey(1L),
                "true",
                QuestionAnswerGeneratingRedisKey.TTL
            )
            verify(valueOperations).set(
                QuestionAnswerGenerateCooldownRedisKey(1L),
                "true",
                QuestionAnswerGenerateCooldownRedisKey.TTL
            )
            val captor = argumentCaptor<me.loghub.api.entity.question.QuestionAnswer>()
            verify(questionAnswerRepository).save(captor.capture())
            assertEquals("generated title", captor.firstValue.title)
            assertEquals("generated content", captor.firstValue.content)
            assertEquals(bot, captor.firstValue.writer)
            assertEquals(question, captor.firstValue.question)
            verify(redisTemplate).delete(QuestionAnswerGeneratingRedisKey(1L))
        }

        @Test
        fun `should save rejection answer when response is rejected`() {
            val req = AnswerGenerateRequest(
                questionId = 1L,
                questionTitle = "question title",
                questionContent = "question content",
                chatModel = me.loghub.api.constant.ai.LogHubChatModel.GPT_5,
                userInstruction = null
            )
            val bot = QuestionFixtures.writer(id = 99L, username = Bot.USERNAME)
            val question = QuestionFixtures.question(id = 1L)
            val res = AnswerGenerateResponse(
                title = "ignored title",
                content = "ignored content",
                rejectionReason = AnswerGenerateResponse.RejectionReason.OFF_TOPIC
            )
            whenever(userRepository.findByUsername(Bot.USERNAME)).thenReturn(bot)
            doReturn(Optional.of(question)).whenever(questionRepository).findById(1L)
            whenever(taskAPIProxy.generateAnswer(req)).thenReturn(res)
            whenever(questionAnswerRepository.save(any<me.loghub.api.entity.question.QuestionAnswer>())).thenAnswer { invocation ->
                invocation.arguments.first() as me.loghub.api.entity.question.QuestionAnswer
            }

            questionAnswerGenerateService.generateAnswerAsync(req)

            val captor = argumentCaptor<me.loghub.api.entity.question.QuestionAnswer>()
            verify(questionAnswerRepository).save(captor.capture())
            assertEquals("답변이 거절되었습니다", captor.firstValue.title)
            assertEquals(AnswerGenerateResponse.RejectionReason.OFF_TOPIC.message, captor.firstValue.content)
        }

        @Test
        fun `should clear generating status when bot user does not exist`() {
            val req = AnswerGenerateRequest(
                questionId = 1L,
                questionTitle = "question title",
                questionContent = "question content",
                chatModel = me.loghub.api.constant.ai.LogHubChatModel.GPT_5,
                userInstruction = null
            )
            whenever(userRepository.findByUsername(Bot.USERNAME)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                questionAnswerGenerateService.generateAnswerAsync(req)
            }

            verify(questionAnswerRepository, never()).save(any())
            verify(redisTemplate).delete(QuestionAnswerGeneratingRedisKey(1L))
        }
    }
}
