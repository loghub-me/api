package me.loghub.api.exception.auth.token

class BadRefreshTokenException(
    override val message: String,
) : BadTokenException(message)