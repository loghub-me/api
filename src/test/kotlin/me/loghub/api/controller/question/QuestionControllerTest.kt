package me.loghub.api.controller.question

import me.loghub.api.controller.BaseControllerTest
import me.loghub.api.dto.question.PostQuestionDTO
import me.loghub.api.dto.question.QuestionDetailDTO
import me.loghub.api.dto.question.QuestionForEditDTO
import me.loghub.api.dto.question.QuestionSort
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.client.expectBody
import org.springframework.web.util.UriComponentsBuilder
import kotlin.test.Test

@TestClassOrder(ClassOrderer.OrderAnnotation::class)
class QuestionControllerTest : BaseControllerTest() {
    object Question {
        object Id {
            const val BY_MEMBER1 = 1L
            const val INVALID = 999L
        }

        object Slug {
            const val BY_MEMBER1 = "question-1"
            const val INVALID = "unknown-question"
        }
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SearchQuestions {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `searchQuestions - ok - no params`() {
            rest.get().uri("/questions")
                .exchange()
                .expectStatus().isOk
        }

        @Test
        fun `searchQuestions - ok - with params`() {
            val uri = UriComponentsBuilder.fromPath("/questions")
                .queryParam("query", "question")
                .queryParam("sort", QuestionSort.trending)
                .queryParam("page", 1)
                .toUriString()
            rest.get().uri(uri)
                .exchange()
                .expectStatus().isOk
        }

        @Test
        fun `searchQuestions - bad_request - invalid sort`() {
            val uri = UriComponentsBuilder.fromPath("/questions")
                .queryParam("sort", "foo")
                .toUriString()
            rest.get().uri(uri)
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `searchQuestions - bad_request - invalid page`() {
            val uri = UriComponentsBuilder.fromPath("/questions")
                .queryParam("page", 0)
                .toUriString()
            rest.get().uri(uri)
                .exchange()
                .expectStatus().isBadRequest
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetQuestion {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getQuestion - not_found`() {
            rest.get().uri("/questions/@${member1.username}/${Question.Slug.INVALID}")
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `getQuestion - ok`() {
            rest.get().uri("/questions/@${member1.username}/${Question.Slug.BY_MEMBER1}")
                .exchange()
                .expectStatus().isOk
                .expectBody<QuestionDetailDTO>()
        }
    }

    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetQuestionForEdit {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getQuestionForEdit - unauthorized`() {
            rest.get().uri("/questions/${Question.Id.BY_MEMBER1}/for-edit")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `getQuestionForEdit - forbidden`() {
            rest.get().uri("/questions/${Question.Id.BY_MEMBER1}/for-edit")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `getQuestionForEdit - not_found`() {
            rest.get().uri("/questions/${Question.Id.INVALID}/for-edit")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `getQuestionForEdit - ok`() {
            rest.get().uri("/questions/${Question.Id.BY_MEMBER1}/for-edit")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
                .expectBody<QuestionForEditDTO>()
        }
    }

    @Nested
    @Order(4)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PostQuestion {
        val bodyForPost = PostQuestionDTO(
            title = "New Question",
            content = "This is a new question.",
            topicSlugs = listOf("java", "spring"),
        )

        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `postQuestion - unauthorized`() {
            rest.post().uri("/questions")
                .body(bodyForPost)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `postQuestion - bad_request - short title`() {
            rest.post().uri("/questions")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForPost.copy(title = "f"))
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `postQuestion - bad_request - empty content`() {
            rest.post().uri("/questions")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForPost.copy(content = ""))
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `postQuestion - created`() {
            rest.post().uri("/questions")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForPost)
                .exchange()
                .expectStatus().isCreated
        }
    }

    @Nested
    @Order(5)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class EditQuestion {
        val bodyForEdit = PostQuestionDTO(
            title = "Edited Question",
            content = "This is an edited question.",
            topicSlugs = listOf("kotlin", "spring-boot"),
        )

        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `editQuestion - unauthorized`() {
            rest.put().uri("/questions/${Question.Id.BY_MEMBER1}")
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `editQuestion - forbidden`() {
            rest.put().uri("/questions/${Question.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `editQuestion - not_found`() {
            rest.put().uri("/questions/${Question.Id.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `editQuestion - bad_request - short title`() {
            rest.put().uri("/questions/${Question.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit.copy(title = "f"))
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `editQuestion - ok`() {
            rest.put().uri("/questions/${Question.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isOk
        }
    }

    @Nested
    @Order(6)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteQuestion {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `deleteQuestion - unauthorized`() {
            rest.delete().uri("/questions/${Question.Id.BY_MEMBER1}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `deleteQuestion - forbidden`() {
            rest.delete().uri("/questions/${Question.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `deleteQuestion - not_found`() {
            rest.delete().uri("/questions/${Question.Id.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteQuestion - ok`() {
            rest.delete().uri("/questions/${Question.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
        }
    }

    @Nested
    @Order(7)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class CloseQuestion {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `closeQuestion - unauthorized`() {
            rest.post().uri("/questions/${Question.Id.BY_MEMBER1}/close")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `closeQuestion - forbidden`() {
            rest.post().uri("/questions/${Question.Id.BY_MEMBER1}/close")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `closeQuestion - not_found`() {
            rest.post().uri("/questions/${Question.Id.INVALID}/close")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `closeQuestion - ok`() {
            rest.post().uri("/questions/${Question.Id.BY_MEMBER1}/close")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
        }
    }
}