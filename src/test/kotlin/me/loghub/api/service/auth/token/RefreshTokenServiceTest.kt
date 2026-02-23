package me.loghub.api.service.auth.token

import me.loghub.api.lib.redis.key.auth.RefreshTokenRedisKey
import me.loghub.api.service.auth.AuthFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import kotlin.test.assertTrue

class RefreshTokenServiceTest {
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var valueOperations: ValueOperations<String, String>

    private lateinit var refreshTokenService: RefreshTokenService

    @BeforeEach
    fun setUp() {
        redisTemplate = mock()
        valueOperations = mock()

        org.mockito.kotlin.whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        refreshTokenService = RefreshTokenService(redisTemplate)
    }

    @Nested
    inner class GenerateTokenTest {
        @Test
        fun `should generate refresh token and store it in redis`() {
            val user = AuthFixtures.user(id = 7L)

            val token = refreshTokenService.generateToken(user)

            assertTrue(token.value.startsWith("7:"))
            verify(valueOperations).set(
                eq(RefreshTokenRedisKey(token.value)),
                eq("7"),
                eq(RefreshTokenRedisKey.TTL)
            )
        }
    }

    @Nested
    inner class RevokeTokenTest {
        @Test
        fun `should revoke refresh token by deleting redis key`() {
            val token = "7:refresh-token"

            refreshTokenService.revokeToken(token)

            verify(redisTemplate).delete(RefreshTokenRedisKey(token))
        }
    }
}
