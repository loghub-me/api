package me.loghub.api.controller.article

import me.loghub.api.controller.BaseControllerTest
import me.loghub.api.dto.article.comment.PostArticleCommentDTO
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import kotlin.test.Test

@TestClassOrder(ClassOrderer.OrderAnnotation::class)
class ArticleCommentControllerTest : BaseControllerTest() {
    object ArticleComment {
        object Id {
            const val BY_MEMBER1 = 1L
            const val INVALID = 999L
        }
    }

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetComments {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getComments - ok`() {
            rest.get().uri("/articles/1/comments")
                .exchange()
                .expectStatus().isOk
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetReplies {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getReplies - ok`() {
            rest.get().uri("/articles/1/comments/${ArticleComment.Id.BY_MEMBER1}/replies")
                .exchange()
                .expectStatus().isOk
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
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `postComment - unauthorized`() {
            rest.post().uri("/articles/1/comments")
                .body(bodyForPost)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `postComment - created - without parentId`() {
            rest.post().uri("/articles/1/comments")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForPost)
                .exchange()
                .expectStatus().isCreated
        }

        @Test
        fun `postComment - created - with parentId`() {
            val bodyForPostWithParentId = bodyForPost.copy(parentId = ArticleComment.Id.BY_MEMBER1)
            rest.post().uri("/articles/1/comments")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForPostWithParentId)
                .exchange()
                .expectStatus().isCreated
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
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `editComment - unauthorized`() {
            rest.put().uri("/articles/1/comments/${ArticleComment.Id.BY_MEMBER1}")
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `editComment - forbidden`() {
            rest.put().uri("/articles/1/comments/${ArticleComment.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `editComment - not_found`() {
            rest.put().uri("/articles/1/comments/${ArticleComment.Id.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `editComment - ok`() {
            rest.put().uri("/articles/1/comments/${ArticleComment.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isOk
        }
    }

    @Nested
    @Order(5)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteComment {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `deleteComment - unauthorized`() {
            rest.delete().uri("/articles/1/comments/${ArticleComment.Id.BY_MEMBER1}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `deleteComment - forbidden`() {
            rest.delete().uri("/articles/1/comments/${ArticleComment.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `deleteComment - not_found`() {
            rest.delete().uri("/articles/1/comments/${ArticleComment.Id.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteComment - ok`() {
            rest.delete().uri("/articles/1/comments/${ArticleComment.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
        }
    }
}