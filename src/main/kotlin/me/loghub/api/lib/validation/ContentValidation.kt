package me.loghub.api.lib.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Constraint(validatedBy = [])
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER
)
@Retention(AnnotationRetention.RUNTIME)
@NotBlank(message = "내용은 필수 입력 항목입니다.")
@Size(min = 10, max = 16384, message = "내용은 10자 이상 16384자 이하이어야 합니다.")
annotation class ContentValidation(
    val message: String = "잘못된 내용입니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)