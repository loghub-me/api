package me.loghub.api.dto.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import me.loghub.api.constant.validation.RegexExpression
import me.loghub.api.lib.validation.Trimmed

data class UpdateUserProfileDTO(
    @field:NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    @field:Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하로 입력해주세요.")
    @field:Pattern(regexp = RegexExpression.NICKNAME, message = "닉네임은 영문 대소문자, 숫자, 한글로만 입력해주세요.")
    @field:Trimmed
    val nickname: String,

    @field:Size(max = 512, message = "자기소개는 최대 512자까지 입력할 수 있습니다.")
    val readme: String,
)
