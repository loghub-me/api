package kr.loghub.api.exception.entity

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.exception.FieldException

class EntityNotFoundFieldException(
    field: String,
    message: String = ResponseMessage.Default.NOT_FOUND
) : FieldException(field, message)