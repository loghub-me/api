package me.loghub.api.controller.article

import me.loghub.api.dto.article.comment.PostArticleCommentDTO
import me.loghub.api.dto.auth.token.TokenDTO
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
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation::class)
@ActiveProfiles("test")
class ArticleCommentControllerTest(
    @Autowired private val rest: TestRestTemplate,
    @Autowired private val jdbcTemplate: JdbcTemplate,
) {
    companion object {
        lateinit var member1: User
        lateinit var member1Token: TokenDTO
        lateinit var member2: User
        lateinit var member2Token: TokenDTO

        object ArticleComment {
            object Id {
                const val BY_MEMBER1 = 1L
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
    inner class GetComments {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `getComments - ok`() {
            val response = getComments<String>(1L)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }


    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetReplies {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `getReplies - ok`() {
            val response = getReplies<String>(1L, ArticleComment.Id.BY_MEMBER1)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }

    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PostComment {
        val bodyForPost = PostArticleCommentDTO(
            content = "This is a new comment.",
            parentId = null
        )

        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `postComment - unauthenticated`() {
            val response = postComment<String>(1L, bodyForPost)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `postComment - created - without parentId`() {
            val response = postComment<String>(1L, bodyForPost, member1Token)
            assertEquals(HttpStatus.CREATED, response.statusCode)
        }

        @Test
        fun `postComment - created - with parentId`() {
            val bodyForPostWithParentId = bodyForPost.copy(parentId = ArticleComment.Id.BY_MEMBER1)
            val response = postComment<String>(1L, bodyForPostWithParentId, member1Token)
            assertEquals(HttpStatus.CREATED, response.statusCode)
        }
    }

    @Nested
    @Order(4)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class EditComment {
        val bodyForEdit = PostArticleCommentDTO(
            content = "This is a edited comment.",
            parentId = null
        )

        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `editComment - unauthenticated`() {
            val response = editComment<String>(1L, ArticleComment.Id.BY_MEMBER1, bodyForEdit)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `editComment - forbidden`() {
            val response = editComment<String>(1L, ArticleComment.Id.BY_MEMBER1, bodyForEdit, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `editComment - not_found`() {
            val response = editComment<String>(1L, ArticleComment.Id.INVALID, bodyForEdit, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        fun `editComment - ok`() {
            val response = editComment<String>(1L, ArticleComment.Id.BY_MEMBER1, bodyForEdit, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }

    @Nested
    @Order(5)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteComment {
        @BeforeAll
        fun setupDatabase() = resetDatabase()

        @Test
        fun `deleteComment - unauthenticated`() {
            val response = deleteComment<String>(1L, ArticleComment.Id.BY_MEMBER1)
            assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        }

        @Test
        fun `deleteComment - forbidden`() {
            val response = deleteComment<String>(1L, ArticleComment.Id.BY_MEMBER1, member2Token)
            assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        }

        @Test
        fun `deleteComment - not_found`() {
            val response = deleteComment<String>(1L, ArticleComment.Id.INVALID, member1Token)
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteComment - ok`() {
            val response = deleteComment<String>(1L, ArticleComment.Id.BY_MEMBER1, member1Token)
            assertEquals(HttpStatus.OK, response.statusCode)
        }
    }
}