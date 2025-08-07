package me.loghub.api.dto.response

import org.springframework.http.HttpStatus

data class FieldErrorsResponseBody(
    val fieldErrors: Map<String, String>,
    override val status: HttpStatus
) : ResponseBody(status = status)
