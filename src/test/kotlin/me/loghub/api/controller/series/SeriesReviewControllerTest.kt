package me.loghub.api.controller.series

import me.loghub.api.controller.BaseControllerTest
import me.loghub.api.dto.series.review.PostSeriesReviewDTO
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import kotlin.test.Test

@TestClassOrder(ClassOrderer.OrderAnnotation::class)
class SeriesReviewControllerTest : BaseControllerTest() {
    object SeriesReview {
        object Id {
            const val BY_MEMBER1 = 1L
            const val INVALID = 999L
        }
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetReviews {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getReviews - ok`() {
            rest.get().uri("/series/1/reviews")
                .exchange()
                .expectStatus().isOk
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class PostReview {
        val bodyForPost = PostSeriesReviewDTO(
            content = "This is a new review.",
            rating = 3
        )

        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `postReview - unauthorized`() {
            rest.post().uri("/series/1/reviews")
                .body(bodyForPost)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `postReview - created`() {
            rest.post().uri("/series/2/reviews")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForPost)
                .exchange()
                .expectStatus().isCreated
        }
    }

    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class EditReview {
        val bodyForEdit = PostSeriesReviewDTO(
            content = "This is a edited review.",
            rating = 4,
        )

        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `editReview - unauthorized`() {
            rest.put().uri("/series/1/reviews/${SeriesReview.Id.BY_MEMBER1}")
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `editReview - forbidden`() {
            rest.put().uri("/series/1/reviews/${SeriesReview.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `editReview - not_found`() {
            rest.put().uri("/series/1/reviews/${SeriesReview.Id.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `editReview - ok`() {
            rest.put().uri("/series/1/reviews/${SeriesReview.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isOk
        }
    }

    @Nested
    @Order(4)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteReview {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `deleteReview - unauthorized`() {
            rest.delete().uri("/series/1/reviews/${SeriesReview.Id.BY_MEMBER1}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `deleteReview - forbidden`() {
            rest.delete().uri("/series/1/reviews/${SeriesReview.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `deleteReview - not_found`() {
            rest.delete().uri("/series/1/reviews/${SeriesReview.Id.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteReview - ok`() {
            rest.delete().uri("/series/1/reviews/${SeriesReview.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
        }
    }
}