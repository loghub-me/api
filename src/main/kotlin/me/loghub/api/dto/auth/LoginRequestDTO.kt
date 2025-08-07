package me.loghub.api.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import me.loghub.api.lib.validation.Trimmed

data class LoginRequestDTO(
    @field:Email(message = "올바르지 않은 이메일 형식입니다.")
    @field:NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @field:Trimmed
    val email: String,
) {
}
