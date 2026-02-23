package me.loghub.api.service.user

import me.loghub.api.dto.user.UpdateUsernameDTO
import me.loghub.api.exception.auth.PermissionDeniedException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.exception.validation.ConflictFieldException
import me.loghub.api.proxy.TaskAPIProxy
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.AuthFixtures
import me.loghub.api.service.auth.token.RefreshTokenService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.multipart.MultipartFile
import kotlin.test.assertEquals

class UserServiceTest {
    private lateinit var userRepository: UserRepository
    private lateinit var articleRepository: ArticleRepository
    private lateinit var seriesRepository: SeriesRepository
    private lateinit var questionRepository: QuestionRepository
    private lateinit var refreshTokenService: RefreshTokenService
    private lateinit var taskAPIProxy: TaskAPIProxy

    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = mock()
        articleRepository = mock()
        seriesRepository = mock()
        questionRepository = mock()
        refreshTokenService = mock()
        taskAPIProxy = mock()

        userService = UserService(
            userRepository,
            articleRepository,
            seriesRepository,
            questionRepository,
            refreshTokenService,
            taskAPIProxy
        )
    }

    @Nested
    inner class GetUserTest {
        @Test
        fun `should return user detail when user exists`() {
            val user = AuthFixtures.user(id = 1L, username = "testuser")
            whenever(userRepository.findByUsername("testuser")).thenReturn(user)

            val result = userService.getUser("testuser")

            assertEquals(user.id, result.id)
            assertEquals(user.username, result.username)
            verify(userRepository).findByUsername("testuser")
        }

        @Test
        fun `should throw EntityNotFoundException when user does not exist`() {
            whenever(userRepository.findByUsername("missing")).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                userService.getUser("missing")
            }
        }
    }

    @Nested
    inner class UpdateUsernameTest {
        @Test
        fun `should update username and writer usernames when request is valid`() {
            val user = AuthFixtures.user(id = 1L, username = "olduser")
            val foundUser = AuthFixtures.user(id = 1L, username = "olduser")
            val requestBody = UpdateUsernameDTO(newUsername = "newuser")
            whenever(userRepository.existsByUsernameIgnoreCase(requestBody.newUsername)).thenReturn(false)
            whenever(userRepository.findByUsername("olduser")).thenReturn(foundUser)

            userService.updateUsername(requestBody, user)

            assertEquals("newuser", foundUser.username)
            verify(articleRepository).updateWriterUsernameByWriterUsername("olduser", "newuser")
            verify(seriesRepository).updateWriterUsernameByWriterUsername("olduser", "newuser")
            verify(questionRepository).updateWriterUsernameByWriterUsername("olduser", "newuser")
        }

        @Test
        fun `should throw ConflictFieldException when username is not changed`() {
            val user = AuthFixtures.user(id = 1L, username = "sameuser")
            val requestBody = UpdateUsernameDTO(newUsername = "sameuser")

            assertThrows<ConflictFieldException> {
                userService.updateUsername(requestBody, user)
            }
        }

        @Test
        fun `should throw UsernameNotFoundException when current user is not found`() {
            val user = AuthFixtures.user(id = 1L, username = "olduser")
            val requestBody = UpdateUsernameDTO(newUsername = "newuser")
            whenever(userRepository.existsByUsernameIgnoreCase(requestBody.newUsername)).thenReturn(false)
            whenever(userRepository.findByUsername("olduser")).thenReturn(null)

            assertThrows<UsernameNotFoundException> {
                userService.updateUsername(requestBody, user)
            }
        }
    }

    @Nested
    inner class UpdateAvatarTest {
        @Test
        fun `should upload avatar via task api`() {
            val user = AuthFixtures.user(id = 1L)
            val file = mock<MultipartFile>()

            userService.updateAvatar(file, user)

            verify(taskAPIProxy).uploadAvatar(file, 1L)
        }
    }

    @Nested
    inner class WithdrawTest {
        @Test
        fun `should withdraw user and revoke refresh token when refresh token exists`() {
            val user = AuthFixtures.user(id = 1L)
            val refreshToken = "1:refresh-token"

            userService.withdraw(user, refreshToken)

            verify(refreshTokenService).revokeToken(refreshToken)
            verify(taskAPIProxy).deleteAvatar(1L)
            verify(taskAPIProxy).deleteImages(1L)
            verify(userRepository).deleteById(1L)
        }

        @Test
        fun `should throw PermissionDeniedException when user id is null`() {
            val user = AuthFixtures.user(id = 1L).apply { this.id = null }

            assertThrows<PermissionDeniedException> {
                userService.withdraw(user, null)
            }
        }
    }
}
