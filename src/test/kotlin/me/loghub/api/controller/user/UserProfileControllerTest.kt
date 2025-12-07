package me.loghub.api.controller.user

import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.dto.user.UpdateUserProfileDTO
import me.loghub.api.dto.user.UserProfileDTO
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
class UserProfileControllerTest(
    @Autowired private val rest: TestRestTemplate,
    @Autowired private val jdbcTemplate: JdbcTemplate,
) {
    lateinit var member1: User
    lateinit var member1Token: TokenDTO
    lateinit var member2: User
    lateinit var member2Token: TokenDTO

    @BeforeAll
    fun setup(@Autowired grantService: TestGrantService) {
        val (member1, member1Token) = grantService.grant("member1")
        this.member1 = member1
        this.member1Token = member1Token
        val (member2, member2Token) = grantService.grant("member2")
        this.member2 = member2
        this.member2Token = member2Token
    }

    private inline fun <reified T> getUserProfile(token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.get("/users/profile")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> updateProfile(
        body: UpdateUserProfileDTO,
        token: TokenDTO? = null,
    ): ResponseEntity<T> {
        val request = RequestEntity.put("/users/profile")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetUserProfile {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getUserProfile - unauthorized`() {
            val response = getUserProfile<String>()
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `getUserProfile - ok`() {
            val response = getUserProfile<UserProfileDTO>(member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(member1.profile.nickname, response.body?.nickname)
            assertEquals(member1.profile.readme, response.body?.readme)
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class UpdateProfile {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        val bodyForUpdate = UpdateUserProfileDTO(
            nickname = "NewNickname",
            readme = "This is my new readme.",
        )

        @Test
        fun `updateProfile - unauthorized`() {
            val response = updateProfile<String>(bodyForUpdate)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `updateProfile - bad_request - invalid nickname`() {
            val response = updateProfile<String>(bodyForUpdate.copy(nickname = "New_Nickname"), member1Token)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `updateProfile - ok`() {
            val response = updateProfile<String>(bodyForUpdate, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }
}