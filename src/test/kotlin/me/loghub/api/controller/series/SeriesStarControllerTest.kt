package me.loghub.api.controller.series

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
class SeriesStarControllerTest(
    @Autowired private val rest: TestRestTemplate,
    @Autowired private val jdbcTemplate: JdbcTemplate,
) {
    companion object {
        lateinit var member1: User
        lateinit var member1Token: TokenDTO
        lateinit var member2: User
        lateinit var member2Token: TokenDTO

        object Series {
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

    private inline fun <reified T> existsSeriesStar(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.get("/series/star/$id")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> addSeriesStar(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.post("/series/star/$id")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> deleteSeriesStar(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.delete("/series/star/$id")
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
    inner class ExistsSeriesStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `existsSeriesStar - unauthorized`() {
            val response = existsSeriesStar<String>(Series.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `existsSeriesStar - not_found`() {
            val response = existsSeriesStar<DataResponseBody<Boolean>>(Series.Id.BY_MEMBER1, member2Token)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(false, response.body!!.data)
        }

        @Test
        fun `existsSeriesStar - ok`() {
            val response = existsSeriesStar<DataResponseBody<Boolean>>(Series.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(true, response.body!!.data)
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class AddSeriesStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `addSeriesStar - unauthorized`() {
            val response = addSeriesStar<String>(Series.Id.BY_MEMBER2)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `addSeriesStar - not_found`() {
            val response = addSeriesStar<String>(Series.Id.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `addSeriesStar - conflict`() {
            val response = addSeriesStar<String>(Series.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.CONFLICT, response.statusCode)
        }

        @Test
        fun `addSeriesStar - created`() {
            val response = addSeriesStar<String>(Series.Id.BY_MEMBER2, member1Token)
            assertEquals(HttpStatus.CREATED, response.statusCode)
        }
    }


    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteSeriesStar {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `deleteSeriesStar - unauthorized`() {
            val response = deleteSeriesStar<String>(Series.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `deleteSeriesStar - not_found`() {
            val response = deleteSeriesStar<String>(Series.Id.BY_MEMBER2, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteSeriesStar - ok`() {
            val response = deleteSeriesStar<String>(Series.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }
}