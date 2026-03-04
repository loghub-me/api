package me.loghub.api.exception.auth.token

class BadEmailBlockTokenException(
    override val message: String,
) : BadTokenException(message)