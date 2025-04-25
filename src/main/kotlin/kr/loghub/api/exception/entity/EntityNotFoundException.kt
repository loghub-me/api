package kr.loghub.api.exception.entity

import kr.loghub.api.constant.message.ResponseMessage

class EntityNotFoundException(
    val field: String,
    override val message: String = ResponseMessage.NOT_FOUND,
) : RuntimeException(message) {
}