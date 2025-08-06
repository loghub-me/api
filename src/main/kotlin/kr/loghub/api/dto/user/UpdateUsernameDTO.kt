package kr.loghub.api.dto.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kr.loghub.api.constant.validation.RegexExpression
import kr.loghub.api.lib.validation.Trimmed

data class UpdateUsernameDTO(
    @field:NotBlank(message = "유저네임은 필수 입력 항목입니다.")
    @field:Size(min = 4, max = 12, message = "유저네임은 4자 이상 12자 이하이어야 합니다.")
    @field:Pattern(regexp = RegexExpression.USERNAME, message = "유저네임은 영문 소문자와 숫자로만 입력해주세요.")
    @field:Trimmed
    val newUsername: String,
)
