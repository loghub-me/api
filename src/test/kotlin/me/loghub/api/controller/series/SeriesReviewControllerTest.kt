package me.loghub.api.controller.series

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.series.review.PostSeriesReviewDTO
import me.loghub.api.service.test.TestGrantService
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(
    scripts = ["/database/data/truncate.sql", "/database/data/test.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
)
class SeriesReviewControllerTest(@Autowired private val rest: TestRestTemplate) {
    companion object {
        lateinit var member1: TokenDTO;
        lateinit var member2: TokenDTO;

        object SeriesReviewId {
            const val BY_MEMBER1 = 1L
            const val INVALID = 999L
        }

        val bodyForPost = PostSeriesReviewDTO(content = "New review", rating = 5)

        @JvmStatic
        @BeforeAll
        fun setup(@Autowired grantService: TestGrantService) {
            member1 = grantService.generateToken("member1")
            member2 = grantService.generateToken("member2")
        }
    }

    @Test
    fun getReviews() {
        val response = getReviews<String>(1L)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `postReview - unauthenticated`() {
        val response = postReview<String>(1L, bodyForPost)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `postReview - conflict`() {
        val response = postReview<String>(1L, bodyForPost, member1)
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }

    @Test
    fun `postReview - created`() {
        val response = postReview<String>(2L, bodyForPost, member1)
        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun `deleteReview - unauthenticated`() {
        val response = deleteReview<String>(1L, SeriesReviewId.BY_MEMBER1)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `deleteReview - forbidden`() {
        val response = deleteReview<String>(1L, SeriesReviewId.BY_MEMBER1, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `deleteReview - not found`() {
        val response = deleteReview<String>(1L, SeriesReviewId.INVALID, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `deleteReview - ok`() {
        val response = deleteReview<String>(1L, SeriesReviewId.BY_MEMBER1, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
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

    private inline fun <reified T> deleteReview(
        seriesId: Long,
        reviewId: Long,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.delete("/series/${seriesId}/reviews/${reviewId}")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }
}