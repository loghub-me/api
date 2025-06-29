package kr.loghub.api.dto.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kr.loghub.api.lib.validation.Trimmed

data class UpdateUsernameDTO(
    @field:NotBlank(message = "아이디는 필수 입력 항목입니다.")
    @field:Size(min = 4, max = 12, message = "아이디는 4자 이상 12자 이하로 입력해주세요.")
    @field:Pattern(regexp = "^[a-z0-9]*$", message = "유저명은 영문 소문자와 숫자로만 입력해주세요.")
    @field:Trimmed
    val newUsername: String,
)
