package me.loghub.api.controller.user

import me.loghub.api.controller.BaseControllerTest
import me.loghub.api.dto.user.UpdateUserProfileDTO
import me.loghub.api.dto.user.UserProfileDTO
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.client.expectBody
import kotlin.test.Test

@TestClassOrder(ClassOrderer.OrderAnnotation::class)
class UserProfileControllerTest : BaseControllerTest() {
    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetUserProfile {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getUserProfile - unauthorized`() {
            rest.get().uri("/users/profile")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `getUserProfile - ok`() {
            rest.get().uri("/users/profile")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
                .expectBody<UserProfileDTO>()
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class UpdateProfile {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        val bodyForUpdate = UpdateUserProfileDTO(
            nickname = "NewNickname",
            readme = "This is my new readme.",
        )

        @Test
        fun `updateProfile - unauthorized`() {
            rest.put().uri("/users/profile")
                .body(bodyForUpdate)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `updateProfile - bad_request - invalid nickname`() {
            rest.put().uri("/users/profile")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForUpdate.copy(nickname = "New_Nickname"))
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `updateProfile - ok`() {
            rest.put().uri("/users/profile")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForUpdate)
                .exchange()
                .expectStatus().isOk
        }
    }
}