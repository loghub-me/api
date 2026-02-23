package me.loghub.api.service.series

import me.loghub.api.exception.auth.PermissionDeniedException
import me.loghub.api.exception.entity.EntityConflictException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.series.SeriesReviewRepository
import me.loghub.api.repository.series.SeriesStatsRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import kotlin.test.assertEquals

class SeriesReviewServiceTest {
    private lateinit var seriesRepository: SeriesRepository
    private lateinit var seriesStatsRepository: SeriesStatsRepository
    private lateinit var seriesReviewRepository: SeriesReviewRepository

    private lateinit var seriesReviewService: SeriesReviewService

    @BeforeEach
    fun setUp() {
        seriesRepository = mock()
        seriesStatsRepository = mock()
        seriesReviewRepository = mock()

        seriesReviewService = SeriesReviewService(
            seriesRepository,
            seriesStatsRepository,
            seriesReviewRepository
        )
    }

    @Nested
    inner class GetReviewsTest {
        @Test
        fun `should return paginated reviews`() {
            val series = SeriesFixtures.series(id = 1L)
            val review = SeriesFixtures.review(id = 2L, series = series)
            whenever(seriesReviewRepository.findAllBySeriesId(eq(1L), any<Pageable>()))
                .thenReturn(PageImpl(listOf(review)))

            val result = seriesReviewService.getReviews(1L, 1)

            assertEquals(1, result.content.size)
            assertEquals(review.id, result.content.first().id)
            verify(seriesReviewRepository).findAllBySeriesId(eq(1L), any<Pageable>())
        }
    }

    @Nested
    inner class PostReviewTest {
        @Test
        fun `should create review and increment count when request is valid`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            val requestBody = SeriesFixtures.postSeriesReviewDTO(content = "great")
            val savedReview = SeriesFixtures.review(id = 3L, series = series, writer = writer, content = "great")
            whenever(seriesRepository.findWithWriterById(1L)).thenReturn(series)
            whenever(seriesReviewRepository.existsBySeriesAndWriter(series, writer)).thenReturn(false)
            whenever(seriesReviewRepository.save(any<me.loghub.api.entity.series.SeriesReview>())).thenReturn(savedReview)

            val result = seriesReviewService.postReview(1L, requestBody, writer)

            assertEquals(savedReview.id, result.id)
            verify(seriesStatsRepository).incrementReviewCount(1L)
            verify(seriesReviewRepository).save(any())
        }

        @Test
        fun `should throw EntityConflictException when review already exists`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            val requestBody = SeriesFixtures.postSeriesReviewDTO()
            whenever(seriesRepository.findWithWriterById(1L)).thenReturn(series)
            whenever(seriesReviewRepository.existsBySeriesAndWriter(series, writer)).thenReturn(true)

            assertThrows<EntityConflictException> {
                seriesReviewService.postReview(1L, requestBody, writer)
            }

            verify(seriesReviewRepository, never()).save(any())
            verify(seriesStatsRepository, never()).incrementReviewCount(any())
        }
    }

    @Nested
    inner class EditReviewTest {
        @Test
        fun `should edit review when writer has permission`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            val review = SeriesFixtures.review(id = 2L, series = series, writer = writer, content = "before")
            val requestBody = SeriesFixtures.postSeriesReviewDTO(content = "after\n\n\nedit", rating = 4)
            whenever(seriesReviewRepository.findWithGraphBySeriesIdAndId(1L, 2L)).thenReturn(review)

            val result = seriesReviewService.editReview(1L, 2L, requestBody, writer)

            assertEquals(review, result)
            assertEquals("after\n\nedit", result.content)
            assertEquals(4, result.rating)
        }

        @Test
        fun `should throw PermissionDeniedException when another user edits review`() {
            val writer = SeriesFixtures.writer(id = 1L, username = "writer")
            val another = SeriesFixtures.writer(id = 2L, username = "another")
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            val review = SeriesFixtures.review(id = 2L, series = series, writer = another)
            whenever(seriesReviewRepository.findWithGraphBySeriesIdAndId(1L, 2L)).thenReturn(review)

            assertThrows<PermissionDeniedException> {
                seriesReviewService.editReview(1L, 2L, SeriesFixtures.postSeriesReviewDTO(), writer)
            }
        }
    }

    @Nested
    inner class DeleteReviewTest {
        @Test
        fun `should delete review and decrement count when writer has permission`() {
            val writer = SeriesFixtures.writer()
            val series = SeriesFixtures.series(id = 1L, writer = writer)
            val review = SeriesFixtures.review(id = 2L, series = series, writer = writer)
            whenever(seriesReviewRepository.findWithGraphBySeriesIdAndId(1L, 2L)).thenReturn(review)

            seriesReviewService.deleteReview(1L, 2L, writer)

            verify(seriesStatsRepository).decrementReviewCount(1L)
            verify(seriesReviewRepository).delete(review)
        }

        @Test
        fun `should throw EntityNotFoundException when review does not exist`() {
            whenever(seriesReviewRepository.findWithGraphBySeriesIdAndId(1L, 2L)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                seriesReviewService.deleteReview(1L, 2L, SeriesFixtures.writer())
            }
        }
    }
}
