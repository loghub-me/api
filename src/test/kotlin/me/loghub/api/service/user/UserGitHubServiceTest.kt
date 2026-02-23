package me.loghub.api.service.user

import me.loghub.api.config.ClientConfig
import me.loghub.api.dto.task.github.GitHubSocialAccount
import me.loghub.api.dto.task.github.GitHubUserResponse
import me.loghub.api.dto.user.UpdateUserGitHubDTO
import me.loghub.api.entity.user.UserGitHub
import me.loghub.api.exception.entity.EntityConflictException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.exception.github.GitHubUserNotFoundException
import me.loghub.api.proxy.GitHubAPIProxy
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.AuthFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.core.userdetails.UsernameNotFoundException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserGitHubServiceTest {
    private lateinit var userRepository: UserRepository
    private lateinit var githubAPIProxy: GitHubAPIProxy

    private lateinit var userGitHubService: UserGitHubService

    @BeforeEach
    fun setUp() {
        ClientConfig.HOST = "https://loghub.me"
        ClientConfig.DOMAIN = "loghub.me"

        userRepository = mock()
        githubAPIProxy = mock()

        userGitHubService = UserGitHubService(userRepository, githubAPIProxy)
    }

    @Nested
    inner class GetGitHubTest {
        @Test
        fun `should return github info when user exists`() {
            val user = AuthFixtures.user(id = 1L, username = "testuser")
            user.meta.github = UserGitHub(username = "ghuser", verified = true)
            whenever(userRepository.findWithMetaByUsername("testuser")).thenReturn(user)

            val result = userGitHubService.getGitHub(user)

            assertEquals("ghuser", result.username)
            assertTrue(result.verified)
        }

        @Test
        fun `should throw UsernameNotFoundException when user does not exist`() {
            val user = AuthFixtures.user(id = 1L, username = "missing")
            whenever(userRepository.findWithMetaByUsername("missing")).thenReturn(null)

            assertThrows<UsernameNotFoundException> {
                userGitHubService.getGitHub(user)
            }
        }
    }

    @Nested
    inner class UpdateGitHubTest {
        @Test
        fun `should update github username`() {
            val user = AuthFixtures.user(id = 1L)
            val foundUser = AuthFixtures.user(id = 1L)
            whenever(userRepository.findWithMetaById(1L)).thenReturn(foundUser)

            userGitHubService.updateGitHub(UpdateUserGitHubDTO("new-gh-user"), user)

            assertEquals("new-gh-user", foundUser.meta.github.username)
            assertFalse(foundUser.meta.github.verified)
        }

        @Test
        fun `should throw EntityNotFoundException when user does not exist on update`() {
            val user = AuthFixtures.user(id = 1L)
            whenever(userRepository.findWithMetaById(1L)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                userGitHubService.updateGitHub(UpdateUserGitHubDTO("new-gh-user"), user)
            }
        }
    }

    @Nested
    inner class DeleteGitHubTest {
        @Test
        fun `should clear github info`() {
            val user = AuthFixtures.user(id = 1L)
            val foundUser = AuthFixtures.user(id = 1L)
            foundUser.meta.github = UserGitHub("ghuser", true)
            whenever(userRepository.findWithMetaById(1L)).thenReturn(foundUser)

            userGitHubService.deleteGitHub(user)

            assertEquals(null, foundUser.meta.github.username)
            assertFalse(foundUser.meta.github.verified)
        }
    }

    @Nested
    inner class VerifyGitHubTest {
        @Test
        fun `should verify github when profile blog matches user page url`() {
            val user = AuthFixtures.user(id = 1L, username = "testuser")
            val foundUser = AuthFixtures.user(id = 1L, username = "testuser")
            foundUser.meta.github = UserGitHub(username = "ghuser", verified = false)
            whenever(userRepository.findWithMetaById(1L)).thenReturn(foundUser)
            whenever(githubAPIProxy.getUser("ghuser")).thenReturn(GitHubUserResponse("https://loghub.me/testuser"))
            whenever(githubAPIProxy.getUserSocialAccounts("ghuser")).thenReturn(emptyList())

            userGitHubService.verifyGitHub(user)

            assertTrue(foundUser.meta.github.verified)
        }

        @Test
        fun `should throw EntityConflictException when github username is not set`() {
            val user = AuthFixtures.user(id = 1L, username = "testuser")
            val foundUser = AuthFixtures.user(id = 1L, username = "testuser")
            foundUser.meta.github = UserGitHub(username = null, verified = false)
            whenever(userRepository.findWithMetaById(1L)).thenReturn(foundUser)

            assertThrows<EntityConflictException> {
                userGitHubService.verifyGitHub(user)
            }
        }

        @Test
        fun `should throw EntityConflictException when github is already verified`() {
            val user = AuthFixtures.user(id = 1L, username = "testuser")
            val foundUser = AuthFixtures.user(id = 1L, username = "testuser")
            foundUser.meta.github = UserGitHub(username = "ghuser", verified = true)
            whenever(userRepository.findWithMetaById(1L)).thenReturn(foundUser)

            assertThrows<EntityConflictException> {
                userGitHubService.verifyGitHub(user)
            }
        }

        @Test
        fun `should throw GitHubUserNotFoundException when profile is not verifiable`() {
            val user = AuthFixtures.user(id = 1L, username = "testuser")
            val foundUser = AuthFixtures.user(id = 1L, username = "testuser")
            foundUser.meta.github = UserGitHub(username = "ghuser", verified = false)
            whenever(userRepository.findWithMetaById(1L)).thenReturn(foundUser)
            whenever(githubAPIProxy.getUser("ghuser")).thenReturn(GitHubUserResponse("https://github.com/other"))
            whenever(githubAPIProxy.getUserSocialAccounts("ghuser"))
                .thenReturn(listOf(GitHubSocialAccount(provider = "twitter", url = "https://x.com/other")))

            assertThrows<GitHubUserNotFoundException> {
                userGitHubService.verifyGitHub(user)
            }
        }
    }
}
