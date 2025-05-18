package kr.loghub.api.exception.entity

import kr.loghub.api.constant.message.ResponseMessage

class EntityExistsException(
    message: String = ResponseMessage.Default.ALREADY_EXISTS
) : RuntimeException(message)