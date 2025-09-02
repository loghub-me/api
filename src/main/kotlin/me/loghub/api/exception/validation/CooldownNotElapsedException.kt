package me.loghub.api.exception.validation

import me.loghub.api.constant.message.ResponseMessage

class CooldownNotElapsedException(
    message: String = ResponseMessage.Default.COOLDOWN_NOT_ELAPSED
) : RuntimeException(message)
