package me.loghub.api.util

import me.loghub.api.exception.auth.PermissionDeniedException
import me.loghub.api.exception.entity.EntityExistsException
import me.loghub.api.exception.entity.EntityExistsFieldException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.exception.entity.EntityNotFoundFieldException
import me.loghub.api.exception.validation.ConflictFieldException
import me.loghub.api.exception.validation.IllegalFieldException

inline fun checkField(
    field: String,
    condition: Boolean,
    lazyMessage: () -> String,
) {
    if (!condition) {
        val message = lazyMessage()
        throw IllegalFieldException(field, message)
    }
}

inline fun checkExists(
    condition: Boolean,
    lazyMessage: () -> String,
) {
    if (!condition) {
        val message = lazyMessage()
        throw EntityNotFoundException(message)
    }
}

inline fun checkExists(
    field: String,
    condition: Boolean,
    lazyMessage: () -> String,
) {
    if (!condition) {
        val message = lazyMessage()
        throw EntityNotFoundFieldException(field, message)
    }
}

inline fun checkAlreadyExists(
    condition: Boolean,
    lazyMessage: () -> String,
) {
    if (condition) {
        val message = lazyMessage()
        throw EntityExistsException(message)
    }
}

inline fun checkAlreadyExists(
    field: String,
    condition: Boolean,
    lazyMessage: () -> String,
) {
    if (condition) {
        val message = lazyMessage()
        throw EntityExistsFieldException(field, message)
    }
}

inline fun checkPermission(
    condition: Boolean,
    lazyMessage: () -> String,
) {
    if (!condition) {
        val message = lazyMessage()
        throw PermissionDeniedException(message)
    }
}

inline fun requireNotEquals(
    field: String,
    a: Any?,
    b: Any?,
    lazyMessage: () -> String,
) {
    if (a == b) {
        val message = lazyMessage()
        throw ConflictFieldException(field, message)
    }
}