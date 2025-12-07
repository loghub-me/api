package me.loghub.api.controller.user

import me.loghub.api.dto.article.ArticleForImportDTO
import me.loghub.api.dto.auth.token.TokenDTO
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
import org.springframework.web.util.UriComponentsBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation::class)
@ActiveProfiles("test")
class UserArticleControllerTest(
    @Autowired private val rest: TestRestTemplate,
    @Autowired private val jdbcTemplate: JdbcTemplate,
) {
    lateinit var member1: User
    lateinit var member1Token: TokenDTO

    companion object {
        const val MEMBER1_ARTICLE_COUNT = 1
    }

    @BeforeAll
    fun setup(@Autowired grantService: TestGrantService) {
        resetDatabase(jdbcTemplate)
        val (member1, member1Token) = grantService.grant("member1")
        this.member1 = member1
        this.member1Token = member1Token
    }

    private inline fun <reified T> searchArticlesForImport(uri: String, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.get(uri)
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SearchArticlesForImport {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `searchArticlesForImport - ok`() {
            val uri = UriComponentsBuilder.fromPath("/users/articles/for-import")
                .toUriString()
            val response = searchArticlesForImport<List<ArticleForImportDTO>>(uri, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(MEMBER1_ARTICLE_COUNT, response.body?.size)
        }

        @Test
        fun `searchArticlesForImport - ok - with params`() {
            val uri = UriComponentsBuilder.fromPath("/users/articles/for-import")
                .queryParam("query", "asdfghjkl")
                .toUriString()
            val response = searchArticlesForImport<List<ArticleForImportDTO>>(uri, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(0, response.body?.size)
        }
    }
}