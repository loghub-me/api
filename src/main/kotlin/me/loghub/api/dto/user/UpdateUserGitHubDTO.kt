package me.loghub.api.dto.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import me.loghub.api.lib.validation.Trimmed

private const val USERNAME_REGEX = "^[a-zA-Z0-9-]+$"

data class UpdateUserGitHubDTO(
    @field:NotBlank(message = "GitHub 유저네임은 필수 입력 항목입니다.")
    @field:Size(min = 3, max = 39, message = "GitHub 유저네임은 3자 이상 39자 이하로 입력해주세요.")
    @field:Pattern(regexp = USERNAME_REGEX, message = "GitHub 유저네임은 영문 대소문자, 숫자, 하이픈(-)으로만 입력해주세요.")
    @field:Trimmed
    val username: String,
)
