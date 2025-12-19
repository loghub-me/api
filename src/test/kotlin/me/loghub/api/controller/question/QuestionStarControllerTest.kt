package me.loghub.api.controller.question

import me.loghub.api.controller.BaseControllerTest
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import kotlin.test.Test

@TestClassOrder(ClassOrderer.OrderAnnotation::class)
class QuestionStarControllerTest : BaseControllerTest() {
    object Question {
        object Id {
            const val BY_MEMBER1 = 1L
            const val BY_MEMBER2 = 2L
            const val INVALID = 999L
        }
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class ExistsQuestionStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `existsQuestionStar - unauthorized`() {
            rest.get().uri("/questions/star/${Question.Id.BY_MEMBER1}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `existsQuestionStar - not_found`() {
            rest.get().uri("/questions/star/${Question.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.data").isEqualTo(false)
        }

        @Test
        fun `existsQuestionStar - ok`() {
            rest.get().uri("/questions/star/${Question.Id.BY_MEMBER1}")
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
    inner class AddQuestionStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `addQuestionStar - unauthorized`() {
            rest.post().uri("/questions/star/${Question.Id.BY_MEMBER2}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `addQuestionStar - not_found`() {
            rest.post().uri("/questions/star/${Question.Id.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `addQuestionStar - conflict`() {
            rest.post().uri("/questions/star/${Question.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
        }

        @Test
        fun `addQuestionStar - created`() {
            rest.post().uri("/questions/star/${Question.Id.BY_MEMBER2}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isCreated
        }
    }

    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteQuestionStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `deleteQuestionStar - unauthorized`() {
            rest.delete().uri("/questions/star/${Question.Id.BY_MEMBER1}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `deleteQuestionStar - not_found`() {
            rest.delete().uri("/questions/star/${Question.Id.BY_MEMBER2}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteQuestionStar - ok`() {
            rest.delete().uri("/questions/star/${Question.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
        }
    }
}