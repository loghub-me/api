package me.loghub.api.service.auth.token

import me.loghub.api.dto.auth.token.AccessToken
import me.loghub.api.dto.auth.token.RefreshToken
import me.loghub.api.entity.user.User
import me.loghub.api.service.auth.AuthFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class TokenServiceTest {
    private lateinit var accessTokenService: AccessTokenService
    private lateinit var refreshTokenService: RefreshTokenService

    private lateinit var tokenService: TokenService

    @BeforeEach
    fun setUp() {
        accessTokenService = mock()
        refreshTokenService = mock()

        tokenService = TokenService(accessTokenService, refreshTokenService)
    }

    @Test
    fun `should generate token dto using access and refresh token services`() {
        val user = AuthFixtures.user(id = 1L)
        val accessToken = AccessToken("access")
        val refreshToken = RefreshToken("1:refresh")
        whenever(accessTokenService.generateToken(user)).thenReturn(accessToken)
        whenever(refreshTokenService.generateToken(user)).thenReturn(refreshToken)

        val result = tokenService.generateToken(user)

        assertEquals(accessToken, result.accessToken)
        assertEquals(refreshToken, result.refreshToken)
        verify(accessTokenService).generateToken(user)
        verify(refreshTokenService).generateToken(user)
    }
}
