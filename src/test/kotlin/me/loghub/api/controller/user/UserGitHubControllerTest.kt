package me.loghub.api.controller.user

import me.loghub.api.controller.BaseControllerTest
import me.loghub.api.dto.user.UpdateUserGitHubDTO
import me.loghub.api.dto.user.UserGitHubDTO
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.client.expectBody
import kotlin.test.Test

@TestClassOrder(ClassOrderer.OrderAnnotation::class)
class UserGitHubControllerTest : BaseControllerTest() {
    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetUserGitHub {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getUserGitHub - unauthorized`() {
            rest.get().uri("/users/github")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `getUserGitHub - ok`() {
            rest.get().uri("/users/github")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
                .expectBody<UserGitHubDTO>()
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class UpdateGitHub {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        val bodyForUpdate = UpdateUserGitHubDTO(
            username = "gymynnym"
        )

        @Test
        fun `updateGitHub - unauthorized`() {
            rest.put().uri("/users/github")
                .body(bodyForUpdate)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `updateGitHub - bad_request - invalid nickname`() {
            rest.put().uri("/users/github")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForUpdate.copy(username = ":)"))
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `updateGitHub - ok`() {
            rest.put().uri("/users/github")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForUpdate)
                .exchange()
                .expectStatus().isOk
        }
    }

    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class DeleteGitHub {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `deleteGitHub - unauthorized`() {
            rest.delete().uri("/users/github")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteGitHub - ok`() {
            rest.delete().uri("/users/github")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
        }
    }
}