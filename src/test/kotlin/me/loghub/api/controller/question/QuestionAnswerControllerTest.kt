package me.loghub.api.controller.question

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.question.answer.PostQuestionAnswerDTO
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
class QuestionAnswerControllerTest(
    @Autowired private val rest: TestRestTemplate,
    @Autowired private val jdbcTemplate: JdbcTemplate,
) {
    lateinit var member1: User
    lateinit var member1Token: TokenDTO
    lateinit var member2: User
    lateinit var member2Token: TokenDTO

    object QuestionAnswer {
        object Id {
            const val BY_MEMBER2 = 1L
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

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetAnswers {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getAnswers - ok`() {
            val response = getAnswers<String>(1L)
            assertEquals(HttpStatus.OK, response.statusCode)
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
        fun `postAnswer - unauthenticated`() {
            val response = postAnswer<String>(1L, bodyForPost)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `postAnswer - conflict`() {
            val response = postAnswer<String>(1L, bodyForPost, member1Token)
            assertEquals(HttpStatus.CONFLICT, response.statusCode)
        }

        @Test
        fun `postAnswer - created`() {
            val response = postAnswer<String>(1L, bodyForPost, member2Token)
            assertEquals(HttpStatus.CREATED, response.statusCode)
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
        fun `editAnswer - unauthenticated`() {
            val response = editAnswer<String>(1L, QuestionAnswer.Id.BY_MEMBER2, bodyForEdit)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `editAnswer - forbidden`() {
            val response = editAnswer<String>(1L, QuestionAnswer.Id.BY_MEMBER2, bodyForEdit, member1Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `editAnswer - not found`() {
            val response = editAnswer<String>(1L, QuestionAnswer.Id.INVALID, bodyForEdit, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `editAnswer - ok`() {
            val response = editAnswer<String>(1L, QuestionAnswer.Id.BY_MEMBER2, bodyForEdit, member2Token)
            assertEquals(HttpStatus.OK, response.statusCode)
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
        fun `deleteAnswer - unauthenticated`() {
            val response = deleteAnswer<String>(1L, QuestionAnswer.Id.BY_MEMBER2)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `deleteAnswer - forbidden`() {
            val response = deleteAnswer<String>(1L, QuestionAnswer.Id.BY_MEMBER2, member1Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `deleteAnswer - not found`() {
            val response = deleteAnswer<String>(1L, QuestionAnswer.Id.INVALID, member2Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteAnswer - ok`() {
            val response = deleteAnswer<String>(1L, QuestionAnswer.Id.BY_MEMBER2, member2Token)
            assertEquals(HttpStatus.OK, response.statusCode)
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
        fun `acceptAnswer - unauthenticated`() {
            val response = acceptAnswer<String>(1L, QuestionAnswer.Id.BY_MEMBER2)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `acceptAnswer - forbidden`() {
            val response = acceptAnswer<String>(1L, QuestionAnswer.Id.BY_MEMBER2, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `acceptAnswer - not found`() {
            val response = acceptAnswer<String>(1L, QuestionAnswer.Id.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `acceptAnswer - ok`() {
            val response = acceptAnswer<String>(1L, QuestionAnswer.Id.BY_MEMBER2, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }
}