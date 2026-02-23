package me.loghub.api.service.user

import me.loghub.api.dto.article.ArticleSort
import me.loghub.api.dto.question.QuestionFilter
import me.loghub.api.dto.question.QuestionSort
import me.loghub.api.dto.series.SeriesSort
import me.loghub.api.dto.user.post.UserPostProjection
import me.loghub.api.exception.validation.IllegalFieldException
import me.loghub.api.repository.article.ArticleCustomRepository
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.question.QuestionCustomRepository
import me.loghub.api.repository.series.SeriesCustomRepository
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.service.article.ArticleFixtures
import me.loghub.api.service.question.QuestionFixtures
import me.loghub.api.service.series.SeriesFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import kotlin.test.assertEquals

class UserPostServiceTest {
    private lateinit var articleRepository: ArticleRepository
    private lateinit var seriesRepository: SeriesRepository
    private lateinit var articleCustomRepository: ArticleCustomRepository
    private lateinit var seriesCustomRepository: SeriesCustomRepository
    private lateinit var questionCustomRepository: QuestionCustomRepository

    private lateinit var userPostService: UserPostService

    @BeforeEach
    fun setUp() {
        articleRepository = mock()
        seriesRepository = mock()
        articleCustomRepository = mock()
        seriesCustomRepository = mock()
        questionCustomRepository = mock()

        userPostService = UserPostService(
            articleRepository,
            seriesRepository,
            articleCustomRepository,
            seriesCustomRepository,
            questionCustomRepository
        )
    }

    @Nested
    inner class GetUserPostsTest {
        @Test
        fun `should return merged and sorted recent user posts`() {
            val articlePostOld = projection("article old", "/a-old", "2025-01-01T09:00:00+09:00")
            val articlePostNew = projection("article new", "/a-new", "2025-01-03T09:00:00+09:00")
            val seriesPost = projection("series", "/s", "2025-01-02T09:00:00+09:00")
            whenever(articleRepository.findRecentPost(any(), any(), any())).thenReturn(listOf(articlePostOld, articlePostNew))
            whenever(seriesRepository.findRecentChapterPost(any(), any(), any())).thenReturn(listOf(seriesPost))

            val result = userPostService.getUserPosts("testuser")

            assertEquals(3, result.size)
            assertEquals("article new", result[0].title)
            assertEquals("series", result[1].title)
            assertEquals("article old", result[2].title)
        }
    }

    @Nested
    inner class SearchUserArticlesTest {
        @Test
        fun `should return paginated user articles when page is valid`() {
            val article = ArticleFixtures.article()
            whenever(
                articleCustomRepository.search(
                    query = any(),
                    sort = any<ArticleSort>(),
                    pageable = any<Pageable>(),
                    username = anyOrNull(),
                    published = anyOrNull()
                )
            ).thenReturn(PageImpl(listOf(article)))

            val result = userPostService.searchUserArticles("testuser", " query ", ArticleSort.latest, 1)

            assertEquals(1, result.content.size)
            assertEquals(article.id, result.content.first().id)
        }

        @Test
        fun `should throw IllegalFieldException when page is not positive in article search`() {
            assertThrows<IllegalFieldException> {
                userPostService.searchUserArticles("testuser", "query", ArticleSort.latest, 0)
            }
        }
    }

    @Nested
    inner class SearchUserSeriesTest {
        @Test
        fun `should return paginated user series when page is valid`() {
            val series = SeriesFixtures.series()
            whenever(
                seriesCustomRepository.search(
                    query = any(),
                    sort = any<SeriesSort>(),
                    pageable = any<Pageable>(),
                    username = anyOrNull(),
                )
            ).thenReturn(PageImpl(listOf(series)))

            val result = userPostService.searchUserSeries("testuser", " query ", SeriesSort.latest, 1)

            assertEquals(1, result.content.size)
            assertEquals(series.id, result.content.first().id)
        }
    }

    @Nested
    inner class SearchUserQuestionsTest {
        @Test
        fun `should return paginated user questions when page is valid`() {
            val question = QuestionFixtures.question()
            whenever(
                questionCustomRepository.search(
                    query = any(),
                    sort = any<QuestionSort>(),
                    filter = any<QuestionFilter>(),
                    pageable = any<Pageable>(),
                    username = anyOrNull(),
                )
            ).thenReturn(PageImpl(listOf(question)))

            val result = userPostService.searchUserQuestions(
                username = "testuser",
                query = " query ",
                sort = QuestionSort.latest,
                filter = QuestionFilter.all,
                page = 1
            )

            assertEquals(1, result.content.size)
            assertEquals(question.id, result.content.first().id)
        }
    }

    private fun projection(title: String, link: String, publishedAt: String): UserPostProjection {
        val projection = mock<UserPostProjection>()
        whenever(projection.title).thenReturn(title)
        whenever(projection.link).thenReturn(link)
        whenever(projection.publishedAt).thenReturn(publishedAt)
        return projection
    }
}
