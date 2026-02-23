package me.loghub.api.service.question

import me.loghub.api.constant.ai.LogHubChatModel
import me.loghub.api.dto.common.RenderedMarkdownDTO
import me.loghub.api.dto.question.answer.PostQuestionAnswerDTO
import me.loghub.api.dto.task.answer.AnswerGenerateRequest
import me.loghub.api.entity.question.Question
import me.loghub.api.exception.auth.PermissionDeniedException
import me.loghub.api.exception.entity.EntityConflictException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.exception.validation.CooldownNotElapsedException
import me.loghub.api.exception.validation.IllegalFieldException
import me.loghub.api.lib.redis.key.question.QuestionAnswerDraftRedisKey
import me.loghub.api.repository.question.QuestionAnswerRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.question.QuestionStatsRepository
import me.loghub.api.service.common.MarkdownService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuestionAnswerServiceTest {
    private lateinit var questionAnswerRepository: QuestionAnswerRepository
    private lateinit var questionRepository: QuestionRepository
    private lateinit var questionStatsRepository: QuestionStatsRepository
    private lateinit var questionAnswerGenerateService: QuestionAnswerGenerateService
    private lateinit var markdownService: MarkdownService
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var valueOperations: ValueOperations<String, String>

    private lateinit var questionAnswerService: QuestionAnswerService

    @BeforeEach
    fun setUp() {
        questionAnswerRepository = mock()
        questionRepository = mock()
        questionStatsRepository = mock()
        questionAnswerGenerateService = mock()
        markdownService = mock()
        redisTemplate = mock()
        valueOperations = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        questionAnswerService = QuestionAnswerService(
            questionAnswerRepository,
            questionRepository,
            questionStatsRepository,
            questionAnswerGenerateService,
            markdownService,
            redisTemplate
        )
    }

    @Nested
    inner class GetAnswersTest {
        @Test
        fun `should return answers with rendered markdown`() {
            val question = QuestionFixtures.question(id = 1L)
            val answer1 = QuestionFixtures.answer(id = 10L, question = question, content = "answer1")
            val answer2 = QuestionFixtures.answer(id = 11L, question = question, content = "answer2")
            val rendered1 = RenderedMarkdownDTO("<p>a1</p>", emptyList())
            val rendered2 = RenderedMarkdownDTO("<p>a2</p>", emptyList())
            whenever(questionAnswerRepository.findAllWithWriterByQuestionIdOrderByCreatedAt(1L))
                .thenReturn(listOf(answer1, answer2))
            whenever(markdownService.findOrGenerateMarkdownCache(listOf("answer1", "answer2")))
                .thenReturn(listOf(rendered1, rendered2))

            val result = questionAnswerService.getAnswers(1L)

            assertEquals(2, result.size)
            assertEquals(answer1.id, result[0].id)
            assertEquals("<p>a1</p>", result[0].contentHTML)
            assertEquals("<p>a2</p>", result[1].contentHTML)
        }
    }

    @Nested
    inner class GetAnswerForEditTest {
        @Test
        fun `should return answer for edit when writer has permission`() {
            val question = QuestionFixtures.question(id = 1L)
            val writer = QuestionFixtures.writer(id = 2L, username = "answerer")
            val answer = QuestionFixtures.answer(id = 3L, question = question, writer = writer)
            whenever(questionAnswerRepository.findWithWriterByIdAndQuestionId(3L, 1L)).thenReturn(answer)
            whenever(valueOperations.get(QuestionAnswerDraftRedisKey(1L, 3L))).thenReturn("draft answer")

            val result = questionAnswerService.getAnswerForEdit(1L, 3L, writer)

            assertEquals(answer.id, result.id)
            assertEquals("draft answer", result.draft)
        }

        @Test
        fun `should throw EntityNotFoundException when answer does not exist`() {
            whenever(questionAnswerRepository.findWithWriterByIdAndQuestionId(3L, 1L)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                questionAnswerService.getAnswerForEdit(1L, 3L, QuestionFixtures.writer())
            }
        }

        @Test
        fun `should throw PermissionDeniedException when writer has no permission`() {
            val question = QuestionFixtures.question(id = 1L)
            val writer = QuestionFixtures.writer(id = 2L, username = "answerer")
            val another = QuestionFixtures.writer(id = 3L, username = "another")
            val answer = QuestionFixtures.answer(id = 3L, question = question, writer = writer)
            whenever(questionAnswerRepository.findWithWriterByIdAndQuestionId(3L, 1L)).thenReturn(answer)

            assertThrows<PermissionDeniedException> {
                questionAnswerService.getAnswerForEdit(1L, 3L, another)
            }
        }
    }

    @Nested
    inner class PostAnswerTest {
        @Test
        fun `should create answer and increment answer count when request is valid`() {
            val questionWriter = QuestionFixtures.writer(id = 1L, username = "questioner")
            val answerWriter = QuestionFixtures.writer(id = 2L, username = "answerer")
            val question = QuestionFixtures.question(id = 1L, writer = questionWriter)
            val requestBody = QuestionFixtures.postQuestionAnswerDTO()
            val savedAnswer = QuestionFixtures.answer(id = 3L, question = question, writer = answerWriter)
            whenever(questionRepository.findWithWriterById(1L)).thenReturn(question)
            whenever(questionAnswerRepository.save(any<me.loghub.api.entity.question.QuestionAnswer>())).thenReturn(savedAnswer)

            val result = questionAnswerService.postAnswer(1L, requestBody, answerWriter)

            assertEquals(savedAnswer.id, result.id)
            verify(questionStatsRepository).incrementAnswerCount(1L)
        }

        @Test
        fun `should throw EntityNotFoundException when question does not exist`() {
            whenever(questionRepository.findWithWriterById(1L)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                questionAnswerService.postAnswer(1L, QuestionFixtures.postQuestionAnswerDTO(), QuestionFixtures.writer())
            }
        }

        @Test
        fun `should throw EntityConflictException when writer answers own question`() {
            val writer = QuestionFixtures.writer(id = 1L, username = "questioner")
            val question = QuestionFixtures.question(id = 1L, writer = writer)
            whenever(questionRepository.findWithWriterById(1L)).thenReturn(question)

            assertThrows<EntityConflictException> {
                questionAnswerService.postAnswer(1L, QuestionFixtures.postQuestionAnswerDTO(), writer)
            }

            verify(questionAnswerRepository, never()).save(any())
            verify(questionStatsRepository, never()).incrementAnswerCount(any())
        }
    }

    @Nested
    inner class EditAnswerTest {
        @Test
        fun `should edit answer when status is open and writer has permission`() {
            val question = QuestionFixtures.question(id = 1L, status = Question.Status.OPEN)
            val writer = QuestionFixtures.writer(id = 2L, username = "answerer")
            val answer = QuestionFixtures.answer(id = 3L, question = question, writer = writer, accepted = false)
            val requestBody = PostQuestionAnswerDTO(title = "edited", content = "edited content")
            whenever(questionAnswerRepository.findWithWriterByIdAndQuestionId(3L, 1L)).thenReturn(answer)

            val result = questionAnswerService.editAnswer(1L, 3L, requestBody, writer)

            assertEquals("edited", result.title)
            assertEquals("edited content", result.content)
        }

        @Test
        fun `should throw IllegalFieldException when question status is not open on edit`() {
            val question = QuestionFixtures.question(id = 1L, status = Question.Status.CLOSED)
            val writer = QuestionFixtures.writer(id = 2L, username = "answerer")
            val answer = QuestionFixtures.answer(id = 3L, question = question, writer = writer, accepted = false)
            whenever(questionAnswerRepository.findWithWriterByIdAndQuestionId(3L, 1L)).thenReturn(answer)

            assertThrows<IllegalFieldException> {
                questionAnswerService.editAnswer(1L, 3L, QuestionFixtures.postQuestionAnswerDTO(), writer)
            }
        }

        @Test
        fun `should throw PermissionDeniedException when another user edits answer`() {
            val question = QuestionFixtures.question(id = 1L)
            val writer = QuestionFixtures.writer(id = 2L, username = "answerer")
            val another = QuestionFixtures.writer(id = 3L, username = "another")
            val answer = QuestionFixtures.answer(id = 3L, question = question, writer = writer)
            whenever(questionAnswerRepository.findWithWriterByIdAndQuestionId(3L, 1L)).thenReturn(answer)

            assertThrows<PermissionDeniedException> {
                questionAnswerService.editAnswer(1L, 3L, QuestionFixtures.postQuestionAnswerDTO(), another)
            }
        }
    }

    @Nested
    inner class DeleteAnswerTest {
        @Test
        fun `should delete answer and decrement answer count when writer has permission`() {
            val question = QuestionFixtures.question(id = 1L)
            val writer = QuestionFixtures.writer(id = 2L, username = "answerer")
            val answer = QuestionFixtures.answer(id = 3L, question = question, writer = writer)
            whenever(questionAnswerRepository.findWithWriterByIdAndQuestionId(3L, 1L)).thenReturn(answer)

            questionAnswerService.deleteAnswer(1L, 3L, writer)

            verify(questionStatsRepository).decrementAnswerCount(1L)
            verify(questionAnswerRepository).delete(answer)
        }
    }

    @Nested
    inner class AcceptAnswerTest {
        @Test
        fun `should accept answer and solve question when question writer accepts`() {
            val questionWriter = QuestionFixtures.writer(id = 1L, username = "questioner")
            val answerWriter = QuestionFixtures.writer(id = 2L, username = "answerer")
            val question = QuestionFixtures.question(id = 1L, writer = questionWriter, status = Question.Status.OPEN)
            val answer = QuestionFixtures.answer(id = 3L, question = question, writer = answerWriter, accepted = false)
            whenever(questionAnswerRepository.findWithWriterByIdAndQuestionId(3L, 1L)).thenReturn(answer)

            val result = questionAnswerService.acceptAnswer(1L, 3L, questionWriter)

            assertTrue(result.accepted)
            assertEquals(Question.Status.SOLVED, result.question.status)
        }

        @Test
        fun `should throw PermissionDeniedException when non-owner tries to accept answer`() {
            val questionWriter = QuestionFixtures.writer(id = 1L, username = "questioner")
            val another = QuestionFixtures.writer(id = 3L, username = "another")
            val question = QuestionFixtures.question(id = 1L, writer = questionWriter, status = Question.Status.OPEN)
            val answer = QuestionFixtures.answer(id = 3L, question = question)
            whenever(questionAnswerRepository.findWithWriterByIdAndQuestionId(3L, 1L)).thenReturn(answer)

            assertThrows<PermissionDeniedException> {
                questionAnswerService.acceptAnswer(1L, 3L, another)
            }
        }
    }

    @Nested
    inner class RequestGenerateAnswerTest {
        @Test
        fun `should request generated answer when writer has permission and cooldown elapsed`() {
            val writer = QuestionFixtures.writer(id = 1L, username = "questioner")
            val question = QuestionFixtures.question(id = 1L, writer = writer, status = Question.Status.OPEN)
            val requestBody = QuestionFixtures.requestGenerateAnswerDTO(
                instruction = "focus on examples",
                chatModel = LogHubChatModel.GPT_5
            )
            whenever(questionRepository.findWithWriterById(1L)).thenReturn(question)
            whenever(questionAnswerGenerateService.checkGenerateCooldown(1L)).thenReturn(false)

            questionAnswerService.requestGenerateAnswer(1L, requestBody, writer)

            val captor = argumentCaptor<AnswerGenerateRequest>()
            verify(questionAnswerGenerateService).generateAnswerAsync(captor.capture())
            assertEquals(1L, captor.firstValue.questionId)
            assertEquals(question.title, captor.firstValue.questionTitle)
            assertEquals(question.content, captor.firstValue.questionContent)
            assertEquals(LogHubChatModel.GPT_5, captor.firstValue.chatModel)
            assertEquals("focus on examples", captor.firstValue.userInstruction)
        }

        @Test
        fun `should throw CooldownNotElapsedException when cooldown has not elapsed`() {
            val writer = QuestionFixtures.writer(id = 1L, username = "questioner")
            val question = QuestionFixtures.question(id = 1L, writer = writer, status = Question.Status.OPEN)
            val requestBody = QuestionFixtures.requestGenerateAnswerDTO()
            whenever(questionRepository.findWithWriterById(1L)).thenReturn(question)
            whenever(questionAnswerGenerateService.checkGenerateCooldown(1L)).thenReturn(true)

            assertThrows<CooldownNotElapsedException> {
                questionAnswerService.requestGenerateAnswer(1L, requestBody, writer)
            }

            verify(questionAnswerGenerateService, never()).generateAnswerAsync(any())
        }

        @Test
        fun `should throw PermissionDeniedException when writer is not question owner`() {
            val writer = QuestionFixtures.writer(id = 1L, username = "questioner")
            val another = QuestionFixtures.writer(id = 2L, username = "another")
            val question = QuestionFixtures.question(id = 1L, writer = writer, status = Question.Status.OPEN)
            whenever(questionRepository.findWithWriterById(1L)).thenReturn(question)

            assertThrows<PermissionDeniedException> {
                questionAnswerService.requestGenerateAnswer(1L, QuestionFixtures.requestGenerateAnswerDTO(), another)
            }
        }
    }
}
