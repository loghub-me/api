package me.loghub.api.service.auth.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import me.loghub.api.entity.user.User
import me.loghub.api.service.auth.AuthFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AccessTokenServiceTest {
    private lateinit var accessTokenService: AccessTokenService

    @BeforeEach
    fun setUp() {
        val issuer = "test-issuer"
        val algorithm = Algorithm.HMAC256("secret")
        val verifier = JWT.require(algorithm).withIssuer(issuer).build()
        accessTokenService = AccessTokenService(
            issuer = issuer,
            expiration = 3600L,
            jwtAlgorithm = algorithm,
            jwtVerifier = verifier,
        )
    }

    @Nested
    inner class GenerateTokenTest {
        @Test
        fun `should generate access token and authentication`() {
            val user = AuthFixtures.user(
                id = 1L,
                email = "token@loghub.me",
                username = "tokenuser",
                nickname = "tokenuser",
                provider = User.Provider.LOCAL,
                role = User.Role.ADMIN,
            )

            val token = accessTokenService.generateToken(user)
            val authentication = accessTokenService.generateAuthentication(token.value)
            val principal = authentication.principal as User

            assertTrue(token.value.isNotBlank())
            assertEquals(user.id, principal.id)
            assertEquals(user.email, principal.email)
            assertEquals(user.username, principal.username)
            assertEquals(user.nickname, principal.nickname)
            assertEquals(User.Role.ADMIN, principal.role)
            assertEquals("ROLE_ADMIN", authentication.authorities.first().authority)
        }
    }

    @Nested
    inner class GeneratePrincipalTest {
        @Test
        fun `should throw IllegalArgumentException when required claims are missing`() {
            val decodedToken = mock<DecodedJWT>()
            whenever(decodedToken.claims).thenReturn(emptyMap())
            whenever(decodedToken.subject).thenReturn("1")

            assertThrows<IllegalArgumentException> {
                accessTokenService.generatePrincipal(decodedToken)
            }
        }
    }

    @Nested
    inner class GenerateAuthoritiesTest {
        @Test
        fun `should generate spring authorities from role claim`() {
            val roleClaim = mock<Claim>()
            whenever(roleClaim.asString()).thenReturn("MEMBER")

            val result = accessTokenService.generateAuthorities(mapOf("role" to roleClaim))

            assertEquals(1, result.size)
            assertEquals("ROLE_MEMBER", result.first().authority)
        }
    }
}
