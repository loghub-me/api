package kr.loghub.api.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class ResponseBody(
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
    open val status: HttpStatus,
) {
    @get:JsonProperty("code")
    val code: Int get() = status.value()

    fun toResponseEntity() = ResponseEntity.status(status).body(this)
}
