package me.loghub.api.service.article

import me.loghub.api.dto.article.ArticleSort
import me.loghub.api.dto.common.RenderedMarkdownDTO
import me.loghub.api.entity.article.Article
import me.loghub.api.exception.auth.PermissionDeniedException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.lib.redis.key.article.ArticleDraftRedisKey
import me.loghub.api.repository.article.ArticleCustomRepository
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.topic.TopicRepository
import me.loghub.api.service.common.MarkdownService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ArticleServiceTest {
    private lateinit var articleRepository: ArticleRepository
    private lateinit var articleCustomRepository: ArticleCustomRepository
    private lateinit var topicRepository: TopicRepository
    private lateinit var markdownService: MarkdownService
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var valueOperations: ValueOperations<String, String>

    private lateinit var articleService: ArticleService

    @BeforeEach
    fun setUp() {
        articleRepository = mock()
        articleCustomRepository = mock()
        topicRepository = mock()
        markdownService = mock()
        redisTemplate = mock()
        valueOperations = mock()

        whenever(redisTemplate.opsForValue()).thenReturn(valueOperations)

        articleService = ArticleService(
            articleRepository,
            articleCustomRepository,
            topicRepository,
            markdownService,
            redisTemplate
        )
    }

    @Nested
    inner class SearchArticlesTest {
        @Test
        fun `should return paginated articles when search successful`() {
            val writer = ArticleFixtures.writer()
            val article = ArticleFixtures.article(writer = writer)
            val articlePage = PageImpl(listOf(article))
            whenever(
                articleCustomRepository.search(
                    query = any<String>(),
                    sort = any<ArticleSort>(),
                    pageable = any<Pageable>(),
                    anyOrNull(),
                    anyOrNull(),
                )
            ).thenReturn(articlePage)

            val result = articleService.searchArticles("query", ArticleSort.latest, 1)

            assertEquals(1, result.content.size)
            assertEquals(article.id, result.content.first().id)
            verify(articleCustomRepository).search(
                query = eq("query"),
                sort = eq(ArticleSort.latest),
                pageable = any(),
                anyOrNull(),
                anyOrNull(),
            )
            verifyNoMoreInteractions(articleCustomRepository)
        }
    }

    @Nested
    inner class GetArticleTest {
        @Test
        fun `should return article detail when article exists and is published`() {
            val writer = ArticleFixtures.writer()
            val article = ArticleFixtures.article(writer = writer, published = true)
            val renderedMarkdown = RenderedMarkdownDTO(html = "rendered content", anchors = emptyList())
            whenever(articleRepository.findWithWriterByCompositeKey(any<String>(), any<String>())).thenReturn(article)
            whenever(markdownService.findOrGenerateMarkdownCache(any<String>())).thenReturn(renderedMarkdown)

            val result = articleService.getArticle("username", "slug")

            assertEquals(article.id, result.id)
            assertEquals(renderedMarkdown.html, result.content.html)
            verify(articleRepository).findWithWriterByCompositeKey("username", "slug")
            verify(markdownService).findOrGenerateMarkdownCache(article.content)
        }

        @Test
        fun `should throw EntityNotFoundException when article does not exist`() {
            whenever(articleRepository.findWithWriterByCompositeKey(any<String>(), any<String>())).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                articleService.getArticle("username", "slug")
            }
        }

        @Test
        fun `should throw EntityNotFoundException when article is not published`() {
            val writer = ArticleFixtures.writer()
            val article = ArticleFixtures.article(writer = writer, published = false)
            whenever(articleRepository.findWithWriterByCompositeKey(any<String>(), any<String>())).thenReturn(article)

            assertThrows<EntityNotFoundException> {
                articleService.getArticle("username", "slug")
            }
        }
    }

    @Nested
    inner class GetArticleForEditTest {
        @Test
        fun `should return article for edit when article exists and user has permission`() {
            val writer = ArticleFixtures.writer()
            val article = ArticleFixtures.article(writer = writer)
            val draft = "draft content"
            whenever(articleRepository.findWithWriterById(any<Long>())).thenReturn(article)
            whenever(redisTemplate.opsForValue().get(any<String>())).thenReturn(draft)

            val result = articleService.getArticleForEdit(1L, writer)

            assertEquals(article.id, result.id)
            assertEquals(draft, result.draft)
            verify(articleRepository).findWithWriterById(1L)
            verify(redisTemplate.opsForValue()).get(ArticleDraftRedisKey(article.id!!))
        }

        @Test
        fun `should throw EntityNotFoundException when article does not exist`() {
            whenever(articleRepository.findWithWriterById(any<Long>())).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                articleService.getArticleForEdit(1L, ArticleFixtures.writer())
            }
        }

        @Test
        fun `should throw EntityNotFoundException when user does not have permission`() {
            val writer = ArticleFixtures.writer(id = 1L, username = "writer")
            val reader = ArticleFixtures.writer(id = 2L, username = "reader")
            val article = ArticleFixtures.article(writer = writer)
            whenever(articleRepository.findWithWriterById(any<Long>())).thenReturn(article)

            assertThrows<PermissionDeniedException> {
                articleService.getArticleForEdit(1L, reader)
            }
        }
    }

    @Nested
    inner class PostArticleTest {
        @Test
        fun `should create and return article when post article request is valid`() {
            val requestBody = ArticleFixtures.postArticleDTO()
            val writer = ArticleFixtures.writer()
            val article = ArticleFixtures.article(writer = writer)
            whenever(articleRepository.existsByCompositeKey(any<String>(), any<String>())).thenReturn(false)
            whenever(topicRepository.findBySlugIn(any<List<String>>())).thenReturn(emptySet())
            whenever(markdownService.normalizeMarkdown(any<String>())).thenReturn("normalized content")
            whenever(articleRepository.save(any<Article>())).thenReturn(article)

            val result = articleService.postArticle(requestBody, writer)

            assertEquals(article.id, result.id)
            verify(articleRepository).existsByCompositeKey(writer.username, "test-article")
            verify(topicRepository).findBySlugIn(requestBody.topicSlugs)
            verify(markdownService).normalizeMarkdown(requestBody.content)
            verify(articleRepository).save(any<Article>())
        }

        @Test
        fun `should generate unique slug when slug already exists`() {
            val requestBody = ArticleFixtures.postArticleDTO()
            val writer = ArticleFixtures.writer()
            val article = ArticleFixtures.article(writer = writer)
            whenever(articleRepository.existsByCompositeKey(any<String>(), any<String>())).thenReturn(true, false)
            whenever(topicRepository.findBySlugIn(any<List<String>>())).thenReturn(emptySet())
            whenever(markdownService.normalizeMarkdown(any<String>())).thenReturn("normalized content")
            whenever(articleRepository.save(any<Article>())).thenReturn(article)

            val result = articleService.postArticle(requestBody, writer)

            assertEquals(article.id, result.id)
            val slugCaptor = argumentCaptor<String>()
            verify(articleRepository, times(2)).existsByCompositeKey(eq(writer.username), slugCaptor.capture())
            assertEquals("test-article", slugCaptor.firstValue)
            assertTrue(slugCaptor.secondValue.startsWith("test-article-"))
            verify(topicRepository).findBySlugIn(requestBody.topicSlugs)
            verify(markdownService).normalizeMarkdown(requestBody.content)
            verify(articleRepository).save(any<Article>())
        }
    }

    @Nested
    inner class EditArticleTest {
        @Test
        fun `should update and return article when edit article request is valid`() {
            val requestBody = ArticleFixtures.postArticleDTO()
            val writer = ArticleFixtures.writer()
            val article = ArticleFixtures.article(writer = writer)
            whenever(articleRepository.findWithWriterById(any<Long>())).thenReturn(article)
            whenever(
                articleRepository.existsByCompositeKeyAndIdNot(
                    any<String>(),
                    any<String>(),
                    any<Long>()
                )
            ).thenReturn(false)
            whenever(topicRepository.findBySlugIn(any<List<String>>())).thenReturn(emptySet())
            whenever(markdownService.normalizeMarkdown(any<String>())).thenReturn("normalized content")

            val result = articleService.editArticle(1L, requestBody, writer)

            assertEquals(article.id, result.id)
            verify(articleRepository).findWithWriterById(1L)
            verify(articleRepository).existsByCompositeKeyAndIdNot(writer.username, "test-article", 1L)
            verify(topicRepository).findBySlugIn(requestBody.topicSlugs)
            verify(markdownService).normalizeMarkdown(requestBody.content)
            verify(articleRepository, never()).save(any<Article>())
        }
    }

    @Nested
    inner class DeleteArticleTest {
        @Test
        fun `should delete article when article exists and user has permission`() {
            val writer = ArticleFixtures.writer()
            val article = ArticleFixtures.article(writer = writer)
            whenever(articleRepository.findWithWriterById(any<Long>())).thenReturn(article)

            articleService.deleteArticle(1L, writer)

            verify(articleRepository).findWithWriterById(1L)
            verify(articleRepository).delete(article)
        }

        @Test
        fun `should throw EntityNotFoundException when article does not exist`() {
            whenever(articleRepository.findWithWriterById(any<Long>())).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                articleService.deleteArticle(1L, ArticleFixtures.writer())
            }
        }

        @Test
        fun `should throw PermissionDeniedException when user does not have permission`() {
            val writer = ArticleFixtures.writer(id = 1L, username = "writer")
            val reader = ArticleFixtures.writer(id = 2L, username = "reader")
            val article = ArticleFixtures.article(writer = writer)
            whenever(articleRepository.findWithWriterById(any<Long>())).thenReturn(article)

            assertThrows<PermissionDeniedException> {
                articleService.deleteArticle(1L, reader)
            }
        }
    }
}
