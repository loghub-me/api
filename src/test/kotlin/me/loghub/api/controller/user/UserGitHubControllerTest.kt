package me.loghub.api.controller.user

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.user.UpdateUserGitHubDTO
import me.loghub.api.dto.user.UserGitHubDTO
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
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation::class)
@ActiveProfiles("test")
class UserGitHubControllerTest(
    @Autowired private val rest: TestRestTemplate,
    @Autowired private val jdbcTemplate: JdbcTemplate,
    @Autowired private val grantService: TestGrantService,
) {
    lateinit var member1: User
    lateinit var member1Token: TokenDTO
    lateinit var member2: User
    lateinit var member2Token: TokenDTO

    @BeforeAll
    fun setup() {
        resetDatabase(jdbcTemplate)
        val (member1, member1Token) = grantService.grant("member1")
        this.member1 = member1
        this.member1Token = member1Token
        val (member2, member2Token) = grantService.grant("member2")
        this.member2 = member2
        this.member2Token = member2Token
    }

    private inline fun <reified T> getUserGitHub(token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.get("/users/github")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> updateGitHub(
        body: UpdateUserGitHubDTO,
        token: TokenDTO? = null,
    ): ResponseEntity<T> {
        val request = RequestEntity.put("/users/github")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    private inline fun <reified T> deleteGitHub(token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.delete("/users/github")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetUserGitHub {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getUserGitHub - unauthorized`() {
            val response = getUserGitHub<String>()
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `getUserGitHub - ok`() {
            val response = getUserGitHub<UserGitHubDTO>(member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(member1.github.username, response.body?.username)
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class UpdateGitHub {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        val bodyForUpdate = UpdateUserGitHubDTO(
            username = "gymynnym"
        )

        @Test
        fun `updateGitHub - unauthorized`() {
            val response = updateGitHub<String>(bodyForUpdate)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `updateGitHub - bad_request - invalid nickname`() {
            val response = updateGitHub<String>(bodyForUpdate.copy(username = ":)"), member1Token)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `updateGitHub - ok`() {
            val response = updateGitHub<String>(bodyForUpdate, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }

    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class DeleteGitHub {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `deleteGitHub - unauthorized`() {
            val response = deleteGitHub<String>()
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteGitHub - ok`() {
            val response = deleteGitHub<String>(member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }
}