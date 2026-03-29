package me.loghub.api.listener

import me.loghub.api.dto.article.event.ArticleCreatedEvent
import me.loghub.api.dto.question.event.QuestionCreatedEvent
import me.loghub.api.dto.series.event.SeriesCreatedEvent
import me.loghub.api.entity.notification.Notification
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.user.UserFollowRepository
import me.loghub.api.service.article.ArticleFixtures
import me.loghub.api.service.auth.AuthFixtures
import me.loghub.api.service.notification.NotificationService
import me.loghub.api.service.question.QuestionFixtures
import me.loghub.api.service.series.SeriesFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

class PostNotificationEventListenerTest {
    private lateinit var articleRepository: ArticleRepository
    private lateinit var seriesRepository: SeriesRepository
    private lateinit var questionRepository: QuestionRepository
    private lateinit var userFollowRepository: UserFollowRepository
    private lateinit var notificationService: NotificationService

    private lateinit var listener: PostNotificationEventListener

    @BeforeEach
    fun setUp() {
        articleRepository = mock()
        seriesRepository = mock()
        questionRepository = mock()
        userFollowRepository = mock()
        notificationService = mock()

        listener = PostNotificationEventListener(
            articleRepository,
            seriesRepository,
            questionRepository,
            userFollowRepository,
            notificationService,
        )
    }

    @Nested
    inner class HandleArticleCreatedEventTest {
        @Test
        fun `should notify followers of the article writer in batches`() {
            val writer = AuthFixtures.user(id = 1L, username = "writer")
            val follower1 = AuthFixtures.user(id = 2L, username = "follower1")
            val follower2 = AuthFixtures.user(id = 3L, username = "follower2")
            val article = ArticleFixtures.article(id = 10L, writer = writer)
            val event = ArticleCreatedEvent(article.id!!, writer.id!!)

            whenever(articleRepository.findWithWriterById(article.id!!)).thenReturn(article)
            whenever(userFollowRepository.findFollowersByFolloweeOrderByIdDesc(eq(writer), any<Pageable>()))
                .thenReturn(PageImpl(listOf(follower1, follower2)))

            listener.handle(event)

            verify(notificationService).createNotifications(check { requests ->
                assert(requests.size == 2)
                assert(requests.all { it.targetType == Notification.TargetType.ARTICLE })
                assert(requests.all { it.article == article })
                assert(requests.all { it.actor == writer })
                assert(requests.map { it.recipient }.containsAll(listOf(follower1, follower2)))
            })
        }

        @Test
        fun `should paginate followers across multiple pages`() {
            val writer = AuthFixtures.user(id = 1L, username = "writer")
            val followers1 = (1..100).map { AuthFixtures.user(id = it.toLong() + 10, username = "f$it") }
            val followers2 = listOf(AuthFixtures.user(id = 200L, username = "last"))
            val article = ArticleFixtures.article(id = 10L, writer = writer)
            val event = ArticleCreatedEvent(article.id!!, writer.id!!)

            whenever(articleRepository.findWithWriterById(article.id!!)).thenReturn(article)
            whenever(
                userFollowRepository.findFollowersByFolloweeOrderByIdDesc(eq(writer), eq(PageRequest.of(0, 100)))
            ).thenReturn(PageImpl(followers1, PageRequest.of(0, 100), 101))
            whenever(
                userFollowRepository.findFollowersByFolloweeOrderByIdDesc(eq(writer), eq(PageRequest.of(1, 100)))
            ).thenReturn(PageImpl(followers2, PageRequest.of(1, 100), 101))

            listener.handle(event)

            verify(notificationService, times(2)).createNotifications(any())
        }

        @Test
        fun `should do nothing when article is not found`() {
            val event = ArticleCreatedEvent(99L, 0L)
            whenever(articleRepository.findWithWriterById(99L)).thenReturn(null)

            listener.handle(event)

            verifyNoInteractions(userFollowRepository, notificationService)
        }

        @Test
        fun `should call createNotifications with empty list when writer has no followers`() {
            val writer = AuthFixtures.user(id = 1L, username = "writer")
            val article = ArticleFixtures.article(id = 10L, writer = writer)
            val event = ArticleCreatedEvent(article.id!!, writer.id!!)

            whenever(articleRepository.findWithWriterById(article.id!!)).thenReturn(article)
            whenever(userFollowRepository.findFollowersByFolloweeOrderByIdDesc(eq(writer), any<Pageable>()))
                .thenReturn(PageImpl(emptyList()))

            listener.handle(event)

            verifyNoInteractions(notificationService)
        }
    }

    @Nested
    inner class HandleSeriesCreatedEventTest {
        @Test
        fun `should notify followers of the series writer`() {
            val writer = AuthFixtures.user(id = 1L, username = "writer")
            val follower = AuthFixtures.user(id = 2L, username = "follower")
            val series = SeriesFixtures.series(id = 10L, writer = writer)
            val event = SeriesCreatedEvent(series.id!!, writer.id!!)

            whenever(seriesRepository.findWithWriterById(series.id!!)).thenReturn(series)
            whenever(userFollowRepository.findFollowersByFolloweeOrderByIdDesc(eq(writer), any<Pageable>()))
                .thenReturn(PageImpl(listOf(follower)))

            listener.handle(event)

            verify(notificationService).createNotifications(check { requests ->
                assert(requests.size == 1)
                assert(requests.first().targetType == Notification.TargetType.SERIES)
                assert(requests.first().series == series)
                assert(requests.first().actor == writer)
                assert(requests.first().recipient == follower)
            })
        }

        @Test
        fun `should do nothing when series is not found`() {
            val event = SeriesCreatedEvent(99L, 0L)
            whenever(seriesRepository.findWithWriterById(99L)).thenReturn(null)

            listener.handle(event)

            verifyNoInteractions(userFollowRepository, notificationService)
        }
    }

    @Nested
    inner class HandleQuestionCreatedEventTest {
        @Test
        fun `should notify followers of the question writer`() {
            val writer = AuthFixtures.user(id = 1L, username = "writer")
            val follower = AuthFixtures.user(id = 2L, username = "follower")
            val question = QuestionFixtures.question(id = 10L, writer = writer)
            val event = QuestionCreatedEvent(question.id!!, writer.id!!)

            whenever(questionRepository.findWithWriterById(question.id!!)).thenReturn(question)
            whenever(userFollowRepository.findFollowersByFolloweeOrderByIdDesc(eq(writer), any<Pageable>()))
                .thenReturn(PageImpl(listOf(follower)))

            listener.handle(event)

            verify(notificationService).createNotifications(check { requests ->
                assert(requests.size == 1)
                assert(requests.first().targetType == Notification.TargetType.QUESTION)
                assert(requests.first().question == question)
                assert(requests.first().actor == writer)
                assert(requests.first().recipient == follower)
            })
        }

        @Test
        fun `should do nothing when question is not found`() {
            val event = QuestionCreatedEvent(99L, 0L)
            whenever(questionRepository.findWithWriterById(99L)).thenReturn(null)

            listener.handle(event)

            verifyNoInteractions(userFollowRepository, notificationService)
        }
    }
}
