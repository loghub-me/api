package me.loghub.api.controller.article

import me.loghub.api.controller.BaseControllerTest
import me.loghub.api.dto.article.ArticleDetailDTO
import me.loghub.api.dto.article.ArticleForEditDTO
import me.loghub.api.dto.article.ArticleSort
import me.loghub.api.dto.article.PostArticleDTO
import me.loghub.api.util.resetDatabase
import org.junit.jupiter.api.*
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.client.expectBody
import org.springframework.web.util.UriComponentsBuilder
import kotlin.test.Test

@TestClassOrder(ClassOrderer.OrderAnnotation::class)
class ArticleControllerTest : BaseControllerTest() {
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

    @Nested
    @Order(1)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SearchArticles {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `searchArticles - ok - no params`() {
            rest.get().uri("/articles")
                .exchange()
                .expectStatus().isOk
        }

        @Test
        fun `searchArticles - ok - with params`() {
            val uri = UriComponentsBuilder.fromPath("/articles")
                .queryParam("query", "article")
                .queryParam("sort", ArticleSort.trending)
                .queryParam("page", 1)
                .toUriString()
            rest.get().uri(uri)
                .exchange()
                .expectStatus().isOk
        }

        @Test
        fun `searchArticles - bad_request - invalid sort`() {
            val uri = UriComponentsBuilder.fromPath("/articles")
                .queryParam("sort", "foo")
                .toUriString()
            rest.get().uri(uri)
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `searchArticles - bad_request - invalid page`() {
            val uri = UriComponentsBuilder.fromPath("/articles")
                .queryParam("page", 0)
                .toUriString()
            rest.get().uri(uri)
                .exchange()
                .expectStatus().isBadRequest
        }
    }

    @Nested
    @Order(2)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetArticle {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getArticle - not_found`() {
            rest.get().uri("/articles/@${member1.username}/${Article.Slug.INVALID}")
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `getArticle - ok`() {
            rest.get().uri("/articles/@${member1.username}/${Article.Slug.BY_MEMBER1}")
                .exchange()
                .expectStatus().isOk
                .expectBody<ArticleDetailDTO>()
        }
    }

    @Nested
    @Order(3)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetArticleForEdit {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `getArticleForEdit - unauthorized`() {
            rest.get().uri("/articles/${Article.Id.BY_MEMBER1}/for-edit")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `getArticleForEdit - forbidden`() {
            rest.get().uri("/articles/${Article.Id.BY_MEMBER1}/for-edit")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `getArticleForEdit - not_found`() {
            rest.get().uri("/articles/${Article.Id.INVALID}/for-edit")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `getArticleForEdit - ok`() {
            rest.get().uri("/articles/${Article.Id.BY_MEMBER1}/for-edit")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
                .expectBody<ArticleForEditDTO>()
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
            published = true,
        )

        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `postArticle - unauthorized`() {
            rest.post().uri("/articles")
                .body(bodyForPost)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `postArticle - bad_request - short title`() {
            rest.post().uri("/articles")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForPost.copy(title = "f"))
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `postArticle - bad_request - empty content`() {
            rest.post().uri("/articles")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForPost.copy(content = ""))
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `postArticle - bad_request - invalid thumbnail`() {
            rest.post().uri("/articles")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForPost.copy(thumbnail = "invalid-thumbnail-path"))
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `postArticle - created`() {
            rest.post().uri("/articles")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForPost)
                .exchange()
                .expectStatus().isCreated
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
            published = true,
        )

        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `editArticle - unauthorized`() {
            rest.put().uri("/articles/${Article.Id.BY_MEMBER1}")
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `editArticle - forbidden`() {
            rest.put().uri("/articles/${Article.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `editArticle - not_found`() {
            rest.put().uri("/articles/${Article.Id.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        fun `editArticle - bad_request - short title`() {
            rest.put().uri("/articles/${Article.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit.copy(title = "f"))
                .exchange()
                .expectStatus().isBadRequest
        }

        @Test
        fun `editArticle - ok`() {
            rest.put().uri("/articles/${Article.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .body(bodyForEdit)
                .exchange()
                .expectStatus().isOk
        }
    }

    @Nested
    @Order(6)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class DeleteArticle {
        @BeforeAll
        fun setupDatabase() = resetDatabase(jdbcTemplate)

        @Test
        fun `deleteArticle - unauthorized`() {
            rest.delete().uri("/articles/${Article.Id.BY_MEMBER1}")
                .exchange()
                .expectStatus().isUnauthorized
        }

        @Test
        fun `deleteArticle - forbidden`() {
            rest.delete().uri("/articles/${Article.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member2Token.authorization)
                .exchange()
                .expectStatus().isForbidden
        }

        @Test
        fun `deleteArticle - not_found`() {
            rest.delete().uri("/articles/${Article.Id.INVALID}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isNotFound
        }

        @Test
        @Order(Integer.MAX_VALUE)
        fun `deleteArticle - ok`() {
            rest.delete().uri("/articles/${Article.Id.BY_MEMBER1}")
                .header(HttpHeaders.AUTHORIZATION, member1Token.authorization)
                .exchange()
                .expectStatus().isOk
        }
    }
}