package kr.loghub.api.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kr.loghub.api.lib.validation.Trimmed

data class JoinConfirmDTO(
    @field:Email(message = "올바르지 않은 이메일 형식입니다.")
    @field:NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @field:Trimmed
    val email: String,

    @field:Size(min = 6, max = 6, message = "인증코드는 6자리 숫자입니다.")
    @field:Trimmed
    val otp: String,
)
