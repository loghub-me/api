package me.loghub.api.controller.question

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.response.DataResponseBody
import me.loghub.api.entity.user.User
import me.loghub.api.service.test.TestGrantService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation::class)
@ActiveProfiles("test")
class QuestionStarControllerTest(
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
                const val BY_MEMBER2 = 2L
                const val INVALID = 999L
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

    private inline fun <reified T> existsQuestionStar(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.get("/questions/star/$id")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> addQuestionStar(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.post("/questions/star/$id")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> deleteQuestionStar(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.delete("/questions/star/$id")
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
    inner class ExistsQuestionStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `existsQuestionStar - unauthorized`() {
            val response = existsQuestionStar<String>(Question.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `existsQuestionStar - not_found`() {
            val response = existsQuestionStar<DataResponseBody<Boolean>>(Question.Id.BY_MEMBER1, member2Token)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(false, response.body!!.data)
        }

        @Test
        fun `existsQuestionStar - ok`() {
            val response = existsQuestionStar<DataResponseBody<Boolean>>(Question.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(true, response.body!!.data)
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class AddQuestionStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `addQuestionStar - unauthorized`() {
            val response = addQuestionStar<String>(Question.Id.BY_MEMBER2)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `addQuestionStar - not_found`() {
            val response = addQuestionStar<String>(Question.Id.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `addQuestionStar - conflict`() {
            val response = addQuestionStar<String>(Question.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.CONFLICT, response.statusCode)
        }

        @Test
        fun `addQuestionStar - created`() {
            val response = addQuestionStar<String>(Question.Id.BY_MEMBER2, member1Token)
            assertEquals(HttpStatus.CREATED, response.statusCode)
        }
    }


    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteQuestionStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `deleteQuestionStar - unauthorized`() {
            val response = deleteQuestionStar<String>(Question.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `deleteQuestionStar - not_found`() {
            val response = deleteQuestionStar<String>(Question.Id.BY_MEMBER2, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteQuestionStar - ok`() {
            val response = deleteQuestionStar<String>(Question.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }
}