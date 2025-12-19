package me.loghub.api.controller.user

import me.loghub.api.controller.BaseControllerTest
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import org.springframework.web.util.UriComponentsBuilder
import kotlin.test.Test

@TestClassOrder(ClassOrderer.OrderAnnotation::class)
class UserArticleControllerTest : BaseControllerTest() {
    companion object {
        const val MEMBER1_ARTICLE_COUNT = 1
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SearchArticlesForImport {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `searchArticlesForImport - ok`() {
            rest.get().uri("/users/articles/for-import")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.length()").isEqualTo(MEMBER1_ARTICLE_COUNT)
        }

        @Test
        fun `searchArticlesForImport - ok - with params`() {
            val uri = UriComponentsBuilder.fromPath("/users/articles/for-import")
                .queryParam("query", "asdfghjkl")
                .toUriString()
            rest.get().uri(uri)
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0)
        }
    }
}