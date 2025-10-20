package me.loghub.api.controller.article

import me.loghub.api.dto.article.comment.PostArticleCommentDTO
import me.loghub.api.dto.auth.token.TokenDTO
import me.loghub.api.service.test.TestGrantService
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(
    scripts = ["/database/data/truncate.sql", "/database/data/test.sql"],
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
)
class ArticleCommentControllerTest(@Autowired private val rest: TestRestTemplate) {
    companion object {
        lateinit var member1: TokenDTO;
        lateinit var member2: TokenDTO;

        object ArticleCommentId {
            const val BY_MEMBER1 = 1L
            const val INVALID = 999L
        }

        val bodyForPost = PostArticleCommentDTO(content = "New comment", parentId = null)
        val bodyForEdit = PostArticleCommentDTO(content = "Edited comment", parentId = null)
        val bodyForPostReply = PostArticleCommentDTO(content = "New reply", parentId = ArticleCommentId.BY_MEMBER1)

        @JvmStatic
        @BeforeAll
        fun setup(@Autowired grantService: TestGrantService) {
            member1 = grantService.generateToken("member1")
            member2 = grantService.generateToken("member2")
        }
    }

    @Test
    fun getComments() {
        val response = getComments<String>(1L)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun getReplies() {
        val response = getReplies<String>(1L, ArticleCommentId.BY_MEMBER1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `postComment - unauthenticated`() {
        val response = postComment<String>(1L, bodyForPost)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `postComment - created 1`() {
        val response = postComment<String>(1L, bodyForPost, member1)
        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun `postComment - created 2`() {
        val response = postComment<String>(1L, bodyForPostReply, member1)
        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun `editComment - unauthenticated`() {
        val response = editComment<String>(1L, ArticleCommentId.BY_MEMBER1, bodyForPost)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `editComment - forbidden`() {
        val response = editComment<String>(1L, ArticleCommentId.BY_MEMBER1, bodyForEdit, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `editComment - ok`() {
        val response = editComment<String>(1L, ArticleCommentId.BY_MEMBER1, bodyForEdit, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `deleteComment - unauthenticated`() {
        val response = deleteComment<String>(1L, ArticleCommentId.BY_MEMBER1)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `deleteComment - forbidden`() {
        val response = deleteComment<String>(1L, ArticleCommentId.BY_MEMBER1, member2)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun `deleteComment - not found`() {
        val response = deleteComment<String>(1L, ArticleCommentId.INVALID, member1)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `deleteComment - ok`() {
        val response = deleteComment<String>(1L, ArticleCommentId.BY_MEMBER1, member1)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    private inline fun <reified T> getComments(articleId: Long) =
        rest.getForEntity("/articles/${articleId}/comments", T::class.java)

    private inline fun <reified T> getReplies(articleId: Long, commentId: Long) =
        rest.getForEntity("/articles/${articleId}/comments/${commentId}/replies", T::class.java)

    private inline fun <reified T> postComment(
        articleId: Long,
        body: PostArticleCommentDTO,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.post("/articles/${articleId}/comments")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    private inline fun <reified T> editComment(
        articleId: Long,
        commentId: Long,
        body: PostArticleCommentDTO,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.put("/articles/${articleId}/comments/${commentId}")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.body(body), T::class.java)
    }

    private inline fun <reified T> deleteComment(
        articleId: Long,
        commentId: Long,
        token: TokenDTO? = null
    ): ResponseEntity<T> {
        val request = RequestEntity.delete("/articles/${articleId}/comments/${commentId}")
        token?.let { request.header(HttpHeaders.AUTHORIZATION, it.authorization) }
        return rest.exchange(request.build(), T::class.java)
    }
}