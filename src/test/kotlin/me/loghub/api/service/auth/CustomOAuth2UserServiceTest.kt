package me.loghub.api.service.auth

import me.loghub.api.dto.auth.oauth2.OAuth2UserAttributes
import me.loghub.api.entity.user.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.reflect.InvocationTargetException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CustomOAuth2UserServiceTest {
    private lateinit var customOAuth2UserService: CustomOAuth2UserService

    @BeforeEach
    fun setUp() {
        customOAuth2UserService = CustomOAuth2UserService()
    }

    @Nested
    inner class ExtractAttributesTest {
        @Test
        fun `should extract google oauth2 attributes`() {
            val result = extractAttributes(
                registrationId = User.Provider.GOOGLE.registrationId,
                attributes = mapOf("email" to "google@loghub.me")
            )

            assertEquals("google@loghub.me", result.email)
            assertEquals(User.Provider.GOOGLE, result.provider)
        }

        @Test
        fun `should extract github oauth2 attributes`() {
            val result = extractAttributes(
                registrationId = User.Provider.GITHUB.registrationId,
                attributes = mapOf("email" to "github@loghub.me")
            )

            assertEquals("github@loghub.me", result.email)
            assertEquals(User.Provider.GITHUB, result.provider)
        }

        @Test
        fun `should throw UnsupportedOperationException for unsupported provider`() {
            val exception = assertThrows<InvocationTargetException> {
                extractAttributes(
                    registrationId = "kakao",
                    attributes = mapOf("email" to "kakao@loghub.me")
                )
            }
            assertTrue(exception.cause is UnsupportedOperationException)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun extractAttributes(registrationId: String, attributes: Map<String, Any>): OAuth2UserAttributes {
        val method = CustomOAuth2UserService::class.java.getDeclaredMethod(
            "extractAttributes",
            String::class.java,
            Map::class.java
        )
        method.isAccessible = true
        return method.invoke(customOAuth2UserService, registrationId, attributes) as OAuth2UserAttributes
    }
}
