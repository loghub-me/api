package me.loghub.api.service.user

import me.loghub.api.dto.user.UpdateUserProfileDTO
import me.loghub.api.entity.user.UserProfile
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.AuthFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.core.userdetails.UsernameNotFoundException
import kotlin.test.assertEquals

class UserProfileServiceTest {
    private lateinit var userRepository: UserRepository

    private lateinit var userProfileService: UserProfileService

    @BeforeEach
    fun setUp() {
        userRepository = mock()
        userProfileService = UserProfileService(userRepository)
    }

    @Nested
    inner class GetProfileTest {
        @Test
        fun `should return user profile when user exists`() {
            val user = AuthFixtures.user(id = 1L, username = "testuser")
            user.updateProfile(UserProfile(readme = "hello"))
            whenever(userRepository.findWithMetaByUsername("testuser")).thenReturn(user)

            val result = userProfileService.getProfile(user)

            assertEquals("hello", result.readme)
        }

        @Test
        fun `should throw UsernameNotFoundException when user does not exist`() {
            val user = AuthFixtures.user(id = 1L, username = "missing")
            whenever(userRepository.findWithMetaByUsername("missing")).thenReturn(null)

            assertThrows<UsernameNotFoundException> {
                userProfileService.getProfile(user)
            }
        }
    }

    @Nested
    inner class UpdateProfileTest {
        @Test
        fun `should update nickname and profile readme when user exists`() {
            val user = AuthFixtures.user(id = 1L, username = "testuser")
            val foundUser = AuthFixtures.user(id = 1L, username = "testuser")
            val requestBody = UpdateUserProfileDTO(
                nickname = "newnick",
                readme = "updated readme",
            )
            whenever(userRepository.findWithMetaByUsername("testuser")).thenReturn(foundUser)

            userProfileService.updateProfile(requestBody, user)

            assertEquals("newnick", foundUser.nickname)
            assertEquals("updated readme", foundUser.meta.profile.readme)
            verify(userRepository).findWithMetaByUsername("testuser")
        }

        @Test
        fun `should throw UsernameNotFoundException when user does not exist on update`() {
            val user = AuthFixtures.user(id = 1L, username = "missing")
            val requestBody = UpdateUserProfileDTO(
                nickname = "newnick",
                readme = "updated readme",
            )
            whenever(userRepository.findWithMetaByUsername("missing")).thenReturn(null)

            assertThrows<UsernameNotFoundException> {
                userProfileService.updateProfile(requestBody, user)
            }
        }
    }
}
