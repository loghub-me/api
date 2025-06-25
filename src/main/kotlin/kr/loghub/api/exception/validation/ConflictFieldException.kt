package kr.loghub.api.exception.validation

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.exception.FieldException

class ConflictFieldException(
    field: String,
    message: String = ResponseMessage.Default.CONFLICT_FIELD
) : FieldException(field, message)