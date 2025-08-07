package me.loghub.api.exception.entity

import me.loghub.api.constant.message.ResponseMessage

class EntityExistsException(
    message: String = ResponseMessage.Default.ALREADY_EXISTS
) : RuntimeException(message)