package me.loghub.api.controller.article

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.response.DataResponseBody
import me.loghub.api.service.test.TestGrantService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(
    scripts = ["/database/data/truncate.sql", "/database/data/test.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
)
class ArticleStarControllerTest(@Autowired private val rest: TestRestTemplate) {
    companion object {
        lateinit var member1: TokenDTO;
        lateinit var member2: TokenDTO;

        object ArticleId {
            const val BY_MEMBER1 = 1L
            const val BY_MEMBER2 = 2L
            const val INVALID = 999L
        }

        @JvmStatic
        @BeforeAll
        fun setup(@Autowired grantService: TestGrantService) {
            member1 = grantService.generateToken("member1")
            member2 = grantService.generateToken("member2")
        }
    }

    @Test
    fun `existsArticleStar - unauthorized`() {
        val response = existsArticleStar<String>(ArticleId.BY_MEMBER1)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `existsArticleStar - not found`() {
        val response = existsArticleStar<DataResponseBody<Boolean>>(ArticleId.BY_MEMBER1, member2)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(false, response.body!!.data)
    }

    @Test
    fun `existsArticleStar - ok`() {
        val response = existsArticleStar<DataResponseBody<Boolean>>(ArticleId.BY_MEMBER1, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(true, response.body!!.data)
    }

    @Test
    fun `addArticleStar - unauthorized`() {
        val response = addArticleStar<String>(ArticleId.BY_MEMBER2)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `addArticleStar - not found`() {
        val response = addArticleStar<String>(ArticleId.INVALID, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `addArticleStar - conflict`() {
        val response = addArticleStar<String>(ArticleId.BY_MEMBER1, member1)
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }

    @Test
    fun `addArticleStar - created`() {
        val response = addArticleStar<String>(ArticleId.BY_MEMBER2, member1)
        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun `deleteArticleStar - unauthorized`() {
        val response = deleteArticleStar<String>(ArticleId.BY_MEMBER1)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `deleteArticleStar - not found`() {
        val response = deleteArticleStar<String>(ArticleId.BY_MEMBER2, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `deleteArticleStar - ok`() {
        val response = deleteArticleStar<String>(ArticleId.BY_MEMBER1, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    private inline fun <reified T> existsArticleStar(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.get("/articles/star/$id")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> addArticleStar(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.post("/articles/star/$id")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> deleteArticleStar(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.delete("/articles/star/$id")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }
}