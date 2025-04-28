package kr.loghub.api.exception.auth

import kr.loghub.api.constant.message.ResponseMessage

class PermissionDeniedException(
    override val message: String = ResponseMessage.Auth.FORBIDDEN
) : RuntimeException(message)