package me.loghub.api.controller.article

import me.loghub.api.dto.article.*
import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.service.test.TestGrantService
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.domain.Page
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.util.UriComponentsBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(
    scripts = ["/database/data/truncate.sql", "/database/data/test.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
)
class ArticleControllerTest(@Autowired private val rest: TestRestTemplate) {
    companion object {
        lateinit var member1: TokenDTO;
        lateinit var member2: TokenDTO;

        object ArticleId {
            const val BY_MEMBER1 = 1L
            const val INVALID = 999L
        }

        val bodyForPost = PostArticleDTO(
            title = "New Article",
            content = "New Article Content",
            thumbnail = "default/article-thumbnail.webp",
            topicSlugs = listOf("java", "spring")
        )
        val bodyForEdit = PostArticleDTO(
            title = "Edited Article",
            content = "Edited Article Content",
            thumbnail = "default/article-thumbnail.webp",
            topicSlugs = listOf("kotlin", "spring")
        )

        @JvmStatic
        @BeforeAll
        fun setup(@Autowired grantService: TestGrantService) {
            member1 = grantService.generateToken("member1")
            member2 = grantService.generateToken("member2")
        }
    }

    @Test
    fun `searchArticles - default`() {
        val response = searchArticles<Page<ArticleDTO>>("/articles")
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `searchArticles - with params`() {
        val uri = UriComponentsBuilder.fromPath("/articles")
            .queryParam("query", "article")
            .queryParam("sort", ArticleSort.trending)
            .queryParam("page", 1)
            .toUriString()
        val response = searchArticles<Page<ArticleDTO>>(uri)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `getArticle - not found`() {
        val response = getArticle<String>("member1", "unknown-article")
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `getArticle - found`() {
        val response = getArticle<ArticleDetailDTO>("member1", "article-1")
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("article-1", response.body?.slug)
    }

    @Test
    fun `getArticleForEdit - unauthorized`() {
        val response = getArticleForEdit<String>(ArticleId.BY_MEMBER1)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `getArticleForEdit - forbidden`() {
        val response = getArticleForEdit<String>(ArticleId.BY_MEMBER1, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `getArticleForEdit - not found`() {
        val response = getArticleForEdit<String>(ArticleId.INVALID, member2)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `getArticleForEdit - found`() {
        val response = getArticleForEdit<ArticleForEditDTO>(ArticleId.BY_MEMBER1, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(ArticleId.BY_MEMBER1, response.body!!.id)
    }

    @Test
    fun `postArticle - unauthorized`() {
        val response = postArticle<String>(bodyForPost)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `postArticle - created`() {
        val response = postArticle<String>(bodyForPost, member1)
        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun `editArticle - unauthorized`() {
        val response = editArticle<String>(ArticleId.BY_MEMBER1, bodyForEdit)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `editArticle - forbidden`() {
        val response = editArticle<String>(ArticleId.BY_MEMBER1, bodyForEdit, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `editArticle - not found`() {
        val response = editArticle<String>(ArticleId.INVALID, bodyForEdit, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `editArticle - ok`() {
        val response = editArticle<String>(ArticleId.BY_MEMBER1, bodyForEdit, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `deleteArticle - unauthorized`() {
        val response = deleteArticle<String>(ArticleId.BY_MEMBER1)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `deleteArticle - forbidden`() {
        val response = deleteArticle<String>(ArticleId.BY_MEMBER1, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `deleteArticle - not found`() {
        val response = deleteArticle<String>(ArticleId.INVALID, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `deleteArticle - ok`() {
        val response = deleteArticle<String>(ArticleId.BY_MEMBER1, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    private inline fun <reified T> searchArticles(uri: String) = rest.getForEntity(uri, T::class.java)
    private inline fun <reified T> getArticle(username: String, slug: String) =
        rest.getForEntity("/articles/@${username}/${slug}", T::class.java)

    private inline fun <reified T> getArticleForEdit(id: Long, token: TokenDTO? = null): ResponseEntity<T> {
        val request = RequestEntity.get("/articles/$id/edit")
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
        token: TokenDTO? = null
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
}