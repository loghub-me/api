package me.loghub.api.service.notification

import me.loghub.api.dto.notification.LastEventId
import me.loghub.api.dto.notification.event.NotificationConnectedEvent
import me.loghub.api.dto.notification.event.NotificationCreatedEvent
import me.loghub.api.dto.notification.event.NotificationEvent
import me.loghub.api.dto.notification.event.NotificationPingEvent
import me.loghub.api.mapper.notification.NotificationMapper
import me.loghub.api.repository.notification.NotificationRepository
import me.loghub.api.repository.user.UserRepository
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Service
class NotificationStreamService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
) {
    private companion object {
        const val EMITTER_TIMEOUT = 0L // no timeout, use heartbeat to keep connection alive
        const val HEARTBEAT_INTERVAL = 30_000L // 30 seconds
    }

    private val emitters = ConcurrentHashMap<Long, MutableSet<SseEmitter>>()

    fun subscribe(recipientId: Long, lastEventId: LastEventId?): SseEmitter {
        val emitter = SseEmitter(EMITTER_TIMEOUT)
        emitter.onCompletion { removeEmitter(recipientId, emitter) }
        emitter.onTimeout { removeEmitter(recipientId, emitter) }
        emitter.onError { removeEmitter(recipientId, emitter) }

        val userEmitters = emitters.computeIfAbsent(recipientId) { ConcurrentHashMap.newKeySet() }
        userEmitters += emitter

        sendToEmitter(recipientId, emitter, NotificationConnectedEvent(recipientId = recipientId))
        lastEventId?.let { replayMissedNotifications(recipientId, lastEventId, emitter) }

        return emitter
    }

    fun publish(event: NotificationEvent) =
        emitters[event.recipientId]
            ?.toList()
            ?.forEach { emitter -> sendToEmitter(event.recipientId, emitter, event) }

    @Scheduled(fixedDelay = HEARTBEAT_INTERVAL)
    fun heartbeat() = emitters.forEach { (recipientId, userEmitters) ->
        userEmitters.toList().forEach { emitter ->
            sendToEmitter(recipientId, emitter, NotificationPingEvent(recipientId))
        }
    }

    private fun replayMissedNotifications(recipientId: Long, lastEventId: LastEventId, emitter: SseEmitter) {
        val recipient = userRepository.getReferenceById(recipientId)
        notificationRepository.findTop20ByRecipientAndIdGreaterThanOrderByIdAsc(recipient, lastEventId.value)
            .map(NotificationMapper::map)
            .forEach { notification ->
                sendToEmitter(
                    recipientId = recipientId,
                    emitter = emitter,
                    event = NotificationCreatedEvent(notification, recipientId)
                )
            }
    }

    private fun sendToEmitter(recipientId: Long, emitter: SseEmitter, event: NotificationEvent) {
        try {
            val sseEvent = SseEmitter.event()
                .name(event.name)
                .data(event, MediaType.APPLICATION_JSON)

            if (event is NotificationCreatedEvent) {
                sseEvent.id(event.data.id.toString())
            }

            emitter.send(sseEvent)
        } catch (_: Exception) {
            removeEmitter(recipientId, emitter)
        }
    }

    private fun removeEmitter(recipientId: Long, emitter: SseEmitter) {
        val userEmitters = emitters[recipientId] ?: return

        userEmitters.remove(emitter)
        if (userEmitters.isEmpty()) {
            emitters.remove(recipientId)
        }
    }
}
