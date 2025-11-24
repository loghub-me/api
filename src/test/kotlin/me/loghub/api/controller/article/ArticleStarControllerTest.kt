package me.loghub.api.controller.article

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
class ArticleStarControllerTest(
    @Autowired private val rest: TestRestTemplate,
    @Autowired private val jdbcTemplate: JdbcTemplate,
) {
    companion object {
        lateinit var member1: User
        lateinit var member1Token: TokenDTO
        lateinit var member2: User
        lateinit var member2Token: TokenDTO

        object Article {
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
    inner class ExistsArticleStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `existsArticleStar - unauthorized`() {
            val response = existsArticleStar<String>(Article.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `existsArticleStar - not_found`() {
            val response = existsArticleStar<DataResponseBody<Boolean>>(Article.Id.BY_MEMBER1, member2Token)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(false, response.body!!.data)
        }

        @Test
        fun `existsArticleStar - ok`() {
            val response = existsArticleStar<DataResponseBody<Boolean>>(Article.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(true, response.body!!.data)
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class AddArticleStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `addArticleStar - unauthorized`() {
            val response = addArticleStar<String>(Article.Id.BY_MEMBER2)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `addArticleStar - not_found`() {
            val response = addArticleStar<String>(Article.Id.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `addArticleStar - conflict`() {
            val response = addArticleStar<String>(Article.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.CONFLICT, response.statusCode)
        }

        @Test
        fun `addArticleStar - created`() {
            val response = addArticleStar<String>(Article.Id.BY_MEMBER2, member1Token)
            assertEquals(HttpStatus.CREATED, response.statusCode)
        }
    }


    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteArticleStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `deleteArticleStar - unauthorized`() {
            val response = deleteArticleStar<String>(Article.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `deleteArticleStar - not_found`() {
            val response = deleteArticleStar<String>(Article.Id.BY_MEMBER2, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteArticleStar - ok`() {
            val response = deleteArticleStar<String>(Article.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }
}