package me.loghub.api.service.user

import me.loghub.api.entity.user.UserFollow
import me.loghub.api.exception.entity.EntityConflictException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.exception.validation.IllegalFieldException
import me.loghub.api.repository.user.UserFollowRepository
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.AuthFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserFollowServiceTest {
    private lateinit var userRepository: UserRepository
    private lateinit var userFollowRepository: UserFollowRepository

    private lateinit var userFollowService: UserFollowService

    @BeforeEach
    fun setUp() {
        userRepository = mock()
        userFollowRepository = mock()

        userFollowService = UserFollowService(userRepository, userFollowRepository)
    }

    @Nested
    inner class GetFollowersTest {
        @Test
        fun `should return followers when page is valid`() {
            val followeeId = 1L
            val followee = AuthFixtures.user(id = followeeId, username = "followee")
            val follower = AuthFixtures.user(id = 2L, username = "follower")
            whenever(userRepository.getReferenceById(followeeId)).thenReturn(followee)
            whenever(
                userFollowRepository.findFollowersByFolloweeOrderByIdDesc(
                    followee,
                    PageRequest.of(0, 10)
                )
            ).thenReturn(PageImpl(listOf(follower)))

            val result = userFollowService.getFollowers(followeeId, 1)

            assertEquals(1, result.content.size)
            assertEquals(follower.id, result.content.first().id)
            assertEquals(follower.username, result.content.first().username)
            verify(userRepository).getReferenceById(followeeId)
            verify(userFollowRepository).findFollowersByFolloweeOrderByIdDesc(followee, PageRequest.of(0, 10))
        }

        @Test
        fun `should throw IllegalFieldException when page is not positive`() {
            assertThrows<IllegalFieldException> {
                userFollowService.getFollowers(1L, 0)
            }

            verifyNoInteractions(userRepository, userFollowRepository)
        }
    }

    @Nested
    inner class GetFolloweesTest {
        @Test
        fun `should return followees when page is valid`() {
            val followerId = 1L
            val follower = AuthFixtures.user(id = followerId, username = "follower")
            val followee = AuthFixtures.user(id = 2L, username = "followee")
            whenever(userRepository.getReferenceById(followerId)).thenReturn(follower)
            whenever(
                userFollowRepository.findFolloweesByFollowerOrderByIdDesc(
                    follower,
                    PageRequest.of(0, 10)
                )
            ).thenReturn(PageImpl(listOf(followee)))

            val result = userFollowService.getFollowees(followerId, 1)

            assertEquals(1, result.content.size)
            assertEquals(followee.id, result.content.first().id)
            assertEquals(followee.username, result.content.first().username)
            verify(userRepository).getReferenceById(followerId)
            verify(userFollowRepository).findFolloweesByFollowerOrderByIdDesc(follower, PageRequest.of(0, 10))
        }

        @Test
        fun `should throw IllegalFieldException when page is not positive`() {
            assertThrows<IllegalFieldException> {
                userFollowService.getFollowees(1L, 0)
            }

            verifyNoInteractions(userRepository, userFollowRepository)
        }
    }

    @Nested
    inner class ExistsFollowTest {
        @Test
        fun `should return true when follower already follows user`() {
            val followeeId = 1L
            val followee = AuthFixtures.user(id = followeeId, username = "followee")
            val follower = AuthFixtures.user(id = 2L, username = "follower")
            whenever(userRepository.getReferenceById(followeeId)).thenReturn(followee)
            whenever(userFollowRepository.existsByFollowerAndFollowee(follower, followee)).thenReturn(true)

            val result = userFollowService.existsFollow(followeeId, follower)

            assertTrue(result)
            verify(userRepository).getReferenceById(followeeId)
            verify(userFollowRepository).existsByFollowerAndFollowee(follower, followee)
        }
    }

    @Nested
    inner class FollowUserTest {
        @Test
        fun `should save follow when follow does not already exist`() {
            val followeeId = 1L
            val followee = AuthFixtures.user(id = followeeId, username = "followee")
            val follower = AuthFixtures.user(id = 2L, username = "follower")
            whenever(userRepository.getReferenceById(followeeId)).thenReturn(followee)
            whenever(userFollowRepository.existsByFollowerAndFollowee(follower, followee)).thenReturn(false)
            whenever(userFollowRepository.save(any<UserFollow>())).thenAnswer { invocation ->
                invocation.arguments.first() as UserFollow
            }

            val result = userFollowService.followUser(followeeId, follower)

            assertEquals(follower, result.follower)
            assertEquals(followee, result.followee)

            val savedFollowCaptor = argumentCaptor<UserFollow>()
            verify(userFollowRepository).save(savedFollowCaptor.capture())
            assertEquals(follower, savedFollowCaptor.firstValue.follower)
            assertEquals(followee, savedFollowCaptor.firstValue.followee)
        }

        @Test
        fun `should throw EntityConflictException when follower tries to follow self`() {
            val follower = AuthFixtures.user(id = 1L, username = "follower")
            whenever(userRepository.getReferenceById(1L)).thenReturn(follower)

            assertThrows<EntityConflictException> {
                userFollowService.followUser(1L, follower)
            }

            verify(userRepository).getReferenceById(1L)
            verify(userFollowRepository, never()).existsByFollowerAndFollowee(any(), any())
            verify(userFollowRepository, never()).save(any<UserFollow>())
        }

        @Test
        fun `should throw EntityConflictException when follow already exists`() {
            val followeeId = 1L
            val followee = AuthFixtures.user(id = followeeId, username = "followee")
            val follower = AuthFixtures.user(id = 2L, username = "follower")
            whenever(userRepository.getReferenceById(followeeId)).thenReturn(followee)
            whenever(userFollowRepository.existsByFollowerAndFollowee(follower, followee)).thenReturn(true)

            assertThrows<EntityConflictException> {
                userFollowService.followUser(followeeId, follower)
            }

            verify(userRepository).getReferenceById(followeeId)
            verify(userFollowRepository).existsByFollowerAndFollowee(follower, followee)
            verify(userFollowRepository, never()).save(any<UserFollow>())
        }
    }

    @Nested
    inner class UnfollowUserTest {
        @Test
        fun `should delete follow when follow exists`() {
            val followeeId = 1L
            val followee = AuthFixtures.user(id = followeeId, username = "followee")
            val follower = AuthFixtures.user(id = 2L, username = "follower")
            val follow = UserFollow(id = 11L, follower = follower, followee = followee)
            whenever(userRepository.getReferenceById(followeeId)).thenReturn(followee)
            whenever(userFollowRepository.findByFollowerAndFollowee(follower, followee)).thenReturn(follow)

            userFollowService.unfollowUser(followeeId, follower)

            verify(userRepository).getReferenceById(followeeId)
            verify(userFollowRepository).findByFollowerAndFollowee(follower, followee)
            verify(userFollowRepository).delete(follow)
        }

        @Test
        fun `should throw EntityNotFoundException when follow does not exist`() {
            val followeeId = 1L
            val followee = AuthFixtures.user(id = followeeId, username = "followee")
            val follower = AuthFixtures.user(id = 2L, username = "follower")
            whenever(userRepository.getReferenceById(followeeId)).thenReturn(followee)
            whenever(userFollowRepository.findByFollowerAndFollowee(follower, followee)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                userFollowService.unfollowUser(followeeId, follower)
            }

            verify(userRepository).getReferenceById(followeeId)
            verify(userFollowRepository).findByFollowerAndFollowee(follower, followee)
            verify(userFollowRepository, never()).delete(any())
        }
    }
}
