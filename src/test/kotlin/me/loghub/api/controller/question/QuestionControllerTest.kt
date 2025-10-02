package me.loghub.api.controller.question

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.question.*
import me.loghub.api.service.test.TestGrantService
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.domain.Page
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.util.UriComponentsBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(
    scripts = ["/database/data/truncate.sql", "/database/data/test.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
)
class QuestionControllerTest(@Autowired private val rest: TestRestTemplate) {
    companion object {
        lateinit var member1: TokenDTO;
        lateinit var member2: TokenDTO;

        object QuestionId {
            const val BY_MEMBER1 = 1L
            const val INVALID = 999L
        }

        val bodyForPost = PostQuestionDTO(
            title = "New Question",
            content = "New Question Content",
            topicSlugs = listOf("java", "spring")
        )
        val bodyForEdit = PostQuestionDTO(
            title = "Edited Question",
            content = "Edited Question Content",
            topicSlugs = listOf("kotlin", "spring")
        )

        @JvmStatic
        @BeforeAll
        fun setup(@Autowired grantService: TestGrantService) {
            member1 = grantService.generateToken("member1")
            member2 = grantService.generateToken("member2")
        }
    }

    @Test
    fun `searchQuestions - default`() {
        val response = searchQuestions<Page<QuestionDTO>>("/questions")
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `searchQuestions - with params`() {
        val uri = UriComponentsBuilder.fromPath("/questions")
            .queryParam("query", "question")
            .queryParam("sort", QuestionSort.trending)
            .queryParam("filter", QuestionFilter.all)
            .queryParam("page", 1)
            .toUriString()
        val response = searchQuestions<Page<QuestionDTO>>(uri)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `getQuestion - not found`() {
        val response = getQuestion<String>("member1", "unknown-question")
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `getQuestion - found`() {
        val response = getQuestion<QuestionDetailDTO>("member1", "question-1")
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("question-1", response.body?.slug)
    }

    @Test
    fun `getQuestionForEdit - unauthorized`() {
        val response = getQuestionForEdit<String>(QuestionId.BY_MEMBER1)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `getQuestionForEdit - forbidden`() {
        val response = getQuestionForEdit<String>(QuestionId.BY_MEMBER1, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `getQuestionForEdit - not found`() {
        val response = getQuestionForEdit<String>(QuestionId.INVALID, member2)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `getQuestionForEdit - found`() {
        val response = getQuestionForEdit<QuestionForEditDTO>(QuestionId.BY_MEMBER1, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(QuestionId.BY_MEMBER1, response.body!!.id)
    }

    @Test
    fun `postQuestion - unauthorized`() {
        val response = postQuestion<String>(bodyForPost)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `postQuestion - created`() {
        val response = postQuestion<String>(bodyForPost, member1)
        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun `editQuestion - unauthorized`() {
        val response = editQuestion<String>(QuestionId.BY_MEMBER1, bodyForEdit)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `editQuestion - forbidden`() {
        val response = editQuestion<String>(QuestionId.BY_MEMBER1, bodyForEdit, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `editQuestion - not found`() {
        val response = editQuestion<String>(QuestionId.INVALID, bodyForEdit, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `editQuestion - ok`() {
        val response = editQuestion<String>(QuestionId.BY_MEMBER1, bodyForEdit, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `deleteQuestion - unauthorized`() {
        val response = deleteQuestion<String>(QuestionId.BY_MEMBER1)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `deleteQuestion - forbidden`() {
        val response = deleteQuestion<String>(QuestionId.BY_MEMBER1, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `deleteQuestion - not found`() {
        val response = deleteQuestion<String>(QuestionId.INVALID, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `deleteQuestion - ok`() {
        val response = deleteQuestion<String>(QuestionId.BY_MEMBER1, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `closeQuestion - unauthorized`() {
        val response = closeQuestion<String>(QuestionId.BY_MEMBER1)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `closeQuestion - forbidden`() {
        val response = closeQuestion<String>(QuestionId.BY_MEMBER1, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `closeQuestion - not found`() {
        val response = closeQuestion<String>(QuestionId.INVALID, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `closeQuestion - ok`() {
        val response = closeQuestion<String>(QuestionId.BY_MEMBER1, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    private inline fun <reified T> searchQuestions(uri: String) = rest.getForEntity(uri, T::class.java)
    private inline fun <reified T> getQuestion(username: String, slug: String) =
        rest.getForEntity("/questions/@${username}/${slug}", T::class.java)

    private inline fun <reified T> getQuestionForEdit(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.get("/questions/$id/edit")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> postQuestion(body: PostQuestionDTO, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.post("/questions")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    private inline fun <reified T> editQuestion(
        id: Long,
        body: PostQuestionDTO,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.put("/questions/$id")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    private inline fun <reified T> deleteQuestion(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.delete("/questions/$id")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> closeQuestion(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.post("/questions/$id/close")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }
}