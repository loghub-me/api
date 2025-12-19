package me.loghub.api.controller.question

import me.loghub.api.controller.BaseControllerTest
import me.loghub.api.dto.question.answer.PostQuestionAnswerDTO
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import kotlin.test.Test

@TestClassOrder(ClassOrderer.OrderAnnotation::class)
class QuestionAnswerControllerTest : BaseControllerTest() {
    object QuestionAnswer {
        object Id {
            const val BY_MEMBER2 = 1L
            const val INVALID = 999L
        }
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetAnswers {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getAnswers - ok`() {
            rest.get().uri("/questions/1/answers")
                .exchange()
                .expectStatus().isOk
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PostAnswer {
        val bodyForPost = PostQuestionAnswerDTO(
            title = "New Answer",
            content = "This is a new answer.",
        )

        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `postAnswer - unauthorized`() {
            rest.post().uri("/questions/1/answers")
                .body(bodyForPost)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `postAnswer - conflict`() {
            rest.post().uri("/questions/1/answers")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForPost)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
        }

        @Test
        fun `postAnswer - created`() {
            rest.post().uri("/questions/1/answers")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .body(bodyForPost)
                .exchange()
                .expectStatus().isCreated
        }
    }

    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class EditAnswer {
        val bodyForEdit = PostQuestionAnswerDTO(
            title = "Edited Article",
            content = "This is a edited answer.",
        )

        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `editAnswer - unauthorized`() {
            rest.put().uri("/questions/1/answers/${QuestionAnswer.Id.BY_MEMBER2}")
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `editAnswer - forbidden`() {
            rest.put().uri("/questions/1/answers/${QuestionAnswer.Id.BY_MEMBER2}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `editAnswer - not_found`() {
            rest.put().uri("/questions/1/answers/${QuestionAnswer.Id.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `editAnswer - ok`() {
            rest.put().uri("/questions/1/answers/${QuestionAnswer.Id.BY_MEMBER2}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isOk
        }
    }

    @Nested
    @Order(4)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteAnswer {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `deleteAnswer - unauthorized`() {
            rest.delete().uri("/questions/1/answers/${QuestionAnswer.Id.BY_MEMBER2}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `deleteAnswer - forbidden`() {
            rest.delete().uri("/questions/1/answers/${QuestionAnswer.Id.BY_MEMBER2}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `deleteAnswer - not_found`() {
            rest.delete().uri("/questions/1/answers/${QuestionAnswer.Id.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteAnswer - ok`() {
            rest.delete().uri("/questions/1/answers/${QuestionAnswer.Id.BY_MEMBER2}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isOk
        }
    }

    @Nested
    @Order(5)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class AcceptAnswer {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `acceptAnswer - unauthorized`() {
            rest.post().uri("/questions/1/answers/${QuestionAnswer.Id.BY_MEMBER2}/accept")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `acceptAnswer - forbidden`() {
            rest.post().uri("/questions/1/answers/${QuestionAnswer.Id.BY_MEMBER2}/accept")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `acceptAnswer - not_found`() {
            rest.post().uri("/questions/1/answers/${QuestionAnswer.Id.INVALID}/accept")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `acceptAnswer - ok`() {
            rest.post().uri("/questions/1/answers/${QuestionAnswer.Id.BY_MEMBER2}/accept")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
        }
    }
}