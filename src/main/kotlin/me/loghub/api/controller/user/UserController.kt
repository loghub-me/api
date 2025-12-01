package me.loghub.api.controller.user

import me.loghub.api.dto.user.UserDetailDTO
import me.loghub.api.dto.user.UserProfileDTO
import me.loghub.api.service.user.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {
    @GetMapping("/@{username}")
    fun getUser(@PathVariable username: String): ResponseEntity<UserDetailDTO> {
        val user = userService.getUser(username)
        return ResponseEntity.ok(user)
    }

    @GetMapping("/@{username}/profile")
    fun getUserProfile(@PathVariable username: String): ResponseEntity<UserProfileDTO> {
        val user = userService.getUserProfile(username)
        return ResponseEntity.ok(user)
    }
}