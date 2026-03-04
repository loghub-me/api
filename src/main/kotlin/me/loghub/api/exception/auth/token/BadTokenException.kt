package me.loghub.api.exception.auth.token

open class BadTokenException(
    override val message: String,
) : RuntimeException(message)