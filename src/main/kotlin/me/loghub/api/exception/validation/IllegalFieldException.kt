package me.loghub.api.exception.validation

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.exception.FieldException

class IllegalFieldException(
    field: String,
    message: String = ResponseMessage.Default.INVALID_FIELD
) : FieldException(field, message)