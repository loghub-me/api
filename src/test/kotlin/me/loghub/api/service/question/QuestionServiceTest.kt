package me.loghub.api.service.question

import me.loghub.api.dto.common.RenderedMarkdownDTO
import me.loghub.api.dto.question.QuestionFilter
import me.loghub.api.dto.question.QuestionSort
import me.loghub.api.exception.auth.PermissionDeniedException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.exception.validation.IllegalFieldException
import me.loghub.api.lib.redis.key.question.QuestionDraftRedisKey
import me.loghub.api.repository.question.QuestionCustomRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.topic.TopicRepository
import me.loghub.api.service.common.MarkdownService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import kotlin.test.assertEquals

class QuestionServiceTest {
    private lateinit var questionRepository: QuestionRepository
    private lateinit var questionCustomRepository: QuestionCustomRepository
    private lateinit var topicRepository: TopicRepository
    private lateinit var markdownService: MarkdownService
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var valueOperations: ValueOperations<String, String>

    private lateinit var questionService: QuestionService

    @BeforeEach
    fun setUp() {
        questionRepository = mock()
        questionCustomRepository = mock()
        topicRepository = mock()
        markdownService = mock()
        redisTemplate = mock()
        valueOperations = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        questionService = QuestionService(
            questionRepository,
            questionCustomRepository,
            topicRepository,
            markdownService,
            redisTemplate
        )
    }

    @Nested
    inner class SearchQuestionsTest {
        @Test
        fun `should return paginated questions when search successful`() {
            val question = QuestionFixtures.question()
            whenever(
                questionCustomRepository.search(
                    query = any<String>(),
                    sort = any<QuestionSort>(),
                    filter = any<QuestionFilter>(),
                    pageable = any<Pageable>(),
                    anyOrNull(),
                )
            ).thenReturn(PageImpl(listOf(question)))

            val result = questionService.searchQuestions(" query ", QuestionSort.latest, QuestionFilter.all, 1)

            assertEquals(1, result.content.size)
            assertEquals(question.id, result.content.first().id)
            verify(questionCustomRepository).search(
                query = eq("query"),
                sort = eq(QuestionSort.latest),
                filter = eq(QuestionFilter.all),
                pageable = any(),
                anyOrNull(),
            )
        }

        @Test
        fun `should throw IllegalFieldException when page is not positive`() {
            assertThrows<IllegalFieldException> {
                questionService.searchQuestions("query", QuestionSort.latest, QuestionFilter.all, 0)
            }

            verifyNoInteractions(questionCustomRepository)
        }
    }

    @Nested
    inner class GetQuestionTest {
        @Test
        fun `should return question detail when question exists`() {
            val question = QuestionFixtures.question()
            val renderedMarkdown = RenderedMarkdownDTO(html = "rendered content", anchors = emptyList())
            whenever(questionRepository.findWithWriterByCompositeKey("writer", "slug")).thenReturn(question)
            whenever(markdownService.findOrGenerateMarkdownCache(question.content)).thenReturn(renderedMarkdown)

            val result = questionService.getQuestion("writer", "slug")

            assertEquals(question.id, result.id)
            assertEquals(renderedMarkdown.html, result.content.html)
            verify(questionRepository).findWithWriterByCompositeKey("writer", "slug")
            verify(markdownService).findOrGenerateMarkdownCache(question.content)
        }

        @Test
        fun `should throw EntityNotFoundException when question does not exist`() {
            whenever(questionRepository.findWithWriterByCompositeKey(any(), any())).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                questionService.getQuestion("writer", "slug")
            }
        }
    }

    @Nested
    inner class GetQuestionForEditTest {
        @Test
        fun `should return question for edit when writer has permission`() {
            val writer = QuestionFixtures.writer()
            val question = QuestionFixtures.question(id = 1L, writer = writer)
            whenever(questionRepository.findWithWriterById(1L)).thenReturn(question)
            whenever(valueOperations.get(QuestionDraftRedisKey(1L))).thenReturn("draft content")

            val result = questionService.getQuestionForEdit(1L, writer)

            assertEquals(question.id, result.id)
            assertEquals("draft content", result.draft)
        }

        @Test
        fun `should throw PermissionDeniedException when writer has no permission`() {
            val writer = QuestionFixtures.writer(id = 1L, username = "writer")
            val reader = QuestionFixtures.writer(id = 2L, username = "reader")
            val question = QuestionFixtures.question(id = 1L, writer = writer)
            whenever(questionRepository.findWithWriterById(1L)).thenReturn(question)

            assertThrows<PermissionDeniedException> {
                questionService.getQuestionForEdit(1L, reader)
            }
        }
    }

    @Nested
    inner class PostQuestionTest {
        @Test
        fun `should create and return question when request is valid`() {
            val writer = QuestionFixtures.writer()
            val requestBody = QuestionFixtures.postQuestionDTO(title = "New Question")
            whenever(questionRepository.existsByCompositeKey(writer.username, "new-question")).thenReturn(false)
            whenever(topicRepository.findBySlugIn(requestBody.topicSlugs)).thenReturn(emptySet())
            whenever(markdownService.normalizeMarkdown(requestBody.content)).thenReturn("normalized content")
            whenever(questionRepository.save(any<me.loghub.api.entity.question.Question>())).thenAnswer { invocation ->
                invocation.arguments.first() as me.loghub.api.entity.question.Question
            }

            val result = questionService.postQuestion(requestBody, writer)

            assertEquals("new-question", result.slug)
            assertEquals("normalized content", result.normalizedContent)
            verify(questionRepository).existsByCompositeKey(writer.username, "new-question")
            verify(markdownService).normalizeMarkdown(requestBody.content)
            verify(questionRepository).save(any())
        }
    }

    @Nested
    inner class EditQuestionTest {
        @Test
        fun `should update question when request is valid`() {
            val writer = QuestionFixtures.writer()
            val question = QuestionFixtures.question(id = 1L, writer = writer)
            val requestBody = QuestionFixtures.postQuestionDTO(title = "Updated Question", content = "updated content")
            whenever(questionRepository.findWithWriterById(1L)).thenReturn(question)
            whenever(questionRepository.existsByCompositeKeyAndIdNot(writer.username, "updated-question", 1L)).thenReturn(false)
            whenever(markdownService.normalizeMarkdown(requestBody.content)).thenReturn("normalized updated content")
            whenever(topicRepository.findBySlugIn(requestBody.topicSlugs)).thenReturn(emptySet())

            val result = questionService.editQuestion(1L, requestBody, writer)

            assertEquals("updated-question", result.slug)
            assertEquals("normalized updated content", result.normalizedContent)
        }
    }

    @Nested
    inner class DeleteQuestionTest {
        @Test
        fun `should delete question when writer has permission`() {
            val writer = QuestionFixtures.writer()
            val question = QuestionFixtures.question(id = 1L, writer = writer)
            whenever(questionRepository.findWithWriterById(1L)).thenReturn(question)

            questionService.deleteQuestion(1L, writer)

            verify(questionRepository).delete(question)
        }
    }

    @Nested
    inner class CloseQuestionTest {
        @Test
        fun `should close question when status is open and writer has permission`() {
            val writer = QuestionFixtures.writer()
            val question = QuestionFixtures.question(id = 1L, writer = writer)
            whenever(questionRepository.findWithWriterById(1L)).thenReturn(question)

            val result = questionService.closeQuestion(1L, writer)

            assertEquals(me.loghub.api.entity.question.Question.Status.CLOSED, result.status)
        }

        @Test
        fun `should throw IllegalFieldException when question status is not open`() {
            val writer = QuestionFixtures.writer()
            val question = QuestionFixtures.question(
                id = 1L,
                writer = writer,
                status = me.loghub.api.entity.question.Question.Status.CLOSED
            )
            whenever(questionRepository.findWithWriterById(1L)).thenReturn(question)

            assertThrows<IllegalFieldException> {
                questionService.closeQuestion(1L, writer)
            }
        }
    }
}
