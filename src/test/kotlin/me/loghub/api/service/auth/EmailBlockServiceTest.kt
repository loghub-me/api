package me.loghub.api.service.auth

import me.loghub.api.dto.auth.email.EmailBlockDTO
import me.loghub.api.exception.auth.token.BadEmailBlockTokenException
import me.loghub.api.lib.redis.key.auth.BlockedEmailRedisKey
import me.loghub.api.lib.redis.key.auth.EmailBlockTokenRedisKey
import me.loghub.api.util.sha256
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EmailBlockServiceTest {
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var valueOperations: ValueOperations<String, String>

    private lateinit var emailBlockService: EmailBlockService

    @BeforeEach
    fun setUp() {
        redisTemplate = mock()
        valueOperations = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        emailBlockService = EmailBlockService(redisTemplate)
    }

    @Nested
    inner class GenerateBlockTokenTest {
        @Test
        fun `should generate block token and store hashed email in redis`() {
            val email = "blocked@loghub.me"

            val token = emailBlockService.generateBlockToken(email)

            assertTrue(token.toString().isNotBlank())
            verify(valueOperations).set(
                eq(EmailBlockTokenRedisKey(token)),
                eq(sha256(email)),
                eq(EmailBlockTokenRedisKey.TTL)
            )
        }
    }

    @Nested
    inner class BlockEmailTest {
        @Test
        fun `should block email when token is valid`() {
            val token = UUID.randomUUID()
            val requestBody = EmailBlockDTO(token)
            val hashedEmail = sha256("blocked@loghub.me")
            whenever(valueOperations.getAndDelete(EmailBlockTokenRedisKey(token))).thenReturn(hashedEmail)

            emailBlockService.blockEmail(requestBody)

            verify(valueOperations).getAndDelete(EmailBlockTokenRedisKey(token))
            verify(valueOperations).set(
                eq(BlockedEmailRedisKey(hashedEmail)),
                eq("true"),
                eq(BlockedEmailRedisKey.TTL)
            )
        }

        @Test
        fun `should throw BadEmailBlockTokenException when token is invalid`() {
            val token = UUID.randomUUID()
            val requestBody = EmailBlockDTO(token)
            whenever(valueOperations.getAndDelete(EmailBlockTokenRedisKey(token))).thenReturn(null)

            assertThrows<BadEmailBlockTokenException> {
                emailBlockService.blockEmail(requestBody)
            }

            verify(valueOperations, never()).set(any(), any(), any<java.time.Duration>())
        }
    }

    @Nested
    inner class IsDeniedEmailTest {
        @Test
        fun `should return true when email is denied`() {
            val email = "denied@loghub.me"
            whenever(redisTemplate.hasKey(BlockedEmailRedisKey(sha256(email)))).thenReturn(true)

            val result = emailBlockService.isDeniedEmail(email)

            assertTrue(result)
            verify(redisTemplate).hasKey(BlockedEmailRedisKey(sha256(email)))
        }

        @Test
        fun `should return false when email is not denied`() {
            val email = "available@loghub.me"
            whenever(redisTemplate.hasKey(BlockedEmailRedisKey(sha256(email)))).thenReturn(false)

            val result = emailBlockService.isDeniedEmail(email)

            assertFalse(result)
            verify(redisTemplate).hasKey(BlockedEmailRedisKey(sha256(email)))
        }

        @Test
        fun `should return false when denied email key check returns null`() {
            val email = "unknown@loghub.me"
            whenever(redisTemplate.hasKey(BlockedEmailRedisKey(sha256(email)))).thenReturn(null)

            val result = emailBlockService.isDeniedEmail(email)

            assertFalse(result)
            verify(redisTemplate).hasKey(BlockedEmailRedisKey(sha256(email)))
        }
    }
}
