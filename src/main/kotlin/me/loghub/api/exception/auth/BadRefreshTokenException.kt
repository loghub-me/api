package me.loghub.api.exception.auth

class BadRefreshTokenException(
    override val message: String,
) : RuntimeException(message)