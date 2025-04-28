package kr.loghub.api.dto.response

import org.springframework.http.HttpStatus

data class RedirectResponseBody(
    val key: String,
    val message: String,
    override val status: HttpStatus
) : ResponseBody(status = status)
