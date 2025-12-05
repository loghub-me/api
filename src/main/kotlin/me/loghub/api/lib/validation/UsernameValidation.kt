package me.loghub.api.lib.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

private const val USERNAME_REGEX = "[a-zA-Z0-9]+$"

// root, admin,
// search, post, edit,
// join, login, logout,
// setting, settings, support, notifications,
// article, articles, series, question, questions, topic, topics
private const val USERNAME_RESERVED =
    "root|admin|search|post|edit|join|login|logout|setting|settings|support|notifications|article|articles|series|question|questions|topic|topics"
private const val USERNAME_RESERVED_REGEX = "^(?!.*\\b($USERNAME_RESERVED)\\b).*$"

@Constraint(validatedBy = [])
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER
)
@Retention(AnnotationRetention.RUNTIME)
@NotBlank(message = "유저네임은 필수 입력 항목입니다.")
@Size(min = 4, max = 12, message = "유저네임은 4자 이상 12자 이하이어야 합니다.")
@Pattern(regexp = USERNAME_REGEX, message = "유저네임은 영문 소문자와 숫자로만 입력해주세요.")
@Pattern(regexp = USERNAME_RESERVED_REGEX, message = "유저네임에 사용할 수 없는 단어가 포함되어 있습니다.")
@Trimmed
annotation class UsernameValidation(
    val message: String = "잘못된 유저네임입니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)