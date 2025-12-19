package me.loghub.api.controller.user

import me.loghub.api.controller.BaseControllerTest
import me.loghub.api.dto.user.UpdateUsernameDTO
import me.loghub.api.dto.user.UserDetailDTO
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.client.expectBody
import kotlin.test.Test

@TestClassOrder(ClassOrderer.OrderAnnotation::class)
class UserControllerTest : BaseControllerTest() {
    object Username {
        const val INVALID = "invalid_member"
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetUser {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getUser - not_found`() {
            rest.get().uri("/users/@${Username.INVALID}")
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `getUser - ok`() {
            rest.get().uri("/users/@${member1.username}")
                .exchange()
                .expectStatus().isOk
                .expectBody<UserDetailDTO>()
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class UpdateUsername {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        val bodyForUpdate = UpdateUsernameDTO(newUsername = "member1new")

        @Test
        fun `updateUsername - unauthorized`() {
            rest.put().uri("/users/username")
                .body(bodyForUpdate)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `updateUsername - bad_request - invalid`() {
            rest.put().uri("/users/username")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(UpdateUsernameDTO(newUsername = "invalid_username!"))
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `updateUsername - ok`() {
            rest.put().uri("/users/username")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForUpdate)
                .exchange()
                .expectStatus().isOk
        }
    }
}