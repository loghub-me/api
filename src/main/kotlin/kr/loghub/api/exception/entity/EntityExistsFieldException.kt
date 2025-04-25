package kr.loghub.api.exception.entity

import kr.loghub.api.constant.message.ResponseMessage

class EntityExistsFieldException(
    val field: String,
    override val message: String = ResponseMessage.INVALID_REQUEST,
) : RuntimeException(message) {
}