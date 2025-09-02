package me.loghub.api.util

import me.loghub.api.exception.entity.EntityNotFoundException
import java.util.*

fun <T> Optional<T>.orElseThrowNotFound(lazyMessage: () -> String): T {
    val message = lazyMessage()
    return this.orElseThrow { EntityNotFoundException(message) }
}
