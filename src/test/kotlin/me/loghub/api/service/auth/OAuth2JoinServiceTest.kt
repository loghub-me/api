package me.loghub.api.service.auth

import me.loghub.api.dto.auth.join.OAuth2JoinInfoDTO
import me.loghub.api.dto.task.avatar.AvatarGenerateRequest
import me.loghub.api.entity.user.User
import me.loghub.api.exception.auth.BadOTPException
import me.loghub.api.lib.redis.key.auth.OAuth2JoinTokenRedisKey
import me.loghub.api.proxy.TaskAPIProxy
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.token.TokenService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OAuth2JoinServiceTest {
    private lateinit var userRepository: UserRepository
    private lateinit var tokenService: TokenService
    private lateinit var redisTemplate: RedisTemplate<String, OAuth2JoinInfoDTO>
    private lateinit var valueOperations: ValueOperations<String, OAuth2JoinInfoDTO>
    private lateinit var taskAPIProxy: TaskAPIProxy

    private lateinit var oAuth2JoinService: OAuth2JoinService

    @BeforeEach
    fun setUp() {
        userRepository = mock()
        tokenService = mock()
        redisTemplate = mock()
        valueOperations = mock()
        taskAPIProxy = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        oAuth2JoinService = OAuth2JoinService(
            userRepository,
            tokenService,
            redisTemplate,
            taskAPIProxy
        )
    }

    @Nested
    inner class IssueTokenTest {
        @Test
        fun `should issue oauth2 join token and store info in redis`() {
            val email = "oauth@loghub.me"

            val token = oAuth2JoinService.issueToken(email, User.Provider.GITHUB)

            val infoCaptor = argumentCaptor<OAuth2JoinInfoDTO>()
            verify(valueOperations).set(
                eq(OAuth2JoinTokenRedisKey(email)),
                infoCaptor.capture(),
                eq(OAuth2JoinTokenRedisKey.TTL)
            )
            assertTrue(token.isNotBlank())
            assertEquals(token, infoCaptor.firstValue.token)
            assertEquals(email, infoCaptor.firstValue.email)
            assertEquals(User.Provider.GITHUB, infoCaptor.firstValue.provider)
        }
    }

    @Nested
    inner class ConfirmJoinTest {
        @Test
        fun `should confirm oauth2 join when token is valid`() {
            val requestBody = AuthFixtures.oauth2JoinConfirmDTO(
                email = "oauth@loghub.me",
                token = "123e4567-e89b-12d3-a456-426614174000"
            )
            val info = OAuth2JoinInfoDTO(
                token = requestBody.token,
                email = requestBody.email,
                provider = User.Provider.GITHUB,
            )
            val joinedUser = requestBody.toUserEntity(User.Provider.GITHUB).apply { id = 11L }
            val tokenDTO = AuthFixtures.tokenDTO()
            whenever(valueOperations.get(OAuth2JoinTokenRedisKey(requestBody.email))).thenReturn(info)
            whenever(userRepository.save(any<me.loghub.api.entity.user.User>())).thenReturn(joinedUser)
            whenever(tokenService.generateToken(joinedUser)).thenReturn(tokenDTO)

            val result = oAuth2JoinService.confirmJoin(requestBody)

            assertEquals(tokenDTO, result.first)
            assertEquals(joinedUser.id, result.second.id)
            verify(redisTemplate).delete(OAuth2JoinTokenRedisKey(requestBody.email))
            verify(taskAPIProxy).generateAvatar(AvatarGenerateRequest(joinedUser.id!!))
        }

        @Test
        fun `should throw BadOTPException when join token info does not exist`() {
            val requestBody = AuthFixtures.oauth2JoinConfirmDTO()
            whenever(valueOperations.get(OAuth2JoinTokenRedisKey(requestBody.email))).thenReturn(null)

            assertThrows<BadOTPException> {
                oAuth2JoinService.confirmJoin(requestBody)
            }
        }

        @Test
        fun `should throw BadOTPException when join token mismatches`() {
            val requestBody = AuthFixtures.oauth2JoinConfirmDTO(
                token = "123e4567-e89b-12d3-a456-426614174000"
            )
            val info = OAuth2JoinInfoDTO(
                token = "223e4567-e89b-12d3-a456-426614174000",
                email = requestBody.email,
                provider = User.Provider.GITHUB,
            )
            whenever(valueOperations.get(OAuth2JoinTokenRedisKey(requestBody.email))).thenReturn(info)

            assertThrows<BadOTPException> {
                oAuth2JoinService.confirmJoin(requestBody)
            }

            verify(redisTemplate, never()).delete(any<String>())
        }
    }
}
