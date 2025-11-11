package me.loghub.api.controller.notification

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.notification.NotificationDTO
import me.loghub.api.dto.response.DataResponseBody
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.entity.user.User
import me.loghub.api.service.user.NotificationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notifications")
class NotificationController(private val notificationService: NotificationService) {
    @GetMapping
    fun getNotifications(@AuthenticationPrincipal user: User): ResponseEntity<List<NotificationDTO>> {
        val notifications = notificationService.getNotifications(user)
        return ResponseEntity.ok(notifications)
    }

    @GetMapping("/count")
    fun countNotifications(@AuthenticationPrincipal user: User): ResponseEntity<ResponseBody> {
        val notificationCount = notificationService.countNotifications(user)
        return DataResponseBody(notificationCount, HttpStatus.OK).toResponseEntity()
    }

    @DeleteMapping("/{timestamp}")
    fun deleteNotification(
        @PathVariable timestamp: Long,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<ResponseBody> {
        notificationService.deleteNotification(user, timestamp)
        return MessageResponseBody(
            message = ResponseMessage.Notification.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}