package kr.loghub.api.exception.validation

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.exception.FieldException

class IllegalFieldException(
    field: String,
    message: String = ResponseMessage.Default.INVALID_FIELD
) : FieldException(field, message)