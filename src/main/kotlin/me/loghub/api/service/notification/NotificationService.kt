package me.loghub.api.service.notification

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.notification.CreateNotificationDTO
import me.loghub.api.dto.notification.NotificationDTO
import me.loghub.api.dto.notification.event.NotificationCreatedEvent
import me.loghub.api.dto.notification.event.NotificationDeletedEvent
import me.loghub.api.dto.notification.event.NotificationReadAllEvent
import me.loghub.api.dto.notification.event.NotificationReadEvent
import me.loghub.api.entity.user.User
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.mapper.notification.NotificationMapper
import me.loghub.api.repository.notification.NotificationRepository
import me.loghub.api.util.checkPermission
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional(readOnly = true)
    fun getNotifications(user: User, cursor: Long?): List<NotificationDTO> =
        notificationRepository.findByRecipientLimit20(
            recipient = user,
            cursor = cursor,
        ).map(NotificationMapper::map)

    @Transactional(readOnly = true)
    fun countUnreadNotifications(user: User): Long =
        notificationRepository.countByRecipientAndReadAtIsNull(user)

    @Transactional
    fun createNotification(requestBody: CreateNotificationDTO): NotificationDTO {
        val notification = notificationRepository.save(requestBody.toEntity())
        val notificationDTO = NotificationMapper.map(notification)
        val recipientId = notification.recipient.id!!

        eventPublisher.publishEvent(NotificationCreatedEvent(notificationDTO, recipientId))
        return notificationDTO
    }

    @Transactional
    fun readNotification(user: User, notificationId: Long) {
        val notification = notificationRepository.findWithRecipientById(notificationId)
            ?: throw EntityNotFoundException(ResponseMessage.Notification.NOT_FOUND)

        checkPermission(notification.recipient == user) { ResponseMessage.Notification.PERMISSION_DENIED }
        if (notification.read) return

        notification.markAsRead()

        val recipientId = notification.recipient.id!!
        eventPublisher.publishEvent(NotificationReadEvent(notificationId, recipientId))
    }

    @Transactional
    fun readAllNotifications(user: User) {
        val readCount = notificationRepository.markAllAsRead(user.id!!, LocalDateTime.now())
        if (readCount == 0) return

        val recipientId = user.id!!
        eventPublisher.publishEvent(NotificationReadAllEvent(readCount, recipientId))
    }

    @Transactional
    fun deleteNotification(user: User, notificationId: Long) {
        val notification = notificationRepository.findWithRecipientById(notificationId)
            ?: throw EntityNotFoundException(ResponseMessage.Notification.NOT_FOUND)

        checkPermission(notification.recipient == user) { ResponseMessage.Notification.PERMISSION_DENIED }

        notificationRepository.delete(notification)

        val recipientId = notification.recipient.id!!
        eventPublisher.publishEvent(NotificationDeletedEvent(notificationId, recipientId))
    }
}