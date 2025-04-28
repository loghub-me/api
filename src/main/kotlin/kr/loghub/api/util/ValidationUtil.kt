package kr.loghub.api.util

import kr.loghub.api.exception.auth.PermissionDeniedException
import kr.loghub.api.exception.entity.EntityExistsFieldException
import kr.loghub.api.exception.entity.EntityNotFoundFieldException
import kr.loghub.api.exception.validation.IllegalFieldException

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
    field: String,
    condition: Boolean,
    lazyMessage: () -> String,
) {
    if (condition) {
        val message = lazyMessage()
        throw EntityExistsFieldException(field, message)
    }
}

inline fun checkNotExists(
    field: String,
    condition: Boolean,
    lazyMessage: () -> String,
) {
    if (!condition) {
        val message = lazyMessage()
        throw EntityNotFoundFieldException(field, message)
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
