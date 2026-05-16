package me.loghub.api.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.OffsetDateTime

abstract class ResponseBody(
    val timestamp: OffsetDateTime = OffsetDateTime.now(),
    open val status: HttpStatus,
) {
    @get:JsonProperty("code")
    val code: Int get() = status.value()

    fun toResponseEntity() = ResponseEntity.status(status).body(this)
}
