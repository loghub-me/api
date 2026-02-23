package me.loghub.api.service.series

import me.loghub.api.dto.common.RenderedMarkdownDTO
import me.loghub.api.exception.auth.PermissionDeniedException
import me.loghub.api.exception.entity.EntityConflictException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.exception.validation.IllegalFieldException
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.series.SeriesChapterRepository
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.service.article.ArticleFixtures
import me.loghub.api.service.common.MarkdownService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SeriesChapterServiceTest {
    private lateinit var seriesRepository: SeriesRepository
    private lateinit var seriesChapterRepository: SeriesChapterRepository
    private lateinit var articleRepository: ArticleRepository
    private lateinit var markdownService: MarkdownService
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var valueOperations: ValueOperations<String, String>

    private lateinit var seriesChapterService: SeriesChapterService

    @BeforeEach
    fun setUp() {
        seriesRepository = mock()
        seriesChapterRepository = mock()
        articleRepository = mock()
        markdownService = mock()
        redisTemplate = mock()
        valueOperations = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        seriesChapterService = SeriesChapterService(
            seriesRepository,
            seriesChapterRepository,
            articleRepository,
            markdownService,
            redisTemplate
        )
    }

    @Nested
    inner class GetChapterTest {
        @Test
        fun `should return chapter detail when chapter exists and is published`() {
            val series = SeriesFixtures.series(id = 1L)
            val chapter = SeriesFixtures.chapter(id = 2L, series = series, sequence = 1, published = true)
            val rendered = RenderedMarkdownDTO(html = "<p>rendered</p>", anchors = emptyList())
            whenever(seriesRepository.getReferenceById(1L)).thenReturn(series)
            whenever(seriesChapterRepository.findWithWriterBySeriesAndSequence(series, 1)).thenReturn(chapter)
            whenever(markdownService.findOrGenerateMarkdownCache(chapter.content)).thenReturn(rendered)

            val result = seriesChapterService.getChapter(1L, 1)

            assertEquals(chapter.id, result.id)
            assertEquals(rendered.html, result.content.html)
            verify(markdownService).findOrGenerateMarkdownCache(chapter.content)
        }

        @Test
        fun `should throw EntityNotFoundException when chapter does not exist`() {
            val series = SeriesFixtures.series(id = 1L)
            whenever(seriesRepository.getReferenceById(1L)).thenReturn(series)
            whenever(seriesChapterRepository.findWithWriterBySeriesAndSequence(series, 1)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                seriesChapterService.getChapter(1L, 1)
            }
        }

        @Test
        fun `should throw EntityNotFoundException when chapter is not published`() {
            val series = SeriesFixtures.series(id = 1L)
            val chapter = SeriesFixtures.chapter(id = 2L, series = series, sequence = 1, published = false)
            whenever(seriesRepository.getReferenceById(1L)).thenReturn(series)
            whenever(seriesChapterRepository.findWithWriterBySeriesAndSequence(series, 1)).thenReturn(chapter)

            assertThrows<EntityNotFoundException> {
                seriesChapterService.getChapter(1L, 1)
            }
        }
    }

    @Nested
    inner class GetChapterForEditTest {
        @Test
        fun `should return chapter for edit when writer has permission`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            val chapter = SeriesFixtures.chapter(id = 2L, series = series, writer = writer)
            whenever(seriesRepository.getReferenceById(1L)).thenReturn(series)
            whenever(seriesChapterRepository.findWithWriterBySeriesAndId(series, 2L)).thenReturn(chapter)
            whenever(valueOperations.get("series:1:chapter:2:draft")).thenReturn("draft content")

            val result = seriesChapterService.getChapterForEdit(1L, 2L, writer)

            assertEquals(chapter.id, result.id)
            assertEquals("draft content", result.draft)
            verify(valueOperations).get("series:1:chapter:2:draft")
        }

        @Test
        fun `should throw PermissionDeniedException when writer has no permission`() {
            val writer = SeriesFixtures.writer(id = 1L, username = "writer")
            val reader = SeriesFixtures.writer(id = 2L, username = "reader")
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            val chapter = SeriesFixtures.chapter(id = 2L, series = series, writer = writer)
            whenever(seriesRepository.getReferenceById(1L)).thenReturn(series)
            whenever(seriesChapterRepository.findWithWriterBySeriesAndId(series, 2L)).thenReturn(chapter)

            assertThrows<PermissionDeniedException> {
                seriesChapterService.getChapterForEdit(1L, 2L, reader)
            }
        }
    }

    @Nested
    inner class CreateChapterTest {
        @Test
        fun `should create chapter when writer has permission and chapter count is below limit`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = 1L, writer = writer, chapterCount = 1)
            whenever(seriesRepository.findById(1L)).thenReturn(Optional.of(series))
            whenever(seriesRepository.increaseAndGetChapterCount(1L, 20)).thenReturn(2)
            whenever(seriesChapterRepository.save(any<me.loghub.api.entity.series.SeriesChapter>())).thenAnswer { invocation ->
                invocation.arguments.first() as me.loghub.api.entity.series.SeriesChapter
            }

            val result = seriesChapterService.createChapter(1L, writer)

            assertEquals(2, result.sequence)
            assertEquals("새 챕터", result.title)
            assertFalse(result.published)
            verify(seriesRepository).increaseAndGetChapterCount(1L, 20)
            verify(seriesChapterRepository).save(any())
        }

        @Test
        fun `should throw EntityConflictException when chapter count reached limit`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = 1L, writer = writer, chapterCount = 20)
            whenever(seriesRepository.findById(1L)).thenReturn(Optional.of(series))

            assertThrows<EntityConflictException> {
                seriesChapterService.createChapter(1L, writer)
            }

            verify(seriesRepository, never()).increaseAndGetChapterCount(any(), any())
            verify(seriesChapterRepository, never()).save(any())
        }
    }

    @Nested
    inner class ImportChapterTest {
        @Test
        fun `should import article as chapter when writer has permission`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = 1L, writer = writer, chapterCount = 0)
            val article = ArticleFixtures.article(id = 10L, writer = writer, title = "Imported Article")
            whenever(seriesRepository.findById(1L)).thenReturn(Optional.of(series))
            whenever(articleRepository.findWithWriterById(10L)).thenReturn(article)
            whenever(seriesRepository.increaseAndGetChapterCount(1L, 20)).thenReturn(1)
            whenever(seriesChapterRepository.save(any<me.loghub.api.entity.series.SeriesChapter>())).thenAnswer { invocation ->
                invocation.arguments.first() as me.loghub.api.entity.series.SeriesChapter
            }

            val result = seriesChapterService.importChapter(1L, 10L, writer)

            assertEquals(article.title, result.title)
            assertEquals(article.content, result.content)
            assertEquals(1, result.sequence)
            verify(seriesChapterRepository).save(any())
        }

        @Test
        fun `should throw PermissionDeniedException when article writer is different`() {
            val writer = SeriesFixtures.writer(id = 1L, username = "writer")
            val another = SeriesFixtures.writer(id = 2L, username = "another")
            val series = SeriesFixtures.series(id = 1L, writer = writer, chapterCount = 0)
            val article = ArticleFixtures.article(id = 10L, writer = another)
            whenever(seriesRepository.findById(1L)).thenReturn(Optional.of(series))
            whenever(articleRepository.findWithWriterById(10L)).thenReturn(article)

            assertThrows<PermissionDeniedException> {
                seriesChapterService.importChapter(1L, 10L, writer)
            }

            verify(seriesChapterRepository, never()).save(any())
        }
    }

    @Nested
    inner class EditChapterTest {
        @Test
        fun `should edit and publish chapter when request is valid`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            val chapter = SeriesFixtures.chapter(id = 2L, series = series, writer = writer, published = false)
            val requestBody = SeriesFixtures.editSeriesChapterDTO(
                title = "Edited title",
                content = "Edited content",
                published = true
            )
            whenever(seriesRepository.getReferenceById(1L)).thenReturn(series)
            whenever(seriesChapterRepository.findWithWriterBySeriesAndId(series, 2L)).thenReturn(chapter)
            whenever(markdownService.normalizeMarkdown(requestBody.content)).thenReturn("normalized edited content")

            val result = seriesChapterService.editChapter(1L, 2L, requestBody, writer)

            assertEquals("Edited title", result.title)
            assertEquals("normalized edited content", result.normalizedContent)
            assertTrue(result.published)
            verify(markdownService).normalizeMarkdown(requestBody.content)
        }
    }

    @Nested
    inner class DeleteChapterTest {
        @Test
        fun `should delete chapter and shift following chapter sequences`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            val chapter = SeriesFixtures.chapter(id = 2L, series = series, writer = writer, sequence = 2)
            val chapter3 = SeriesFixtures.chapter(id = 3L, series = series, writer = writer, sequence = 3)
            val chapter4 = SeriesFixtures.chapter(id = 4L, series = series, writer = writer, sequence = 4)
            whenever(seriesRepository.getReferenceById(1L)).thenReturn(series)
            whenever(seriesChapterRepository.findWithWriterBySeriesAndId(series, 2L)).thenReturn(chapter)
            whenever(
                seriesChapterRepository.findAllBySeriesIdAndSequenceGreaterThanOrderBySequenceAsc(1L, 2)
            ).thenReturn(listOf(chapter3, chapter4))

            seriesChapterService.deleteChapter(1L, 2L, writer)

            verify(seriesChapterRepository).delete(chapter)
            assertEquals(2, chapter3.sequence)
            assertEquals(3, chapter4.sequence)
        }
    }

    @Nested
    inner class UpdateChapterSequenceTest {
        @Test
        fun `should update chapter sequences when request is valid`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            val chapter1 = SeriesFixtures.chapter(id = 1L, series = series, writer = writer, sequence = 1)
            val chapter2 = SeriesFixtures.chapter(id = 2L, series = series, writer = writer, sequence = 2)
            val chapter3 = SeriesFixtures.chapter(id = 3L, series = series, writer = writer, sequence = 3)
            whenever(seriesRepository.getReferenceById(1L)).thenReturn(series)
            whenever(seriesChapterRepository.findWithWriterAllBySeriesOrderBySequenceAsc(series))
                .thenReturn(listOf(chapter1, chapter2, chapter3))
            val requestBody = SeriesFixtures.updateSeriesChapterSequenceDTO(listOf(2, 1, 3))

            seriesChapterService.updateChapterSequence(1L, requestBody, writer)

            assertEquals(2, chapter1.sequence)
            assertEquals(1, chapter2.sequence)
            assertEquals(3, chapter3.sequence)
        }

        @Test
        fun `should throw IllegalFieldException when sequence size is invalid`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            val chapter1 = SeriesFixtures.chapter(id = 1L, series = series, writer = writer, sequence = 1)
            val chapter2 = SeriesFixtures.chapter(id = 2L, series = series, writer = writer, sequence = 2)
            whenever(seriesRepository.getReferenceById(1L)).thenReturn(series)
            whenever(seriesChapterRepository.findWithWriterAllBySeriesOrderBySequenceAsc(series))
                .thenReturn(listOf(chapter1, chapter2))
            val requestBody = SeriesFixtures.updateSeriesChapterSequenceDTO(listOf(1))

            assertThrows<IllegalFieldException> {
                seriesChapterService.updateChapterSequence(1L, requestBody, writer)
            }
        }

        @Test
        fun `should throw PermissionDeniedException when chapter writer is different`() {
            val writer = SeriesFixtures.writer(id = 1L, username = "writer")
            val another = SeriesFixtures.writer(id = 2L, username = "another")
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            val chapter1 = SeriesFixtures.chapter(id = 1L, series = series, writer = writer, sequence = 1)
            val chapter2 = SeriesFixtures.chapter(id = 2L, series = series, writer = another, sequence = 2)
            whenever(seriesRepository.getReferenceById(1L)).thenReturn(series)
            whenever(seriesChapterRepository.findWithWriterAllBySeriesOrderBySequenceAsc(series))
                .thenReturn(listOf(chapter1, chapter2))
            val requestBody = SeriesFixtures.updateSeriesChapterSequenceDTO(listOf(1, 2))

            assertThrows<PermissionDeniedException> {
                seriesChapterService.updateChapterSequence(1L, requestBody, writer)
            }
        }
    }
}
