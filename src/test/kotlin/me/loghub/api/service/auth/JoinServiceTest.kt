package me.loghub.api.service.auth

import me.loghub.api.config.ClientConfig
import me.loghub.api.dto.auth.join.JoinInfoDTO
import me.loghub.api.dto.task.avatar.AvatarGenerateRequest
import me.loghub.api.dto.task.mail.JoinMailSendRequest
import me.loghub.api.exception.auth.BadOTPException
import me.loghub.api.exception.entity.EntityExistsFieldException
import me.loghub.api.exception.validation.IllegalFieldException
import me.loghub.api.lib.redis.key.auth.JoinOTPRedisKey
import me.loghub.api.proxy.TaskAPIProxy
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

class JoinServiceTest {
    private lateinit var redisTemplate: RedisTemplate<String, JoinInfoDTO>
    private lateinit var valueOperations: ValueOperations<String, JoinInfoDTO>
    private lateinit var userRepository: UserRepository
    private lateinit var tokenService: TokenService
    private lateinit var mailService: MailService
    private lateinit var taskAPIProxy: TaskAPIProxy

    private lateinit var joinService: JoinService

    @BeforeEach
    fun setUp() {
        ClientConfig.HOST = "https://loghub.me"
        ClientConfig.DOMAIN = "loghub.me"

        redisTemplate = mock()
        valueOperations = mock()
        userRepository = mock()
        tokenService = mock()
        mailService = mock()
        taskAPIProxy = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        joinService = JoinService(
            redisTemplate,
            userRepository,
            tokenService,
            mailService,
            taskAPIProxy
        )
    }

    @Nested
    inner class RequestJoinTest {
        @Test
        fun `should request join when request is joinable`() {
            val requestBody = AuthFixtures.joinRequestDTO(
                email = "join@loghub.me",
                username = "joinuser",
                nickname = "joinuser"
            )
            whenever(userRepository.existsByEmail(requestBody.email)).thenReturn(false)
            whenever(userRepository.existsByUsernameIgnoreCase(requestBody.username)).thenReturn(false)

            joinService.requestJoin(requestBody)

            val infoCaptor = argumentCaptor<JoinInfoDTO>()
            verify(valueOperations).set(
                eq(JoinOTPRedisKey(requestBody.email)),
                infoCaptor.capture(),
                eq(JoinOTPRedisKey.TTL)
            )
            verify(mailService).sendMailAsync(any<JoinMailSendRequest>())
            assertEquals(requestBody.email, infoCaptor.firstValue.email)
            assertEquals(requestBody.username, infoCaptor.firstValue.username)
            assertEquals(requestBody.nickname, infoCaptor.firstValue.nickname)
            assertEquals(6, infoCaptor.firstValue.otp.length)
        }

        @Test
        fun `should throw IllegalFieldException when username is reserved`() {
            val requestBody = AuthFixtures.joinRequestDTO(username = "admin")

            assertThrows<IllegalFieldException> {
                joinService.requestJoin(requestBody)
            }

            verify(userRepository, never()).existsByEmail(any())
            verify(mailService, never()).sendMailAsync(any())
        }

        @Test
        fun `should throw EntityExistsFieldException when email already exists`() {
            val requestBody = AuthFixtures.joinRequestDTO(email = "exists@loghub.me")
            whenever(userRepository.existsByEmail(requestBody.email)).thenReturn(true)

            assertThrows<EntityExistsFieldException> {
                joinService.requestJoin(requestBody)
            }

            verify(userRepository, never()).existsByUsernameIgnoreCase(any())
            verify(mailService, never()).sendMailAsync(any())
        }
    }

    @Nested
    inner class ConfirmJoinTest {
        @Test
        fun `should confirm join when otp is valid`() {
            val requestBody = AuthFixtures.joinConfirmDTO(email = "join@loghub.me", otp = "ABC123")
            val info = JoinInfoDTO(
                otp = "ABC123",
                email = requestBody.email,
                username = "joinuser",
                nickname = "joinuser",
            )
            val joinedUser = info.toUserEntity().apply { id = 10L }
            val tokenDTO = AuthFixtures.tokenDTO()
            whenever(valueOperations.get(JoinOTPRedisKey(requestBody.email))).thenReturn(info)
            whenever(userRepository.save(any<me.loghub.api.entity.user.User>())).thenReturn(joinedUser)
            whenever(tokenService.generateToken(joinedUser)).thenReturn(tokenDTO)

            val result = joinService.confirmJoin(requestBody)

            assertEquals(tokenDTO, result.first)
            assertEquals(joinedUser.id, result.second.id)
            verify(redisTemplate).delete(JoinOTPRedisKey(requestBody.email))
            verify(taskAPIProxy).generateAvatar(AvatarGenerateRequest(joinedUser.id!!))
        }

        @Test
        fun `should throw BadOTPException when otp info is missing`() {
            val requestBody = AuthFixtures.joinConfirmDTO(email = "join@loghub.me", otp = "ABC123")
            whenever(valueOperations.get(JoinOTPRedisKey(requestBody.email))).thenReturn(null)

            assertThrows<BadOTPException> {
                joinService.confirmJoin(requestBody)
            }
        }

        @Test
        fun `should throw BadOTPException when otp does not match`() {
            val requestBody = AuthFixtures.joinConfirmDTO(email = "join@loghub.me", otp = "ABC123")
            val info = JoinInfoDTO(
                otp = "DIFF12",
                email = requestBody.email,
                username = "joinuser",
                nickname = "joinuser",
            )
            whenever(valueOperations.get(JoinOTPRedisKey(requestBody.email))).thenReturn(info)

            assertThrows<BadOTPException> {
                joinService.confirmJoin(requestBody)
            }

            verify(redisTemplate, never()).delete(any<String>())
        }
    }
}
