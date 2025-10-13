package me.loghub.api.dto.auth.join

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import me.loghub.api.constant.validation.RegexExpression
import me.loghub.api.lib.validation.Trimmed

data class JoinRequestDTO(
    @field:Email(message = "올바르지 않은 이메일 형식입니다.")
    @field:NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @field:Trimmed
    val email: String,

    @field:NotBlank(message = "유저네임은 필수 입력 항목입니다.")
    @field:Size(min = 4, max = 12, message = "유저네임은 4자 이상 12자 이하이어야 합니다.")
    @field:Pattern(regexp = RegexExpression.USERNAME, message = "유저네임은 영문 소문자와 숫자로만 입력해주세요.")
    @field:Pattern(regexp = RegexExpression.USERNAME_RESERVED, message = "유저네임에 사용할 수 없는 단어가 포함되어 있습니다.")
    @field:Trimmed
    val username: String,

    @field:NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    @field:Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하로 입력해주세요.")
    @field:Pattern(regexp = RegexExpression.NICKNAME, message = "닉네임은 영문 대소문자, 숫자, 한글로만 입력해주세요.")
    @field:Trimmed
    val nickname: String,
)
