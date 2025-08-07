package me.loghub.api.exception.auth

import me.loghub.api.constant.message.ResponseMessage

class PermissionDeniedException(
    override val message: String = ResponseMessage.Auth.FORBIDDEN
) : RuntimeException(message)