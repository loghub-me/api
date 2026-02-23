package me.loghub.api.service.series

import me.loghub.api.entity.user.UserStar
import me.loghub.api.exception.entity.EntityConflictException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.series.SeriesStatsRepository
import me.loghub.api.repository.user.UserStarRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SeriesStarServiceTest {
    private lateinit var userStarRepository: UserStarRepository
    private lateinit var seriesRepository: SeriesRepository
    private lateinit var seriesStatsRepository: SeriesStatsRepository

    private lateinit var seriesStarService: SeriesStarService

    @BeforeEach
    fun setUp() {
        userStarRepository = mock()
        seriesRepository = mock()
        seriesStatsRepository = mock()

        seriesStarService = SeriesStarService(userStarRepository, seriesRepository, seriesStatsRepository)
    }

    @Nested
    inner class ExistsStarTest {
        @Test
        fun `should return true when user already starred series`() {
            val seriesId = 1L
            val stargazer = SeriesFixtures.writer(id = 10L, username = "stargazer")
            val seriesRef = SeriesFixtures.series(id = seriesId)
            whenever(seriesRepository.getReferenceById(seriesId)).thenReturn(seriesRef)
            whenever(userStarRepository.existsBySeriesAndStargazer(seriesRef, stargazer)).thenReturn(true)

            val result = seriesStarService.existsStar(seriesId, stargazer)

            assertTrue(result)
            verify(seriesRepository).getReferenceById(seriesId)
            verify(userStarRepository).existsBySeriesAndStargazer(seriesRef, stargazer)
        }
    }

    @Nested
    inner class AddStarTest {
        @Test
        fun `should add star when series exists and star does not exist`() {
            val seriesId = 1L
            val stargazer = SeriesFixtures.writer(id = 10L, username = "stargazer")
            val seriesRef = SeriesFixtures.series(id = seriesId)
            whenever(seriesRepository.getReferenceById(seriesId)).thenReturn(seriesRef)
            whenever(userStarRepository.existsBySeriesAndStargazer(seriesRef, stargazer)).thenReturn(false)
            whenever(seriesRepository.existsById(seriesId)).thenReturn(true)
            whenever(userStarRepository.save(any<UserStar>())).thenAnswer { invocation ->
                invocation.arguments.first() as UserStar
            }

            val result = seriesStarService.addStar(seriesId, stargazer)

            assertEquals(UserStar.Target.SERIES, result.target)
            assertEquals(seriesRef, result.series)
            assertEquals(stargazer, result.stargazer)
            verify(seriesStatsRepository).incrementStarCount(seriesId)
        }

        @Test
        fun `should throw EntityConflictException when star already exists`() {
            val seriesId = 1L
            val stargazer = SeriesFixtures.writer(id = 10L, username = "stargazer")
            val seriesRef = SeriesFixtures.series(id = seriesId)
            whenever(seriesRepository.getReferenceById(seriesId)).thenReturn(seriesRef)
            whenever(userStarRepository.existsBySeriesAndStargazer(seriesRef, stargazer)).thenReturn(true)

            assertThrows<EntityConflictException> {
                seriesStarService.addStar(seriesId, stargazer)
            }

            verify(seriesRepository, never()).existsById(any())
            verify(seriesStatsRepository, never()).incrementStarCount(any())
            verify(userStarRepository, never()).save(any<UserStar>())
        }

        @Test
        fun `should throw EntityNotFoundException when series does not exist`() {
            val seriesId = 1L
            val stargazer = SeriesFixtures.writer(id = 10L, username = "stargazer")
            val seriesRef = SeriesFixtures.series(id = seriesId)
            whenever(seriesRepository.getReferenceById(seriesId)).thenReturn(seriesRef)
            whenever(userStarRepository.existsBySeriesAndStargazer(seriesRef, stargazer)).thenReturn(false)
            whenever(seriesRepository.existsById(seriesId)).thenReturn(false)

            assertThrows<EntityNotFoundException> {
                seriesStarService.addStar(seriesId, stargazer)
            }

            verify(seriesStatsRepository, never()).incrementStarCount(any())
            verify(userStarRepository, never()).save(any<UserStar>())
        }
    }

    @Nested
    inner class DeleteStarTest {
        @Test
        fun `should delete star when star exists`() {
            val seriesId = 1L
            val stargazer = SeriesFixtures.writer(id = 10L, username = "stargazer")
            val seriesRef = SeriesFixtures.series(id = seriesId)
            whenever(seriesRepository.getReferenceById(seriesId)).thenReturn(seriesRef)
            whenever(userStarRepository.deleteBySeriesAndStargazer(seriesRef, stargazer)).thenReturn(1)

            seriesStarService.deleteStar(seriesId, stargazer)

            verify(userStarRepository).deleteBySeriesAndStargazer(seriesRef, stargazer)
            verify(seriesStatsRepository).decrementStarCount(seriesId)
        }

        @Test
        fun `should throw EntityNotFoundException when star does not exist`() {
            val seriesId = 1L
            val stargazer = SeriesFixtures.writer(id = 10L, username = "stargazer")
            val seriesRef = SeriesFixtures.series(id = seriesId)
            whenever(seriesRepository.getReferenceById(seriesId)).thenReturn(seriesRef)
            whenever(userStarRepository.deleteBySeriesAndStargazer(seriesRef, stargazer)).thenReturn(0)

            assertThrows<EntityNotFoundException> {
                seriesStarService.deleteStar(seriesId, stargazer)
            }

            verify(seriesStatsRepository, never()).decrementStarCount(any())
        }
    }
}
