package me.loghub.api.lib.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import kotlin.reflect.KClass

private const val THUMBNAIL_REGEX = "^[0-9]+/[a-zA-Z0-9_-]+\\.webp$"

@Constraint(validatedBy = [])
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER
)
@Retention(AnnotationRetention.RUNTIME)
@NotBlank(message = "썸네일은 필수 입력 항목입니다.")
@Pattern(regexp = THUMBNAIL_REGEX, message = "올바르지 않은 썸네일 형식입니다.")
@Trimmed
annotation class ThumbnailValidation(
    val message: String = "잘못된 썸네일입니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)