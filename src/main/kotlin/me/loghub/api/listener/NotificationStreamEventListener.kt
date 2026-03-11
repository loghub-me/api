package me.loghub.api.listener

import me.loghub.api.dto.notification.event.NotificationEvent
import me.loghub.api.service.notification.NotificationStreamService
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class NotificationStreamEventListener(private val notificationStreamService: NotificationStreamService) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: NotificationEvent) {
        notificationStreamService.publish(event)
    }
}