package me.loghub.api.service.article

import me.loghub.api.constant.trending.ArticleTrendingScoreDelta
import me.loghub.api.entity.user.UserStar
import me.loghub.api.exception.entity.EntityConflictException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.article.ArticleStatsRepository
import me.loghub.api.repository.user.UserStarRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ArticleStarServiceTest {
    private lateinit var userStarRepository: UserStarRepository
    private lateinit var articleRepository: ArticleRepository
    private lateinit var articleStatsRepository: ArticleStatsRepository
    private lateinit var articleTrendingScoreService: ArticleTrendingScoreService

    private lateinit var articleStarService: ArticleStarService

    @BeforeEach
    fun setUp() {
        userStarRepository = mock()
        articleRepository = mock()
        articleStatsRepository = mock()
        articleTrendingScoreService = mock()

        articleStarService = ArticleStarService(
            userStarRepository,
            articleRepository,
            articleStatsRepository,
            articleTrendingScoreService,
        )
    }

    @Nested
    inner class ExistsStarTest {
        @Test
        fun `should return true when user already starred article`() {
            val articleId = 1L
            val stargazer = ArticleFixtures.writer(id = 10L, username = "stargazer")
            val articleRef = ArticleFixtures.article(id = articleId)
            whenever(articleRepository.getReferenceById(articleId)).thenReturn(articleRef)
            whenever(userStarRepository.existsByArticleAndStargazer(articleRef, stargazer)).thenReturn(true)

            val result = articleStarService.existsStar(articleId, stargazer)

            assertTrue(result)
            verify(articleRepository).getReferenceById(articleId)
            verify(userStarRepository).existsByArticleAndStargazer(articleRef, stargazer)
        }
    }

    @Nested
    inner class AddStarTest {
        @Test
        fun `should add star when article exists and user has not starred yet`() {
            val articleId = 1L
            val stargazer = ArticleFixtures.writer(id = 10L, username = "stargazer")
            val article = ArticleFixtures.article(id = articleId)
            whenever(articleRepository.findById(articleId)).thenReturn(Optional.of(article))
            whenever(userStarRepository.existsByArticleAndStargazer(article, stargazer)).thenReturn(false)
            whenever(userStarRepository.save(any<UserStar>())).thenAnswer { invocation ->
                invocation.arguments.first() as UserStar
            }

            val result = articleStarService.addStar(articleId, stargazer)

            assertEquals(UserStar.Target.ARTICLE, result.target)
            assertEquals(article, result.article)
            assertEquals(stargazer, result.stargazer)
            verify(articleRepository).findById(articleId)
            verify(userStarRepository).existsByArticleAndStargazer(article, stargazer)
            verify(articleStatsRepository).incrementStarCount(articleId)
            verify(articleTrendingScoreService).updateTrendingScore(articleId, ArticleTrendingScoreDelta.STAR)

            val savedStarCaptor = argumentCaptor<UserStar>()
            verify(userStarRepository).save(savedStarCaptor.capture())
            assertEquals(UserStar.Target.ARTICLE, savedStarCaptor.firstValue.target)
            assertEquals(article, savedStarCaptor.firstValue.article)
            assertEquals(stargazer, savedStarCaptor.firstValue.stargazer)
        }

        @Test
        fun `should throw EntityConflictException when star already exists`() {
            val articleId = 1L
            val stargazer = ArticleFixtures.writer(id = 10L, username = "stargazer")
            val article = ArticleFixtures.article(id = articleId)
            whenever(articleRepository.findById(articleId)).thenReturn(Optional.of(article))
            whenever(userStarRepository.existsByArticleAndStargazer(article, stargazer)).thenReturn(true)

            assertThrows<EntityConflictException> {
                articleStarService.addStar(articleId, stargazer)
            }

            verify(articleRepository).findById(articleId)
            verify(userStarRepository).existsByArticleAndStargazer(article, stargazer)
            verify(articleStatsRepository, never()).incrementStarCount(any())
            verify(userStarRepository, never()).save(any<UserStar>())
            verify(articleTrendingScoreService, never()).updateTrendingScore(any(), any())
        }

        @Test
        fun `should throw EntityNotFoundException when article does not exist`() {
            val articleId = 1L
            val stargazer = ArticleFixtures.writer(id = 10L, username = "stargazer")
            whenever(articleRepository.findById(articleId)).thenReturn(Optional.empty())

            assertThrows<EntityNotFoundException> {
                articleStarService.addStar(articleId, stargazer)
            }

            verify(articleRepository).findById(articleId)
            verify(userStarRepository, never()).existsByArticleAndStargazer(any(), any())
            verify(articleStatsRepository, never()).incrementStarCount(any())
            verify(userStarRepository, never()).save(any<UserStar>())
            verify(articleTrendingScoreService, never()).updateTrendingScore(any(), any())
        }
    }

    @Nested
    inner class DeleteStarTest {
        @Test
        fun `should delete star when star exists`() {
            val articleId = 1L
            val stargazer = ArticleFixtures.writer(id = 10L, username = "stargazer")
            val articleRef = ArticleFixtures.article(id = articleId)
            whenever(articleRepository.getReferenceById(articleId)).thenReturn(articleRef)
            whenever(userStarRepository.deleteByArticleAndStargazer(articleRef, stargazer)).thenReturn(1)

            articleStarService.deleteStar(articleId, stargazer)

            verify(userStarRepository).deleteByArticleAndStargazer(articleRef, stargazer)
            verify(articleStatsRepository).decrementStarCount(articleId)
            verify(articleTrendingScoreService).updateTrendingScore(articleId, -ArticleTrendingScoreDelta.STAR)
        }

        @Test
        fun `should throw EntityNotFoundException when star does not exist`() {
            val articleId = 1L
            val stargazer = ArticleFixtures.writer(id = 10L, username = "stargazer")
            val articleRef = ArticleFixtures.article(id = articleId)
            whenever(articleRepository.getReferenceById(articleId)).thenReturn(articleRef)
            whenever(userStarRepository.deleteByArticleAndStargazer(articleRef, stargazer)).thenReturn(0)

            assertThrows<EntityNotFoundException> {
                articleStarService.deleteStar(articleId, stargazer)
            }

            verify(userStarRepository).deleteByArticleAndStargazer(articleRef, stargazer)
            verify(articleStatsRepository, never()).decrementStarCount(any())
            verify(articleTrendingScoreService, never()).updateTrendingScore(any(), any())
        }
    }
}
