package kr.loghub.api.dto.response

import org.springframework.http.HttpStatus

data class DataResponseBody<T>(
    val data: T,
    override val status: HttpStatus
) : ResponseBody(status = status)
