package me.loghub.api.controller.series

import me.loghub.api.controller.BaseControllerTest
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import kotlin.test.Test

@TestClassOrder(ClassOrderer.OrderAnnotation::class)
class SeriesStarControllerTest : BaseControllerTest() {
    object Series {
        object Id {
            const val BY_MEMBER1 = 1L
            const val BY_MEMBER2 = 2L
            const val INVALID = 999L
        }
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class ExistsSeriesStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `existsSeriesStar - unauthorized`() {
            rest.get().uri("/series/star/${Series.Id.BY_MEMBER1}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `existsSeriesStar - not_found`() {
            rest.get().uri("/series/star/${Series.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.data").isEqualTo(false)
        }

        @Test
        fun `existsSeriesStar - ok`() {
            rest.get().uri("/series/star/${Series.Id.BY_MEMBER1}")
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
    inner class AddSeriesStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `addSeriesStar - unauthorized`() {
            rest.post().uri("/series/star/${Series.Id.BY_MEMBER2}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `addSeriesStar - not_found`() {
            rest.post().uri("/series/star/${Series.Id.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `addSeriesStar - conflict`() {
            rest.post().uri("/series/star/${Series.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
        }

        @Test
        fun `addSeriesStar - created`() {
            rest.post().uri("/series/star/${Series.Id.BY_MEMBER2}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isCreated
        }
    }

    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteSeriesStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `deleteSeriesStar - unauthorized`() {
            rest.delete().uri("/series/star/${Series.Id.BY_MEMBER1}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `deleteSeriesStar - not_found`() {
            rest.delete().uri("/series/star/${Series.Id.BY_MEMBER2}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteSeriesStar - ok`() {
            rest.delete().uri("/series/star/${Series.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
        }
    }
}