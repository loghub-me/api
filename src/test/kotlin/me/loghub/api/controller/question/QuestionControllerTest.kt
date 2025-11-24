package me.loghub.api.controller.question

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.question.*
import me.loghub.api.entity.user.User
import me.loghub.api.service.test.TestGrantService
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.data.domain.Page
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.util.UriComponentsBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation::class)
@ActiveProfiles("test")
class QuestionControllerTest(
    @Autowired private val rest: TestRestTemplate,
    @Autowired private val jdbcTemplate: JdbcTemplate,
) {
    companion object {
        lateinit var member1: User
        lateinit var member1Token: TokenDTO
        lateinit var member2: User
        lateinit var member2Token: TokenDTO

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

        @JvmStatic
        @BeforeAll
        fun setup(@Autowired grantService: TestGrantService) {
            val (member1, member1Token) = grantService.grant("member1")
            this.member1 = member1
            this.member1Token = member1Token
            val (member2, member2Token) = grantService.grant("member2")
            this.member2 = member2
            this.member2Token = member2Token
        }
    }

    private inline fun <reified T> searchQuestions(uri: String) =
        rest.getForEntity(uri, T::class.java)

    private inline fun <reified T> getQuestion(username: String, slug: String) =
        rest.getForEntity("/questions/@${username}/${slug}", T::class.java)

    private inline fun <reified T> getQuestionForEdit(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.get("/questions/$id/for-edit")
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
        token: TokenDTO? = null,
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

    private fun resetDatabase() {
        val dataSource = jdbcTemplate.dataSource
            ?: error("DataSource is required for resetting database")
        val populator = ResourceDatabasePopulator().apply {
            addScript(ClassPathResource("/database/data/truncate.sql"))
            addScript(ClassPathResource("/database/data/test.sql"))
        }

        DatabasePopulatorUtils.execute(populator, dataSource)
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SearchQuestions {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `searchQuestions - ok - no params`() {
            val response = searchQuestions<Page<QuestionDTO>>("/questions")
            assertEquals(HttpStatus.OK, response.statusCode)
        }

        @Test
        fun `searchQuestions - ok - with params`() {
            val uri = UriComponentsBuilder.fromPath("/questions")
                .queryParam("query", "question")
                .queryParam("sort", QuestionSort.trending)
                .queryParam("page", 1)
                .toUriString()
            val response = searchQuestions<Page<QuestionDTO>>(uri)
            assertEquals(HttpStatus.OK, response.statusCode)
        }

        @Test
        fun `searchQuestions - bad_request - invalid sort`() {
            val uri = UriComponentsBuilder.fromPath("/questions")
                .queryParam("sort", "foo")
                .toUriString()
            val response = searchQuestions<String>(uri)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        fun `searchQuestions - bad_request - invalid page`() {
            val uri = UriComponentsBuilder.fromPath("/questions")
                .queryParam("page", 0)
                .toUriString()
            val response = searchQuestions<String>(uri)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetQuestion {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `getQuestion - not_found`() {
            val response = getQuestion<String>(member1.username, Question.Slug.INVALID)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `getQuestion - ok`() {
            val response = getQuestion<QuestionDetailDTO>(member1.username, Question.Slug.BY_MEMBER1)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(Question.Slug.BY_MEMBER1, response.body?.slug)
        }
    }

    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetQuestionForEdit {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `getQuestionForEdit - unauthorized`() {
            val response = getQuestionForEdit<String>(Question.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `getQuestionForEdit - forbidden`() {
            val response = getQuestionForEdit<String>(Question.Id.BY_MEMBER1, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `getQuestionForEdit - not_found`() {
            val response = getQuestionForEdit<String>(Question.Id.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `getQuestionForEdit - ok`() {
            val response = getQuestionForEdit<QuestionForEditDTO>(Question.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(Question.Id.BY_MEMBER1, response.body?.id)
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
        fun setupDatabase() = resetDatabase()

        @Test
        fun `postQuestion - unauthorized`() {
            val response = postQuestion<String>(bodyForPost)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `postQuestion - bad_request - short title`() {
            val response = postQuestion<String>(bodyForPost.copy(title = "f"), member1Token)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        fun `postQuestion - bad_request - empty content`() {
            val response = postQuestion<String>(bodyForPost.copy(content = ""), member1Token)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        fun `postQuestion - created`() {
            val response = postQuestion<String>(bodyForPost, member1Token)
            assertEquals(HttpStatus.CREATED, response.statusCode)
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
        fun setupDatabase() = resetDatabase()

        @Test
        fun `editQuestion - unauthorized`() {
            val response = editQuestion<String>(Question.Id.BY_MEMBER1, bodyForEdit)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `editQuestion - forbidden`() {
            val response = editQuestion<String>(Question.Id.BY_MEMBER1, bodyForEdit, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `editQuestion - not_found`() {
            val response = editQuestion<String>(Question.Id.INVALID, bodyForEdit, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `editQuestion - bad_request - short title`() {
            val response = editQuestion<String>(Question.Id.BY_MEMBER1, bodyForEdit.copy(title = "f"), member1Token)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        fun `editQuestion - ok`() {
            val response = editQuestion<String>(Question.Id.BY_MEMBER1, bodyForEdit, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }

    @Nested
    @Order(6)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteQuestion {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `deleteQuestion - unauthorized`() {
            val response = deleteQuestion<String>(Question.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `deleteQuestion - forbidden`() {
            val response = deleteQuestion<String>(Question.Id.BY_MEMBER1, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `deleteQuestion - not_found`() {
            val response = deleteQuestion<String>(Question.Id.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteQuestion - ok`() {
            val response = deleteQuestion<String>(Question.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }

    @Nested
    @Order(7)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class CloseQuestion {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `closeQuestion - unauthorized`() {
            val response = closeQuestion<String>(Question.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `closeQuestion - forbidden`() {
            val response = closeQuestion<String>(Question.Id.BY_MEMBER1, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `closeQuestion - not_found`() {
            val response = closeQuestion<String>(Question.Id.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `closeQuestion - ok`() {
            val response = closeQuestion<String>(Question.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }
}