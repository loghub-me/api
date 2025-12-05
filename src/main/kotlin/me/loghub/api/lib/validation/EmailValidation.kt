package me.loghub.api.lib.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import kotlin.reflect.KClass

@Constraint(validatedBy = [])
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER
)
@Retention(AnnotationRetention.RUNTIME)
@Email(message = "올바르지 않은 이메일 형식입니다.")
@NotBlank(message = "이메일은 필수 입력 항목입니다.")
@Trimmed
annotation class EmailValidation(
    val message: String = "잘못된 이메일입니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)