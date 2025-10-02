package me.loghub.api.controller.question

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.question.answer.PostQuestionAnswerDTO
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
class QuestionAnswerControllerTest(@Autowired private val rest: TestRestTemplate) {
    companion object {
        lateinit var member1: TokenDTO;
        lateinit var member2: TokenDTO;

        object QuestionAnswerId {
            const val BY_MEMBER2 = 1L
            const val INVALID = 999L
        }

        val bodyForPost = PostQuestionAnswerDTO(title = "New Answer", content = "New Answer Content")

        @JvmStatic
        @BeforeAll
        fun setup(@Autowired grantService: TestGrantService) {
            member1 = grantService.generateToken("member1")
            member2 = grantService.generateToken("member2")
        }
    }

    @Test
    fun getAnswers() {
        val response = getAnswers<String>(1L)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `postAnswer - unauthenticated`() {
        val response = postAnswer<String>(1L, bodyForPost)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `postAnswer - conflict`() {
        val response = postAnswer<String>(1L, bodyForPost, member1)
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }

    @Test
    fun `postAnswer - created`() {
        val response = postAnswer<String>(1L, bodyForPost, member2)
        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun `editAnswer - unauthenticated`() {
        val response = editAnswer<String>(1L, QuestionAnswerId.BY_MEMBER2, bodyForPost)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `editAnswer - forbidden`() {
        val response = editAnswer<String>(1L, QuestionAnswerId.BY_MEMBER2, bodyForPost, member1)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `editAnswer - not found`() {
        val response = editAnswer<String>(1L, QuestionAnswerId.INVALID, bodyForPost, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `editAnswer - ok`() {
        val response = editAnswer<String>(1L, QuestionAnswerId.BY_MEMBER2, bodyForPost, member2)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `deleteAnswer - unauthenticated`() {
        val response = deleteAnswer<String>(1L, QuestionAnswerId.BY_MEMBER2)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `deleteAnswer - forbidden`() {
        val response = deleteAnswer<String>(1L, QuestionAnswerId.BY_MEMBER2, member1)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `deleteAnswer - not found`() {
        val response = deleteAnswer<String>(1L, QuestionAnswerId.INVALID, member2)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `deleteAnswer - ok`() {
        val response = deleteAnswer<String>(1L, QuestionAnswerId.BY_MEMBER2, member2)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `acceptAnswer - unauthenticated`() {
        val response = acceptAnswer<String>(1L, QuestionAnswerId.BY_MEMBER2)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `acceptAnswer - forbidden`() {
        val response = acceptAnswer<String>(1L, QuestionAnswerId.BY_MEMBER2, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `acceptAnswer - not found`() {
        val response = acceptAnswer<String>(1L, QuestionAnswerId.INVALID, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `acceptAnswer - ok`() {
        val response = acceptAnswer<String>(1L, QuestionAnswerId.BY_MEMBER2, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    private inline fun <reified T> getAnswers(questionId: Long) =
        rest.getForEntity("/questions/${questionId}/answers", T::class.java)

    private inline fun <reified T> postAnswer(
        questionId: Long,
        body: PostQuestionAnswerDTO,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.post("/questions/${questionId}/answers")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    private inline fun <reified T> editAnswer(
        questionId: Long,
        answerId: Long,
        body: PostQuestionAnswerDTO,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.put("/questions/${questionId}/answers/${answerId}")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    private inline fun <reified T> deleteAnswer(
        questionId: Long,
        answerId: Long,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.delete("/questions/${questionId}/answers/${answerId}")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> acceptAnswer(
        questionId: Long,
        answerId: Long,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.post("/questions/${questionId}/answers/${answerId}/accept")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }
}