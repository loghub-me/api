package me.loghub.api.controller.article

import me.loghub.api.controller.BaseControllerTest
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import kotlin.test.Test

@TestClassOrder(ClassOrderer.OrderAnnotation::class)
class ArticleStarControllerTest : BaseControllerTest() {
    object Article {
        object Id {
            const val BY_MEMBER1 = 1L
            const val BY_MEMBER2 = 2L
            const val INVALID = 999L
        }
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class ExistsArticleStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `existsArticleStar - unauthorized`() {
            rest.get().uri("/articles/star/${Article.Id.BY_MEMBER1}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `existsArticleStar - not_found`() {
            rest.get().uri("/articles/star/${Article.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.data").isEqualTo(false)
        }

        @Test
        fun `existsArticleStar - ok`() {
            rest.get().uri("/articles/star/${Article.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.data").isEqualTo(true)
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class AddArticleStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `addArticleStar - unauthorized`() {
            rest.post().uri("/articles/star/${Article.Id.BY_MEMBER2}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `addArticleStar - not_found`() {
            rest.post().uri("/articles/star/${Article.Id.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `addArticleStar - conflict`() {
            rest.post().uri("/articles/star/${Article.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
        }

        @Test
        fun `addArticleStar - created`() {
            rest.post().uri("/articles/star/${Article.Id.BY_MEMBER2}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isCreated
        }
    }

    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteArticleStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `deleteArticleStar - unauthorized`() {
            rest.delete().uri("/articles/star/${Article.Id.BY_MEMBER1}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `deleteArticleStar - not_found`() {
            rest.delete().uri("/articles/star/${Article.Id.BY_MEMBER2}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteArticleStar - ok`() {
            rest.delete().uri("/articles/star/${Article.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
        }
    }
}