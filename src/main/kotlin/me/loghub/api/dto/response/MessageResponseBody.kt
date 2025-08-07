package me.loghub.api.dto.response

import org.springframework.http.HttpStatus

data class MessageResponseBody(
    val message: String,
    override val status: HttpStatus
) : ResponseBody(status = status)
