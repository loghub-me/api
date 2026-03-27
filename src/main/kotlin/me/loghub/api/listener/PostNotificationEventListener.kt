package me.loghub.api.listener

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.config.AsyncConfig
import me.loghub.api.dto.article.event.ArticleCreatedEvent
import me.loghub.api.dto.notification.CreateNotificationDTO
import me.loghub.api.dto.question.event.QuestionCreatedEvent
import me.loghub.api.dto.series.event.SeriesCreatedEvent
import me.loghub.api.entity.notification.Notification
import me.loghub.api.entity.user.User
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.user.UserFollowRepository
import me.loghub.api.service.notification.NotificationService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.PageRequest
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PostNotificationEventListener(
    private val articleRepository: ArticleRepository,
    private val seriesRepository: SeriesRepository,
    private val questionRepository: QuestionRepository,
    private val userFollowRepository: UserFollowRepository,
    private val notificationService: NotificationService,
) {
    private companion object {
        private val logger = KotlinLogging.logger { }
        private const val FOLLOWER_BATCH_SIZE = 100
    }

    @Async(AsyncConfig.NotificationExecutor.NAME)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: ArticleCreatedEvent) {
        val article = articleRepository.findWithWriterById(event.articleId) ?: return
        val writer = article.writer

        notifyFollowers(writer) { follower ->
            CreateNotificationDTO(
                targetType = Notification.TargetType.ARTICLE,
                article = article,
                actor = writer,
                recipient = follower,
            )
        }
    }

    @Async(AsyncConfig.NotificationExecutor.NAME)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: SeriesCreatedEvent) {
        val series = seriesRepository.findWithWriterById(event.seriesId) ?: return
        val writer = series.writer

        notifyFollowers(writer) { follower ->
            CreateNotificationDTO(
                targetType = Notification.TargetType.SERIES,
                series = series,
                actor = writer,
                recipient = follower,
            )
        }
    }

    @Async(AsyncConfig.NotificationExecutor.NAME)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: QuestionCreatedEvent) {
        val question = questionRepository.findWithWriterById(event.questionId) ?: return
        val writer = question.writer

        notifyFollowers(writer) { follower ->
            CreateNotificationDTO(
                targetType = Notification.TargetType.QUESTION,
                question = question,
                actor = writer,
                recipient = follower,
            )
        }
    }

    private fun notifyFollowers(writer: User, createRequest: (User) -> CreateNotificationDTO) {
        var pageNumber = 0
        do {
            val pageable = PageRequest.of(pageNumber, FOLLOWER_BATCH_SIZE)
            val page = userFollowRepository.findFollowersByFolloweeOrderByIdDesc(writer, pageable)

            if (page.content.isNotEmpty()) {
                val requests = page.content.map { follower -> createRequest(follower) }

                try {
                    notificationService.createNotifications(requests)
                } catch (e: DataIntegrityViolationException) {
                    logger.warn(e) { "Skip notification fan-out due to data integrity violation." }
                } catch (e: JpaObjectRetrievalFailureException) {
                    logger.warn(e) { "Skip notification fan-out due to missing referenced entity." }
                }
            }

            pageNumber++
        } while (page.hasNext())
    }
}
