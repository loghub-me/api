package me.loghub.api.exception.entity

import me.loghub.api.constant.message.ResponseMessage

class EntityConflictException(
    message: String = ResponseMessage.Default.ALREADY_EXISTS
) : RuntimeException(message)
