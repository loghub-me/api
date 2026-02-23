package me.loghub.api.service.user

import me.loghub.api.dto.article.ArticleSort
import me.loghub.api.repository.article.ArticleCustomRepository
import me.loghub.api.service.article.ArticleFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import kotlin.test.assertEquals

class UserArticleServiceTest {
    private lateinit var articleCustomRepository: ArticleCustomRepository

    private lateinit var userArticleService: UserArticleService

    @BeforeEach
    fun setUp() {
        articleCustomRepository = mock()
        userArticleService = UserArticleService(articleCustomRepository)
    }

    @Nested
    inner class SearchUnpublishedArticlesTest {
        @Test
        fun `should return unpublished articles for user`() {
            val user = ArticleFixtures.writer(username = "testuser")
            val article = ArticleFixtures.article(writer = user, published = false)
            whenever(
                articleCustomRepository.search(
                    query = any(),
                    sort = any<ArticleSort>(),
                    pageable = any<Pageable>(),
                    username = anyOrNull(),
                    published = anyOrNull(),
                )
            ).thenReturn(PageImpl(listOf(article)))

            val result = userArticleService.searchUnpublishedArticles("query", user)

            assertEquals(1, result.size)
            assertEquals(article.id, result.first().id)
            verify(articleCustomRepository).search(
                query = eq("query"),
                sort = eq(ArticleSort.latest),
                pageable = any<Pageable>(),
                username = eq(user.username),
                published = eq(false),
            )
        }
    }

    @Nested
    inner class SearchArticlesForImportTest {
        @Test
        fun `should return articles for import for user`() {
            val user = ArticleFixtures.writer(username = "testuser")
            val article = ArticleFixtures.article(writer = user, published = true)
            whenever(
                articleCustomRepository.search(
                    query = any(),
                    sort = any<ArticleSort>(),
                    pageable = any<Pageable>(),
                    username = anyOrNull(),
                    published = anyOrNull(),
                )
            ).thenReturn(PageImpl(listOf(article)))

            val result = userArticleService.searchArticlesForImport("query", user)

            assertEquals(1, result.size)
            assertEquals(article.id, result.first().id)
            verify(articleCustomRepository).search(
                query = eq("query"),
                sort = eq(ArticleSort.latest),
                pageable = any<Pageable>(),
                username = eq(user.username),
                published = eq(true),
            )
        }
    }
}
