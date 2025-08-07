package me.loghub.api.exception.entity

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.exception.FieldException

class EntityNotFoundFieldException(
    field: String,
    message: String = ResponseMessage.Default.NOT_FOUND
) : FieldException(field, message)