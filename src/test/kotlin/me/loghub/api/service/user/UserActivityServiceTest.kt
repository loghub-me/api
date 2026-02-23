package me.loghub.api.service.user

import me.loghub.api.dto.user.activity.UserActivityProjection
import me.loghub.api.dto.user.activity.UserActivitySummaryDTO
import me.loghub.api.entity.user.UserActivity
import me.loghub.api.exception.validation.IllegalFieldException
import me.loghub.api.repository.user.UserActivityRepository
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.AuthFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.assertEquals

class UserActivityServiceTest {
    private lateinit var userActivityRepository: UserActivityRepository
    private lateinit var userRepository: UserRepository

    private lateinit var userActivityService: UserActivityService

    @BeforeEach
    fun setUp() {
        userActivityRepository = mock()
        userRepository = mock()

        userActivityService = UserActivityService(userActivityRepository, userRepository)
    }

    @Nested
    inner class GetActivitySummariesTest {
        @Test
        fun `should return activity summaries when date range is valid`() {
            val userId = 1L
            val from = LocalDate.now().minusDays(2)
            val to = LocalDate.now().minusDays(1)
            val user = AuthFixtures.user(id = userId)
            val summaries = listOf(UserActivitySummaryDTO(date = to, count = 2))
            whenever(userRepository.getReferenceById(userId)).thenReturn(user)
            whenever(userActivityRepository.findSummaryDTOsByUserAndCreatedDateBetween(user, from, to))
                .thenReturn(summaries)

            val result = userActivityService.getActivitySummaries(userId, from, to)

            assertEquals(1, result.size)
            assertEquals(2, result.first().count)
            verify(userActivityRepository).findSummaryDTOsByUserAndCreatedDateBetween(user, from, to)
        }

        @Test
        fun `should throw IllegalFieldException when to date is in the future`() {
            assertThrows<IllegalFieldException> {
                userActivityService.getActivitySummaries(
                    userId = 1L,
                    from = LocalDate.now().minusDays(1),
                    to = LocalDate.now().plusDays(1)
                )
            }

            verifyNoInteractions(userRepository, userActivityRepository)
        }
    }

    @Nested
    inner class GetActivitiesTest {
        @Test
        fun `should return user activities when date is valid`() {
            val userId = 1L
            val date = LocalDate.now().minusDays(1)
            val user = AuthFixtures.user(id = userId)
            val projection = mock<UserActivityProjection>()
            whenever(projection.id).thenReturn(1L)
            whenever(projection.title).thenReturn("title")
            whenever(projection.href).thenReturn("/articles/a")
            whenever(projection.action).thenReturn(UserActivity.Action.PUBLISH_ARTICLE)
            whenever(userRepository.getReferenceById(userId)).thenReturn(user)
            whenever(userActivityRepository.findProjectionByUserIdAndCreatedDate(userId, date))
                .thenReturn(listOf(projection))

            val result = userActivityService.getActivities(userId, date)

            assertEquals(1, result.size)
            assertEquals(1L, result.first().id)
            assertEquals("title", result.first().title)
            verify(userActivityRepository).findProjectionByUserIdAndCreatedDate(userId, date)
        }

        @Test
        fun `should throw IllegalFieldException when activity date is in the future`() {
            assertThrows<IllegalFieldException> {
                userActivityService.getActivities(1L, LocalDate.now().plusDays(1))
            }

            verifyNoInteractions(userRepository, userActivityRepository)
        }
    }
}
