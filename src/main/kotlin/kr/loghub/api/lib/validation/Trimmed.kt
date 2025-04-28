package kr.loghub.api.lib.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.ReportAsSingleViolation
import jakarta.validation.constraints.Pattern
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass


@MustBeDocumented
@Constraint(validatedBy = [])
@Target(FIELD, VALUE_PARAMETER, ANNOTATION_CLASS, PROPERTY_GETTER)
@Retention(RUNTIME)
@Pattern(
    regexp = "^\\S(.*\\S)?$",
    message = "앞뒤에 공백 없이 작성해주세요."
)
@ReportAsSingleViolation  // 실패 시 이 애너테이션의 message만 노출
annotation class Trimmed(
    val message: String = "앞뒤에 공백 없이 작성해주세요.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)