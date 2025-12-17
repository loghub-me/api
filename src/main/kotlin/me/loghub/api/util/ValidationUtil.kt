@file:OptIn(ExperimentalContracts::class)

package me.loghub.api.util

import me.loghub.api.exception.auth.PermissionDeniedException
import me.loghub.api.exception.entity.EntityConflictException
import me.loghub.api.exception.entity.EntityExistsFieldException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.exception.entity.EntityNotFoundFieldException
import me.loghub.api.exception.validation.ConflictFieldException
import me.loghub.api.exception.validation.CooldownNotElapsedException
import me.loghub.api.exception.validation.IllegalFieldException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

inline fun checkField(
    field: String,
    condition: Boolean,
    lazyMessage: () -> String,
) {
    contract {
        returns() implies condition
    }

    if (!condition) {
        val message = lazyMessage()
        throw IllegalFieldException(field, message)
    }
}

inline fun checkExists(
    condition: Boolean,
    lazyMessage: () -> String,
) {
    contract {
        returns() implies condition
    }

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
    contract {
        returns() implies condition
    }

    if (!condition) {
        val message = lazyMessage()
        throw EntityNotFoundFieldException(field, message)
    }
}

inline fun checkPublished(
    condition: Boolean,
    lazyMessage: () -> String,
) {
    contract {
        returns() implies condition
    }

    if (!condition) {
        val message = lazyMessage()
        throw EntityNotFoundException(message)
    }
}

inline fun checkConflict(
    condition: Boolean,
    lazyMessage: () -> String,
) {
    contract {
        returns() implies !condition
    }

    if (condition) {
        val message = lazyMessage()
        throw EntityConflictException(message)
    }
}

inline fun checkConflict(
    field: String,
    condition: Boolean,
    lazyMessage: () -> String,
) {
    contract {
        returns() implies !condition
    }

    if (condition) {
        val message = lazyMessage()
        throw EntityExistsFieldException(field, message)
    }
}

inline fun checkCooldown(
    condition: Boolean,
    lazyMessage: () -> String,
) {
    contract {
        returns() implies !condition
    }

    if (condition) {
        val message = lazyMessage()
        throw CooldownNotElapsedException(message)
    }
}

inline fun checkPermission(
    condition: Boolean,
    lazyMessage: () -> String,
) {
    contract {
        returns() implies condition
    }

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
