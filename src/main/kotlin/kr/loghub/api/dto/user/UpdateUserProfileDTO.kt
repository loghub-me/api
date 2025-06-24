package kr.loghub.api.dto.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kr.loghub.api.lib.validation.Trimmed

data class UpdateUserProfileDTO(
    @field:Email(message = "이메일 형식이 아닙니다.")
    @field:NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @field:Trimmed
    val nickname: String,

    @field:Size(max = 512, message = "자기소개는 최대 512자까지 입력할 수 있습니다.")
    @field:Trimmed
    val readme: String,
)
