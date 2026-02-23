package me.loghub.api.service.user

import me.loghub.api.dto.user.UpdateUserPrivacyDTO
import me.loghub.api.entity.user.UserPrivacy
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

class UserPrivacyServiceTest {
    private lateinit var userRepository: UserRepository

    private lateinit var userPrivacyService: UserPrivacyService

    @BeforeEach
    fun setUp() {
        userRepository = mock()
        userPrivacyService = UserPrivacyService(userRepository)
    }

    @Nested
    inner class GetPrivacyTest {
        @Test
        fun `should return user privacy when user exists`() {
            val user = AuthFixtures.user(id = 1L, username = "testuser")
            user.updatePrivacy(UserPrivacy(emailPublic = true))
            whenever(userRepository.findByUsername("testuser")).thenReturn(user)

            val result = userPrivacyService.getPrivacy(user)

            assertEquals(true, result.emailPublic)
        }

        @Test
        fun `should throw UsernameNotFoundException when user does not exist`() {
            val user = AuthFixtures.user(id = 1L, username = "missing")
            whenever(userRepository.findByUsername("missing")).thenReturn(null)

            assertThrows<UsernameNotFoundException> {
                userPrivacyService.getPrivacy(user)
            }
        }
    }

    @Nested
    inner class UpdatePrivacyTest {
        @Test
        fun `should update privacy when user exists`() {
            val user = AuthFixtures.user(id = 1L, username = "testuser")
            val foundUser = AuthFixtures.user(id = 1L, username = "testuser")
            whenever(userRepository.findByUsername("testuser")).thenReturn(foundUser)

            userPrivacyService.updatePrivacy(UpdateUserPrivacyDTO(emailPublic = true), user)

            assertEquals(true, foundUser.privacy.emailPublic)
        }

        @Test
        fun `should throw UsernameNotFoundException when user does not exist on update`() {
            val user = AuthFixtures.user(id = 1L, username = "missing")
            whenever(userRepository.findByUsername("missing")).thenReturn(null)

            assertThrows<UsernameNotFoundException> {
                userPrivacyService.updatePrivacy(UpdateUserPrivacyDTO(emailPublic = true), user)
            }
        }
    }
}
