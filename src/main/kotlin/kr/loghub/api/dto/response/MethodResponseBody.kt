package kr.loghub.api.dto.response

import org.springframework.http.HttpStatus

data class MethodResponseBody(
    val id: Long,
    val message: String,
    override val status: HttpStatus
) : ResponseBody(status = status)
