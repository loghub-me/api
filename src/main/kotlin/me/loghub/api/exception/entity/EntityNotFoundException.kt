package me.loghub.api.exception.entity

import me.loghub.api.constant.message.ResponseMessage

class EntityNotFoundException(
    message: String = ResponseMessage.Default.NOT_FOUND
) : RuntimeException(message)