package kr.loghub.api.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import kr.loghub.api.entity.auth.LoginOTP
import kr.loghub.api.lib.validation.Trimmed
import kr.loghub.api.util.OTPBuilder

data class LoginRequestDTO(
    @field:Email(message = "이메일 형식이 아닙니다.")
    @field:NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @field:Trimmed
    val email: String,
) {
    fun toEntity() = LoginOTP(
        otp = OTPBuilder.generateOTP(),
        email = email,
    )
}
