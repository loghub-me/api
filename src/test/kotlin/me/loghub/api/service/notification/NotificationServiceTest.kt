package me.loghub.api.service.notification

import me.loghub.api.dto.notification.CreateNotificationDTO
import me.loghub.api.dto.notification.event.NotificationCreatedEvent
import me.loghub.api.dto.notification.event.NotificationDeletedEvent
import me.loghub.api.dto.notification.event.NotificationReadAllEvent
import me.loghub.api.dto.notification.event.NotificationReadEvent
import me.loghub.api.entity.notification.Notification
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.repository.notification.NotificationRepository
import me.loghub.api.service.article.ArticleFixtures
import me.loghub.api.service.auth.AuthFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NotificationServiceTest {
    private lateinit var notificationRepository: NotificationRepository
    private lateinit var eventPublisher: ApplicationEventPublisher

    private lateinit var notificationService: NotificationService

    @BeforeEach
    fun setUp() {
        notificationRepository = mock()
        eventPublisher = mock()

        notificationService = NotificationService(notificationRepository, eventPublisher)
    }

    @Nested
    inner class GetNotificationsTest {
        @Test
        fun `should return mapped notifications in descending order`() {
            val recipient = AuthFixtures.user(id = 1L, username = "recipient")
            val actor = AuthFixtures.user(id = 2L, username = "actor")
            val article = ArticleFixtures.article(id = 3L, writer = actor)
            val notifications = listOf(
                notification(id = 3L, actor = actor, recipient = recipient, article = article),
                notification(
                    id = 2L,
                    actor = actor,
                    recipient = recipient,
                    article = article,
                    readAt = LocalDateTime.now()
                ),
            )
            whenever(notificationRepository.findTop20ByRecipient(eq(recipient), eq(null)))
                .thenReturn(notifications)

            val result = notificationService.getNotifications(recipient, null)

            assertEquals(listOf(3L, 2L), result.map { it.id })
            assertEquals(listOf(false, true), result.map { it.read })
        }
    }

    @Nested
    inner class CountUnreadNotificationsTest {
        @Test
        fun `should return unread notification count`() {
            val recipient = AuthFixtures.user(id = 1L)
            whenever(notificationRepository.countByRecipientAndReadAtIsNull(recipient)).thenReturn(4L)

            val result = notificationService.countUnreadNotifications(recipient)

            assertEquals(4L, result)
        }
    }

    @Nested
    inner class CreateNotificationTest {
        @Test
        fun `should save notification and publish created event`() {
            val recipient = AuthFixtures.user(id = 1L, username = "recipient")
            val actor = AuthFixtures.user(id = 2L, username = "actor")
            val article = ArticleFixtures.article(id = 3L, writer = actor)
            val request = CreateNotificationDTO(
                targetType = Notification.TargetType.ARTICLE,
                article = article,
                actor = actor,
                recipient = recipient,
            )
            whenever(notificationRepository.save(any<Notification>())).thenAnswer { invocation ->
                (invocation.arguments.first() as Notification).apply { id = 10L }
            }

            val result = notificationService.createNotification(request)

            assertEquals(10L, result.id)
            verify(notificationRepository).save(any<Notification>())
            verify(eventPublisher).publishEvent(check<NotificationCreatedEvent> {
                assertEquals("notification.created", it.name)
                assertEquals(1L, it.recipientId)
                assertEquals(10L, it.data.id)
            })
        }
    }

    @Nested
    inner class ReadNotificationTest {
        @Test
        fun `should mark notification as read and publish read event`() {
            val recipient = AuthFixtures.user(id = 1L)
            val actor = AuthFixtures.user(id = 2L)
            val article = ArticleFixtures.article(id = 3L, writer = actor)
            val notification =
                notification(id = 2L, actor = actor, recipient = recipient, article = article, readAt = null)
            whenever(notificationRepository.findWithRecipientById(2L)).thenReturn(notification)

            notificationService.readNotification(recipient, 2L)

            assertNotNull(notification.readAt)
            verify(eventPublisher).publishEvent(check<NotificationReadEvent> {
                assertEquals("notification.read", it.name)
                assertEquals(1L, it.recipientId)
                assertEquals(2L, it.notificationId)
            })
        }

        @Test
        fun `should throw EntityNotFoundException when notification is missing`() {
            val recipient = AuthFixtures.user(id = 1L)
            whenever(notificationRepository.findWithRecipientById(2L)).thenReturn(null)

            assertThrows<EntityNotFoundException> {
                notificationService.readNotification(recipient, 2L)
            }
        }
    }

    @Nested
    inner class ReadAllNotificationsTest {
        @Test
        fun `should mark all unread notifications as read and publish read all event`() {
            val recipient = AuthFixtures.user(id = 1L)
            whenever(notificationRepository.markAllAsRead(eq(1L), any())).thenReturn(3)

            notificationService.readAllNotifications(recipient)

            verify(notificationRepository).markAllAsRead(eq(1L), any())
            verify(eventPublisher).publishEvent(check<NotificationReadAllEvent> {
                assertEquals("notification.read_all", it.name)
                assertEquals(1L, it.recipientId)
                assertEquals(3, it.readCount)
            })
        }
    }

    @Nested
    inner class DeleteNotificationTest {
        @Test
        fun `should delete owned notification and publish deleted event`() {
            val recipient = AuthFixtures.user(id = 1L)
            val actor = AuthFixtures.user(id = 2L)
            val article = ArticleFixtures.article(id = 3L, writer = actor)
            val notification = notification(id = 2L, actor = actor, recipient = recipient, article = article)
            whenever(notificationRepository.findWithRecipientById(2L)).thenReturn(notification)

            notificationService.deleteNotification(recipient, 2L)

            verify(notificationRepository).delete(notification)
            verify(eventPublisher).publishEvent(check<NotificationDeletedEvent> {
                assertEquals("notification.deleted", it.name)
                assertEquals(1L, it.recipientId)
                assertEquals(2L, it.notificationId)
            })
        }
    }

    private fun notification(
        id: Long,
        actor: me.loghub.api.entity.user.User,
        recipient: me.loghub.api.entity.user.User,
        article: me.loghub.api.entity.article.Article,
        readAt: LocalDateTime? = null,
    ) = Notification(
        readAt = readAt,
        type = Notification.Type.INFO,
        targetType = Notification.TargetType.ARTICLE,
        article = article,
        actor = actor,
        recipient = recipient,
    ).apply {
        this.id = id
        this.createdAt = LocalDateTime.now().minusMinutes(id)
        this.updatedAt = this.createdAt
    }
}
