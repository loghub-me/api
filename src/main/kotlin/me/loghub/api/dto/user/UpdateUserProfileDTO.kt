package me.loghub.api.dto.user

import jakarta.validation.constraints.Size
import me.loghub.api.lib.validation.NicknameValidation

data class UpdateUserProfileDTO(
    @field:NicknameValidation
    val nickname: String,

    @field:Size(max = 1024, message = "자기소개는 최대 1024자까지 입력할 수 있습니다.")
    val readme: String,
)
