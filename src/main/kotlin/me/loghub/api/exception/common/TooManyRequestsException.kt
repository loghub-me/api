package me.loghub.api.exception.common

import me.loghub.api.constant.message.ResponseMessage

class TooManyRequestsException(
    override val message: String = ResponseMessage.Default.TOO_MANY_REQUESTS
) : RuntimeException(message)
