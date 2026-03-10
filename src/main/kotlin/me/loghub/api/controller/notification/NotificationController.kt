package me.loghub.api.controller.notification

import jakarta.servlet.http.HttpServletResponse
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.notification.LastEventId
import me.loghub.api.dto.notification.NotificationDTO
import me.loghub.api.dto.response.DataResponseBody
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.entity.user.User
import me.loghub.api.service.notification.NotificationService
import me.loghub.api.service.notification.NotificationStreamService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/notifications")
class NotificationController(
    private val notificationService: NotificationService,
    private val notificationStreamService: NotificationStreamService,
) {
    @GetMapping
    fun getNotifications(
        @AuthenticationPrincipal user: User,
        @RequestParam(required = false) cursor: Long?,
    ): ResponseEntity<List<NotificationDTO>> {
        val notifications = notificationService.getNotifications(user, cursor)
        return ResponseEntity.ok(notifications)
    }

    @GetMapping("/unread/count")
    fun countUnreadNotifications(@AuthenticationPrincipal user: User): ResponseEntity<ResponseBody> {
        val notificationCount = notificationService.countUnreadNotifications(user)
        return DataResponseBody(notificationCount, HttpStatus.OK).toResponseEntity()
    }

    @GetMapping(path = ["/stream"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamNotifications(
        @AuthenticationPrincipal user: User,
        @RequestHeader(name = LastEventId.Header.NAME, required = false) lastEventIdHeader: String?,
        response: HttpServletResponse,
    ): SseEmitter {
        val lastEventId = lastEventIdHeader?.let { LastEventId.Header.from(it) }

        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store")
        return notificationStreamService.subscribe(user.id!!, lastEventId)
    }

    @PatchMapping("/{id}/read")
    fun readNotification(
        @PathVariable id: Long,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<ResponseBody> {
        notificationService.readNotification(user, id)
        return MessageResponseBody(
            message = ResponseMessage.Notification.READ_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PatchMapping("/all/read")
    fun readAllNotifications(@AuthenticationPrincipal user: User): ResponseEntity<ResponseBody> {
        notificationService.readAllNotifications(user)
        return MessageResponseBody(
            message = ResponseMessage.Notification.READ_ALL_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping("/{id}")
    fun deleteNotification(
        @PathVariable id: Long,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<ResponseBody> {
        notificationService.deleteNotification(user, id)
        return MessageResponseBody(
            message = ResponseMessage.Notification.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}
