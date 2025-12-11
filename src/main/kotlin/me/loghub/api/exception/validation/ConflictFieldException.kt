package me.loghub.api.exception.validation

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.exception.common.FieldException

class ConflictFieldException(
    field: String,
    message: String = ResponseMessage.Default.CONFLICT_FIELD
) : FieldException(field, message)