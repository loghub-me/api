package me.loghub.api.lib.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Constraint(validatedBy = [])
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER
)
@Retention(AnnotationRetention.RUNTIME)
@Size(min = 6, max = 6, message = "인증코드는 6자리 숫자입니다.")
@Trimmed
annotation class OTPValidation(
    val message: String = "잘못된 인증코드입니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)