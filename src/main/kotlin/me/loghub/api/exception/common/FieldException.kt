package me.loghub.api.exception.common

open class FieldException(
    val field: String,
    override val message: String
) : RuntimeException(message)