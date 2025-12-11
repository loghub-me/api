package me.loghub.api.exception.entity

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.exception.common.FieldException

class EntityExistsFieldException(
    field: String,
    message: String = ResponseMessage.Default.ALREADY_EXISTS
) : FieldException(field, message)