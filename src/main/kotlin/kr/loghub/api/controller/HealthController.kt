package kr.loghub.api.controller

import kr.loghub.api.constant.ResponseMessage
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/health")
class HealthController {
    @GetMapping
    fun checkHealth() = ResponseEntity.ok().body(ResponseMessage.IM_GOOD)
}