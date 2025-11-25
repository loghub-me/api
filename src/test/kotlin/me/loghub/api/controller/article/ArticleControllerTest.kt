package me.loghub.api.controller.article

import me.loghub.api.dto.article.*
import me.loghub.api.dto.auth.token.TokenDTO
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
class ArticleControllerTest(
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
                const val INVALID = 999L
            }

            object Slug {
                const val BY_MEMBER1 = "article-1"
                const val INVALID = "unknown-article"
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

    private inline fun <reified T> searchArticles(uri: String) =
        rest.getForEntity(uri, T::class.java)

    private inline fun <reified T> getArticle(username: String, slug: String) =
        rest.getForEntity("/articles/@${username}/${slug}", T::class.java)

    private inline fun <reified T> getArticleForEdit(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.get("/articles/$id/for-edit")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }

    private inline fun <reified T> postArticle(body: PostArticleDTO, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.post("/articles")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    private inline fun <reified T> editArticle(
        id: Long,
        body: PostArticleDTO,
        token: TokenDTO? = null,
    ): ResponseEntity<T> {
        val request = RequestEntity.put("/articles/$id")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    private inline fun <reified T> deleteArticle(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.delete("/articles/$id")
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
    inner class SearchArticles {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `searchArticles - ok - no params`() {
            val response = searchArticles<Page<ArticleDTO>>("/articles")
            assertEquals(HttpStatus.OK, response.statusCode)
        }

        @Test
        fun `searchArticles - ok - with params`() {
            val uri = UriComponentsBuilder.fromPath("/articles")
                .queryParam("query", "article")
                .queryParam("sort", ArticleSort.trending)
                .queryParam("page", 1)
                .toUriString()
            val response = searchArticles<Page<ArticleDTO>>(uri)
            assertEquals(HttpStatus.OK, response.statusCode)
        }

        @Test
        fun `searchArticles - bad_request - invalid sort`() {
            val uri = UriComponentsBuilder.fromPath("/articles")
                .queryParam("sort", "foo")
                .toUriString()
            val response = searchArticles<String>(uri)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        fun `searchArticles - bad_request - invalid page`() {
            val uri = UriComponentsBuilder.fromPath("/articles")
                .queryParam("page", 0)
                .toUriString()
            val response = searchArticles<String>(uri)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetArticle {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `getArticle - not_found`() {
            val response = getArticle<String>(member1.username, Article.Slug.INVALID)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `getArticle - ok`() {
            val response = getArticle<ArticleDetailDTO>(member1.username, Article.Slug.BY_MEMBER1)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(Article.Slug.BY_MEMBER1, response.body?.slug)
        }
    }

    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetArticleForEdit {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `getArticleForEdit - unauthorized`() {
            val response = getArticleForEdit<String>(Article.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `getArticleForEdit - forbidden`() {
            val response = getArticleForEdit<String>(Article.Id.BY_MEMBER1, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `getArticleForEdit - not_found`() {
            val response = getArticleForEdit<String>(Article.Id.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `getArticleForEdit - ok`() {
            val response = getArticleForEdit<ArticleForEditDTO>(Article.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(Article.Id.BY_MEMBER1, response.body?.id)
        }
    }

    @Nested
    @Order(4)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PostArticle {
        val bodyForPost = PostArticleDTO(
            title = "New Article",
            content = "This is a new article.",
            thumbnail = "0/default-article-thumbnail.webp",
            topicSlugs = listOf("java", "spring"),
        )

        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `postArticle - unauthorized`() {
            val response = postArticle<String>(bodyForPost)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `postArticle - bad_request - short title`() {
            val response = postArticle<String>(bodyForPost.copy(title = "f"), member1Token)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        fun `postArticle - bad_request - empty content`() {
            val response = postArticle<String>(bodyForPost.copy(content = ""), member1Token)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        fun `postArticle - bad_request - invalid thumbnail`() {
            val response = postArticle<String>(bodyForPost.copy(thumbnail = "foo"), member1Token)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        fun `postArticle - created`() {
            val response = postArticle<String>(bodyForPost, member1Token)
            assertEquals(HttpStatus.CREATED, response.statusCode)
        }
    }

    @Nested
    @Order(5)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class EditArticle {
        val bodyForEdit = PostArticleDTO(
            title = "Edited Article",
            content = "This is an edited article.",
            thumbnail = "0/default-article-thumbnail.webp",
            topicSlugs = listOf("kotlin", "spring-boot"),
        )

        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `editArticle - unauthorized`() {
            val response = editArticle<String>(Article.Id.BY_MEMBER1, bodyForEdit)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `editArticle - forbidden`() {
            val response = editArticle<String>(Article.Id.BY_MEMBER1, bodyForEdit, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `editArticle - not_found`() {
            val response = editArticle<String>(Article.Id.INVALID, bodyForEdit, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `editArticle - bad_request - short title`() {
            val response = editArticle<String>(Article.Id.BY_MEMBER1, bodyForEdit.copy(title = "f"), member1Token)
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @Test
        fun `editArticle - ok`() {
            val response = editArticle<String>(Article.Id.BY_MEMBER1, bodyForEdit, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }

    @Nested
    @Order(6)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteArticle {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `deleteArticle - unauthorized`() {
            val response = deleteArticle<String>(Article.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `deleteArticle - forbidden`() {
            val response = deleteArticle<String>(Article.Id.BY_MEMBER1, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `deleteArticle - not_found`() {
            val response = deleteArticle<String>(Article.Id.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteArticle - ok`() {
            val response = deleteArticle<String>(Article.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }
}