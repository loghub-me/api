package me.loghub.api.exception.auth

class BadOTPException(
    override val message: String,
) : RuntimeException(message)