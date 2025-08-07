package me.loghub.api.dto.response

import org.springframework.http.HttpStatus

data class RedirectResponseBody(
    val pathname: String,
    val message: String,
    override val status: HttpStatus
) : ResponseBody(status = status)
