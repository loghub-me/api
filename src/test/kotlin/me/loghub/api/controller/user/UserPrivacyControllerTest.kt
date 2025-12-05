package me.loghub.api.controller.user

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.user.UpdateUserPrivacyDTO
import me.loghub.api.dto.user.UserPrivacyDTO
import me.loghub.api.entity.user.User
import me.loghub.api.service.test.TestGrantService
import org.junit.jupiter.api.*
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
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation::class)
@ActiveProfiles("test")
class UserPrivacyControllerTest(
    @Autowired private val rest: TestRestTemplate,
    @Autowired private val jdbcTemplate: JdbcTemplate,
) {
    companion object {
        lateinit var member1: User
        lateinit var member1Token: TokenDTO
        lateinit var member2: User
        lateinit var member2Token: TokenDTO

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

    private inline fun <reified T> getUserPrivacy(token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.get("/users/privacy")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> updatePrivacy(
        body: UpdateUserPrivacyDTO,
        token: TokenDTO? = null,
    ): ResponseEntity<T> {
        val request = RequestEntity.put("/users/privacy")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
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
    inner class GetUserPrivacy {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `getUserPrivacy - unauthorized`() {
            val response = getUserPrivacy<String>()
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `getUserPrivacy - ok`() {
            val response = getUserPrivacy<UserPrivacyDTO>(member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(member1.privacy.emailPublic, response.body?.emailPublic)
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class UpdatePrivacy {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        val bodyForUpdate = UpdateUserPrivacyDTO(emailPublic = true)

        @Test
        fun `updatePrivacy - unauthorized`() {
            val response = updatePrivacy<String>(bodyForUpdate)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `updatePrivacy - ok`() {
            val response = updatePrivacy<String>(bodyForUpdate, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }
}