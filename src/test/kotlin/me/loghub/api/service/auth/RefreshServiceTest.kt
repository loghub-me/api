package me.loghub.api.service.auth

import me.loghub.api.dto.auth.token.RefreshToken
import me.loghub.api.exception.auth.BadRefreshTokenException
import me.loghub.api.exception.validation.IllegalFieldException
import me.loghub.api.lib.redis.key.auth.RefreshTokenRedisKey
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.service.auth.token.TokenService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration
import java.util.Optional
import kotlin.test.assertEquals

class RefreshServiceTest {
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var valueOperations: ValueOperations<String, String>
    private lateinit var userRepository: UserRepository
    private lateinit var tokenService: TokenService

    private lateinit var refreshService: RefreshService

    @BeforeEach
    fun setUp() {
        redisTemplate = mock()
        valueOperations = mock()
        userRepository = mock()
        tokenService = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        refreshService = RefreshService(redisTemplate, userRepository, tokenService)
    }

    @Nested
    inner class RefreshTokenTest {
        @Test
        fun `should refresh token when refresh token is valid`() {
            val token = "1:refresh-token"
            val redisKey = RefreshTokenRedisKey(token)
            val user = AuthFixtures.user(id = 1L)
            val tokenDTO = AuthFixtures.tokenDTO()
            whenever(valueOperations.get(redisKey)).thenReturn("1")
            whenever(userRepository.findById(1L)).thenReturn(Optional.of(user))
            whenever(tokenService.generateToken(user)).thenReturn(tokenDTO)

            val result = refreshService.refreshToken(token)

            assertEquals(tokenDTO, result.first)
            assertEquals(user.id, result.second.id)
            verify(redisTemplate).expire(eq(redisKey), any<Duration>())
            verify(tokenService).generateToken(user)
        }

        @Test
        fun `should throw IllegalFieldException when token is null`() {
            assertThrows<IllegalFieldException> {
                refreshService.refreshToken(null)
            }
        }

        @Test
        fun `should throw BadRefreshTokenException when token is not found in redis`() {
            val token = "1:refresh-token"
            whenever(valueOperations.get(RefreshTokenRedisKey(token))).thenReturn(null)

            assertThrows<BadRefreshTokenException> {
                refreshService.refreshToken(token)
            }
        }

        @Test
        fun `should throw BadRefreshTokenException when user is not found`() {
            val token = "1:refresh-token"
            val redisKey = RefreshTokenRedisKey(token)
            whenever(valueOperations.get(redisKey)).thenReturn("1")
            whenever(userRepository.findById(1L)).thenReturn(Optional.empty())

            assertThrows<BadRefreshTokenException> {
                refreshService.refreshToken(token)
            }
        }
    }
}
