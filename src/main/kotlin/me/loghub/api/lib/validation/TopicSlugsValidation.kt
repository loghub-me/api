package me.loghub.api.lib.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Constraint(validatedBy = [])
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER
)
@Retention(AnnotationRetention.RUNTIME)
@NotNull(message = "토픽은 필수 입력 항목입니다.")
@Size(max = 10, message = "토픽은 최대 10개까지 선택할 수 있습니다.")
annotation class TopicSlugsValidation(
    val message: String = "잘못된 토픽입니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)