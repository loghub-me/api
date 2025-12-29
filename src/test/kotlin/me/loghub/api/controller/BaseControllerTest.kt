package me.loghub.api.controller

import me.loghub.api.dto.auth.token.AccessToken
import me.loghub.api.entity.user.User
import me.loghub.api.service.test.TestGrantService
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.client.RestTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseControllerTest {
    @LocalServerPort
    var port: Int = 0

    @Autowired
    protected lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    protected lateinit var grantService: TestGrantService

    lateinit var rest: RestTestClient

    lateinit var member1: User
    lateinit var member1Token: AccessToken
    lateinit var member2: User
    lateinit var member2Token: AccessToken

    @BeforeEach
    fun setupRestClient() {
        this.rest = RestTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .build()
    }

    @BeforeAll
    fun setupTestData() {
        resetDatabase(jdbcTemplate)

        val (member1, member1Token) = grantService.grant("member1")
        this.member1 = member1
        this.member1Token = member1Token.accessToken
        val (member2, member2Token) = grantService.grant("member2")
        this.member2 = member2
        this.member2Token = member2Token.accessToken
    }
}