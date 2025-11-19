package me.loghub.api.dto.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import me.loghub.api.constant.validation.RegexExpression
import me.loghub.api.lib.validation.Trimmed

data class UpdateUsernameDTO(
    @field:NotBlank(message = "유저네임은 필수 입력 항목입니다.")
    @field:Size(min = 4, max = 12, message = "유저네임은 4자 이상 12자 이하이어야 합니다.")
    @field:Pattern(regexp = RegexExpression.USERNAME, message = "유저네임은 영문 소문자와 숫자로만 입력해주세요.")
    @field:Pattern(regexp = RegexExpression.USERNAME_RESERVED, message = "유저네임에 사용할 수 없는 단어가 포함되어 있습니다.")
    @field:Trimmed
    val newUsername: String,
)
