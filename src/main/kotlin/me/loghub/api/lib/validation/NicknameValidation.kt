package me.loghub.api.lib.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

private const val NICKNAME_REGEX = "^[a-zA-Z0-9가-힣]+(?: [a-zA-Z0-9가-힣]+)*$"

@Constraint(validatedBy = [])
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER
)
@Retention(AnnotationRetention.RUNTIME)
@NotBlank(message = "닉네임은 필수 입력 항목입니다.")
@Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하로 입력해주세요.")
@Pattern(regexp = NICKNAME_REGEX, message = "닉네임은 영문 대소문자, 숫자, 한글로만 입력해주세요.")
@Trimmed
annotation class NicknameValidation(
    val message: String = "잘못된 닉네임입니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)