package me.loghub.api.service.auth

import me.loghub.api.config.ClientConfig
import me.loghub.api.dto.task.mail.LoginMailSendRequest
import me.loghub.api.exception.auth.BadOTPException
import me.loghub.api.exception.entity.EntityNotFoundFieldException
import me.loghub.api.lib.redis.key.auth.LoginOTPRedisKey
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.token.TokenService
import me.loghub.api.service.common.MailService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import kotlin.test.assertEquals

class LoginServiceTest {
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var valueOperations: ValueOperations<String, String>
    private lateinit var userRepository: UserRepository
    private lateinit var tokenService: TokenService
    private lateinit var mailService: MailService

    private lateinit var loginService: LoginService

    @BeforeEach
    fun setUp() {
        ClientConfig.HOST = "https://loghub.me"
        ClientConfig.DOMAIN = "loghub.me"

        redisTemplate = mock()
        valueOperations = mock()
        userRepository = mock()
        tokenService = mock()
        mailService = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        loginService = LoginService(
            redisTemplate,
            userRepository,
            tokenService,
            mailService
        )
    }

    @Nested
    inner class RequestLoginTest {
        @Test
        fun `should request login when email exists`() {
            val requestBody = AuthFixtures.loginRequestDTO(email = "login@loghub.me")
            whenever(userRepository.existsByEmail(requestBody.email)).thenReturn(true)

            loginService.requestLogin(requestBody)

            verify(userRepository).existsByEmail(requestBody.email)
            val otpCaptor = argumentCaptor<String>()
            verify(valueOperations).set(
                eq(LoginOTPRedisKey(requestBody.email)),
                otpCaptor.capture(),
                eq(LoginOTPRedisKey.TTL)
            )
            verify(mailService).sendMailAsync(any<LoginMailSendRequest>())
            assertEquals(6, otpCaptor.firstValue.length)
        }

        @Test
        fun `should throw EntityNotFoundFieldException when email does not exist`() {
            val requestBody = AuthFixtures.loginRequestDTO(email = "missing@loghub.me")
            whenever(userRepository.existsByEmail(requestBody.email)).thenReturn(false)

            assertThrows<EntityNotFoundFieldException> {
                loginService.requestLogin(requestBody)
            }

            verify(mailService, never()).sendMailAsync(any())
            verify(valueOperations, never()).set(
                any<String>(),
                any<String>(),
                any<java.time.Duration>()
            )
        }
    }

    @Nested
    inner class ConfirmLoginTest {
        @Test
        fun `should confirm login when otp and user are valid`() {
            val requestBody = AuthFixtures.loginConfirmDTO(email = "login@loghub.me", otp = "ABC123")
            val user = AuthFixtures.user(email = requestBody.email)
            val tokenDTO = AuthFixtures.tokenDTO()
            whenever(valueOperations.get(LoginOTPRedisKey(requestBody.email))).thenReturn("ABC123")
            whenever(userRepository.findByEmail(requestBody.email)).thenReturn(user)
            whenever(tokenService.generateToken(user)).thenReturn(tokenDTO)

            val result = loginService.confirmLogin(requestBody)

            assertEquals(tokenDTO, result.first)
            assertEquals(user.id, result.second.id)
            assertEquals(user.username, result.second.username)
            verify(redisTemplate).delete(LoginOTPRedisKey(requestBody.email))
            verify(tokenService).generateToken(user)
        }

        @Test
        fun `should throw BadOTPException when otp is missing in redis`() {
            val requestBody = AuthFixtures.loginConfirmDTO(email = "login@loghub.me", otp = "ABC123")
            whenever(valueOperations.get(LoginOTPRedisKey(requestBody.email))).thenReturn(null)

            assertThrows<BadOTPException> {
                loginService.confirmLogin(requestBody)
            }
        }

        @Test
        fun `should throw BadOTPException when otp does not match`() {
            val requestBody = AuthFixtures.loginConfirmDTO(email = "login@loghub.me", otp = "ABC123")
            whenever(valueOperations.get(LoginOTPRedisKey(requestBody.email))).thenReturn("DIFF12")

            assertThrows<BadOTPException> {
                loginService.confirmLogin(requestBody)
            }

            verify(redisTemplate, never()).delete(any<String>())
        }

        @Test
        fun `should throw EntityNotFoundFieldException when user does not exist`() {
            val requestBody = AuthFixtures.loginConfirmDTO(email = "login@loghub.me", otp = "ABC123")
            whenever(valueOperations.get(LoginOTPRedisKey(requestBody.email))).thenReturn("ABC123")
            whenever(userRepository.findByEmail(requestBody.email)).thenReturn(null)

            assertThrows<EntityNotFoundFieldException> {
                loginService.confirmLogin(requestBody)
            }
        }
    }
}
