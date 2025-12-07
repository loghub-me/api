package me.loghub.api.controller.series

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.series.review.PostSeriesReviewDTO
import me.loghub.api.entity.user.User
import me.loghub.api.service.test.TestGrantService
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation::class)
@ActiveProfiles("test")
class SeriesReviewControllerTest(
    @Autowired private val rest: TestRestTemplate,
    @Autowired private val jdbcTemplate: JdbcTemplate,
) {
    lateinit var member1: User
    lateinit var member1Token: TokenDTO
    lateinit var member2: User
    lateinit var member2Token: TokenDTO

    object SeriesReview {
        object Id {
            const val BY_MEMBER1 = 1L
            const val INVALID = 999L
        }
    }

    @BeforeAll
    fun setup(@Autowired grantService: TestGrantService) {
        resetDatabase(jdbcTemplate)
        val (member1, member1Token) = grantService.grant("member1")
        this.member1 = member1
        this.member1Token = member1Token
        val (member2, member2Token) = grantService.grant("member2")
        this.member2 = member2
        this.member2Token = member2Token
    }

    private inline fun <reified T> getReviews(seriesId: Long) =
        rest.getForEntity("/series/${seriesId}/reviews", T::class.java)

    private inline fun <reified T> postReview(
        seriesId: Long,
        body: PostSeriesReviewDTO,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.post("/series/${seriesId}/reviews")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    private inline fun <reified T> editReview(
        seriesId: Long,
        reviewId: Long,
        body: PostSeriesReviewDTO,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.put("/series/${seriesId}/reviews/${reviewId}")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    private inline fun <reified T> deleteReview(
        seriesId: Long,
        reviewId: Long,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.delete("/series/${seriesId}/reviews/${reviewId}")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetReviews {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getReviews - ok`() {
            val response = getReviews<String>(1L)
            assertEquals(HttpStatus.OK, response.statusCode)
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
        fun `postReview - unauthenticated`() {
            val response = postReview<String>(1L, bodyForPost)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `postReview - created`() {
            val response = postReview<String>(2L, bodyForPost, member1Token)
            assertEquals(HttpStatus.CREATED, response.statusCode)
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
        fun `editReview - unauthenticated`() {
            val response = editReview<String>(1L, SeriesReview.Id.BY_MEMBER1, bodyForEdit)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `editReview - forbidden`() {
            val response = editReview<String>(1L, SeriesReview.Id.BY_MEMBER1, bodyForEdit, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `editReview - not_found`() {
            val response = editReview<String>(1L, SeriesReview.Id.INVALID, bodyForEdit, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `editReview - ok`() {
            val response = editReview<String>(1L, SeriesReview.Id.BY_MEMBER1, bodyForEdit, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
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
        fun `deleteReview - unauthenticated`() {
            val response = deleteReview<String>(1L, SeriesReview.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `deleteReview - forbidden`() {
            val response = deleteReview<String>(1L, SeriesReview.Id.BY_MEMBER1, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `deleteReview - not_found`() {
            val response = deleteReview<String>(1L, SeriesReview.Id.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteReview - ok`() {
            val response = deleteReview<String>(1L, SeriesReview.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }
}