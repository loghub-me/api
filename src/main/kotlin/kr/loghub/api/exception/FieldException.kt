package kr.loghub.api.exception

open class FieldException(
    val field: String,
    override val message: String
) : RuntimeException(message)