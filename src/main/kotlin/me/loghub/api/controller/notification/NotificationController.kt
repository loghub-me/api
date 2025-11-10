package me.loghub.api.controller.notification

import me.loghub.api.dto.response.DataResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.entity.user.User
import me.loghub.api.service.user.NotificationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notifications")
class NotificationController(private val notificationService: NotificationService) {
    @GetMapping
    fun getNotifications(@AuthenticationPrincipal user: User): ResponseEntity<ResponseBody> {
        val notifications = notificationService.getNotifications(user)
        return DataResponseBody(notifications, HttpStatus.OK).toResponseEntity()
    }

    @GetMapping("/count")
    fun countNotifications(@AuthenticationPrincipal user: User): ResponseEntity<ResponseBody> {
        val notificationCount = notificationService.countNotifications(user)
        return DataResponseBody(notificationCount, HttpStatus.OK).toResponseEntity()
    }
}