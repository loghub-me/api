package me.loghub.api.service.series

import me.loghub.api.dto.series.SeriesSort
import me.loghub.api.exception.auth.PermissionDeniedException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.exception.validation.IllegalFieldException
import me.loghub.api.repository.series.SeriesCustomRepository
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.topic.TopicRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import kotlin.test.assertEquals

class SeriesServiceTest {
    private lateinit var seriesRepository: SeriesRepository
    private lateinit var seriesCustomRepository: SeriesCustomRepository
    private lateinit var topicRepository: TopicRepository

    private lateinit var seriesService: SeriesService

    @BeforeEach
    fun setUp() {
        seriesRepository = mock()
        seriesCustomRepository = mock()
        topicRepository = mock()

        seriesService = SeriesService(
            seriesRepository,
            seriesCustomRepository,
            topicRepository
        )
    }

    @Nested
    inner class SearchSeriesTest {
        @Test
        fun `should return paginated series when search successful`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(writer = writer)
            val seriesPage = PageImpl(listOf(series))
            whenever(
                seriesCustomRepository.search(
                    query = any<String>(),
                    sort = any<SeriesSort>(),
                    pageable = any<Pageable>(),
                    anyOrNull(),
                )
            ).thenReturn(seriesPage)

            val result = seriesService.searchSeries(" query ", SeriesSort.latest, 1)

            assertEquals(1, result.content.size)
            assertEquals(series.id, result.content.first().id)
            verify(seriesCustomRepository).search(
                query = eq("query"),
                sort = eq(SeriesSort.latest),
                pageable = any(),
                anyOrNull(),
            )
        }

        @Test
        fun `should throw IllegalFieldException when page is not positive`() {
            assertThrows<IllegalFieldException> {
                seriesService.searchSeries("query", SeriesSort.latest, 0)
            }

            verifyNoInteractions(seriesCustomRepository)
        }
    }

    @Nested
    inner class GetSeriesTest {
        @Test
        fun `should return only published chapters when series exists`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            val publishedChapter = SeriesFixtures.chapter(id = 1L, series = series, published = true, sequence = 1)
            val unpublishedChapter = SeriesFixtures.chapter(id = 2L, series = series, published = false, sequence = 2)
            series.chapters = mutableListOf(publishedChapter, unpublishedChapter)
            whenever(seriesRepository.findWithGraphByCompositeKey("writer", "test-series")).thenReturn(series)

            val result = seriesService.getSeries("writer", "test-series")

            assertEquals(series.id, result.id)
            assertEquals(1, result.chapters.size)
            assertEquals(publishedChapter.id, result.chapters.first().id)
            verify(seriesRepository).findWithGraphByCompositeKey("writer", "test-series")
        }

        @Test
        fun `should throw EntityNotFoundException when series does not exist`() {
            whenever(seriesRepository.findWithGraphByCompositeKey(any(), any())).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                seriesService.getSeries("writer", "slug")
            }
        }
    }

    @Nested
    inner class GetSeriesForEditTest {
        @Test
        fun `should return series for edit when writer has permission`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(writer = writer)
            whenever(seriesRepository.findWithGraphById(1L)).thenReturn(series)

            val result = seriesService.getSeriesForEdit(1L, writer)

            assertEquals(series.id, result.id)
            verify(seriesRepository).findWithGraphById(1L)
        }

        @Test
        fun `should throw PermissionDeniedException when writer has no permission`() {
            val writer = SeriesFixtures.writer(id = 1L, username = "writer")
            val reader = SeriesFixtures.writer(id = 2L, username = "reader")
            val series = SeriesFixtures.series(writer = writer)
            whenever(seriesRepository.findWithGraphById(1L)).thenReturn(series)

            assertThrows<PermissionDeniedException> {
                seriesService.getSeriesForEdit(1L, reader)
            }
        }
    }

    @Nested
    inner class PostSeriesTest {
        @Test
        fun `should create and return series when request is valid`() {
            val writer = SeriesFixtures.writer()
            val requestBody = SeriesFixtures.postSeriesDTO(title = "New Series")
            whenever(seriesRepository.existsByCompositeKey(writer.username, "new-series")).thenReturn(false)
            whenever(topicRepository.findBySlugIn(requestBody.topicSlugs)).thenReturn(emptySet())
            whenever(seriesRepository.save(any<me.loghub.api.entity.series.Series>())).thenAnswer { invocation ->
                invocation.arguments.first() as me.loghub.api.entity.series.Series
            }

            val result = seriesService.postSeries(requestBody, writer)

            assertEquals("new-series", result.slug)
            assertEquals(requestBody.title, result.title)
            verify(seriesRepository).existsByCompositeKey(writer.username, "new-series")
            verify(topicRepository).findBySlugIn(requestBody.topicSlugs)
            verify(seriesRepository).save(any())
        }
    }

    @Nested
    inner class EditSeriesTest {
        @Test
        fun `should update series when request is valid`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            val requestBody = SeriesFixtures.postSeriesDTO(title = "Updated Series")
            whenever(seriesRepository.findWithWriterById(1L)).thenReturn(series)
            whenever(seriesRepository.existsByCompositeKeyAndIdNot(writer.username, "updated-series", 1L)).thenReturn(false)
            whenever(topicRepository.findBySlugIn(requestBody.topicSlugs)).thenReturn(emptySet())

            val result = seriesService.editSeries(1L, requestBody, writer)

            assertEquals("updated-series", result.slug)
            assertEquals("Updated Series", result.title)
            verify(seriesRepository).findWithWriterById(1L)
            verify(seriesRepository).existsByCompositeKeyAndIdNot(writer.username, "updated-series", 1L)
            verify(topicRepository).findBySlugIn(requestBody.topicSlugs)
        }

        @Test
        fun `should throw PermissionDeniedException when writer has no permission on edit`() {
            val writer = SeriesFixtures.writer(id = 1L, username = "writer")
            val reader = SeriesFixtures.writer(id = 2L, username = "reader")
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            val requestBody = SeriesFixtures.postSeriesDTO()
            whenever(seriesRepository.findWithWriterById(1L)).thenReturn(series)

            assertThrows<PermissionDeniedException> {
                seriesService.editSeries(1L, requestBody, reader)
            }
        }
    }

    @Nested
    inner class DeleteSeriesTest {
        @Test
        fun `should delete series when writer has permission`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            whenever(seriesRepository.findWithWriterById(1L)).thenReturn(series)

            seriesService.deleteSeries(1L, writer)

            verify(seriesRepository).findWithWriterById(1L)
            verify(seriesRepository).delete(series)
        }

        @Test
        fun `should throw EntityNotFoundException when series does not exist on delete`() {
            whenever(seriesRepository.findWithWriterById(1L)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                seriesService.deleteSeries(1L, SeriesFixtures.writer())
            }
        }
    }
}
