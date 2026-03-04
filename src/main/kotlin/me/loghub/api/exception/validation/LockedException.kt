package me.loghub.api.exception.validation

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.exception.common.FieldException

class LockedException(
    field: String,
    message: String = ResponseMessage.Default.LOCKED
) : FieldException(field, message)