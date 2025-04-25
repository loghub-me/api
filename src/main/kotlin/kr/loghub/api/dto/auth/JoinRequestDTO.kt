package kr.loghub.api.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kr.loghub.api.entity.auth.otp.JoinOTP
import kr.loghub.api.util.OTPBuilder

data class JoinRequestDTO(
    @field:Email(message = "이메일 형식이 아닙니다.")
    @field:NotBlank(message = "이메일은 필수 입력 항목입니다.")
    val email: String,

    @field:NotBlank(message = "아이디는 필수 입력 항목입니다.")
    @field:Size(min = 4, max = 12, message = "아이디는 4자 이상 12자 이하로 입력해주세요.")
    @field:Pattern(regexp = "^[a-z0-9]*$", message = "유저명은 영문 소문자와 숫자로만 입력해주세요.")
    val username: String,

    @field:NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    @field:Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하로 입력해주세요.")
    @field:Pattern(regexp = "^[a-zA-Z0-9가-힣]*$", message = "닉네임은 영문 대소문자, 숫자, 한글로만 입력해주세요.")
    val nickname: String,
) {
    fun toEntity() = JoinOTP(
        otp = OTPBuilder.generateOTP(),
        email = email,
        username = username,
        nickname = nickname,
    )
}
