package me.loghub.api.controller.user

import me.loghub.api.controller.BaseControllerTest
import me.loghub.api.dto.user.UpdateUserPrivacyDTO
import me.loghub.api.dto.user.UserPrivacyDTO
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.client.expectBody
import kotlin.test.Test

@TestClassOrder(ClassOrderer.OrderAnnotation::class)
class UserPrivacyControllerTest : BaseControllerTest() {
    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetUserPrivacy {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getUserPrivacy - unauthorized`() {
            rest.get().uri("/users/privacy")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `getUserPrivacy - ok`() {
            rest.get().uri("/users/privacy")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
                .expectBody<UserPrivacyDTO>()
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class UpdatePrivacy {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        val bodyForUpdate = UpdateUserPrivacyDTO(emailPublic = true)

        @Test
        fun `updatePrivacy - unauthorized`() {
            rest.put().uri("/users/privacy")
                .body(bodyForUpdate)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `updatePrivacy - ok`() {
            rest.put().uri("/users/privacy")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForUpdate)
                .exchange()
                .expectStatus().isOk
        }
    }
}